/*
 * Copyright 2011 Lauri Nevala.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.evry.ecgtid;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.xmlpull.v1.XmlPullParserException;

import com.evry.ecgtid.objects.MetaData;
import com.evry.ecgtid.objects.TimeSheetData;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class CalendarView extends MasterActivity {

	public static Calendar calendar;
	public CalendarAdapter adapter;
	String date = "";
	Map<Integer, Object> date_WorkingHoursCollection = new HashMap<Integer, Object>();
	Map<Integer, Object> date_TimeSheetCollection = new HashMap<Integer, Object>();
	Map<String, Object> monthCollection = new HashMap<String, Object>();
	static String currentMonth;
	static String currentYear;
	boolean isToday = false;
	Soap soap;
	GridView gridview;
	boolean notCurrMonth = true;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.calendar);
		Bundle bundle = getIntent().getExtras();
		calendar = (Calendar) bundle.getSerializable("calendar");
		//notCurrMonth = bundle.getBoolean("notCurrMonth");
		soap = new Soap(this);

		if (calendar.get(Calendar.MONTH) == Calendar.getInstance().get(Calendar.MONTH) && DataExchange.getInstance().getConfiguration() != null){
			notCurrMonth = false;
			calendar = Calendar.getInstance(DataExchange.getInstance().getConfiguration().locale);
		}
		
		
		
		final SimpleDateFormat sdf = new SimpleDateFormat("dd");
		
		currentYear = (String) DateFormat.format("yyyy", calendar);

		AsyncSoapCall asyncSoapCall = new AsyncSoapCall();
		asyncSoapCall.execute(this);

		TextView previous = (TextView) findViewById(R.id.previous);
		previous.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {

				if (calendar.get(Calendar.MONTH) == calendar.getActualMinimum(Calendar.MONTH)) {
					calendar.set((calendar.get(Calendar.YEAR) - 1), calendar.getActualMaximum(Calendar.MONTH), Integer.parseInt(sdf.format(new Date())));
				} else {
					calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH) - 1);
					calendar.set(Calendar.DAY_OF_MONTH, Integer.parseInt(sdf.format(new Date())));
				}
				if (String.valueOf(calendar.get(Calendar.MONTH)).length() > 1)
					currentMonth = String.valueOf(calendar.get(Calendar.MONTH) + 1);
				else
					currentMonth = "0" + String.valueOf(calendar.get(Calendar.MONTH) + 1);
				currentYear = (String) DateFormat.format("yyyy", calendar);

				
				getIntent().setAction(null);
				new AsyncSoapCall().execute(CalendarView.this);

			}
		});

		TextView next = (TextView) findViewById(R.id.next);
		next.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {

				if (calendar.get(Calendar.MONTH) == calendar.getActualMaximum(Calendar.MONTH)) {
					calendar.set((calendar.get(Calendar.YEAR) + 1), calendar.getActualMinimum(Calendar.MONTH), Integer.parseInt(sdf.format(new Date())));
				} else {
					calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH) + 1);
					calendar.set(Calendar.DAY_OF_MONTH, Integer.parseInt(sdf.format(new Date())));
				}
				if (String.valueOf(calendar.get(Calendar.MONTH)).length() > 1)
					currentMonth = String.valueOf(calendar.get(Calendar.MONTH) + 1);
				else
					currentMonth = "0" + String.valueOf(calendar.get(Calendar.MONTH) + 1);
				currentYear = (String) DateFormat.format("yyyy", calendar);

				
				
				getIntent().setAction(null);
				new AsyncSoapCall().execute(CalendarView.this);

			}
		});

		gridview = (GridView) findViewById(R.id.gridview);
		gridview.setOnItemClickListener(new OnItemClickListener() {
			@SuppressWarnings("unchecked")
			public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
				TextView dateView = (TextView) v.findViewById(R.id.date);
				if (dateView instanceof TextView && !dateView.getText().equals("")) {
				

					if (dateView.getText().length() == 1)
						date = DateFormat.format("yyyy-MM", calendar) + "-0" + dateView.getText();
					else
						date = DateFormat.format("yyyy-MM", calendar) + "-" + dateView.getText();

					

					ArrayList<MetaData> metaDataList = new ArrayList<MetaData>();
					ArrayList<TimeSheetData> timeSheetDataList = new ArrayList<TimeSheetData>();
					metaDataList = DataExchange.getInstance().getMetaData();
					timeSheetDataList = (ArrayList<TimeSheetData>) date_TimeSheetCollection.get(Integer.parseInt((String) dateView.getText()));
					
					float registerNumber;

					if (date_WorkingHoursCollection.get(Integer.parseInt((String) dateView.getText())) == null) {
						registerNumber = 0;
					} else {
						registerNumber = (Float) date_WorkingHoursCollection.get(Integer.parseInt((String) dateView.getText()));
					}

					DataExchange.getInstance().setTimeSheetData(timeSheetDataList);
					DataExchange.getInstance().setMetaData(metaDataList);

					if (registerNumber >= 1) {

						Intent intent = new Intent(CalendarView.this, RegistrationManager.class);
						intent.putExtra("date", date);
						intent.putExtra("calendar", calendar);
						startActivity(intent);
					} else {

						Intent intent = new Intent(CalendarView.this, Registration.class);
						intent.putExtra("date", date);
						intent.putExtra("newRegistration", getResources().getString(R.string.new_reg));
						intent.putExtra("calendar", calendar);
						startActivity(intent);
					}

				}
				// }

			}
		});
	}


	@Override
	public void onBackPressed() {
		quitAlert();
	}

	private void quitAlert() {
		String quitTitle = getResources().getString(R.string.quit_btn);
		String quitMsg = getResources().getString(R.string.quit_alert);

		AlertDialog alertDialog = new AlertDialog.Builder(this).create();
		alertDialog.setTitle(quitTitle);
		alertDialog.setMessage(quitMsg);
		alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				CalendarView.this.finish();
			}
		});

		alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {

			}
		});
		alertDialog.setIcon(R.drawable.logo);
		alertDialog.show();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.calendar_menu, menu);
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
		
		case R.id.today:
			calendar = Calendar.getInstance();
			isToday = true;
			new AsyncSoapCall().execute(this);
		break;
		
		case R.id.refresh:
			monthCollection.remove(currentMonth);
			new AsyncSoapCall().execute(this);
		break;
		
		case R.id.weekTotal:
			
			new AsyncSoapCall().execute(this);
		break;

		default:
			return super.onOptionsItemSelected(item);
		}
		return true;
	}

	public static String getCurrentMonth() {
		return (String) DateFormat.format("MM", calendar);
	}

	public static String getCurrentYear() {
		return (String) DateFormat.format("yyyy", calendar);
	}

	private class AsyncSoapCall extends AsyncTask<CalendarView, Void, String> {
		public ProgressDialog dialog;
		LinkedList<Map<Integer, Object>> collections;
		RelativeLayout headerLayout, weekDaysLayout;
		ImageView calendarLine;

		protected void onPreExecute() {

			String refreshMsg = getResources().getString(R.string.refresh_msg);
			dialog = ProgressDialog.show(CalendarView.this, "", refreshMsg, true, false);
			dialog.setCancelable(false);
			dialog.setCanceledOnTouchOutside(false);
			if (calendar.get(Calendar.MONTH) == Calendar.getInstance().get(Calendar.MONTH))
				notCurrMonth = false;
			else 
				notCurrMonth = true;
				
			headerLayout = (RelativeLayout) findViewById(R.id.header);
			weekDaysLayout = (RelativeLayout) findViewById(R.id.weekDays);
			calendarLine = (ImageView) findViewById(R.id.calendarLine);
			gridview = (GridView) findViewById(R.id.gridview);
			
			headerLayout.setVisibility(View.INVISIBLE);
			weekDaysLayout.setVisibility(View.INVISIBLE);
			calendarLine.setVisibility(View.INVISIBLE);
			gridview.setVisibility(View.INVISIBLE);
		}

		@SuppressWarnings("unchecked")
		@Override
		protected String doInBackground(CalendarView... params) {
			String result = "";

			try {

				if (getIntent().getAction() == null || isToday) {
					currentMonth = (String) DateFormat.format("MM", calendar);
					
					
					if (DataExchange.getInstance().getMonthCollection().get(currentMonth) == null){
						SoapManager manager = new SoapManager(CalendarView.this);
						
						List<Map<Integer, Object>> list = manager.getTimeSheetDataForMonth(DataExchange.getInstance().getUsername(), DataExchange.getInstance().getPassword());
						
						date_TimeSheetCollection = list.get(0);
						date_WorkingHoursCollection = list.get(1);

						collections = new LinkedList<Map<Integer,Object>>();
						collections.add(date_TimeSheetCollection);
						collections.add(date_WorkingHoursCollection);
						monthCollection.put(currentMonth, collections);
						DataExchange.getInstance().setMonthCollection(monthCollection);
					} else {
						
						monthCollection = DataExchange.getInstance().getMonthCollection();
						collections = new LinkedList<Map<Integer,Object>>();
						collections = (LinkedList<Map<Integer, Object>>) monthCollection.get(currentMonth);											
						date_TimeSheetCollection = collections.get(0);
						date_WorkingHoursCollection = collections.get(1);
					}

				} else {
					
					monthCollection = DataExchange.getInstance().getMonthCollection();
					collections = new LinkedList<Map<Integer,Object>>();
					collections = (LinkedList<Map<Integer, Object>>) monthCollection.get(currentMonth);					
											
					date_TimeSheetCollection = collections.get(0);
					date_WorkingHoursCollection = collections.get(1);	
					
					
					ArrayList<TimeSheetData> timeSheetDataNewList = new ArrayList<TimeSheetData>();
					timeSheetDataNewList = DataExchange.getInstance().getTimeSheetData();
					
					Bundle bundle = getIntent().getExtras();
					int key = bundle.getInt("key");

					date_TimeSheetCollection.remove(key);
				

					date_TimeSheetCollection.put(key, timeSheetDataNewList);
					
					if (getIntent().getExtras().getBoolean("isDelete")){
						if ((Float) date_WorkingHoursCollection.get(key) - getIntent().getExtras().getFloat("regHours") == 0){
							date_WorkingHoursCollection.remove(key);
						} else {
							
							date_WorkingHoursCollection.put(key,(Float) date_WorkingHoursCollection.get(key) - getIntent().getExtras().getFloat("regHours"));
						}
					}
						
					
					collections.clear();
					collections.add(date_TimeSheetCollection);
					collections.add(date_WorkingHoursCollection);
					monthCollection.put(currentMonth, collections);
					DataExchange.getInstance().setMonthCollection(monthCollection);
					
					if (getIntent().getExtras().getBoolean("isNewRegistration") && !getIntent().getExtras().getBoolean("isDelete")){
						
						if(date_WorkingHoursCollection.get(key) == null)
							date_WorkingHoursCollection.put(key, getIntent().getExtras().getFloat("regHours"));
						else
							date_WorkingHoursCollection.put(key, (Float)date_WorkingHoursCollection.get(key) + getIntent().getExtras().getFloat("regHours"));
						collections.clear();
						collections.add(date_TimeSheetCollection);
						collections.add(date_WorkingHoursCollection);
						monthCollection.put(currentMonth, collections);
						DataExchange.getInstance().setMonthCollection(monthCollection);
					} else if (getIntent().getExtras().getBoolean("updateRegistration")) {
						float totalHours = 0f;
						for (int i = 0;i < timeSheetDataNewList.size();i++){
							TimeSheetData sheetData = timeSheetDataNewList.get(i);
							if (sheetData.RecordId != 0)
								totalHours += Float.valueOf(sheetData.Value);
						}
						date_WorkingHoursCollection.put(key, totalHours);
					}
				}


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
				boolean notCurrentYear = false;
				if (Calendar.getInstance().get(Calendar.YEAR) != calendar.get(Calendar.YEAR)){
					monthCollection.clear();
					notCurrentYear = true;
				}
				headerLayout.setVisibility(View.VISIBLE);
				weekDaysLayout.setVisibility(View.VISIBLE);
				calendarLine.setVisibility(View.VISIBLE);
				gridview.setVisibility(View.VISIBLE);
				
				adapter = new CalendarAdapter(CalendarView.this, calendar, date_WorkingHoursCollection, notCurrMonth, notCurrentYear);

				TextView title = (TextView) findViewById(R.id.title);
				TextView headerView = (TextView) findViewById(R.id.companyName);
				Typeface type = Typeface.createFromAsset(getAssets(), "Avant Garde Book BT.ttf"); 
				title.setTypeface(type);
				String[] months = getResources().getStringArray(R.array.moths);
				
				String month = "";
				for (int i = 0;i < months.length;i++){
					if (calendar.get(Calendar.MONTH) == i)
						month = months[i];
						
				}
				title.setText(month + " " + android.text.format.DateFormat.format("yyyy", calendar));
				headerView.setText(getResources().getString(R.string.time_reg));

				gridview.setAdapter(adapter);
				adapter.refreshDays();
				dialog.dismiss();
			}
		}
	}
	
	
}
