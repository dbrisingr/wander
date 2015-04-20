package com.wander.main;

import java.util.ArrayList;

import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

public final class LocationsDatabase {

	private ArrayList<LocationsData> locationsList;

	public LocationsDatabase() {

		locationsList = new ArrayList<>();

		locationsList.add(new LocationsData("House Test", new LatLng(51.501848,
				-0.096902)));
		locationsList.add(new LocationsData("Campus Test", new LatLng(
				51.502993, -0.089620)));
		locationsList.add(new LocationsData("Borough Market", new LatLng(
				51.505684, -0.091090)));
		locationsList.add(new LocationsData("Hyde Park", new LatLng(51.507428,
				-0.165698)));
		locationsList.add(new LocationsData("St. Paul's Cathedral", new LatLng(
				51.514039, -0.098340)));
		locationsList.add(new LocationsData("British Museum", new LatLng(
				51.519573, -0.126957)));
		locationsList.add(new LocationsData("National Gallery", new LatLng(
				51.509109, -0.128342)));
		locationsList.add(new LocationsData("Tate Modern", new LatLng(
				51.491239, -0.127046)));
		locationsList.add(new LocationsData("Tower Of London", new LatLng(
				51.508248, -0.076103)));
		locationsList.add(new LocationsData("Science Museum", new LatLng(
				51.497869, -0.174599)));

	}

	public void randomLocation() {

	}

	public void addLocation(LocationsData toAdd) {
		locationsList.add(toAdd);
		Log.d("success", "New Location added");

	}

	public void removeLocation(LocationsData toRemove) {
		locationsList.remove(toRemove);
		Log.d("success", toRemove.toString() + " removed");
	}

	public LocationsData searchData(String locationName) {
		LocationsData location = null;
		for (int i = 0; i < locationsList.size(); i++) {
			if (locationsList.get(i).getLocation().equals(locationName)) {
				location = locationsList.get(i);
				break;
			}
		}
		return location;
	}

	public LocationsData getData(int i) {
		return locationsList.get(i);
	}

	public ArrayList<LocationsData> getArray() {
		return locationsList;
	}

}
