/*
 * Copyright (C) 2013 Andreas Stuetz <andreas.stuetz@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package activity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import redbearprotocol.IRBLProtocol;
import redbearprotocol.RBLProtocol;
import redbearservice.IRedBearServiceEventListener;
import redbearservice.RedBearService;

import model.Device;
import model.NavDrawerItem;
import model.Pin;
import nyp.fypj.ninjatrack.R;
import adapter.InstrumentHandler;
import adapter.NavDrawerListAdapter;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.SharedPreferences.Editor;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.ParcelUuid;
import android.preference.PreferenceManager;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.MenuItem;

import fragment.PageSlidingTabStripFragment;
import fragment.ProfileFragment;
import fragment.SettingFragment;
import fragment.WebsiteFragment;

@SuppressWarnings("deprecation")
public class MainActivity extends SherlockFragmentActivity implements IRBLProtocol {
	
	private DrawerLayout drawerLayout;
	private ListView drawerList;
	private ActionBarDrawerToggle drawerToggle;
	private MenuItem startRecord, stopRecord;

	private CharSequence title;
	private CharSequence drawerTitle;	
	private String[] navTitles;
	private TypedArray navIcons;	
	private ArrayList<NavDrawerItem> drawerItems;
	private NavDrawerListAdapter adapter;
	
	public static SharedPreferences sp;
	private Device device;
	private RedBearService redBearService;
	private RBLProtocol protocol;
	private List<Integer> buttonPins;
	private SparseArray<Pin> pins;
	private HashMap<String, Pin> changeValues;
	private boolean isFirstReadPin = true;
	private boolean isFirstReadRssi = true;
	private boolean pin2, pin3, pin5, pin8, pin9, pin10, pin11, pin12, pin18, pin21, pin22, pin23;
	
	private Timer timer;
	private Timer myTimer;
	private TimerTask timerTask;
	private TimerTask myTimerTask;
	private boolean timerFlag;
	private int timeout = 3000;
	private ProgressDialog progress;
	
	public static int position = 0;
	private boolean active;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		sp = PreferenceManager.getDefaultSharedPreferences(this);
		
		device = DeviceListActivity.device;
		redBearService = DeviceListActivity.redBearService;
		buttonPins = Arrays.asList(2, 3, 5, 8, 9, 10, 11, 12);
		pins = new SparseArray<Pin>();
		changeValues = new HashMap<String, Pin>();
		timer = new Timer();
		timerTask = new TimerTask() {
			@Override
			public void run() {
				if (MainActivity.this != null) {
					MainActivity.this.runOnUiThread(new Runnable() {
						@Override
						public void run() {
							new AlertDialog.Builder(MainActivity.this).setTitle("No response").setMessage("Please reconnect to device.").setPositiveButton("OK", new OnClickListener() {
								@Override
								public void onClick(DialogInterface arg0, int arg1) {
									MainActivity.this.finish();
								}
							}).show();
						}
					});
				}
			}
		};
		myTimerTask = new TimerTask(){

			@Override
			public void run() {
				handler.sendEmptyMessage(3);
			}
			
		};
		myTimer = new Timer();
		myTimer.schedule(myTimerTask, 20000);
		
		if(device != null) {
			device.setRssi(0);
			protocol = new RBLProtocol(device.getAddress());
			protocol.setIRBLProtocol(this);
		}
		timerFlag = false;
		progress = new ProgressDialog(MainActivity.this);
		if(getIntent().getBooleanExtra("frompin", true)) {
			progress.setMessage("Setting pin information");
		}
		else {
			progress.setMessage("Retrieving pin information");
		}
		progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		progress.setCancelable(false);
		progress.setCanceledOnTouchOutside(false);
		progress.show();
		
		drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		drawerList = (ListView) findViewById(R.id.left_drawer);
		navTitles = getResources().getStringArray(R.array.drawer_titles);
		navIcons = getResources().obtainTypedArray(R.array.drawer_icons);

		title = drawerTitle = getTitle();
		// Set a custom shadow that overlays the main content when the drawer opens
		drawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
		
		// Set up the drawer's list view with items and click listener
		drawerItems = new ArrayList<NavDrawerItem>();
		drawerItems.add(new NavDrawerItem(navTitles[0], navIcons.getResourceId(0, -1))); // Music		
		drawerItems.add(new NavDrawerItem(navTitles[1], navIcons.getResourceId(1, -1))); // Profile
		drawerItems.add(new NavDrawerItem(navTitles[2], navIcons.getResourceId(2, -1))); // Setting
		drawerItems.add(new NavDrawerItem(navTitles[3], navIcons.getResourceId(3, -1))); // Website
		navIcons.recycle();

		adapter = new NavDrawerListAdapter(getApplicationContext(), drawerItems);
		drawerList.setAdapter(adapter);
		drawerList.setOnItemClickListener(new DrawerItemClickListener());

		// Enable ActionBar app icon to behave as action to toggle nav drawer
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setHomeButtonEnabled(true);

		// ActionBarDrawerToggle ties together the the proper interactions between the sliding drawer and the action bar app icon
		drawerToggle = new ActionBarDrawerToggle(this, /* Host Activity */ drawerLayout, /* DrawerLayout object */
			R.drawable.icon_drawer, /* nav drawer image to replace 'Up' caret */
			R.string.drawer_open, /* "open drawer" description for accessibility */
			R.string.drawer_close /* "close drawer" description for accessibility */
		) {
			public void onDrawerClosed(View view) {
				getActionBar().setTitle(title);
				invalidateOptionsMenu(); // Creates call to onPrepareOptionsMenu()
			}

			public void onDrawerOpened(View drawerView) {
				getActionBar().setTitle(drawerTitle);
				invalidateOptionsMenu(); // Creates call to onPrepareOptionsMenu()
			}
		};
		drawerLayout.setDrawerListener(drawerToggle);

		if (savedInstanceState == null) {
			selectItem(0);
		}
		
		MainActivity.this.setVolumeControlStream(AudioManager.STREAM_MUSIC);
	}
	
	@Override
	protected void onResume() {
		if(redBearService != null) {
			if(device != null) {
				if(protocol != null) {
					protocol.setmIRedBearService(redBearService);
				}
				redBearService.setListener(redBearServiceEventListener);
				redBearService.readRssi(device.getAddress());
			}
		}
		if(protocol != null) {
			protocol.queryProtocolVersion();
		}
		handler.sendEmptyMessageDelayed(1, timeout);
		
		super.onResume();
	}

	@Override
	protected void onStart() {
		super.onStart();
		active = true;
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		active = false;
	}

	@Override
	public boolean onCreateOptionsMenu(com.actionbarsherlock.view.Menu menu) {
		getSupportMenuInflater().inflate(R.menu.main, menu);
		
		startRecord = (MenuItem) menu.findItem(R.id.action_start_record);
		stopRecord = (MenuItem) menu.findItem(R.id.action_stop_record);
		
		return true;
	}
	
	@SuppressLint("InflateParams")
	@Override
	public boolean onOptionsItemSelected(com.actionbarsherlock.view.MenuItem item) {

		switch (item.getItemId()) {

			case android.R.id.home: 
				if (drawerLayout.isDrawerOpen(drawerList)) {
					drawerLayout.closeDrawer(drawerList);
				} else {
					drawerLayout.openDrawer(drawerList);
				}
				break;
				
			case R.id.action_about:
				AlertDialog.Builder aboutBuilder = new AlertDialog.Builder(this);
				aboutBuilder.setMessage("If instruments are not playing according to settings made, please reconnect to device.").setPositiveButton("Ok", null).create().show();
				break;
				
			case R.id.action_start_record:
				AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
				AlertDialog.Builder startBuilder = new AlertDialog.Builder(this);
				
				if(audioManager.isWiredHeadsetOn()) {
					Toast.makeText(this, "Please remove headset", Toast.LENGTH_LONG).show();
				}
				else {
					startBuilder.setMessage("Start recording?").setPositiveButton("Ok", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							startRecord.setVisible(false);
							stopRecord.setVisible(true);
							try {
								InstrumentHandler.StartRecording();
								Toast.makeText(MainActivity.this, "Recording started..", Toast.LENGTH_SHORT).show();
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
					}).setNegativeButton("Cancel", null).create().show();
				}
				break;
				
			case R.id.action_stop_record:
				startRecord.setVisible(true);
				stopRecord.setVisible(false);
				
				View rootView = LayoutInflater.from(this).inflate(R.layout.filename_dialog, null);
				final EditText et_filename = (EditText) rootView.findViewById(R.id.et_filename);
				
				AlertDialog.Builder stopBuilder = new AlertDialog.Builder(this);
				stopBuilder.setView(rootView).setTitle("Input desired file name").setPositiveButton("Ok", null);
				
				final AlertDialog stopDialog = stopBuilder.create();
				stopDialog.setOnShowListener(new DialogInterface.OnShowListener() {
					@Override
					public void onShow(DialogInterface dialogInterface) {
						Button btn_ok = stopDialog.getButton(AlertDialog.BUTTON_POSITIVE);
						btn_ok.setOnClickListener(new View.OnClickListener() {
							@Override
							public void onClick(View v) {
								if(et_filename.getText().toString().length() > 1) {
									InstrumentHandler.filename = et_filename.getText().toString();
									
									ContentResolver contentResolver = getContentResolver();
									Uri uri = InstrumentHandler.StopRecording(contentResolver);
									sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri));
									
									Toast.makeText(MainActivity.this, "Saved", Toast.LENGTH_SHORT).show();
									stopDialog.dismiss();
								}
								else {
									Toast.makeText(MainActivity.this, "Please enter file name", Toast.LENGTH_SHORT).show();
								}
							}
						});
					}
				});
				stopDialog.show();
				break;
		}

		return super.onOptionsItemSelected(item);
	}
	
	// The click listener for ListView in the navigation drawer
	private class DrawerItemClickListener implements ListView.OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			selectItem(position);
		}
	}
	
	@Override
	public void setTitle(CharSequence cst) {
		title = cst;
		getActionBar().setTitle(title);
	}
	
	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		// Sync the toggle state after onRestoreInstanceState has occurred.
		drawerToggle.syncState();
	}
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		// Pass any configuration change to the drawer toggles
		drawerToggle.onConfigurationChanged(newConfig);
	}
	
	private void selectItem(int position) {

		drawerItems.get(0).setIcon(R.drawable.icon_music1);
		drawerItems.get(1).setIcon(R.drawable.icon_profile1);
		drawerItems.get(2).setIcon(R.drawable.icon_setting1);
		drawerItems.get(3).setIcon(R.drawable.icon_website1);
		
		switch (position) {
			case 0: // Music
				drawerItems.get(position).setIcon(R.drawable.icon_music2);
				getSupportFragmentManager().beginTransaction().replace(R.id.content, PageSlidingTabStripFragment.newInstance()).commit();
				break;
			case 1: // Profile
				drawerItems.get(position).setIcon(R.drawable.icon_profile2);
				getSupportFragmentManager().beginTransaction().replace(R.id.content, new ProfileFragment()).commit();
				break;
			case 2: // Setting
				drawerItems.get(position).setIcon(R.drawable.icon_setting2);
				getSupportFragmentManager().beginTransaction().replace(R.id.content, new SettingFragment()).commit();
				break;
			case 3: // Website
				drawerItems.get(position).setIcon(R.drawable.icon_website2);
				getSupportFragmentManager().beginTransaction().replace(R.id.content, new WebsiteFragment()).commit();
				break;
			default:
				break;
		}

		// Update selected item and title, then close the drawer
		drawerList.setItemChecked(position, true);
		drawerList.setSelection(position);
		setTitle(navTitles[position]);
		drawerLayout.closeDrawer(drawerList);
	}
	
	private final IRedBearServiceEventListener redBearServiceEventListener = new IRedBearServiceEventListener() {
		@Override
		public void onDeviceFound(String address, String name, int rssi, int bondState, byte[] scanRecord, ParcelUuid[] uuids) {}

		@Override
		public void onDeviceRssiUpdate(final String deviceAddress, final int rssi, final int state) {
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					if(state == 0) {
						if(deviceAddress.equals(device.getAddress())) {
							device.setRssi(rssi);
						}
					}
					if (isFirstReadRssi) {
						handler.sendEmptyMessageDelayed(0, 1000);
						isFirstReadRssi = false;
					} else {
						handler.sendEmptyMessageDelayed(0, 300);
					}
				}
			});
		}

		@Override
		public void onDeviceConnectStateChange(final String deviceAddress, final int state) {
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					if(protocol != null) {
						protocol.queryProtocolVersion();
					}
				}
			});
		}

		@Override
		public void onDeviceReadValue(int[] value) {
			if(protocol != null) {
				if(value == null){
					progress.dismiss();
					handler.sendEmptyMessage(3);
				}
				protocol.parseData(value);
			}
		}

		@Override
		public void onDeviceCharacteristicFound() {}
	};
	
	@Override
	public void protocolDidReceiveCustomData(int[] data, int length) {
		final int count = data.length;
		char[] chars = new char[count];
		
		for(int i = 0; i <count; i++) {
			chars[i] = (char) data[i];
		}
		
		String temp = new String(chars);
		if(temp.contains("ABC")) {
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					if (changeValues != null) {
						final int count = pins.size();
						for (int i = 0; i < count; i++) {
							int key = pins.keyAt(i);
							Pin pInfo = pins.get(key);
							Pin changedPin = changeValues.get(key + "");

							if (changedPin != null) {
								pInfo.setMode(changedPin.getMode());
								pInfo.setValue(changedPin.getValue());
							}
						}
						changeValues = null;
						isFirstReadPin = false;
					}
				}
			});
		}
	}

	@Override
	public void protocolDidReceiveProtocolVersion(int major, int minor, int bugfix) {
		if (protocol != null) {
			int[] data = { 'B', 'L', 'E' };
			protocol.sendCustomData(data, 3);
			protocol.queryTotalPinCount();
		}
	}

	@Override
	public void protocolDidReceiveTotalPinCount(int count) {
		if(protocol != null) {
			protocol.queryPinAll();
		}
	}

	@Override
	public void protocolDidReceivePinCapability(int pin, int value) {
		if(value == 0) {}
		else {
			if(pins == null) {
				return;
			}
			Pin pinInfo = new Pin();
			pinInfo.setPin(pin);
			
			ArrayList<Integer> modes = new ArrayList<Integer>();
			modes.add(INPUT);
			
			if((value & PIN_CAPABILITY_DIGITAL) == PIN_CAPABILITY_DIGITAL) {
				modes.add(OUTPUT);
			}
			if((value & PIN_CAPABILITY_ANALOG) == PIN_CAPABILITY_ANALOG) {
				modes.add(ANALOG);
			}
			if((value & PIN_CAPABILITY_PWM) == PIN_CAPABILITY_PWM) {
				modes.add(PWM);
			}
			if((value & PIN_CAPABILITY_SERVO) == PIN_CAPABILITY_SERVO) {
				modes.add(SERVO);
			}
			
			final int count = modes.size();
			pinInfo.setModes(new int[count]);
			for(int i = 0; i < count; i++) {
				pinInfo.getModes()[i] = modes.get(i);
			}
			pins.put(pin, pinInfo);
			modes.clear();
		}
	}

	@Override
	public void protocolDidReceivePinMode(int pin, int mode) {
		if(pins == null) {
			return;
		}
		
		Pin pinInfo = pins.get(pin);
		pinInfo.setMode(mode);
	}

	@Override
	public void protocolDidReceivePinData(int pin, int mode, int value) {
		byte _mode = (byte) (mode & 0x0F);
		
		if(pins == null) {
			return;
		}
		
		Pin pinInfo;
		if (isFirstReadPin) {
			pinInfo = new Pin();
			pinInfo.setPin(pin);
			pinInfo.setMode(mode);
			if ((_mode == INPUT) || (_mode == OUTPUT))
				pinInfo.setValue(value);
			else if (_mode == ANALOG)
				pinInfo.setValue(((mode >> 4) << 8) + value);
			else if (_mode == PWM)
				pinInfo.setValue(value);
			else if (_mode == SERVO)
				pinInfo.setValue(value);
			changeValues.put(pin + "", pinInfo);
		} else {
			pinInfo = pins.get(pin);
			pinInfo.setMode(_mode);
			if ((_mode == INPUT) || (_mode == OUTPUT))
				pinInfo.setValue(value);
			else if (_mode == ANALOG)
				pinInfo.setValue(((mode >> 4) << 8) + value);
			else if (_mode == PWM)
				pinInfo.setValue(value);
			else if (_mode == SERVO)
				pinInfo.setValue(value);
		}
		
		if(pinInfo.getPin() == 2) pin2 = true;
		if(pinInfo.getPin() == 3) pin3 = true;
		if(pinInfo.getPin() == 5) pin5 = true;
		if(pinInfo.getPin() == 8) pin8 = true;
		if(pinInfo.getPin() == 9) pin9 = true;
		if(pinInfo.getPin() == 10) pin10 = true;
		if(pinInfo.getPin() == 11) pin11 = true;
		if(pinInfo.getPin() == 12) pin12 = true;
		if(pinInfo.getPin() == 18) pin18 = true;
		if(pinInfo.getPin() == 21) pin21 = true;
		if(pinInfo.getPin() == 22) pin22 = true;
		if(pinInfo.getPin() == 23) pin23 = true;
		
		if(pin2 && pin3 && pin5 && pin8 && pin9 && pin10 && pin11 && pin12 && pin18 && pin21 && pin22 && pin23) {
			progress.dismiss();
			myTimer.cancel();
			if (timerFlag == true) {
				timerTask.cancel(); 
				InstrumentHandler.SetMode(protocol, sp, pins);
			}
		}
		
		// Switch Pins
		if(pinInfo.getPin() == 22) {
			if(pinInfo.getValue() == 0) {
				InstrumentHandler.switch1_flag = 0;
			}
			else {
				InstrumentHandler.switch1_flag = 1;
			}
		}
		if(pinInfo.getPin() == 23) {
			if(pinInfo.getValue() == 0) {
				InstrumentHandler.switch2_flag = 0;
			}
			else {
				InstrumentHandler.switch2_flag = 1;
			}
		}
		
		if(pinInfo.getPin() == 21) { // Flex sensor
			InstrumentHandler.flex = pinInfo.getValue();
		}
		if(pinInfo.getPin() == 18) { // Accelerometer
			InstrumentHandler.accx = pinInfo.getValue();
			InstrumentHandler.MonitorAccx(pinInfo.getValue());
			try {
				InstrumentHandler.PlaySound(MainActivity.this, -1);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		try {
			InstrumentHandler.SetSound(this);
			
			// Changing of tabs to respective instrument
			if(InstrumentHandler.curr_flag != InstrumentHandler.prev_flag) {
				switch(InstrumentHandler.CheckFlags(this)) {
					case InstrumentHandler.RECORDER_FLAG:
						MainActivity.position = 0;
						getSupportFragmentManager().beginTransaction().replace(R.id.content, PageSlidingTabStripFragment.newInstance()).commit();
						break;
					case InstrumentHandler.SAXOPHONE_FLAG:
						MainActivity.position = 2;
						getSupportFragmentManager().beginTransaction().replace(R.id.content, PageSlidingTabStripFragment.newInstance()).commit();
						break;
					case InstrumentHandler.DRUM_FLAG:
						MainActivity.position = 1;
						getSupportFragmentManager().beginTransaction().replace(R.id.content, PageSlidingTabStripFragment.newInstance()).commit();
						break;
//					case InstrumentHandler.BELL_FLAG:
//						MainActivity.position = 3;
//						getSupportFragmentManager().beginTransaction().replace(R.id.content, PageSlidingTabStripFragment.newInstance()).commit();
//						break;
//					case InstrumentHandler.HARP_FLAG:
//						MainActivity.position = 4;
//						getSupportFragmentManager().beginTransaction().replace(R.id.content, PageSlidingTabStripFragment.newInstance()).commit();
//						break;
				}
			}
			
			if(!isFirstReadPin) {
				if(buttonPins.contains(pinInfo.getPin())) { // Check for button
					if(pinInfo.getValue() == 1) { // Button pressed
						switch(pinInfo.getPin()) { // Check for which button
							case 2:
								InstrumentHandler.PlaySound(MainActivity.this, 1);
								break;
							case 3:
								InstrumentHandler.PlaySound(MainActivity.this, 2);
								break;
							case 5:
								InstrumentHandler.PlaySound(MainActivity.this, 3);
								break;
							case 8:
								InstrumentHandler.PlaySound(MainActivity.this, 4);
								break;
							case 9:
								InstrumentHandler.PlaySound(MainActivity.this, 5);
								break;
							case 10:
								InstrumentHandler.PlaySound(MainActivity.this, 6);
								break;
							case 11:
								InstrumentHandler.PlaySound(MainActivity.this, 7);
								break;
							case 12:
								InstrumentHandler.PlaySound(MainActivity.this, 8);
								break;
						}
					}
					else if(pinInfo.getValue() == 0 && (InstrumentHandler.CheckFlags(this) == InstrumentHandler.RECORDER_FLAG || InstrumentHandler.CheckFlags(this) == InstrumentHandler.SAXOPHONE_FLAG)) {
						switch(pinInfo.getPin()) { // Check for which button
							case 2:
								InstrumentHandler.StopSound(MainActivity.this, 1);
								break;
							case 3:
								InstrumentHandler.StopSound(MainActivity.this, 2);
								break;
							case 5:
								InstrumentHandler.StopSound(MainActivity.this, 3);
								break;
							case 8:
								InstrumentHandler.StopSound(MainActivity.this, 4);
								break;
							case 9:
								InstrumentHandler.StopSound(MainActivity.this, 5);
								break;
							case 10:
								InstrumentHandler.StopSound(MainActivity.this, 6);
								break;
							case 11:
								InstrumentHandler.StopSound(MainActivity.this, 7);
								break;
							case 12:
								InstrumentHandler.StopSound(MainActivity.this, 8);
								break;
						}
					}
					else if(pinInfo.getValue() == 0 && InstrumentHandler.CheckFlags(this) == InstrumentHandler.BELL_FLAG) {
						InstrumentHandler.StopSound(MainActivity.this, -1);
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		// Button Pin 2, 3, 5, 8, 9, 10, 11, 12
		// Accelerometer Pin 18
		// Flex Sensor Pin 21
		// Switch Pin 22, 23
		// Press|Switch on/off - 1/0
		
		System.out.println("PIN: " + pinInfo.getPin() + " |VALUE: " + pinInfo.getValue() + " |MODE: " + pinInfo.getMode());
	}
	
	Handler.Callback handlerCallback = new Handler.Callback() {
		@Override
		public boolean handleMessage(Message msg) {

			if (msg.what == 0) {
				if (redBearService != null) {
					if (device != null) {
						redBearService.readRssi(device.getAddress());
					}
				}
			}
			else if (msg.what == 1) {
				if (pins.size() == 0) {
					if (protocol != null) {
						protocol.queryProtocolVersion();
					}
					if (MainActivity.this != null) {
						handler.sendEmptyMessageDelayed(2, timeout);
					}
				}
			}
			else if (msg.what == 2) {
				if (pins.size() == 0) {
					if (protocol != null) {
						protocol.queryProtocolVersion();
					}
					timer.schedule(timerTask, timeout);
					timerFlag = true;
				}
			}
			else if(msg.what == 3){
				myTimer.cancel();
				if(!active)
					return true;
				new AlertDialog.Builder(MainActivity.this).setTitle("No response").setMessage("Please reconnect to device.").setPositiveButton("OK", new OnClickListener() {
					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						MainActivity.this.finish();
					}
				}).show();
			}
			
			return true;
		}
	};
	
	public Handler h = new Handler();
	
	Handler handler = new Handler(handlerCallback);
	
	public Handler handleTabs = new Handler(new Handler.Callback() {
		@Override
		public boolean handleMessage(Message msg) {
			if(msg.getData().getInt("FLAG") == InstrumentHandler.BELL_FLAG) {
				MainActivity.position = 3;
				getSupportFragmentManager().beginTransaction().replace(R.id.content, PageSlidingTabStripFragment.newInstance()).commit();
			}
			else{
				MainActivity.position = 4;
				getSupportFragmentManager().beginTransaction().replace(R.id.content, PageSlidingTabStripFragment.newInstance()).commit();
			}
			return true;
		}
	});
}