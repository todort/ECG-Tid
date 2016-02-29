package com.evry.ecgtid;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.BaseAdapter;
import android.widget.TextView;

public class CalendarAdapter extends BaseAdapter {
	static final int FIRST_DAY_OF_WEEK = 0; // Sunday = 0, Monday = 1

	private Context mContext;
	public String[] days;
	private java.util.Calendar calendar;
	private Calendar selectedDate;
	private Map<Integer, Object> dateValuePair;
	Activity _activity;
	static String currentDay;
	boolean currentMonth, _notCurrentYear;
	int i = 0;

	public CalendarAdapter(Context c, Calendar calendarCalendar, Map<Integer, Object> date_WorkingHoursCollection, boolean currentMonth, boolean notCurrentYear) {
		calendar = calendarCalendar;
		selectedDate = (Calendar) calendarCalendar.clone();
		mContext = c;
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		dateValuePair = new HashMap<Integer, Object>();
		dateValuePair = date_WorkingHoursCollection;
		this.currentMonth = currentMonth;
		_notCurrentYear = notCurrentYear;
		refreshDays();
	}

	public int getCount() {
		return days.length;
	}

	public Object getItem(int position) {
		return null;
	}

	public long getItemId(int position) {
		return 0;
	}

	// create a new view for each item referenced by the Adapter
	public View getView(int position, View convertView, ViewGroup parent) {
		View v = convertView;
		TextView dayView;
		

		if (convertView == null) { // if it's not recycled, initialize some
									// attributes
			LayoutInflater vi = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = vi.inflate(R.layout.calendar_item, null);

		}
		dayView = (TextView) v.findViewById(R.id.date);

		// create date string for comparison
		String date = days[position];

		// disable empty days from the beginning
		if (days[position].equals("")) {
			dayView.setClickable(false);
			dayView.setFocusable(false);
		}

		// mark current day as focused
		if (calendar.get(Calendar.YEAR) == selectedDate.get(Calendar.YEAR) && calendar.get(Calendar.MONTH) == selectedDate.get(Calendar.MONTH)
				&& days[position].equals("" + selectedDate.get(Calendar.DAY_OF_MONTH)) && !currentMonth) {
			v.setBackgroundResource(R.drawable.calendar_circle);
		} else {
			v.setBackgroundResource(R.layout.calendar_item_none_states);
		}
		
		if (_notCurrentYear)
			v.setBackgroundResource(R.layout.calendar_item_none_states);

		dayView.setText(days[position]);

		

		// show icon if date is not empty and it exists in the items array
		TextView registrationsNumberView = (TextView) v.findViewById(R.id.registrationsNumber);
	
		int parseDate = 0;
		if (date.length() > 0) {
			parseDate = Integer.parseInt(date);
		}

		if (date.length() > 0 && dateValuePair.containsKey(parseDate)) {

			registrationsNumberView.setVisibility(View.VISIBLE);
			
			float registrationsNumber = (Float) dateValuePair.get(parseDate);
			registrationsNumberView.setText(String.valueOf(registrationsNumber).substring(0, String.valueOf(registrationsNumber).lastIndexOf(".")));
			if (v.getBackground().getClass().getName().equals("android.graphics.drawable.BitmapDrawable"))
				v.setBackgroundResource(R.drawable.calendar_circle);
			else
				v.setBackgroundResource(R.layout.calendar_item_states);
		}
		
		return v;
	}

	public void refreshDays() {
		// clear items
	//	items.clear();

		int lastDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
		int firstDay = (int) calendar.get(Calendar.DAY_OF_WEEK);

		// figure size of the array
		if (firstDay == 1) {
			days = new String[lastDay + (FIRST_DAY_OF_WEEK * 6)];
		} else {
			days = new String[lastDay + firstDay - (FIRST_DAY_OF_WEEK + 1)];
		}

		int j = FIRST_DAY_OF_WEEK;

		// populate empty days before first real day
		if (firstDay > 1) {
			for (j = 0; j < firstDay - FIRST_DAY_OF_WEEK; j++) {
				days[j] = "";
			}
		} else {
			for (j = 0; j < FIRST_DAY_OF_WEEK * 6; j++) {
				days[j] = "";
			}
			j = FIRST_DAY_OF_WEEK * 6 + 1; // sunday => 1, monday => 7
		}

		// populate days
		int dayNumber = 1;
		for (int i = j - 1; i < days.length; i++) {
			days[i] = "" + dayNumber;
			dayNumber++;
		}
	}

	public static String getCurrentDay() {

		return currentDay;
	}
}