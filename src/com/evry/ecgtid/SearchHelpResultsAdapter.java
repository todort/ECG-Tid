package com.evry.ecgtid;

import java.util.List;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;

public class SearchHelpResultsAdapter extends BaseAdapter {

	List<LinearLayout> linearLayoutList;

	

	public SearchHelpResultsAdapter(List<LinearLayout> linearLayoutList2) {
		
		
		this.linearLayoutList = linearLayoutList2;
		
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		View rowView = convertView;
	//	if (rowView == null) {
		
			rowView = (View) getItem(position);
			
	//	}
	
		return rowView;
	}

	public int getCount() {
		
		return linearLayoutList.size();
	}

	public Object getItem(int position) {
		
		return linearLayoutList.get(position);
	}

	public long getItemId(int position) {
		
		return position;
	}
}