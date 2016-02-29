package com.evry.ecgtid;

import java.util.LinkedList;
import java.util.Map;

import android.widget.ArrayAdapter;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class SearchHelpAdapter extends ArrayAdapter<String> {

	private Activity context;
	private LinkedList<String> captions;
	Map<String, String> captionValue;

	static class ViewHolder {
		public TextView title;
		public TextView subtitle;
	}

	public SearchHelpAdapter(Activity context, LinkedList<String> captions, Map<String, String> captionValue) {
		super(context, R.layout.search_help_item, captions);
		this.context = context;
		this.captions = captions;
		this.captionValue = captionValue;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View rowView = convertView;
		if (rowView == null) {
			LayoutInflater inflater = context.getLayoutInflater();
			rowView = inflater.inflate(R.layout.search_help_item, null);
			ViewHolder viewHolder = new ViewHolder();
			viewHolder.title = (TextView) rowView.findViewById(R.id.search_item_title);
			viewHolder.subtitle = (TextView) rowView.findViewById(R.id.search_item_subtitle_text);
			rowView.setTag(viewHolder);
		}

		ViewHolder holder = (ViewHolder) rowView.getTag();
		
		String s = captions.get(position);
		
		
			
			
				
		holder.title.setText(s);
		holder.subtitle.setText(captionValue.get(captions.get(position)));
	
	//	if (holder.subtitle.isFocusable())
	//		holder.subtitle.setSelection(holder.subtitle.getText().length());	
		
		return rowView;
	}
}
