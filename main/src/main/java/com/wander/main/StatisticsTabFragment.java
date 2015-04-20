package com.wander.main;

import java.util.ArrayList;

import android.app.Activity;
import android.app.Fragment;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.ActionBar.LayoutParams;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class StatisticsTabFragment extends Fragment {

	private View rootView;
	private Activity activity;
	private ArrayList<String> timeArray;

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		rootView = inflater.inflate(R.layout.fragment_statistics, container,
				false);

		activity = getActivity();
		return rootView;
	}

	@Override
	public void onStart() {
		super.onStart();
	}

	@Override
	public void onResume() {

		super.onResume();
		getArray();
		addScrollView();
		setLayouts();
		showAllTexts();

	}

	private void getArray() {
		DataManager dm = new DataManager(activity);
		timeArray = dm.getTimeArray(activity.getResources().getString(
				R.string.latest_time_file)
				+ getArguments().getString("location").toUpperCase());
	}

	private void setLayouts() {

		LinearLayout ll = (LinearLayout) rootView
				.findViewById(R.id.ll_stats_overall);
		ll.setBackgroundColor(Color.WHITE);
		ll.getBackground().setAlpha(40);

		LinearLayout llScroll = (LinearLayout) activity
				.findViewById(R.id.ll_for_stats_scroll);
		llScroll.setBackgroundColor(Color.WHITE);
		llScroll.getBackground().setAlpha(40);

	}

	private void showAllTexts() {
		Bundle b = getArguments();
		String location = b.getString("location");

		TextView destination = (TextView) activity
				.findViewById(R.id.statisticsLocation);
		destination.setText(location);

		TextView textDistance = (TextView) rootView
				.findViewById(R.id.textDistance);

		TextView textEstimated = (TextView) rootView
				.findViewById(R.id.textFastest);

		if (!timeArray.isEmpty()) {
			textDistance.setText(timeArray.get(timeArray.size() - 1));
		}

	}

	private String calculateFastestTime() {
		String fastest = "00:00";

		for (int i = 0; i < timeArray.size() - 1; i++) {

			String temp = timeArray.get(i);
			int f1 = fastest.charAt(0);
			System.out.println(f1);
		}

		return fastest;
	}

	private void addScrollView() {
		LinearLayout llEdit = (LinearLayout) activity
				.findViewById(R.id.ll_for_stats_scroll);

		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);

		ScrollView svLocations = new ScrollView(activity);
		svLocations.setLayoutParams(params);

		TableLayout tbLocations = new TableLayout(activity);
		tbLocations.setLayoutParams(params);
		tbLocations.setStretchAllColumns(true);

		Bundle b = getArguments();
		String location = b.getString("location");
		int distance = b.getInt("distance");

		TableRow rows[] = new TableRow[timeArray.size()];

		if (!timeArray.isEmpty()) {
			for (int i = timeArray.size() - 1; i > 0; i--) {
				rows[i] = new TableRow(activity);
				rows[i].setLayoutParams(new TableLayout.LayoutParams());

				TextView a = new TextView(activity);
				a.setTextAppearance(activity, R.style.ForTextDetails);
				a.setText(timeArray.get(i));
				a.setGravity(Gravity.CENTER);
				a.setTypeface(null, Typeface.BOLD);

				rows[i].addView(a);
				tbLocations.addView(rows[i]);

			}
		}

		svLocations.addView(tbLocations);
		llEdit.addView(svLocations);
	}

	protected void setTimerText(CharSequence distance) {
		TextView textElapsed = (TextView) rootView
				.findViewById(R.id.textElapsed);
		textElapsed.setText(distance);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void onLowMemory() {
		super.onLowMemory();
	}
}
