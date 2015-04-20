package com.wander.main;

import android.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

public class DetailsTabFragment extends Fragment {

	private View rootView;
	private ShowMapActivity sma;

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		rootView = inflater
				.inflate(R.layout.fragment_details, container, false);

		return rootView;
	}

	@Override
	public void onStart() {
		super.onStart();
	}

	@Override
	public void onResume() {
		sma = (ShowMapActivity) getActivity();
		super.onResume();
		setLayout();
		showAllTexts();

	}

	private void setLayout() {
		LinearLayout ll = (LinearLayout) sma
				.findViewById(R.id.ll_details_for_scroll);
		ll.setBackgroundColor(Color.WHITE);
		ll.getBackground().setAlpha(40);

	}

	public void showAllTexts() {
		Bundle b = getArguments();
		String location = b.getString("location");
		int distance = b.getInt("distance");

		TextView mainText = (TextView) rootView.findViewById(R.id.mainText);
		mainText.setText(location);

		if (distance != 0) {
			TextView textDistance = (TextView) rootView
					.findViewById(R.id.textDistance);
			textDistance.setText(distance + " m");
		}

		TextView textTimeElapsed = (TextView) rootView
				.findViewById(R.id.textTimeElapsed);

		CharSequence finalTime = sma.getFinalTime();
		boolean done = sma.getDQ().isDone();
		if (finalTime != null && done == true) {
			textTimeElapsed.setText("Final Time");
			setTimerText(finalTime);
		} else if (finalTime == null) {
			setTimerText("00:00");
		} else {
			setTimerText(finalTime);
		}

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
