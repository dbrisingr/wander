package com.wander.main;

import java.util.ArrayList;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;

public class SearchAdapter extends CursorAdapter {

	private ArrayList<String> items;
	private Button toClick;
	private String buttonText;

	public SearchAdapter(Context context, Cursor cursor, ArrayList<String> items) {

		super(context, cursor, false);
		this.items = items;

	}

	@Override
	public void bindView(View view, final Context context, Cursor cursor) {

		// Show list item data from cursor
		toClick.setText(items.get(cursor.getPosition()));
		buttonText = toClick.getText().toString();

		// Alternatively show data direct from database
		// toClick.setText(cursor.getString(cursor.getColumnIndex("text")));

		toClick.setOnClickListener(new OnClickListener() {

			public void onClick(View arg0) {
				((MainActivity) context).startMapActivityFromSearch(buttonText);

			}

		});
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {

		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		View view = inflater.inflate(R.layout.item, parent, false);

		toClick = (Button) view.findViewById(R.id.to_click);

		return view;

	}

}