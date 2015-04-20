package com.wander.main;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.Chronometer.OnChronometerTickListener;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

public class ShowMapActivity extends Activity {

	private GoogleMap map;
	private String directions;
	private LinearLayout llButton;
	private Button buttonRequest;
	private DirectionsQuery dq;
	private LinearLayout llMap;
	private FragmentManager frgManager;
	private MapFragment mfMap;
	private int actionBarHeight;
	private Chronometer timer;
	private LinearLayout forChrono;
	private DetailsTabFragment dtf;
	private boolean onMapMade;
	private boolean isLoggedIn;

	private CharSequence finalTime;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Drawable wander = getResources().getDrawable(R.drawable.skyline);
		wander.setAlpha(50);
		getWindow().setBackgroundDrawable(wander);

		Intent intent = getIntent();
		directions = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);

		frgManager = getFragmentManager();

		if (directions.equals("free")) {
			setContentView(R.layout.activity_free);

		} else {

			setContentView(R.layout.activity_show_map);
			initialiseTabs();
			initialiseChrono();
			initialiseButtons();
			checkMap();
			onMapMade = false;

		}

	}

	protected void onResume() {
		super.onResume();

		if (onMapMade == false) {
			setMap();
			onMapMade = manipulateMap(directions);
			setMapListener(this);
		}

	}

	public void onBackPressed() {

		if (this.getClass().equals(ShowMapActivity.class)) {
			if (directions.equals("free")) {
				buildDialog("free_dialog_message");
			} else {
				buildDialog("showmap_dialog_message");
			}

		} else {
			super.onBackPressed();
		}
	}

	private void buildDialog(String setMessage) {

		int messageId = getResources().getIdentifier(setMessage, "string",
				getPackageName());
		String toSet = getResources().getString(messageId);
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(toSet);

		builder.setPositiveButton(R.string.yes,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						finish();
					}
				});
		builder.setNegativeButton(R.string.no,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						// User cancelled the dialog
					}
				});

		builder.show();
		builder.create();

	}

	private void initialiseChrono() {
		forChrono = (LinearLayout) findViewById(R.id.ForChrono);
		timer = new Chronometer(this, null, R.attr.ForChronoStyle);
		timer.setOnChronometerTickListener(new OnChronometerTickListener() {
			@Override
			public void onChronometerTick(Chronometer arg0) {
				if (dtf != null) {
					dtf.setTimerText(arg0.getText());
				}
				finalTime = arg0.getText();
			}
		});
		forChrono.addView(timer);
	}

	private void setMap() {

		if (directions.equals("free")) {
			map = ((MapFragment) frgManager.findFragmentById(R.id.map))
					.getMap();
		} else {
			map = mfMap.getMap();
		}
		map.getUiSettings().setMyLocationButtonEnabled(true);
		map.setMyLocationEnabled(true);
	}

	private void initialiseTabs() {
		final ActionBar actionBar = getActionBar();

		/**
		 * Setting padding on top so actionbar does not overlay any views
		 */
		TypedValue tv = new TypedValue();
		getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true);
		actionBarHeight = getResources().getDimensionPixelSize(tv.resourceId);
		llMap = (LinearLayout) findViewById(R.id.MapLayout);
		llMap.setPadding(0, actionBarHeight * 2, 0, 0);

		// Specify that tabs should be displayed in the action bar.
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		// Create a tab listener that is called when the user changes tabs.
		ActionBar.TabListener tabListener = new ActionBar.TabListener() {

			public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {

				FragmentTransaction frgTransaction = frgManager
						.beginTransaction();
				String tabText = tab.getText().toString();

				if (tabText.equals("Map")) {
					if (forChrono != null && llButton != null) {
						forChrono.setVisibility(View.VISIBLE);
						llButton.setVisibility(View.VISIBLE);

						frgTransaction.replace(R.id.map_container, mfMap);
						frgTransaction.commit();
						frgManager.executePendingTransactions();
					}
				} else {
					forChrono.setVisibility(View.GONE);
					llButton.setVisibility(View.GONE);

					Bundle bundle = new Bundle();
					bundle.putString("location", dq.getLocation());
					bundle.putBoolean("isLoggedIn", isLoggedIn);

					try {
						bundle.putInt("distance", dq.getDistance(true));
					} catch (NullPointerException e) {
						Log.d("Distance Calculation",
								"User did not press Start yet");
					}

					if (tabText.equals("Details")) {
						dtf = new DetailsTabFragment();
						dtf.setArguments(bundle);
						frgTransaction.replace(R.id.map_container, dtf);
					} else {
						StatisticsTabFragment stf = new StatisticsTabFragment();
						stf.setArguments(bundle);
						frgTransaction.replace(R.id.map_container, stf);
					}

					/**
					 * If the following line is removed, GoogleMap gets
					 * reinitialised everytime you press the "map" tab :/
					 */
					frgTransaction.addToBackStack(null);
					frgTransaction.commit();
					frgManager.executePendingTransactions();

				}

			}

			public void onTabUnselected(ActionBar.Tab tab,
					FragmentTransaction ft) {
				// hide the given tab
			}

			public void onTabReselected(ActionBar.Tab tab,
					FragmentTransaction ft) {
				// probably ignore this event
			}
		};

		// Add 3 tabs, specifying the tab's text and TabListener
		String[] tabNames = new String[3];
		tabNames[0] = "Map";
		tabNames[1] = "Details";
		tabNames[2] = "Statistics";

		for (int i = 0; i < 3; i++) {
			actionBar.addTab(actionBar.newTab().setText(tabNames[i])
					.setTabListener(tabListener));
		}

	}

	private void initialiseButtons() {

		llButton = (LinearLayout) findViewById(R.id.LinearButtons);

		buttonRequest = new Button(this);
		buttonRequest.setText(getResources().getString(R.string.start));
		buttonRequest.setLayoutParams(new LinearLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		buttonRequest.setGravity(Gravity.CENTER);
		buttonRequest.setTextColor(Color.parseColor("#CC6600"));
		buttonRequest.setBackgroundResource(R.drawable.button_settings);
		buttonRequest.setTypeface(null, Typeface.BOLD);

		llButton.addView(buttonRequest);

	}

	public void checkMap() {
		// Gets the MapView from the XML layout and creates it
		if (map == null) {
			GoogleMapOptions options = new GoogleMapOptions();
			options.camera(new CameraPosition(new LatLng(51.511640, -0.115997),
					15f, 0, 0));

			mfMap = MapFragment.newInstance(options);
			FragmentTransaction frgTransaction = frgManager.beginTransaction();
			frgTransaction.add(R.id.map_container, mfMap);
			frgTransaction.commit();
			frgManager.executePendingTransactions();

		}

		int check = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
		if (check != 0) {
			Toast toast = Toast.makeText(this, "Could not retrieve location",
					Toast.LENGTH_SHORT);
			toast.show();
		}
	}

	public boolean manipulateMap(String directions) {

		dq = new DirectionsQuery(this, map, directions, buttonRequest, timer);

		if (directions.equals("free")) {

			Toast.makeText(this,
					" Exploration Mode" + "\n" + "feel free to wander!",
					Toast.LENGTH_SHORT).show();
			dq.freeDirections();
		} else {

			if (directions.equals("wander")) {
				dq.directionsToEndPoint(true);
			} else {

				dq.directionsToEndPoint(false);
			}
		}
		return true;
	}

	public void setMapListener(final Context forListener) {
		map.setOnMarkerClickListener(new OnMarkerClickListener() {

			@Override
			public boolean onMarkerClick(Marker marker) {
				Toast toasty = Toast.makeText(forListener, "Try harder",
						Toast.LENGTH_SHORT);
				toasty.show();
				return false;
			}

		});
	}

	protected CharSequence getFinalTime() {
		return finalTime;
	}

	protected DetailsTabFragment getTabsFragment() {
		return dtf;
	}

	protected DirectionsQuery getDQ() {
		return dq;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		if (directions.equals("free")) {
			getMenuInflater().inflate(R.menu.show_map_free, menu);
		} else {
			getMenuInflater().inflate(R.menu.show_map_wander, menu);
		}

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		switch (id) {
		case R.id.action_relocate:
			dq.relocate();
			break;
		}

		return super.onOptionsItemSelected(item);
	}
}