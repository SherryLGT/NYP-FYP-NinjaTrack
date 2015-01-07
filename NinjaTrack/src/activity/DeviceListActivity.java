package activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import model.Device;
import nyp.fypj.ninjatrack.R;
import redbearservice.IRedBearServiceEventListener;
import redbearservice.RedBearService;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.ParcelUuid;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class DeviceListActivity extends Activity {
	
	public static RedBearService redBearService;
	private ArrayList<Device> deviceList;
	private Map<String, String> map = null;
	private List<Map<String, String>> listItems;
	private String DEVICE_NAME = "name";
	private String DEVICE_ADDRESS = "address";
	public static Device device;

    private final long SCAN_PERIOD = 3000; // 3 seconds
	
    private ProgressDialog progress;
	private SwipeRefreshLayout swipeLayout;
	private ListView listView;
	private SimpleAdapter adapter;
	
	public RedBearService getRedBearService() {
		return redBearService;
	}
    
	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.device_list);
		
		// Setting up RedBearService
		Intent serviceIntent = new Intent(DeviceListActivity.this, RedBearService.class);
		bindService(serviceIntent, serviceConnection, BIND_AUTO_CREATE);
		
		setTitle("Select a Device");
		progress = new ProgressDialog(this);
		progress.setMessage("Retrieving device");
		progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		progress.setIndeterminate(true);
		progress.show();
		
		swipeLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_container);
		listView = (ListView) findViewById(R.id.lv_devices);
		
		deviceList = new ArrayList<Device>();
		listItems = new ArrayList<Map<String, String>>();
		
		swipeLayout.setOnRefreshListener(new OnRefreshListener() {
			@Override
			public void onRefresh() {
				RefreshTask refreshTask = new RefreshTask();
				refreshTask.execute();
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
		
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				device = deviceList.get(position);
				redBearService.connectDevice(device.getAddress(), false);
				
				Intent intent = new Intent(DeviceListActivity.this, MainActivity.class);
				startActivity(intent);
			}
		});
	}

	@Override
	protected void onResume() {
		super.onResume();
		
		if(device != null) {
			redBearService.disconnectDevice(device.getAddress());
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		
		device = null;
		unbindService(serviceConnection);
	}

	/** Service Connection **/
	private ServiceConnection serviceConnection = new ServiceConnection() {
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			redBearService = ((RedBearService.LocalBinder) service).getService();
			
			if(redBearService != null) {
				if(deviceList != null) {
					deviceList.clear();
				}
				if(listItems != null) {
					if(listItems.size() != 0) {
						listItems.clear();
					}
				}
				redBearService.setListener(redBearServiceEventListener);
				redBearService.startScanDevice();
				
		        new Handler().postDelayed(new Runnable(){
		        	@Override
		        	public void run() {
		        		if(redBearService != null) {
		        			redBearService.stopScanDevice();
		        			progress.dismiss();
		        			for (Device device : deviceList) {
		        				map = new HashMap<String, String>();
		        				map.put(DEVICE_ADDRESS, device.getAddress());
		        				map.put(DEVICE_NAME, device.getName());
		        				listItems.add(map);
		        			}

		        			adapter = new SimpleAdapter(getApplicationContext(), listItems,
		        					R.layout.device_list_item, new String[] { "name", "address" },
		        					new int[] { R.id.tv_device_name, R.id.tv_device_address });
		        			listView.setAdapter(adapter);
		        		}
		        	}
		        }, SCAN_PERIOD);
			}
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			redBearService = null;
		}
	};
	
	/** Service Listener **/
	private IRedBearServiceEventListener redBearServiceEventListener = new IRedBearServiceEventListener() {

		@Override
		public void onDeviceFound(String address, String name, int rssi, int bondState, byte[] scanRecord, ParcelUuid[] uuids) {
			Device newDevice = new Device();
			newDevice.setAddress(address);
			newDevice.setName(name);
			newDevice.setRssi(rssi);
			newDevice.setBondState(bondState);
			newDevice.setScanReadData(scanRecord);
			newDevice.setUuids(uuids);
			
			for(Device tempDevice : deviceList) {
				if(tempDevice.getAddress().equals(newDevice.getAddress())) {
					tempDevice.setRssi(device.getRssi());
					runOnUiThread(new Runnable() {

						@Override
						public void run() {
							if (adapter != null) {
								adapter.notifyDataSetChanged();
							}
						}
					});
				}
			}
			deviceList.add(newDevice);
		}

		@Override
		public void onDeviceRssiUpdate(String deviceAddress, int rssi, int state) {}

		@Override
		public void onDeviceConnectStateChange(String deviceAddress, int state) {}

		@Override
		public void onDeviceReadValue(int[] value) {}

		@Override
		public void onDeviceCharacteristicFound() {}
	};
	
	/** Refreshing **/
	class RefreshTask extends AsyncTask<Void, Void, Void> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			
			deviceList.clear();
			listItems.clear();
			if (redBearService != null) {
				redBearService.stopScanDevice();
				redBearService.startScanDevice();
			}
		}

		@Override
		protected Void doInBackground(Void... params) {
			try {
				Thread.sleep(2000);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			if (redBearService != null) {
				redBearService.stopScanDevice();
			}
			
			for (Device device : deviceList) {
				map = new HashMap<String, String>();
				map.put(DEVICE_ADDRESS, device.getAddress());
				map.put(DEVICE_NAME, device.getName());
				listItems.add(map);
			}
			
			adapter = new SimpleAdapter(getApplicationContext(), listItems,
					R.layout.device_list_item, new String[] { "name", "address" },
					new int[] { R.id.tv_device_name, R.id.tv_device_address });
			listView.setAdapter(adapter); 
			adapter.notifyDataSetChanged();
		}
	}
}
