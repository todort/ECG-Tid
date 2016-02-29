package com.evry.ecgtid;

import java.util.ArrayList;
import java.util.Map;

import android.widget.ArrayAdapter;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

public class RegistrationAdapter extends ArrayAdapter<String> {

	private final Activity context;
	private final ArrayList<String> captions;
	Map<String, String> captionValue;
	int i = 0;

	static class ViewHolder {
		public TextView title;
		public EditText subtitle;
	}

	public RegistrationAdapter(Activity context, ArrayList<String> captions, Map<String, String> captionValue) {
		super(context, R.layout.registration_item, captions);
		this.context = context;
		this.captions = captions;
		this.captionValue = captionValue;

	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View rowView = convertView;
		if (rowView == null) {
			LayoutInflater inflater = context.getLayoutInflater();
			rowView = inflater.inflate(R.layout.registration_item, null);
			ViewHolder viewHolder = new ViewHolder();
			viewHolder.title = (TextView) rowView.findViewById(R.id.list_item_title);
			viewHolder.subtitle = (EditText) rowView.findViewById(R.id.subtitle);
			rowView.setTag(viewHolder);
		}

		ViewHolder holder = (ViewHolder) rowView.getTag();

		String s = "", s1 = "";
		String hours = context.getResources().getString(R.string.hours);

		s = captions.get(position);
		if (s.equals("Time Spend"))
			s = "Content";

		s1 = captionValue.get(s);
		if (s1 == null)
			holder.subtitle.setHint(R.string.none);
		
		
		if (s.equals("Content")){
			s = context.getResources().getString(R.string.time_spend);
			if (s1 == null)
				holder.subtitle.setHint(R.string.none);
			else
				s1 = s1 + " " + hours;
		}
		
		holder.title.setText(s);
		holder.subtitle.setText(s1);
		

		if (holder.subtitle.isFocusable())
			holder.subtitle.setSelection(holder.subtitle.getText().length());
		
		return rowView;
	}
}
