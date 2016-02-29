package com.evry.ecgtid;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.Map;

import org.xmlpull.v1.XmlPullParserException;

import com.evry.ecgtid.objects.MetaData;
import com.evry.ecgtid.objects.TimeSheetData;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

public class RegistrationManager extends MasterActivity implements OnItemClickListener, OnClickListener {

	String date;
	ArrayList<String> captions;
	ArrayList<MetaData> metaDataList;
	ArrayList<TimeSheetData> timeSheetDataList;
	TimeSheetData timeSheetData;
	MetaData metaData;
	Map<String, String> captionValue;
	Map<Integer, Map<String, String>> rowNumber_captionValue;
	ListView listView;
	Calendar calendar;
	float totalHours;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.registration_manager);
		Button newRegistrationBtn = (Button) findViewById(R.id.new_btn);
		newRegistrationBtn.setOnClickListener(this);

		Bundle bundle = getIntent().getExtras();
		if (bundle != null) {
			date = bundle.getString("date");
			calendar = (Calendar) bundle.getSerializable("calendar");
		}

		metaDataList = new ArrayList<MetaData>();
		timeSheetDataList = new ArrayList<TimeSheetData>();
		metaData = new MetaData();
		timeSheetData = new TimeSheetData();
		captions = new ArrayList<String>();
		captionValue = new LinkedHashMap<String, String>();
		rowNumber_captionValue = new LinkedHashMap<Integer, Map<String, String>>();

		metaDataList = DataExchange.getInstance().getMetaData();
		timeSheetDataList = DataExchange.getInstance().getTimeSheetData();

		int a = 0, b = 1;

		for (int j = 0; j < metaDataList.size(); j++) {

			metaData = metaDataList.get(j);
			captions.add(metaData.Caption);

		}
		for (int k = 0; k < timeSheetDataList.size();) {

			timeSheetData = timeSheetDataList.get(k);

			if (a == metaDataList.size())
				a = 0;

			metaData = metaDataList.get(a);

			if ((timeSheetData.ColumnNumber - 1) == metaDataList.indexOf(metaData)) {

				metaData = metaDataList.get(timeSheetData.ColumnNumber - 1);
				if (timeSheetData.RowNumber == b) {
					captionValue = new LinkedHashMap<String, String>();
					b++;
				}

				if (timeSheetData.Value.substring(timeSheetData.Value.lastIndexOf(",") + 1, timeSheetData.Value.length()).equals("25"))
					captionValue.put(metaData.Caption, timeSheetData.Value.substring(0, timeSheetData.Value.lastIndexOf(",")) + ":" + "15");
				else if (timeSheetData.Value.substring(timeSheetData.Value.lastIndexOf(",") + 1, timeSheetData.Value.length()).equals("50"))
					captionValue.put(metaData.Caption, timeSheetData.Value.substring(0, timeSheetData.Value.lastIndexOf(",")) + ":" + "30");
				else if (timeSheetData.Value.substring(timeSheetData.Value.lastIndexOf(",") + 1, timeSheetData.Value.length()).equals("75"))
					captionValue.put(metaData.Caption, timeSheetData.Value.substring(0, timeSheetData.Value.lastIndexOf(",")) + ":" + "45");
				else
					captionValue.put(metaData.Caption, timeSheetData.Value);

				rowNumber_captionValue.put(timeSheetData.RowNumber, captionValue);
				if (timeSheetData.RecordId != 0)
					totalHours += Float.valueOf(timeSheetData.Value.replace(",", "."));
				
				k++;
			}
			a++;
		}

		listView = (ListView) findViewById(R.id.registrationManagerList);
		RegistrationManagerAdapter adapter = new RegistrationManagerAdapter(this, rowNumber_captionValue);
		listView.setOnItemClickListener(this);
		listView.setAdapter(adapter);
		listView.setDivider(getResources().getDrawable(R.drawable.separator));
		TextView headerView = (TextView) findViewById(R.id.companyName);
		headerView.setText(getResources().getString(R.string.registration));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.registration_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		switch (item.getItemId()) {

		case R.id.prefs:
			Intent intent = new Intent(this, SettingsScreen.class);
			startActivity(intent);
			break;

		case R.id.weekTotal:
			new AsyncShowWeekTotal().execute(this);
			break;

		default:
			return super.onOptionsItemSelected(item);
		}
		return true;
	}

	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

		Map<String, String> captionValue = new LinkedHashMap<String, String>();
		captionValue = rowNumber_captionValue.get(position + 1);
		DataExchange.getInstance().setCaptionValueMap(captionValue);
		Intent intent = new Intent(RegistrationManager.this, Registration.class);
		intent.putExtra("date", date);
		intent.putExtra("isMultipleRegistration", true);
		intent.putExtra("row", position + 1);
		intent.putExtra("calendar", calendar);
		intent.putExtra("totalHours", totalHours);
		finish();
		startActivity(intent);
	}

	public void onClick(View v) {

		DataExchange.getInstance().setTimeSheetData(null);
		Intent intent = new Intent(RegistrationManager.this, Registration.class);
		intent.putExtra("date", date);
		intent.putExtra("registrationsNumber", listView.getChildCount());
		intent.putExtra("newRegistration", getResources().getString(R.string.new_reg));
		intent.putExtra("calendar", calendar);
		intent.putExtra("totalHours", totalHours);
		startActivity(intent);

	}

	private class AsyncShowWeekTotal extends AsyncTask<RegistrationManager, Void, String> {
		public ProgressDialog dialog;
		float weekTotal;

		protected void onPreExecute() {

			String pleaseWait = getResources().getString(R.string.please_wait);
			dialog = ProgressDialog.show(RegistrationManager.this, "", pleaseWait, true, false);
			dialog.setCancelable(false);
			dialog.setCanceledOnTouchOutside(false);

		}

		@Override
		protected String doInBackground(RegistrationManager... params) {
			String result = "";
			SoapManager manager = new SoapManager(RegistrationManager.this);
			try {
				weekTotal = manager.getWeekTotal(DataExchange.getInstance().getUsername(), DataExchange.getInstance().getPassword(),
						manager.getWeekTotalMap(date));

			} catch (Exception e) {
				if (e != null) {
					Throwable t = e.getCause();
					if (t != null) {
						if (t instanceof IOException) {
							Log.e("CalendarView/Soap", e.toString());
							result = "IOException";
						} else if (t instanceof XmlPullParserException) {
							Log.e("CalendarView/Soap", e.toString());
							result = "XmlPullParserException";
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
				alarmAlertWithoutIntent(getResources().getString(R.string.week_total),
						String.valueOf(weekTotal) + " " + getResources().getString(R.string.hours));
				dialog.dismiss();

			}
		}
	}

}
