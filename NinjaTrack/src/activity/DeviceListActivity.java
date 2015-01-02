package activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nyp.fypj.ninjatrack.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class DeviceListActivity extends Activity {

	private BluetoothDevice mDevice;
	private ArrayList<BluetoothDevice> devices;
	private List<Map<String, String>> listItems = new ArrayList<Map<String, String>>();
	private SimpleAdapter adapter;
	private Map<String, String> map = null;
	private SwipeRefreshLayout swipeLayout;
	private ListView listView;
	private String DEVICE_NAME = "name";
	private String DEVICE_ADDRESS = "address";
	public static final int RESULT_CODE = 31;

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.device_list);

		setTitle("Select a Device");

		swipeLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_container);
		swipeLayout.setOnRefreshListener(new OnRefreshListener() {
			@Override
			public void onRefresh() {
				new Handler().postDelayed(new Runnable() {
					@Override
					public void run() {
						swipeLayout.setRefreshing(false);
					}
				}, 3000);
			}
		});
		swipeLayout.setColorScheme(
				android.R.color.holo_blue_bright, 
	            android.R.color.holo_green_light, 
	            android.R.color.holo_orange_light, 
	            android.R.color.holo_red_light
	    );
		
		listView = (ListView) findViewById(R.id.lv_devices);

		devices = (ArrayList<BluetoothDevice>) SplashActivity.mLeDeviceList;
		for (BluetoothDevice device : devices) {
			map = new HashMap<String, String>();
			map.put(DEVICE_ADDRESS, device.getAddress());
			map.put(DEVICE_NAME, device.getName());
			listItems.add(map);
		}

		adapter = new SimpleAdapter(getApplicationContext(), listItems,
				R.layout.device_list_item, new String[] { "name", "address" },
				new int[] { R.id.tv_device_name, R.id.tv_device_address });
		adapter.notifyDataSetChanged();
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
				HashMap<String, String> hashMap = (HashMap<String, String>) listItems.get(position);
				String addr = hashMap.get(DEVICE_ADDRESS);
				
				for (BluetoothDevice device : devices) {
					if(device.getAddress().equals(addr)) {
						mDevice = device;
					}
				}
				
				SplashActivity.mBluetoothLeService.connect(addr);
				Intent intent = new Intent(DeviceListActivity.this, MainActivity.class);
				intent.putExtra("device", mDevice);
				startActivity(intent);
				finish();
			}
		});
	}

	@Override
	public void onBackPressed() {
		AlertDialog.Builder dialog = new AlertDialog.Builder(DeviceListActivity.this);
		dialog.setMessage("Quit Ninja?")
			.setPositiveButton("Quit", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					Intent intent = new Intent(getApplicationContext(), SplashActivity.class);
					intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					intent.putExtra("EXIT", true);
					startActivity(intent);
					finish();
				}
			})
			.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.cancel();
				}
			});
		
		dialog.create().show();
	}
}
