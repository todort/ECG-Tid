package com.evry.ecgtid;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.ksoap2.serialization.PropertyInfo;
import org.xmlpull.v1.XmlPullParserException;

import com.evry.ecgtid.objects.MetaData;
import com.evry.ecgtid.objects.SearchHelpResults;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
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
import android.widget.LinearLayout;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

public class SearchHelp extends MasterActivity implements OnClickListener, OnItemClickListener {
	
	Map<String, String> captionValue;
	LinkedList<String> captions;
	Map<String, String> captionTecName, captionTecNameResult;
	Map<String, String> tecNameCaption;
	HashMap<String, String> tecNameValue;
	Map<Integer, HashMap<String, String>> lineNumberTecNameValue;
	String caption;
	ArrayList<SearchHelpResults> searchHelpResultsList;
	ListView searchResultsListView, recentlyUsedListView;
	TextView searchResultsTitle, recentlyUsedTitle;
	int from = 0;
	int to = DataExchange.getInstance().getSearchResultsPerPage();
	List<LinearLayout> linearLayoutList, recentlyUsedList;
	int lineNumber = 1;
	boolean isShowMoreVisible;
	boolean firstClick;
	LinearLayout linearLayout;
	List<Map<Integer, HashMap<String, String>>> lineNumberTecNameValueAll;
	ArrayList<MetaData> resultTabCols;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.search_help);
	//	manager = CacheManager.create();
		
	/*	if (manager.getCache("Cache") == null){
			cache = new Cache(new CacheConfiguration("Cache", DataExchange.getInstance().getRecentlyUsedElements())
			 .diskStorePath(getStoragePath())
			 .overflowToDisk(true)
			 .eternal(true)
			 .diskPersistent(true)
			 .memoryStoreEvictionPolicy(MemoryStoreEvictionPolicy.LFU));
		
		manager.addCache(cache);
		}*/

		Bundle bundle = getIntent().getExtras();
		TextView headerView = (TextView) findViewById(R.id.companyName);
		caption = bundle.getString("caption");
		headerView.setText(caption);
		Button searchBtn = (Button) findViewById(R.id.search_btn);
		searchBtn.setOnClickListener(this);
		searchResultsTitle = (TextView) findViewById(R.id.searchResultsTitle);
		
		linearLayoutList = new ArrayList<LinearLayout>();
		recentlyUsedList = new ArrayList<LinearLayout>();

		ArrayList<MetaData> metaDataList = new ArrayList<MetaData>();
		ArrayList<MetaData> f4SrchCritList = new ArrayList<MetaData>();
		resultTabCols = new ArrayList<MetaData>();
		captions = new LinkedList<String>();
		captionValue = new HashMap<String, String>();
		captionTecName = new LinkedHashMap<String, String>();
		captionTecNameResult = new LinkedHashMap<String, String>();
		tecNameCaption = new HashMap<String, String>();
	//	searchHelpResultsList = new ArrayList<SearchHelpResults>();
		tecNameValue = new HashMap<String, String>();
		lineNumberTecNameValue = new HashMap<Integer, HashMap<String,String>>();
		lineNumberTecNameValueAll = new ArrayList<Map<Integer,HashMap<String,String>>>();
		metaDataList = DataExchange.getInstance().getMetaData();
		MetaData metaData = new MetaData();
		boolean flag = false;
		searchResultsListView = (ListView) findViewById(R.id.searchResultsListView);
		
		
		
		for (int i = 0; i < metaDataList.size(); i++) {

			metaData = metaDataList.get(i);
			if (metaData.Caption.equalsIgnoreCase(caption)) {

				f4SrchCritList = metaData.F4SrchCrit;
				resultTabCols = metaData.ResultTabCols;
				
				if (metaData.Caption.equals("Oper./Act."))
					captionTecName.put("Oper.Act.", metaData.TecName);
				else
					captionTecName.put(metaData.Caption, metaData.TecName);

				flag = true;
			}
			if (flag)
				break;
		}

		for (int i = 0; i < f4SrchCritList.size(); i++) {
			MetaData meta_data = new MetaData();
			meta_data = f4SrchCritList.get(i);
			captions.add(meta_data.Caption);
			captionValue.put(meta_data.Caption, "");
			
			
		}
		
		for (int i = 0; i < resultTabCols.size(); i++) {
			MetaData meta_data = new MetaData();
			meta_data = resultTabCols.get(i);
			captionTecNameResult.put(meta_data.Caption, meta_data.TecName);
			tecNameCaption.put(meta_data.TecName, meta_data.Caption);
			
		}
		
		if (caption.equals("Oper./Act."))
			caption = "Oper.Act.";
		
		@SuppressWarnings("unchecked")
		final
		List<HashMap<String, String>> tecNameValueList = (List<HashMap<String, String>>) readObjectFromFile(getApplicationContext(), caption);
		List<LinearLayout> list = new ArrayList<LinearLayout>();
		recentlyUsedTitle = (TextView) findViewById(R.id.recentlyUsedTitle);
		
		
			
			if (tecNameValueList != null){
				searchResultsListView.setVisibility(View.GONE);
				recentlyUsedTitle.setText(getResources().getString(R.string.recently_used) + " " + tecNameValueList.size());
				
				for(int i = 0;i < tecNameValueList.size();i++){
					buildLayout(tecNameValueList.get(i));
					list.add(linearLayout);
				}
			}
		
		
		
		
	//	Element element = manager.getCache("Cache").get(caption);
		
	//	if (list == null)
	//		list = new ArrayList<CustomLinearLayout>();
	//	if (element != null){  
	//		list = (List<CustomLinearLayout>) element.getObjectValue();
	//	}
		
		SearchHelpAdapter adapter = new SearchHelpAdapter(this, captions, captionValue);
		SearchHelpResultsAdapter recentlyUsedAdapter = new SearchHelpResultsAdapter(list);

		ListView searchCritListView = (ListView) findViewById(R.id.searchCritListView);
		recentlyUsedListView = (ListView) findViewById(R.id.recentlyUsedListView);
		
		searchCritListView.setDivider(getResources().getDrawable(R.drawable.separator));
		
		
		searchResultsListView.setDivider(getResources().getDrawable(R.drawable.separator));

		searchCritListView.setAdapter(adapter);
		searchCritListView.setOnItemClickListener(this);
		recentlyUsedListView.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				
				 tecNameValue = tecNameValueList.get(position);
				 if (caption.equals("Trans. Currency"))
					 caption = "Currency";
				 String value = tecNameValue.get(captionTecNameResult.get(caption));
			     Intent returnIntent = new Intent();
			     returnIntent.putExtra("selectedItem", value);
			     returnIntent.putExtra("Caption", caption);
			     setResult(RESULT_OK, returnIntent); 
			     

			     finish();
			}
			
		});
		recentlyUsedListView.setAdapter(recentlyUsedAdapter);
	}
	
	

	@Override
	protected void onDestroy() {
		super.onDestroy();
//		cache.flush();
	}

	


 
    

	public void onClick(View v) {

		from = 0;
		to = DataExchange.getInstance().getSearchResultsPerPage();
		lineNumber = 1;
		linearLayoutList.clear();
		firstClick = true;
		recentlyUsedListView.setVisibility(View.GONE);
		searchResultsListView.setVisibility(View.VISIBLE);
		searchResultsTitle.setVisibility(View.VISIBLE);
		recentlyUsedTitle.setVisibility(View.GONE);
		new AsyncSearchHelp().execute(SearchHelp.this);
	}

	private class AsyncSearchHelp extends AsyncTask<SearchHelp, Void, String> {
		public ProgressDialog dialog;
		

		protected void onPreExecute() {

			String refreshMsg = getResources().getString(R.string.refresh_msg);
			dialog = ProgressDialog.show(SearchHelp.this, "", refreshMsg, true, false);
			dialog.setCancelable(false);
			dialog.setCanceledOnTouchOutside(false);

		}

		@Override
		protected String doInBackground(SearchHelp... params) {
			
			String result = "";
			Map<Integer, PropertyInfo> map = new HashMap<Integer, PropertyInfo>();
			

			try {
				SoapManager manager = new SoapManager(SearchHelp.this);
				
					for (int i = 0;i < captionValue.size();i++){
						if (captionValue.get(captions.get(i)).length() > 0) {
							map =  manager.getSearchHelpMap(captionTecName.get(captions.get(i)), captionValue.get(captions.get(i)), captionTecName.get(caption), from, to);
	
						}else {
							map =  manager.getSearchHelpMap("", "", captionTecName.get(caption), from, to);
						}
						
					}
				
				
				searchHelpResultsList = new ArrayList<SearchHelpResults>();
			//	lineNumberTecNameValue = new HashMap<Integer, HashMap<String,String>>();
				
				searchHelpResultsList = manager.getSearchHelpResults(DataExchange.getInstance().getUsername(), DataExchange.getInstance().getPassword(), map);
			//	if (searchHelpResultsList.size() == 0)
			//		result = getResources().getString(R.string.locked);
				
				for (int i = 0; i < searchHelpResultsList.size(); i++) {
					SearchHelpResults searchHelpResults = new SearchHelpResults();
					searchHelpResults = searchHelpResultsList.get(i);
					if (searchHelpResults.LineNumber == lineNumber) {
						tecNameValue = new HashMap<String, String>();
						lineNumber++;
					}
					
					tecNameValue.put(searchHelpResults.TecName, searchHelpResults.Value);
					lineNumberTecNameValue.put(searchHelpResults.LineNumber, tecNameValue);
				}
			//	lineNumberTecNameValueAll.add(lineNumberTecNameValue);
				
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
						} else if (t instanceof RuntimeException) {
							Log.e("SearchHelp", e.toString());
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
			//	manager.getCache("Cache").flush();
				
			} else if (result.equals("XmlPullParserException")) {
				String wrongUserPassTitle = getResources().getString(R.string.wrong_user_pass_title);
				String wrongUserPassMsg = getResources().getString(R.string.wrong_user_pass_msg);
				alarmAlert(wrongUserPassTitle, wrongUserPassMsg);
			//	manager.getCache("Cache").flush();
				
			} else if (result.length() == 0) {
				
				
				
				
				
				//new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, listItems);
			//	LayoutInflater inflater = getLayoutInflater();
			//	View rowView = inflater.inflate(R.layout.search_help_results_item, null);
				
				
				
				
				Integer[] lineNumbers = lineNumberTecNameValue.keySet().toArray(new Integer[lineNumberTecNameValue.size()]);
				Arrays.sort(lineNumbers, new Comparator<Integer>() {
					public int compare(Integer integer1, Integer integer2) {

						return integer1.compareTo(integer2);
					}
				});
				
				for (int i = 0;i < lineNumberTecNameValue.size();i++){
					
					
					
					
				/*	LinearLayout containerLayout = new LinearLayout(SearchHelp.this);
					containerLayout.setOrientation(LinearLayout.VERTICAL);
					containerLayout.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
					containerLayout.removeAllViews();*/
					
					tecNameValue = lineNumberTecNameValue.get(lineNumbers[i]);
					
					buildLayout(tecNameValue);
					
					linearLayoutList.add(linearLayout);
				}
				TextView showMore = new TextView(SearchHelp.this);
				LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
				
				params.setMargins(0, 20, 0, 20);
				
				if (searchHelpResultsList.size() / resultTabCols.size() == DataExchange.getInstance().getSearchResultsPerPage()){
					
					LinearLayout linearLayout = new LinearLayout(SearchHelp.this);
					linearLayout.setLayoutParams(new ListView.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
					showMore.setLayoutParams(params);
					showMore.setGravity(Gravity.CENTER);
					showMore.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
					showMore.setText(getResources().getString(R.string.show_more));
					linearLayout.addView(showMore);
					linearLayoutList.add(linearLayout);
					isShowMoreVisible = true;
					
				} else {
					searchResultsTitle.setText(getResources().getString(R.string.search_results_title_all) + " " + String.valueOf(linearLayoutList.size()));
					isShowMoreVisible = false;
				}
			
				SearchHelpResultsAdapter resultsAdapter = new SearchHelpResultsAdapter(linearLayoutList);
				searchResultsListView.setAdapter(resultsAdapter);
				searchResultsListView.setSelection(from - 2);
				
				
				
				searchResultsListView.setOnItemClickListener(new OnItemClickListener() {

					public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
						
						if (position == searchResultsListView.getLastVisiblePosition() && isShowMoreVisible){

							from = linearLayoutList.size();
							to = from + DataExchange.getInstance().getSearchResultsPerPage() - 1;
							linearLayoutList.remove(linearLayoutList.size() - 1);
							firstClick = false;
							linearLayoutList.clear();
							new AsyncSearchHelp().execute(SearchHelp.this);
						} else {
						 if (caption.equals("Trans. Currency"))
						 	caption = "Currency";
						 
					//	 Serializer serializer = new Persister(new CustomMatcher());
						 
						 
					//	 if (!manager.getCache("Cache").isElementOnDisk(caption))
					//		 recentlyUsedList = new ArrayList<CustomLinearLayout>();
					//	Cache<String, Object> cache = CacheBuilder.newBuilder()
					//			.maximumSize(Credentials.getInstance().getRecentlyUsedElements())
					//			.build();
						 
					//	 recentlyUsedList.add(linearLayoutList.get(position));
						 
					//	 cache.put(caption, recentlyUsedList);
						 
						 
				//	     List<LinearLayout> list = (List<LinearLayout>) cache.get(caption);
					//     int o = 0;
					     
				//		 manager.getCache("Cache").put(new Element(caption, recentlyUsedList));
					     
					     
				/*		 try {
							 
							 
							 ByteArrayOutputStream bos = new ByteArrayOutputStream();
						     ObjectOutputStream obj_out = new ObjectOutputStream(bos);
						     obj_out.writeObject(recentlyUsed.linearLayoutList);
						     
						    byte[] encoded = Base64.encode(bos.toByteArray(), Base64.DEFAULT);
						    
						     
						     
						     ByteArrayInputStream bis = new ByteArrayInputStream(Base64.decode(encoded, Base64.DEFAULT));
						     ObjectInputStream obj_in = new ObjectInputStream(bis);
						     
						     
						     CustomLinearLayout[] list = (CustomLinearLayout[]) obj_in.readObject();
						     int l = 0;
						 } catch (IOException e) {
						         e.printStackTrace();
						 } catch (ClassNotFoundException e) {
							
							e.printStackTrace();
						}*/
						 
				/*		 File result = new File(getStoragePath() + caption + ".xml");

						 try {
							serializer.write(recentlyUsed.linearLayoutList, result);
						} catch (Exception e) {
							
							e.printStackTrace();
						}*/
						 tecNameValue = lineNumberTecNameValue.get(position + 1);
						 String value = tecNameValue.get(captionTecNameResult.get(caption));
					     Intent returnIntent = new Intent();
					     returnIntent.putExtra("selectedItem", value);
					     returnIntent.putExtra("Caption", caption);
					     returnIntent.putExtra("tecNameValue", tecNameValue);
					     setResult(RESULT_OK, returnIntent); 
					     
					    
					     
					     
					     finish();
						}
					}
				});
				dialog.dismiss();
				if (firstClick && isShowMoreVisible)
					searchResultsTitle.setText(getResources().getString(R.string.search_results_title_first) + " " + String.valueOf(DataExchange.getInstance().getSearchResultsPerPage()) + " " + getResources().getString(R.string.search_results_title_end));
				else if (isShowMoreVisible)
					searchResultsTitle.setText(getResources().getString(R.string.search_results_title_first) + " " + String.valueOf(linearLayoutList.size() - 1) + " " + getResources().getString(R.string.search_results_title_end));
			
			} else {
				alarmAlertWithoutIntent("", result);
				dialog.dismiss();
				
			}
		}
	}

	public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {

		final EditText subtitle = (EditText) view.findViewById(R.id.search_item_subtitle);
		TextView subtitleTextView = (TextView) view.findViewById(R.id.search_item_subtitle_text);
		subtitle.setVisibility(View.VISIBLE);
		subtitleTextView.setVisibility(View.GONE);
		subtitle.setFocusableInTouchMode(true);
		subtitle.requestFocus();
		
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.showSoftInput(subtitle, InputMethodManager.SHOW_IMPLICIT);
		
		subtitle.requestFocus();

		subtitle.setOnEditorActionListener(new OnEditorActionListener() {

			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_DONE) {

					
					InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
					imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
					captionValue.remove(captions.get(position));
					captionValue.put(captions.get(position), subtitle.getText().toString());				
					
					subtitle.setFocusable(false);
					return true;
				}
				return false;
			}
		});

		final View rootView = findViewById(R.id.searchRootView);
		rootView.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
			public void onGlobalLayout() {
				int heightDiff = rootView.getRootView().getHeight() - rootView.getHeight();
				Button send = (Button) findViewById(R.id.search_btn);
				
				
				if (heightDiff < 100) {
					subtitle.setFocusable(false);
					send.setVisibility(View.VISIBLE);
					searchResultsListView.setVisibility(View.VISIBLE);
					searchResultsTitle.setVisibility(View.VISIBLE);
				} else {
					
					subtitle.setFocusableInTouchMode(true);
					subtitle.requestFocus();
					send.setVisibility(View.GONE);
					searchResultsListView.setVisibility(View.GONE);
					searchResultsTitle.setVisibility(View.GONE);
				}
			}
		});

	
	}
	
	private void buildLayout(HashMap<String, String> tecNameValue) {

		String[] keys = tecNameValue.keySet().toArray(new String[tecNameValue.size()]);
		
		linearLayout = new LinearLayout(SearchHelp.this);
		linearLayout.setOrientation(LinearLayout.VERTICAL);
		linearLayout.setLayoutParams(new ListView.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
		linearLayout.setBackgroundResource(R.layout.list_item_states);
		linearLayout.removeAllViews();
		
		for (int j = 0;j < keys.length;j++){
			
			LinearLayout childsLayout = new LinearLayout(SearchHelp.this);
			childsLayout.setOrientation(LinearLayout.HORIZONTAL);
			childsLayout.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
			childsLayout.removeAllViews();
			
			TextView title = new TextView(SearchHelp.this);
			LinearLayout.LayoutParams paramsSubtitle = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
			LinearLayout.LayoutParams paramsTitle = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		
		
			TextView subtitle = new TextView(SearchHelp.this);
			title.setLayoutParams(paramsTitle);
			paramsSubtitle.setMargins(50, 0, 10, 0);
			paramsTitle.setMargins(10, 0, 0, 0);
	
			subtitle.setLayoutParams(paramsSubtitle);
			
			title.setText(tecNameCaption.get(keys[j]));
			subtitle.setText(tecNameValue.get(keys[j]));
			title.setTextColor(getResources().getColor(R.color.blueItem));
			title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
			
			subtitle.setTextColor(getResources().getColor(R.color.greenItem));
			subtitle.setGravity(Gravity.RIGHT);
			subtitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
			
			childsLayout.addView(title);
			childsLayout.addView(subtitle);
			//containerLayout.addView(childsLayout, k);
			linearLayout.addView(childsLayout);
			
		}

	}
	
	
}
