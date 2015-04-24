package com.wander.main;

import java.util.ArrayList;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.database.MatrixCursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.Toast;
import com.parse.Parse;

public class MainActivity extends Activity {

	protected final static LocationsDatabase ld = new LocationsDatabase();
	protected final static String EXTRA_MESSAGE = "com.example.brisingrv2.MESSAGE";
	protected static boolean isLoggedIn;
	private boolean needToLogin;
	protected static DataManager dm;
	private String ignore;
	protected static SearchView searchView;

	private ArrayList<String> items = new ArrayList<>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Drawable wander = getResources().getDrawable(R.drawable.skyline);
		wander.setAlpha(60);
		getWindow().setBackgroundDrawable(wander);
		addScrollView();

		/**
		 * initialising data
		 */

		dm = new DataManager(this);
		Parse.initialize(this, "ds9dAnzuhmwEqj0kUcI1EKgPGPzlj5QXAcG5ONpy",
				"DHgI4OzLhRUxWgW4FVgejU2D383K6dcf2uRJddPB");

		for (int i = 0; i < ld.getArray().size(); i++) {
			items.add(ld.getArray().get(i).getLocation());
		}

		/**
		 * Super inefficient way of going through this. See if you can do
		 * something else. Temporary workaround
		 */
		if (LoginActivity.isLoggedIn() == false) {
			initialiseLogin();
		}

	}

	protected void initialiseLogin() {

		boolean isFirstActivity = true;
		try {
			if (!getCallingActivity().equals(null)) {
				isFirstActivity = false;
			}
		} catch (NullPointerException e) {
			isFirstActivity = true;
		}
		if (isFirstActivity == true) {
			needToLogin();
		}
		if (needToLogin == true) {
			needToLogin = false;
			openLogin();
		}

	}

	/**
	 * Checks if user has ticked the "Don't prompt me on startup" checkbox
	 */
	protected void needToLogin() {

		ignore = dm.readLocal("ignore_login");
		ignore = ignore.trim();
		if (ignore.equals("true")) {
			needToLogin = false;
		} else {
			needToLogin = true;
		}
	}

	@Override
	public void onResume() {
		super.onResume();

	}

	public void addScrollView() {
		LinearLayout llEdit = (LinearLayout) findViewById(R.id.ll_for_scroll);

		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);

		ScrollView svLocations = new ScrollView(this);
		svLocations.setLayoutParams(params);

		TableLayout tbLocations = new TableLayout(this);
		tbLocations.setLayoutParams(params);
		tbLocations.setStretchAllColumns(true);

		ArrayList<LocationsData> locationsArray = ld.getArray();
		TableRow rows[] = new TableRow[locationsArray.size()];

		for (int i = 0; i < locationsArray.size(); i++) {
			rows[i] = new TableRow(this);
			rows[i].setLayoutParams(new TableLayout.LayoutParams());

			Button x = new Button(this);
			x.setId(i);
			x.setText(locationsArray.get(i).getLocation());
			x.setTextColor(Color.parseColor("#CC6600"));
			x.setBackgroundResource(R.drawable.button_settings);
			x.setTypeface(null, Typeface.BOLD);
			x.setTextSize(18);
			x.setOnClickListener(new OnClickListener() {

				public void onClick(View arg0) {
					startMapActivity(arg0);

				}

			});

			rows[i].addView(x);
			tbLocations.addView(rows[i]);

		}

		svLocations.addView(tbLocations);
		llEdit.addView(svLocations);
	}

	private Menu menu;

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {

		MenuItem login = menu.findItem(R.id.action_login);
		MenuItem logout = menu.findItem(R.id.action_logout);
		if (LoginActivity.isLoggedIn() == true) {
			login.setVisible(false);
			logout.setVisible(true);
		} else {
			login.setVisible(true);
			logout.setVisible(false);
		}

		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main, menu);
		this.menu = menu;

		// Get the SearchView and set the searchable configuration
		SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
		searchView = (SearchView) menu.findItem(R.id.search).getActionView();
		// Assumes current activity is the searchable activity
		searchView.setSearchableInfo(searchManager
				.getSearchableInfo(getComponentName()));
		//
		searchView.setOnQueryTextListener(new OnQueryTextListener() {

			@Override
			public boolean onQueryTextChange(String query) {

				if (query.length() >= 2) {
					loadData(query);
				}

				return true;

			}

			@Override
			public boolean onQueryTextSubmit(String query) {
				// TODO Auto-generated method stub
				return false;
			}

		});
		//

		return super.onCreateOptionsMenu(menu);
	}

	private void loadData(String query) {

		ArrayList<String> toAdd = new ArrayList<>();

		SearchManager manager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
		SearchView search = (SearchView) menu.findItem(R.id.search)
				.getActionView();
		search.setSearchableInfo(manager.getSearchableInfo(getComponentName()));

		//
		for (int j = 0; j < items.size(); j++) {
			String toCheck = items.get(j).toLowerCase();
			if (toCheck.contains(query.toLowerCase())) {
				toAdd.add(items.get(j));
			}
		}

		//

		// Load data from list to cursor
		String[] columns = new String[] { "_id", "text" };
		Object[] temp = new Object[] { 0, "default" };

		MatrixCursor cursor = new MatrixCursor(columns);

		for (int i = 0; i < toAdd.size(); i++) {

			temp[0] = i;
			temp[1] = toAdd.get(i);

			cursor.addRow(temp);

		}
		search.setSuggestionsAdapter(new SearchAdapter(this, cursor, toAdd));
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.

		switch (item.getItemId()) {
		case R.id.action_login:
			openLogin();
			return true;
		case R.id.action_logout:
			System.out.println("ok");
			LoginActivity.logout();
			MenuItem login = menu.findItem(R.id.action_login);
			MenuItem logout = menu.findItem(R.id.action_logout);
			login.setVisible(true);
			logout.setVisible(false);
			Toast toast = Toast.makeText(this, "Logout Successful!",
					Toast.LENGTH_SHORT);
			toast.show();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	public void startMapActivity(View view) {
		Intent intent = new Intent(this, ShowMapActivity.class);
		Button button = (Button) findViewById(view.getId());
		String message = button.getText().toString();

		intent.putExtra(EXTRA_MESSAGE, message);
		startActivity(intent);
	}

	public void startMapActivityFromSearch(String message) {
		Intent intent = new Intent(this, ShowMapActivity.class);
		intent.putExtra(EXTRA_MESSAGE, message);
		startActivity(intent);
	}

	public void openFreeDirections(View view) {
		// FOR CHOOSING YOUR STARTING AND END LOCATION POINTS
		Intent intent = new Intent(this, ShowMapActivity.class);
		String message = "free";
		intent.putExtra(EXTRA_MESSAGE, message);
		startActivity(intent);
	}

	public void openLogin() {
		Intent intent = new Intent(this, LoginActivity.class);
		startActivity(intent);
	}

}
