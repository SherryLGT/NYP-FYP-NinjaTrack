package activity;

import nyp.fypj.ninjatrack.R;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;

public class SplashActivity extends Activity {
	
	private BluetoothManager bluetoothManager;
	private BluetoothAdapter bluetoothAdapter;
	
	private final int REQUEST_ENABLE_BT = 1;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
        WindowManager.LayoutParams.FLAG_FULLSCREEN);
        
		setContentView(R.layout.activity_splashscreen);	
		
		// Initializes Bluetooth adapter
		bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
		bluetoothAdapter = bluetoothManager.getAdapter();
		
		if(bluetoothAdapter.isEnabled()) {
			new Handler().postDelayed(new Runnable(){
	        	@Override
	        	public void run() {
	        		Intent intent = new Intent(SplashActivity.this, DeviceListActivity.class);
	        		startActivity(intent);
	        		finish();
	        	}
	        }, 1000);
		}
	}
    
    @Override
	protected void onResume() {
		super.onResume();
		
		// Ensures Bluetooth status
		if (!bluetoothAdapter.isEnabled()) {
		    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
		    startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		if(requestCode == REQUEST_ENABLE_BT && resultCode == Activity.RESULT_OK) {
    		Intent intent = new Intent(SplashActivity.this, DeviceListActivity.class);
    		startActivity(intent);
    		finish();
		}
		else {
			finish();
		}
	}
}
