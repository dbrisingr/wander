package com.wander.main;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;

import android.content.Context;
import android.util.Log;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

public class DataManager {

	private ParseObject locationDetails;
	private Context context;
	private ArrayList<String> timeArray;

	public DataManager(Context context) {
		this.context = context;
		timeArray = new ArrayList<>();
	}

	protected void writeLocal(String fileName, String toWrite, int contextMode)
			throws IOException {

		toWrite += "\n";
		try {
			System.out.println("Found file name");

			FileOutputStream fos = context
					.openFileOutput(fileName, contextMode);

			fos.write(toWrite.getBytes());
			fos.close();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

	}

	protected String readLocal(String fileName) {

		if (fileName.equals("ignore_login")) {
			String toShow = "";
			try {

				FileInputStream fis = context.openFileInput(fileName);

				int n;
				while ((n = fis.read()) != -1) {
					toShow = toShow + Character.toString((char) n);
				}
				// string temp contains all the data of the file.
				fis.close();
			} catch (IOException e) {

			}
			return toShow;

		}

		timeArray.clear();
		String currentLine = "";

		try {
			FileInputStream fis = context.openFileInput(fileName);
			DataInputStream dis = new DataInputStream(fis);
			BufferedReader br = new BufferedReader(new InputStreamReader(dis));
			while ((currentLine = br.readLine()) != null) {
				// do something with the line you just read
				timeArray.add(currentLine);
			}

			dis.close();

		} catch (IOException e) {
			e.printStackTrace();
		}

		System.out.println("----");
		for (int i = 0; i < timeArray.size(); i++) {
			System.out.println(timeArray.get(i));
		}

		return timeArray.get(timeArray.size() - 1).toString();

	}

	/**
	 * 
	 */

	protected void writeCloud() {

	}

	protected void readCloud() {

	}

	/**
	 * 
	 */

	protected void write() {

		locationDetails = new ParseObject("LocationDetails");
		locationDetails.put("location", "Borough Market");
		locationDetails.put("latestTime", "12:54");
		locationDetails.put("cheatMode", false);
		locationDetails.saveInBackground();

	}

	protected void read() {
		String objectId = locationDetails.getObjectId();
		ParseQuery<ParseObject> query = ParseQuery.getQuery("LocationDetails");
		query.getInBackground(objectId, new GetCallback<ParseObject>() {
			public void done(ParseObject object, ParseException e) {
				if (e == null) {
					System.out.println(object);
					String location = (String) object.get("location");
					String latestTime = (String) object.get("latestTime");
					System.out.println(location + ": " + latestTime);
				} else {
					Log.d("ParseException",
							"e is null. What does that even mean!?");
				}
			}
		});
	}

	protected ArrayList<String> getTimeArray(String fileName) {

		timeArray.clear();
		String currentLine = "";

		try {
			FileInputStream fis = context.openFileInput(fileName);
			DataInputStream dis = new DataInputStream(fis);
			BufferedReader br = new BufferedReader(new InputStreamReader(dis));
			while ((currentLine = br.readLine()) != null) {
				// do something with the line you just read
				timeArray.add(currentLine);
			}

			dis.close();

		} catch (IOException e) {
			e.printStackTrace();
		}

		return timeArray;
	}
	
	public ArrayList<Date> getTimeArray(ArrayList<String> timeArray){
		
		ArrayList<Date> converted = new ArrayList<>();
		
		
		return converted;
	}

}
