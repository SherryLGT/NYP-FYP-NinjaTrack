package activity;

import java.util.ArrayList;

import nyp.fypj.ninjatrack.R;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.view.Window;
import android.view.WindowManager;
import controller.RBLService;

public class SplashActivity extends Activity {
	
	public static RBLService mBluetoothLeService;
	private BluetoothManager mBluetoothManager;
	private BluetoothAdapter mBluetoothAdapter;
	public static ArrayList<BluetoothDevice> mLeDeviceList;
    private boolean mConnected = false;

	private final int REQUEST_ENABLE_BT = 1;
    private final long SCAN_PERIOD = 3000; // 3 seconds
	public final int REQUEST_CODE = 30;
	private boolean flag = true;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
        WindowManager.LayoutParams.FLAG_FULLSCREEN);
        
		setContentView(R.layout.activity_splashscreen);	
		
		mLeDeviceList = new ArrayList<BluetoothDevice>();
		
		// Initializes Bluetooth adapter
		mBluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
		mBluetoothAdapter = mBluetoothManager.getAdapter();

		// Setting up GATT service
		Intent gattServiceIntent = new Intent(SplashActivity.this, RBLService.class);
		bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);
		
		// Ensures Bluetooth status
		if (!mBluetoothAdapter.isEnabled()) {
		    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
		    startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
		}
		else {
			startScan();
		}
	}
    
    @Override
	protected void onResume() {
		super.onResume();
		
		registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		if(requestCode == REQUEST_ENABLE_BT && resultCode == Activity.RESULT_OK) {
			startScan();
		}
		else {
			finish();
		}
	}
    
    /** Run scan **/
    private void startScan() {
		if (!mConnected) {
			scanLeDevice();
			
	        new Handler().postDelayed(new Runnable(){
	        	@Override
	        	public void run() {
	        		Intent intent = new Intent(SplashActivity.this, DeviceListActivity.class);
	        		startActivity(intent);
	        	}
	        }, SCAN_PERIOD);
		} else {
			mBluetoothLeService.disconnect();
			mBluetoothLeService.close();
		}
    }

	/** Broadcast Receiver **/
    private BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
	        final String action = intent.getAction();
	        if (RBLService.ACTION_GATT_CONNECTED.equals(action)) {
	        	flag = true;
	            mConnected = true;
				startReadRssi();
	        } else if (RBLService.ACTION_GATT_DISCONNECTED.equals(action)) {
	        	flag = false;
	            mConnected = false;
	        } else if (RBLService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
	            // Show all the supported services and characteristics on the user interface
	            // displayGattServices(RBLService.getSupportedGattServices());
	        } else if (RBLService.ACTION_DATA_AVAILABLE.equals(action)) {
	            // displayData(intent.getStringExtra(RBLService.EXTRA_DATA));
	        }
		}
    };

    /** Intent Filter **/
	private static IntentFilter makeGattUpdateIntentFilter() {
		final IntentFilter intentFilter = new IntentFilter();

		intentFilter.addAction(RBLService.ACTION_GATT_CONNECTED);
		intentFilter.addAction(RBLService.ACTION_GATT_DISCONNECTED);
		intentFilter.addAction(RBLService.ACTION_GATT_RSSI);

		return intentFilter;
	}

    /** Read Rssi **/
	private void startReadRssi() {
		new Thread() {
			public void run() {
				while (flag) {
					mBluetoothLeService.readRssi();
					try {
						sleep(500);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			};
		}.start();
	}

    /** Service Connection **/
	private ServiceConnection mServiceConnection = new ServiceConnection() {

		@Override
		public void onServiceConnected(ComponentName componentName, IBinder service) {
			mBluetoothLeService = ((RBLService.LocalBinder) service).getService();
			if (!mBluetoothLeService.initialize()) {
				finish();
			}
		}

		@Override
		public void onServiceDisconnected(ComponentName componentName) {
			mBluetoothLeService = null;
		}
	};

	/** Finding BLE Devices **/
    private void scanLeDevice() {
		new Thread() {
			@Override
			public void run() {
				mBluetoothAdapter.startLeScan(mLeScanCallback);

				try {
					Thread.sleep(SCAN_PERIOD);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

				mBluetoothAdapter.stopLeScan(mLeScanCallback);
			}
		}.start();
    }
    
    /** Device Scan Callback **/
    private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {
            runOnUiThread(new Runnable() {
               @Override
               public void run() {
					if (device != null) {
						if (mLeDeviceList.indexOf(device) == -1)
							mLeDeviceList.add(device);
					}
               }
           });
       }
    };

	@Override
	protected void onStop() {
		super.onStop();
		
		flag = false;		
		unregisterReceiver(mGattUpdateReceiver);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		
		if (mServiceConnection != null) {
			unbindService(mServiceConnection);
		}
		
		//System.exit(0);
	}
}
