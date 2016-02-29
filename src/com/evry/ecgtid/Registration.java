package com.evry.ecgtid;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.ksoap2.serialization.PropertyInfo;
import org.xmlpull.v1.XmlPullParserException;

import com.evry.ecgtid.objects.MetaData;
import com.evry.ecgtid.objects.TimeSheetData;
import com.evry.ecgtid.objects.UpdateRecord;

import android.R.drawable;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.TimePicker;

public class Registration extends MasterActivity implements OnClickListener, OnItemClickListener {

	ArrayList<MetaData> metaDataList;
	String date = "";
	ArrayList<TimeSheetData> timeSheetDataList;
	TimeSheetData timeSheetData;
	String registeredHours = "";
	String attAbsType;
	RegistrationAdapter adapter;
	ListView listView;
	ArrayList<String> captions = null, values = null;
	ArrayList<Integer> editedPositions;
	Map<String, String> captionValue;
	Map<Integer, String> updateMap;
	EditText subtitle = null;
	int row = 1;
	int key;
	TimeSheetData timeSheet, nextTimeSheet;
	Bundle bundle;
	List<HashMap<String, String>> tecNameValueList;
	HashMap<String, List<HashMap<String, String>>> captionTecNameValue;
	String resultCaption = "";
	Calendar calendar;
	float totalHours;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.registration_detail);

		Button sendButton = (Button) findViewById(R.id.send_btn);
		Button deleteButton = (Button) findViewById(R.id.delete_btn);
		sendButton.setOnClickListener(this);
		deleteButton.setOnClickListener(this);
		deleteButton.setVisibility(View.VISIBLE);

		bundle = getIntent().getExtras();
		date = bundle.getString("date");
		totalHours = bundle.getFloat("totalHours");
		calendar = (Calendar) bundle.getSerializable("calendar");

		int j = 0;

		metaDataList = new ArrayList<MetaData>();
		timeSheetDataList = new ArrayList<TimeSheetData>();
		metaDataList = DataExchange.getInstance().getMetaData();
		timeSheetDataList = DataExchange.getInstance().getTimeSheetData();
		MetaData metaData = new MetaData();
		timeSheetData = new TimeSheetData();
		editedPositions = new ArrayList<Integer>();
		captionValue = new LinkedHashMap<String, String>();

		captions = new ArrayList<String>();
		updateMap = new LinkedHashMap<Integer, String>();

		for (int i = 0; i < metaDataList.size(); i++) {

			metaData = metaDataList.get(i);
			if (!metaData.DataType.equals("amount"))
				captions.add(metaData.Caption);
			else
				captions.add("Time Spend");

		}
		values = new ArrayList<String>();

		if (this.getIntent().getExtras().getBoolean("isMultipleRegistration") == true) {
			row = bundle.getInt("row");
		}

		if (timeSheetDataList != null) {
			for (int i = 0; i < timeSheetDataList.size();) {

				timeSheetData = timeSheetDataList.get(i);

				
				
				if (timeSheetData.RecordId != 0 && timeSheetData.RowNumber == row)
					registeredHours = timeSheetData.Value;

				if (j == metaDataList.size())
					j = 0;

				metaData = metaDataList.get(j);

				if ((timeSheetData.ColumnNumber - 1) == metaDataList.indexOf(metaData)) {

					metaData = metaDataList.get(timeSheetData.ColumnNumber - 1);
					captionValue.put(metaData.Caption, timeSheetData.Value);
					
					i++;
				}
				j++;
			}
		}

		if (this.getIntent().getExtras().getBoolean("isMultipleRegistration") == true) {
			captionValue = DataExchange.getInstance().getCaptionValueMap();
		}
			

		listView = (ListView) findViewById(R.id.registration_detail_listView);

		/*
		 * if (this.getIntent().getExtras().getBoolean("isNewRegistration") ==
		 * true) { captionValue.clear(); }
		 */

		adapter = new RegistrationAdapter(this, captions, captionValue);
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(this);
		listView.setDivider(getResources().getDrawable(R.drawable.separator));
		TextView headerView = (TextView) findViewById(R.id.companyName);
		String headerTitle = getResources().getString(R.string.registration);
		if (bundle.getString("newRegistration") != null){
			headerTitle = bundle.getString("newRegistration");
			deleteButton.setVisibility(View.INVISIBLE);
		}

		headerView.setText(headerTitle);

	}

	public void onClick(View v) {
		switch (v.getId()) {

		case R.id.send_btn:
			AsyncUpdateRecord updateRecord = new AsyncUpdateRecord();
			updateRecord.execute(this);
			break;

		case R.id.delete_btn:
			confirmDelete();
			break;
		}

	}

	public void onItemClick(final AdapterView<?> parent, View view, final int position, long id) {

		final View v = view;
		MetaData metaData;
		subtitle = (EditText) v.findViewById(R.id.subtitle);

		for (int i = 0; i < metaDataList.size(); i++) {
			metaData = new MetaData();
			metaData = metaDataList.get(i);
			key = metaDataList.indexOf(metaData) + 1;
			final String caption = (String) parent.getItemAtPosition(position);
			if (metaData.Caption.equals(caption) && !metaData.HasF4 && metaData.Tooltip.equals("Att./Absence type")) {
				String[] items = getResources().getStringArray(R.array.att_abs_type);
				showSearchDropDown(items, getResources().getString(R.string.att_abs_prompt), caption, key);

				break;

			} else if (caption.equals("Time Spend") && metaData.DataType.equalsIgnoreCase("amount")) {

				int hours = 0, minutes = 0;

				if (registeredHours.contains(",") && registeredHours.length() > 0) {
					hours = Integer.valueOf(registeredHours.substring(0, registeredHours.indexOf(",")));
					minutes = Integer.valueOf(registeredHours.substring(registeredHours.indexOf(",") + 1, registeredHours.length()));
				} else if (registeredHours.length() == 0) {
					hours = 00;
					minutes = 00;
				} else {
					hours = Integer.valueOf(registeredHours.substring(registeredHours.lastIndexOf(" ") + 1, registeredHours.length()));
					minutes = 00;
				}

				final Dialog dialog = new Dialog(Registration.this, R.style.dialog);
				dialog.setContentView(R.layout.dialog);

				final TimePicker timePicker = (TimePicker) dialog.findViewById(R.id.timePicker);
				TimePicker.OnTimeChangedListener timeChangedListener = new TimePicker.OnTimeChangedListener() {

					public void onTimeChanged(TimePicker timePicker, int hourOfDay, int minute) {

						int nextMinute = 0;

						if (minute >= 45 && minute <= 59)
							nextMinute = 45;
						else if (minute >= 30)
							nextMinute = 30;
						else if (minute >= 15)
							nextMinute = 15;
						else if (minute > 0)
							nextMinute = 0;
						else {
							nextMinute = 0;
						}

						if (minute - nextMinute == 1) {
							if (minute >= 45 && minute <= 59)
								nextMinute = 00;
							else if (minute >= 30)
								nextMinute = 45;
							else if (minute >= 15)
								nextMinute = 30;
							else if (minute > 0)
								nextMinute = 15;
							else {
								nextMinute = 15;
							}
						}
						
					
						
						timePicker.setCurrentMinute(nextMinute);
					}
				};
				int shownMinutes = 0;
				if (minutes == 75)
					shownMinutes = 45;
				else if (minutes == 50)
					shownMinutes = 30;
				else if (minutes == 25)
					shownMinutes = 15;
				
				timePicker.setIs24HourView(true);
				timePicker.setCurrentHour(hours);
				timePicker.setCurrentMinute(shownMinutes);
				timePicker.setOnTimeChangedListener(timeChangedListener);

				Button setButton = (Button) dialog.findViewById(R.id.set_btn);
				setButton.setOnClickListener(new OnClickListener() {

					public void onClick(View v) {
						String hours = getResources().getString(R.string.hours);
						String minute = "0";
						if (timePicker.getCurrentMinute() == 15)
							minute = "25";
						else if (timePicker.getCurrentMinute() == 30)
							minute = "50";
						else if (timePicker.getCurrentMinute() == 45)
							minute = "75";

						registeredHours = String.valueOf(timePicker.getCurrentHour()) + "," + minute;
						SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
						String regHoursShow = "";
						try {
							regHoursShow = sdf.format(sdf.parse(String.valueOf(timePicker.getCurrentHour()) + ":"
									+ String.valueOf(timePicker.getCurrentMinute())));
						} catch (ParseException e) {

							e.printStackTrace();
						}
						subtitle.setText(regHoursShow + " " + hours);
						captionValue.remove("Content");
						captionValue.put("Content", regHoursShow);
						updateMap.put(key, registeredHours);
						dialog.dismiss();
					}
				});

				Button cancelButton = (Button) dialog.findViewById(R.id.cancel_button);
				cancelButton.setOnClickListener(new OnClickListener() {

					public void onClick(View v) {

						dialog.dismiss();
					}
				});

				dialog.show();

				break;
			} else if (metaData.Caption.equals(caption) && !metaData.HasF4 && metaData.Tooltip.equals("Activity Type")) {

				String[] items = getResources().getStringArray(R.array.activity_type);
				showSearchDropDown(items, getResources().getString(R.string.activity_type_prompt), caption, key);

				break;

			} else if (metaData.Caption.equals(caption) && !metaData.HasF4) {

				subtitle.setFocusableInTouchMode(true);
				subtitle.requestFocus();

				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.showSoftInput(subtitle, InputMethodManager.SHOW_FORCED);

				subtitle.requestFocus();

				subtitle.setOnEditorActionListener(new OnEditorActionListener() {

					public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
						if (actionId == EditorInfo.IME_ACTION_DONE) {

							captionValue.remove(caption);
							captionValue.put(caption, subtitle.getText().toString());
							updateMap.put(key, subtitle.getText().toString());

							InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
							imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
							subtitle.setFocusable(false);
							
							nextTimeSheet = new TimeSheetData();
							nextTimeSheet.Value = subtitle.getText().toString();
							nextTimeSheet.RowNumber = row;
							nextTimeSheet.RecordId = 0;
							nextTimeSheet.ColumnNumber = key;
							timeSheetDataList.add(nextTimeSheet);
							
							return true;
						}
						return false;
					}
				});

				final View rootView = findViewById(R.id.rootView);
				rootView.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
					public void onGlobalLayout() {
						int heightDiff = rootView.getRootView().getHeight() - rootView.getHeight();
						Button send = (Button) findViewById(R.id.send_btn);
						Button delete = (Button) findViewById(R.id.delete_btn);

						if (heightDiff < 100) {
							subtitle.setFocusable(false);
							send.setVisibility(View.VISIBLE);
							delete.setVisibility(View.VISIBLE);
						} else {
							subtitle.setFocusableInTouchMode(true);
							subtitle.requestFocus();
							send = (Button) findViewById(R.id.send_btn);
							delete = (Button) findViewById(R.id.delete_btn);
							send.setVisibility(View.GONE);
							delete.setVisibility(View.GONE);
						}
					}
				});
				
				

				break;

			} else if (metaData.Caption.equals(caption) && metaData.HasF4) {

				Intent intent = new Intent(Registration.this, SearchHelp.class);
				intent.putExtra("caption", metaData.Caption);
				startActivityForResult(intent, 1);

				break;
			}

		}
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		Intent intent;
		
		if (bundle.getString("newRegistration") != null) {
			intent = new Intent(this, CalendarView.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			intent.putExtra("calendar", calendar);
		
		} else {
			intent = new Intent(this, RegistrationManager.class);
			intent.putExtra("calendar", calendar);
		
		}
		if (timeSheetDataList != null){
			timeSheetDataList.remove(timeSheet);
			timeSheetDataList.remove(nextTimeSheet);
		}

		captionValue.clear();
		finish();
		startActivity(intent);
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (resultCode == RESULT_OK) {
			String value = data.getStringExtra("selectedItem");
			resultCaption = data.getStringExtra("Caption");
			if (resultCaption.equals("Currency"))
				resultCaption = "Trans. Currency";
			else if (resultCaption.equals("Oper./Act."))
				resultCaption = "Oper.Act.";
			
			if (timeSheetDataList == null)
				timeSheetDataList = new ArrayList<TimeSheetData>();

			HashMap<String, String> tecNameValue = (HashMap<String, String>) data.getSerializableExtra("tecNameValue");

			if (readObjectFromFile(getApplicationContext(), resultCaption) == null) {
				tecNameValueList = new LinkedList<HashMap<String, String>>();
				// captionTecNameValue = new HashMap<String,
				// List<HashMap<String, String>>>();
			} else
				tecNameValueList = (List<HashMap<String, String>>) readObjectFromFile(getApplicationContext(), resultCaption);

			if (tecNameValueList != null && tecNameValue != null)
				tecNameValueList.add(tecNameValue);

			// captionTecNameValue.put(resultCaption, tecNameValueList);

			captionValue.remove(resultCaption);
			captionValue.put(resultCaption, value);
			updateMap.put(key, value);
			timeSheet = new TimeSheetData();
			timeSheet.Value = value;
			timeSheet.RowNumber = row;
			timeSheet.RecordId = 0;
			timeSheet.ColumnNumber = key;
			for (int i = 0;i < timeSheetDataList.size();i++){
				TimeSheetData sheetDataObject = timeSheetDataList.get(i);
				if (sheetDataObject.ColumnNumber > timeSheet.ColumnNumber){
					timeSheetDataList.add(i, timeSheet);
					break;
				}	
			}
			
			adapter.notifyDataSetChanged();
		}
	}

	private class AsyncUpdateRecord extends AsyncTask<Registration, Void, String> {
		public ProgressDialog dialog;
		String updateMsg = "";
		UpdateRecord updateRecord;
		String type = "";
		boolean newRegistration, updateRegistration;
		SoapManager manager = new SoapManager(Registration.this);
		Map<Integer, PropertyInfo> map = new HashMap<Integer, PropertyInfo>();

		protected void onPreExecute() {

			String pleaseWait = getResources().getString(R.string.please_wait);
			dialog = ProgressDialog.show(Registration.this, "", pleaseWait, true, false);
			dialog.setCancelable(false);
			dialog.setCanceledOnTouchOutside(false);

		}

		@Override
		protected String doInBackground(Registration... params) {
			String result = "";
			int k = 0;

			try {

				Integer[] keys = updateMap.keySet().toArray(new Integer[updateMap.size()]);
				Arrays.sort(keys, new Comparator<Integer>() {
					public int compare(Integer integer1, Integer integer2) {

						return integer1.compareTo(integer2);
					}
				});
				
				
				if (updateMap.size() != 0 && timeSheetDataList != null && bundle.getString("newRegistration") == null) {
					newRegistration = false;
					updateRegistration = true;

					for (int i = 0; i < timeSheetDataList.size(); i++) {

					timeSheetData = timeSheetDataList.get(i);
					
					

						if (timeSheetData.ColumnNumber == keys[k] && timeSheetData.RowNumber == row) {
							
							if (timeSheetData.RecordId != 0) {
								if (Float.valueOf(registeredHours.replace(",", ".")) + totalHours - Float.valueOf(timeSheetData.Value) > 24)
									updateMsg = getResources().getString(R.string.validation);
							}
							
								
							map = manager.getUpdateTimeSheetDataMap(date, timeSheetData.RowNumber, timeSheetData.ColumnNumber,
									updateMap.get(timeSheetData.ColumnNumber), timeSheetData.RecordId);
							if (keys.length > 1 && k != keys.length - 1)
								k++;
						}
					}
					
					if (updateMsg.length() == 0){
						updateRecord();
						type = updateRecord.Type;
					} else {
						ArrayList<TimeSheetData> data = manager.getTimeSheetDataForDay(DataExchange.getInstance().getUsername(), DataExchange.getInstance().getPassword(), date);
						DataExchange.getInstance().setTimeSheetData(data);
					}
				}

				else {
					int registrationsNumber = getIntent().getExtras().getInt("registrationsNumber");
					
					if (registeredHours.length() != 0){
						if (Float.valueOf(registeredHours.replace(",", ".")) + totalHours > 24){
							updateMsg = getResources().getString(R.string.validation);
							ArrayList<TimeSheetData> data = manager.getTimeSheetDataForDay(DataExchange.getInstance().getUsername(), DataExchange.getInstance().getPassword(), date);
							DataExchange.getInstance().setTimeSheetData(data);
						} else {
						
							if (updateMap.size() == 0) {
								map = manager.getNewRegistrationDataMap(date, registrationsNumber + 1, 0, "");
								newRegistration = true;
								updateRegistration = false;
								updateRecord();
								type = "X";
							} else {
		
								for (int i = 0; i < updateMap.size(); i++) {
									map = manager.getNewRegistrationDataMap(date, registrationsNumber + 1, keys[i], updateMap.get(keys[i]));
								}
								newRegistration = true;
								updateRegistration = false;
								updateRecord();
								type = updateRecord.Type;
							}
						}
					} else {
						updateMsg = getResources().getString(R.string.no_change);
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
				
				if (type.equals("I")) {
					Intent intent = new Intent(Registration.this, CalendarView.class);
					intent.setAction("action");
					intent.putExtra("key", Integer.valueOf(date.substring(date.lastIndexOf("-") + 1, date.length())));
					intent.putExtra("isNewRegistration", newRegistration);
					intent.putExtra("updateRegistration", updateRegistration);
					intent.putExtra("calendar", Calendar.getInstance());
					String value = registeredHours.replace(",", ".");
					intent.putExtra("regHours", Float.valueOf(value));
					
					intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					if (tecNameValueList != null) {
						if (tecNameValueList.size() > DataExchange.getInstance().getRecentlyUsedElements())
							tecNameValueList.remove(0);

						writeObjectToFile(getApplicationContext(), tecNameValueList, resultCaption);
					}
					alarmAlertUpdateDelete("", updateMsg, intent);
					dialog.dismiss();
				} else {
					if (timeSheetDataList != null)
						timeSheetDataList.remove(timeSheet);
					
					alarmAlertWithoutIntent("", updateMsg);
					dialog.dismiss();
				}
				updateMap.clear();
			}
		}

		private void updateRecord() throws Exception {
			updateRecord = manager.getUpdateTimeSheetData(DataExchange.getInstance().getUsername(), DataExchange.getInstance().getPassword(), map);
			ArrayList<TimeSheetData> data = new ArrayList<TimeSheetData>();
			data = manager.getTimeSheetDataForDay(DataExchange.getInstance().getUsername(), DataExchange.getInstance().getPassword(), date);
			DataExchange.getInstance().setTimeSheetData(data);
			updateMsg = updateRecord.Message;

		}
	}

	private class AsyncDeleteRecord extends AsyncTask<Registration, Void, String> {
		public ProgressDialog dialog;
		UpdateRecord updateRecord;
		String type = "";
		String deleteRecordMsg = "";

		protected void onPreExecute() {

			String pleaseWait = getResources().getString(R.string.please_wait);
			dialog = ProgressDialog.show(Registration.this, "", pleaseWait, true, false);
			dialog.setCancelable(false);
			dialog.setCanceledOnTouchOutside(false);

		}

		@Override
		protected String doInBackground(Registration... params) {
			String result = "";

			SoapManager manager = new SoapManager(Registration.this);
			try {
				updateRecord = manager.getDeleteRecordMsg(DataExchange.getInstance().getUsername(), DataExchange.getInstance().getPassword(),
						manager.getDeleteRecordMap(date, row));
				ArrayList<TimeSheetData> data = new ArrayList<TimeSheetData>();
				data = manager.getTimeSheetDataForDay(DataExchange.getInstance().getUsername(), DataExchange.getInstance().getPassword(), date);
				DataExchange.getInstance().setTimeSheetData(data);

				deleteRecordMsg = updateRecord.Message;
				type = updateRecord.Type;

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
				if (type.equals("X")) {
					alarmAlertWithoutIntent("", deleteRecordMsg);
					dialog.dismiss();
				} else {
					Intent intent = new Intent(Registration.this, CalendarView.class);
					intent.setAction("action");
					intent.putExtra("key", Integer.valueOf(date.substring(date.lastIndexOf("-") + 1, date.length())));
					intent.putExtra("isDelete", true);
					intent.putExtra("calendar", Calendar.getInstance());
			//		String value = timeSheetData.Value.replace(",", ".");
					intent.putExtra("regHours", Float.valueOf(registeredHours));
					intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					alarmAlertUpdateDelete("", deleteRecordMsg, intent);
					dialog.dismiss();
				}
			}
		}
	}

	private void confirmDelete() {
		String deleteTitle = getResources().getString(R.string.delete_title);
		String deleteMsg = getResources().getString(R.string.delete_msg);

		final AlertDialog alertDialog = new AlertDialog.Builder(this).create();
		alertDialog.setTitle(deleteTitle);
		alertDialog.setMessage(deleteMsg);
		alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				AsyncDeleteRecord deleteRecord = new AsyncDeleteRecord();
				deleteRecord.execute(Registration.this);
			}
		});

		alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				alertDialog.dismiss();
			}
		});
		alertDialog.setIcon(R.drawable.logo);
		alertDialog.show();
	}

	private void showSearchDropDown(final String[] items, String prompt, final String caption, final Integer key) {

		ArrayAdapter<String> dialogAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, items);
		List<String> list = Arrays.asList(items);
		int selection = 0;

		for (int j = 0; j < list.size(); j++) {
			boolean flag = false;
			String string = list.get(j);
			if (string.contains(subtitle.getText().toString())) {

				selection = j;
				flag = true;
			}
			if (flag)
				break;
		}
		new AlertDialog.Builder(this).setTitle(prompt).setSingleChoiceItems(dialogAdapter, selection, new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface dialog, int which) {

				attAbsType = items[which];
				captionValue.remove(caption);
				captionValue.put(caption, attAbsType);
				updateMap.put(key, attAbsType);

				dialog.dismiss();

				subtitle.setText(attAbsType);
			}
		}).create().show();
	}

	public static void writeObjectToFile(Context context, Object object, String filename) {

		ObjectOutputStream objectOut = null;
		try {

			FileOutputStream fileOut = context.openFileOutput(filename, Activity.MODE_PRIVATE);
			objectOut = new ObjectOutputStream(fileOut);
			objectOut.writeObject(object);
			fileOut.getFD().sync();

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (objectOut != null) {
				try {
					objectOut.close();
				} catch (IOException e) {

				}
			}
		}
	}
}