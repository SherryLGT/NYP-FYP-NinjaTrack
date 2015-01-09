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

import java.util.ArrayList;

import model.NavDrawerItem;
import nyp.fypj.ninjatrack.R;
import adapter.NavDrawerListAdapter;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.actionbarsherlock.app.SherlockFragmentActivity;

import fragment.PageSlidingTabStripFragment;
import fragment.ProfileFragment;
import fragment.SettingFragment;
import fragment.WebsiteFragment;

@SuppressWarnings("deprecation")
public class MainActivity extends SherlockFragmentActivity {
	
	private DrawerLayout drawerLayout;
	private ListView drawerList;
	private ActionBarDrawerToggle drawerToggle;

	private CharSequence title;
	private CharSequence drawerTitle;
	
	private String[] navTitles;
	private TypedArray navIcons;
	
	private ArrayList<NavDrawerItem> drawerItems;
	private NavDrawerListAdapter adapter;
		
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
				
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

	}

	@Override
	public boolean onCreateOptionsMenu(com.actionbarsherlock.view.Menu menu) {
		getSupportMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(com.actionbarsherlock.view.MenuItem item) {

		switch (item.getItemId()) {

			case android.R.id.home: {
				if (drawerLayout.isDrawerOpen(drawerList)) {
					drawerLayout.closeDrawer(drawerList);
				} else {
					drawerLayout.openDrawer(drawerList);
				}
				break;
			}
		}

		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onBackPressed() {
		if(SettingFragment.pinFragment != null) {
			getFragmentManager().beginTransaction().remove(SettingFragment.pinFragment).commit();
			SettingFragment.pinFragment = null;
		}
		else {
			super.onBackPressed();
		}
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

		if(SettingFragment.pinFragment != null) {
			getFragmentManager().beginTransaction().remove(SettingFragment.pinFragment).commit();
			SettingFragment.pinFragment = null;
		}
		
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
}