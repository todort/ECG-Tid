package com.evry.ecgtid;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import org.xmlpull.v1.XmlPullParserException;

import com.evry.ecgtid.objects.MetaData;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

public class LoginScreen extends MasterActivity implements OnClickListener {

	TextView usernameTextView, passwordTextView = null;
	String user, pass = "";
	SharedPreferences prefs;
	CheckBox checkBox;
	Soap soap;
	ArrayList<MetaData> metaData;
	Configuration c;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);
		TextView loginButton = (TextView) findViewById(R.id.loginButton);
		loginButton.setOnClickListener(this);
		usernameTextView = (TextView) findViewById(R.id.username);
		passwordTextView = (TextView) findViewById(R.id.password);
		checkBox = (CheckBox) findViewById(R.id.checkBox);
		
		PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
		prefs = PreferenceManager.getDefaultSharedPreferences(this);
		String searchResults = prefs.getString("CapacitySearchResults", null);
		String recentlyUsed = prefs.getString("CapacityRecentlyUsed", null);
		checkBox.setChecked(prefs.getBoolean("checked", true));
		DataExchange.getInstance().setSearchResultsPerPage(Integer.parseInt(searchResults.substring(0, searchResults.indexOf(" "))));
		DataExchange.getInstance().setRecentlyUsedElements(Integer.parseInt(recentlyUsed.substring(0, recentlyUsed.indexOf(" "))));
		
			
		usernameTextView.setText(prefs.getString("Username", ""));
		passwordTextView.setText(prefs.getString("Password", ""));
	
		Configuration config = DataExchange.getInstance().getConfiguration();
		
		if (prefs.getString("en", "").equals("en"))
			englishConfiguration();
		else if (prefs.getString("da", "").equals("da"))
			danishConfiguration();
		else
			checkLocalLanguage(config);
		
		passwordTextView.setOnEditorActionListener(new OnEditorActionListener() {

			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_DONE) {
					InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
					imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
					login();
					return true;
				}
				return false;
			}
		});
	}

	private void login() {

		if (usernameTextView.length() == 0 || passwordTextView.length() == 0)
			showFillInUserAndPass();
		else {
			soap = new Soap(this);
			if (soap.isOnline())
				new AsyncLogin().execute(this);
			else {
				String noInternetTitle = getResources().getString(R.string.nointernet_title);
				String noInternetMsg = getResources().getString(R.string.nointernet_msg);
				alarmAlert(noInternetTitle, noInternetMsg);
			}
		}
	}

	public void onClick(View v) {
		switch (v.getId()) {

		case R.id.loginButton:
			login();
			break;
		}
	}

	private void showFillInUserAndPass() {

		String alertMsg = getResources().getString(R.string.alert_msg);
		String alertTitle = getResources().getString(R.string.alert_title);
		alarmAlert(alertTitle, alertMsg);
	}

	@Override
	public void onBackPressed() {
		this.finishActivity(R.layout.login);
		this.finish();
	}

	private class AsyncLogin extends AsyncTask<LoginScreen, Void, String> {
		public ProgressDialog dialog;

		protected void onPreExecute() {
			String loginMsg = getResources().getString(R.string.login_msg);
			dialog = ProgressDialog.show(LoginScreen.this, "", loginMsg, true, false);
			dialog.setCancelable(false);
			dialog.setCanceledOnTouchOutside(false);
		}

		@Override
		protected String doInBackground(LoginScreen... params) {
			String result = "";
			user = usernameTextView.getText().toString();
			pass = passwordTextView.getText().toString();
			DataExchange.getInstance().setUsername(user);
			DataExchange.getInstance().setPassword(pass);
			if (checkBox.isChecked()) {
				if (user != null && user.length() > 0 && pass != null && user.length() > 0) {

					prefs.edit().putString("Username", user).commit();
					prefs.edit().putString("Password", pass).commit();
					prefs.edit().putBoolean("checked", true).commit();
					checkBox.setChecked(true);
				}
			} else {
				
				prefs.edit().putString("Username", "").commit();
				prefs.edit().putString("Password", "").commit();
				prefs.edit().putBoolean("checked", false).commit();
				checkBox.setChecked(false);
			}
			try {

				SoapManager manager = new SoapManager(LoginScreen.this);
				metaData = new ArrayList<MetaData>();
				metaData = manager.getMetaData(DataExchange.getInstance().getUsername(), DataExchange.getInstance().getPassword(), manager.getMetaDataMap(android.os.Build.MODEL));
				DataExchange.getInstance().setMetaData(metaData);	
				
				if (metaData.size() == 0)
					result = getResources().getString(R.string.locked);
			} catch (Exception e) {
				if (e != null) {

					Throwable t = e.getCause();
					if (t != null) {
						if (t instanceof IOException) {
							Log.e("LoginScreen/Soap", e.toString());
							result = "IOException";
						} else if (t instanceof XmlPullParserException) {
							Log.e("LoginScreen/Soap", e.toString());
							result = "XmlPullParserException";
						} else if (t instanceof RuntimeException) {
							Log.e("LoginScreen/Soap", e.toString());
							result = e.getMessage();
						}
					}

				}
			}
			return result;
		}

		protected void onPostExecute(String result) {
			if (result.equals("IOException")) {

				String timeoutTitle = getResources().getString(R.string.timeout_title);
				String timeoutMsg = getResources().getString(R.string.timeout_msg);
				alarmAlert(timeoutTitle, timeoutMsg);

			} else if (result.equals("XmlPullParserException")) {

				String wrongUserPassTitle = getResources().getString(R.string.wrong_user_pass_title);
				String wrongUserPassMsg = getResources().getString(R.string.wrong_user_pass_msg);
				alarmAlert(wrongUserPassTitle, wrongUserPassMsg);
			} else if (result.length() == 0) {
				dialog.dismiss();
				Intent intent = new Intent(LoginScreen.this, CalendarView.class);
				
				intent.putExtra("calendar", Calendar.getInstance(c.locale));
				LoginScreen.this.startActivity(intent);
			} else {
				alarmAlert(null, result);
			}
		}
	}

	private void checkLocalLanguage(Configuration config) {
		if (config == null) {
			if (getResources().getConfiguration().locale.getLanguage().equalsIgnoreCase("en")) {
				englishConfiguration();
			} else if (getResources().getConfiguration().locale.getLanguage().equalsIgnoreCase("da")) {
				danishConfiguration();
			}
		} else {
			if (config.locale.getLanguage().equalsIgnoreCase("en")) {
				englishConfiguration();
			} else if (config.locale.getLanguage().equalsIgnoreCase("da")) {
				danishConfiguration();
			}
		}
	}
	private void englishConfiguration(){
		c = new Configuration(getResources().getConfiguration());
		c.locale = Locale.ENGLISH;
		getResources().updateConfiguration(c, getResources().getDisplayMetrics());
		DataExchange.getInstance().setConfiguration(c);
	}
	private void danishConfiguration(){
		c = new Configuration(getResources().getConfiguration());
		c.locale = new Locale("da");
		getResources().updateConfiguration(c, getResources().getDisplayMetrics());
		DataExchange.getInstance().setConfiguration(c);
	}
}