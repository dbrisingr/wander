package com.wander.main;

import com.google.android.gms.maps.model.LatLng;

public class LocationsData {

	private String locationName;
	private LatLng latLng;

	public LocationsData(String locationName, LatLng latLng) {
		this.locationName = locationName;
		this.latLng = latLng;

	}

	public String getLocation() {
		return locationName;
	}

	public double latitude() {
		return latLng.latitude;
	}

	public double longitude() {
		return latLng.longitude;
	}
	
	public LatLng getLatLng(){
		return latLng;
	}
}
