package com.wander.main;

import java.io.IOException;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

public class LoginActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		Drawable wander = getResources().getDrawable(R.drawable.skyline);
		wander.setAlpha(50);
		getWindow().setBackgroundDrawable(wander);
	}

	protected void onResume() {
		super.onResume();

	}

	protected static boolean isLoggedIn() {
		ParseUser currentUser = ParseUser.getCurrentUser();
		if (currentUser == null) {
			return false;
		} else {
			return true;
		}
	}

	public void initiateRegister(View view) {

		EditText userName = (EditText) findViewById(R.id.user_name);
		final String userNameText = userName.getText().toString();

		EditText password = (EditText) findViewById(R.id.password);
		final String passwordText = password.getText().toString();

		EditText email = (EditText) findViewById(R.id.email);
		final String emailText = email.getText().toString();

		ParseUser user = new ParseUser();

		if (!(userNameText.length() == 0) && !(passwordText.length() == 0)) {

			user.setUsername(userNameText);
			user.setPassword(passwordText);
			if (!(emailText.length() == 0)) {
				user.setEmail(emailText);
			}

			user.signUpInBackground(new SignUpCallback() {
				public void done(ParseException e) {
					if (e == null) {
						Toast toast = Toast.makeText(context,
								"Sign up Successful!", Toast.LENGTH_SHORT);

						toast.show();

						afterRegister();
					} else {
						Toast toast = Toast.makeText(context, e.getMessage(),
								Toast.LENGTH_SHORT);
						toast.show();
					}
				}
			});
		} else {
			Toast toast = Toast.makeText(context,
					"Username and Password fields cannot be empty",
					Toast.LENGTH_SHORT);
			toast.show();
		}

	}

	public void registerLayout(View view) {
		setTitle("Sign up");

		Button signIn = (Button) findViewById(R.id.sign_in_button);
		signIn.setVisibility(View.GONE);

		Button signUp = (Button) findViewById(R.id.sign_up_button);
		signUp.setVisibility(View.VISIBLE);

		Button signInHint = (Button) findViewById(R.id.sign_in_hint);
		signInHint.setVisibility(View.GONE);

		Button signUpHint = (Button) findViewById(R.id.sign_up_hint);
		signUpHint.setVisibility(View.VISIBLE);

		EditText email = (EditText) findViewById(R.id.email);
		email.setVisibility(View.VISIBLE);

	}

	public void initiateLogin(View view) {

		EditText userName = (EditText) findViewById(R.id.user_name);
		final String userNameText = userName.getText().toString();

		EditText password = (EditText) findViewById(R.id.password);
		final String passwordText = password.getText().toString();

		if (!userNameText.equals("") && !passwordText.equals("")) {
			ParseUser.logInInBackground(userNameText, passwordText,
					new LogInCallback() {
						public void done(ParseUser user, ParseException e) {
							if (user != null) {
								Toast toast = Toast
										.makeText(context, "Login Successful!",
												Toast.LENGTH_SHORT);
								toast.show();
								finish();

							} else {

								Log.d("error!", e.getMessage());
								Toast toast = Toast.makeText(context,
										e.getMessage(), Toast.LENGTH_SHORT);
								toast.show();
							}
						}
					});
		} else {
			Toast toast = Toast.makeText(context,
					"Username and Password fields cannot be empty",
					Toast.LENGTH_SHORT);
			toast.show();
		}
	}

	public void loginLayout(View view) {

		setTitle("Sign in");

		Button signIn = (Button) findViewById(R.id.sign_in_button);
		signIn.setVisibility(View.VISIBLE);

		Button signUp = (Button) findViewById(R.id.sign_up_button);
		signUp.setVisibility(View.GONE);

		Button signInHint = (Button) findViewById(R.id.sign_in_hint);
		signInHint.setVisibility(View.VISIBLE);

		Button signUpHint = (Button) findViewById(R.id.sign_up_hint);
		signUpHint.setVisibility(View.GONE);

		EditText email = (EditText) findViewById(R.id.email);
		email.setVisibility(View.INVISIBLE);
	}

	public void afterRegister() {
		setTitle("Sign in");

		Button signIn = (Button) findViewById(R.id.sign_in_button);
		signIn.setVisibility(View.GONE);

		Button signUp = (Button) findViewById(R.id.sign_up_button);
		signUp.setVisibility(View.VISIBLE);

		Button signInHint = (Button) findViewById(R.id.sign_in_hint);
		signInHint.setVisibility(View.GONE);

		Button signUpHint = (Button) findViewById(R.id.sign_up_hint);
		signUpHint.setVisibility(View.VISIBLE);

		EditText email = (EditText) findViewById(R.id.email);
		email.setVisibility(View.VISIBLE);

		System.out.println("u wot m8?");
	}

	private Context context = this;

	public static void logout() {
		ParseUser.logOut();
	}

	public void ignoreSignIn(View view) {
		CheckBox cb = (CheckBox) findViewById(R.id.checkBox);
		DataManager dm = MainActivity.dm;
		if (cb.isChecked()) {
			try {
				dm.writeLocal("ignore_login", "true", Context.MODE_PRIVATE);
			} catch (IOException e) {

				e.printStackTrace();
			}

		} else {
			try {
				dm.writeLocal("ignore_login", "false", Context.MODE_PRIVATE);
			} catch (IOException e) {

				e.printStackTrace();
			}
		}
		finish();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.login, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.

		return super.onOptionsItemSelected(item);
	}

}
