package com.evry.ecgtid;

import java.util.LinkedHashMap;
import java.util.Map;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class RegistrationManagerAdapter extends BaseAdapter {

	private  Activity context;

	Map<Integer, Map<String,String>> rowNumber_captionValue;
	String[] keys;
	int i = 0;

	static class ViewHolder {
		public TextView title;
		public TextView subtitle;
		public TextView title2;
		public TextView subtitle2;
	//	public TextView title3;
	//	public TextView subtitle3;
	}

	public RegistrationManagerAdapter(Activity context, Map<Integer, Map<String,String>> rowNumber_captionValue) {
		super();
		
		this.context = context;
		this.rowNumber_captionValue = rowNumber_captionValue;
		
	}
	
	public int getCount() {
		
		return rowNumber_captionValue.size();
	}

	public Object getItem(int position) {
		
		return rowNumber_captionValue.get(position);
	}

	public long getItemId(int position) {
		
		return position;
	}

	@SuppressWarnings("unchecked")
	public View getView(int position, View convertView, ViewGroup parent) {
		View rowView = convertView;
		if (rowView == null) {
			LayoutInflater inflater = context.getLayoutInflater();
			rowView = inflater.inflate(R.layout.registration_manager_item, null);
			ViewHolder viewHolder = new ViewHolder();
			viewHolder.title = (TextView) rowView.findViewById(R.id.registration_manager_title);
			viewHolder.subtitle = (TextView) rowView.findViewById(R.id.registration_manager_subtitle);
			viewHolder.title2 = (TextView) rowView.findViewById(R.id.registration_manager_title2);
			viewHolder.subtitle2 = (TextView) rowView.findViewById(R.id.registration_manager_subtitle2);
	//		viewHolder.title3 = (TextView) rowView.findViewById(R.id.registration_manager_title3);
	//		viewHolder.subtitle3 = (TextView) rowView.findViewById(R.id.registration_manager_subtitle3);
			rowView.setTag(viewHolder);
		}

		ViewHolder holder = (ViewHolder) rowView.getTag();
		
		
		Map<String, String> captionValue = new LinkedHashMap<String, String>();

		captionValue = (Map<String, String>) getItem(position + 1);
		keys = captionValue.keySet().toArray(new String[captionValue.size()]);
		
		String title = null, title2 = null, subtitle = null, subtitle2 = null;
		String hours = context.getResources().getString(R.string.hours);
		String wbsElement = context.getResources().getString(R.string.wbs_element);
		
	/*	if (keys.length == 1){
			title = keys[0];
			subtitle = captionValue.get(title);
			if (title.equals("Content")){
				title = context.getResources().getString(R.string.time_spend);
				subtitle = subtitle + " " + hours;
			}
	
			
		}else if (keys.length == 2){
			title = keys[0];
			subtitle = captionValue.get(title);
			title2 = keys[1];
			subtitle2 = captionValue.get(title2);
			if (title2.equals("Content")){
				title2 = context.getResources().getString(R.string.time_spend);
				subtitle2 = subtitle2 + " " + hours;
			}
			
		}*/  /*else if (keys.length > 2){	
			title = keys[0];
			subtitle = captionValue.get(title);
			title2 = keys[1];
			subtitle2 = captionValue.get(title2);
			title3 = keys[2];
			subtitle3 = captionValue.get(title3);
			if (title3.equals("Content")){
				title3 = context.getResources().getString(R.string.time_spend);
				subtitle3 = subtitle3 + " " + hours;
			}
		}*/
		for (int i = 0;i < keys.length;i++){
			
			title = keys[i];
			if (title.equals("Content") || title.equals("Indhold"))
				break;
		}
		
		for (int i = 0;i < keys.length;i++){
			
			title2 = keys[i];
			if (title2.equals("WBS Element") || title2.equals("PSP-element"))
				break;
			else
				title2 = wbsElement;
		}
			
			subtitle = captionValue.get(title);
			subtitle2 = captionValue.get(title2);
			
			if (title.equalsIgnoreCase("Content") || title.equalsIgnoreCase("Indhold")) {
				title = context.getResources().getString(R.string.time_spend);
				subtitle = subtitle + " " + hours;
				if (subtitle2 == null)
					subtitle2 = context.getResources().getString(R.string.none);	
				holder.title.setText(title2);
				holder.subtitle.setText(subtitle2);						
				holder.title2.setText(title);
				holder.subtitle2.setText(subtitle);
			}

		return rowView;
	}

	
}
