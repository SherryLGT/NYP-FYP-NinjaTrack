package activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import android.os.IBinder;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import controller.RBLService;

public class RetrieveDevicesActivity extends Activity {

	private RBLService mBluetoothLeService;
	private BluetoothManager mBluetoothManager;
	private BluetoothAdapter mBluetoothAdapter;
	public static ArrayList<BluetoothDevice> mLeDeviceList;
    private boolean mConnected = false;
	
	private static final int REQUEST_ENABLE_BT = 1;
    private static final long SCAN_PERIOD = 3000; // 3 seconds
	public static final int REQUEST_CODE = 30;
	private boolean flag = true;
	private boolean connState = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.device_list);

		// Setting up GATT service
		Intent gattServiceIntent = new Intent(RetrieveDevicesActivity.this, RBLService.class);
		bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);
		
		// Initializes Bluetooth adapter
		mBluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
		mBluetoothAdapter = mBluetoothManager.getAdapter();
		
		// Ensures Bluetooh status
		if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
		    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
		    startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
		}
		
		mLeDeviceList = new ArrayList<BluetoothDevice>();
		
		if (!mConnected) {
			scanLeDevice();

			try {
				Thread.sleep(SCAN_PERIOD);
				Intent intent = new Intent(getApplicationContext(), DeviceListActivity.class);
				startActivityForResult(intent, REQUEST_CODE);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		} else {
			mBluetoothLeService.disconnect();
			mBluetoothLeService.close();
		}
		
		registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		Intent intent = new Intent(RetrieveDevicesActivity.this, MainActivity.class);
		startActivity(intent);
	}

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
    
    /** Broadcast Receiver **/
    private BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
	        final String action = intent.getAction();
	        if (RBLService.ACTION_GATT_CONNECTED.equals(action)) {
	            mConnected = true;
				startReadRssi();
	        } else if (RBLService.ACTION_GATT_DISCONNECTED.equals(action)) {
	            mConnected = false;
	        } else if (RBLService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
	            // Show all the supported services and characteristics on the user interface
	            // displayGattServices(RBLService.getSupportedGattServices());
	        } else if (RBLService.ACTION_DATA_AVAILABLE.equals(action)) {
	            // displayData(intent.getStringExtra(RBLService.EXTRA_DATA));
	        }
		}    	
    };

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

    /** Intent Filter **/
	private static IntentFilter makeGattUpdateIntentFilter() {
		final IntentFilter intentFilter = new IntentFilter();

		intentFilter.addAction(RBLService.ACTION_GATT_CONNECTED);
		intentFilter.addAction(RBLService.ACTION_GATT_DISCONNECTED);
		intentFilter.addAction(RBLService.ACTION_GATT_RSSI);

		return intentFilter;
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

	@Override
	protected void onDestroy() {
		super.onDestroy();

		if (mServiceConnection != null)
			unbindService(mServiceConnection);

		System.exit(0);
	}
}