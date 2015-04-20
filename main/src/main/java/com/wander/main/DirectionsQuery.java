package com.wander.main;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import org.w3c.dom.Document;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.location.Location;
import android.os.SystemClock;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.Toast;

import com.wander.main.GoogleDirection.OnDirectionResponseListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class DirectionsQuery {

	private GoogleMap map;
	private ArrayList<LatLng> markerPoints = new ArrayList<LatLng>();
	private String directions;
	private ShowMapActivity context;
	private LocationsDatabase locationList;
	private LatLng finalLocation;
	private GoogleDirection gd;
	private LatLng myPos;
	private Button buttonRequest;
	private boolean initial;
	private boolean start;
	private LatLng initialLocation;
	private final Chronometer timer;
	private String foundLocation;
	private static DataManager dm;
	private int initialDistance;
	private String currentLocation;

	public DirectionsQuery(Context context, GoogleMap map, String directions,
			Button buttonRequest, Chronometer timer) {
		this.map = map;
		this.directions = directions;
		this.context = (ShowMapActivity) context;
		this.buttonRequest = buttonRequest;
		this.timer = timer;
		locationList = MainActivity.ld;
		start = true;
		initial = true;
		dm = MainActivity.dm;
	}

	protected String getLocation() {
		return foundLocation;
	}

	protected int getDistance(boolean isInitial) {

		double lat1;
		if (isInitial == true) {
			lat1 = myPos.latitude;
		} else {
			lat1 = initialLocation.latitude;
		}

		double lng1 = myPos.longitude;
		double lat2 = finalLocation.latitude;
		double lng2 = finalLocation.longitude;

		return Conversions.getDistance(lat1, lng1, lat2, lng2);
	}

	protected void directionsToEndPoint(boolean random) {

		if (random == true) {

			Random generator = new Random();
			int index = generator.nextInt(locationList.getArray().size());
			foundLocation = locationList.getData(index).getLocation();
			finalLocation = locationList.getData(index).getLatLng();
			currentLocation = locationList.getData(index).getLocation();

			Toast.makeText(context, currentLocation, Toast.LENGTH_SHORT).show();

		} else {
			Toast.makeText(context, directions, Toast.LENGTH_SHORT).show();
			LocationsData ldFound = locationList.searchData(directions);
			foundLocation = ldFound.getLocation();
			finalLocation = ldFound.getLatLng();

		}

		Toast.makeText(context, finalLocation.toString(), Toast.LENGTH_SHORT)
				.show();

		getDirections();
		setButtonListener();

	}

	private void getInitialPosition() {
		map.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
			@Override
			public void onMyLocationChange(Location arg0) {
				myPos = new LatLng(arg0.getLatitude(), arg0.getLongitude());

				if (initial == true) {
					initial = false;
					initialLocation = myPos;

				}
			}
		});
	}

	protected void getDirections() {

		getInitialPosition();

		initialiseMap();

		gd = new GoogleDirection(context);
		gd.setOnDirectionResponseListener(new OnDirectionResponseListener() {
			public void onResponse(String status, Document doc,
					GoogleDirection gd) {
				map.addPolyline(gd.getPolyline(doc, 3, Color.RED));
			}
		});
	}

	private void initialiseMap() {

		Toast toast = Toast.makeText(context, "Try to get here in 15 minutes!"
				+ "\n" + "Click on the marker for hints!", Toast.LENGTH_SHORT);
		toast.show();

		map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
		map.animateCamera(CameraUpdateFactory
				.newCameraPosition(new CameraPosition(finalLocation, 15f, 0, 0)));

		map.addMarker(new MarkerOptions()
				.position(finalLocation)
				.title("final DESTINATION")
				.alpha(0.7f)
				.icon(BitmapDescriptorFactory
						.defaultMarker(BitmapDescriptorFactory.HUE_CYAN)));
	}

	private void setButtonListener() {

		buttonRequest.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {

				if (start == true) {
					buildDialog();
				} else {

					/**
					 * FIXING THIS DON'T FORGET TO PUT BACK
					 */
					placeholder();

				}
			}
		});
	}

	private void placeholder() {

		buttonRequest.setText("Start");
		timer.stop();
		start = true;
		if (directions.equals("wander")) {
			directions = currentLocation;
		}
		try {
			dm.writeLocal(
					context.getResources().getString(R.string.latest_time_file)
							+ directions.toString().toUpperCase(), timer
							.getText().toString(), Context.MODE_APPEND);
			System.out.println("Writing "
					+ context.getResources().getString(
							R.string.latest_time_file)
					+ directions.toUpperCase());
		} catch (IOException e) {
			e.printStackTrace();
		}

		String latestTime = null;
		try {
			latestTime = dm.readLocal(context.getResources().getString(
					R.string.latest_time_file)
					+ directions.toUpperCase());
		} catch (Exception e) {
			e.printStackTrace();
		}
		Toast.makeText(context, latestTime, Toast.LENGTH_SHORT).show();

		CharSequence finalTime = context.getFinalTime();
		DetailsTabFragment dtf = context.getTabsFragment();
		try {
			dtf.setTimerText(finalTime);
		} catch (NullPointerException e) {
			System.out.println("Tabs fragment has not been instantiated yet");
		}

	}

	// REMEMBER TO PUT BACK AHHHHHHH
	@SuppressWarnings("unused")
	private void putBACK() {

		int finalDistance = getDistance(false);
		if (finalDistance <= (initialDistance * 0.1)) {
			buttonRequest.setText("Start");
			timer.stop();
			start = true;

		} else {

			Toast.makeText(context,
					"You're not close enough to the location yet!",
					Toast.LENGTH_SHORT).show();
		}
	}

	private void buildDialog() {

		AlertDialog.Builder builder = new AlertDialog.Builder(context);

		builder.setMessage(R.string.map_dialog_message);
		builder.setPositiveButton(R.string.yes,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {

						map.clear();
						manipulateMap();
						gd.setLogging(true);
						try {
							gd.request(myPos, finalLocation,
									GoogleDirection.MODE_WALKING);
							timer.setBase(SystemClock.elapsedRealtime());
							timer.start();

							initialDistance = getDistance(true);
							/**
							 * cannot use getResources()
							 */
							buttonRequest.setText("End");
							start = false;
							//
						} catch (NullPointerException e) {
							e.printStackTrace();
						}

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

	private void manipulateMap() {

		try {
			map.addMarker(new MarkerOptions()
					.position(initialLocation)
					.title("Start Location")
					.alpha(0.7f)
					.icon(BitmapDescriptorFactory
							.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
		} catch (Exception e) {
			e.printStackTrace();
		}
		map.addMarker(new MarkerOptions()
				.position(finalLocation)
				.title("Final DESTINATION!")
				.alpha(0.7f)
				.icon(BitmapDescriptorFactory
						.defaultMarker(BitmapDescriptorFactory.HUE_CYAN))
				.flat(true).snippet("YOU DID IT! YOU USED TO STUDY HERE"));
	}

	protected void freeDirections() {

		getInitialPosition();

		map.setOnMapClickListener(new OnMapClickListener() {

			@Override
			public void onMapClick(LatLng point) {

				getInitialPosition();

				if (markerPoints.size() >= 2) {
					markerPoints.clear();
					map.clear();

				}

				try {
					MarkerOptions toAdd = new MarkerOptions()
							.position(initialLocation)
							.title("Start Location")
							.alpha(0.7f)
							.icon(BitmapDescriptorFactory
									.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
					map.addMarker(toAdd);
				} catch (Exception e) {
					e.printStackTrace();
				}

				MarkerOptions options = new MarkerOptions();
				options.position(point);

				options.icon(BitmapDescriptorFactory
						.defaultMarker(BitmapDescriptorFactory.HUE_RED));

				map.addMarker(options);

				markerPoints.add(initialLocation);
				markerPoints.add(point);

				if (markerPoints.size() == 2) {
					LatLng origin = markerPoints.get(0);
					System.out.println(origin.toString());
					LatLng dest = markerPoints.get(1);

					gd = new GoogleDirection(context);
					gd.setOnDirectionResponseListener(new OnDirectionResponseListener() {
						public void onResponse(String status, Document doc,
								GoogleDirection gd) {
							map.addPolyline(gd.getPolyline(doc, 3, Color.RED));
						}
					});

					gd.setLogging(true);
					gd.request(origin, dest, GoogleDirection.MODE_WALKING);

				}
			}
		});

	}

	protected boolean isDone() {
		return start;
	}

	protected void relocate() {
		map.clear();
		manipulateMap();
		gd.setLogging(true);
		try {
			gd.request(myPos, finalLocation, GoogleDirection.MODE_WALKING);
		} catch (NullPointerException e) {
			e.printStackTrace();
		}
	}

}