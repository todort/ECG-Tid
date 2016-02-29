package com.evry.ecgtid;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.content.res.Configuration;

import com.evry.ecgtid.objects.MetaData;
import com.evry.ecgtid.objects.TimeSheetData;

public class Credentials {
	
	private static Credentials instance;
	private String username;
	private String password;
	
	private ArrayList<MetaData> metaData;
	private ArrayList<String> headers;
	private ArrayList<TimeSheetData> timeSheetData;
	Map<String, String> captionValue;
	Map<Integer, Object> date_WorkingHoursCollection, date_TimeSheetCollection;
	Map<String, Object> monthCollection = new HashMap<String, Object>();
	int recentlyUsedElements, searchResults;
	
	private Configuration config;
	//private Map<String, ArrayList<Float>> collection;
	
	private Credentials(){
	}
	
	public static Credentials getInstance() {
		if(instance == null)
			instance = new Credentials();
		return instance;
	}
	
	public void setUsername(String username) {this.username = username;}
	public String getUsername() {return username;}
	
	public void setPassword(String password) {this.password = password;}
	public String getPassword() {return password;}
	
	public void setMetaData(ArrayList<MetaData> metaData) {this.metaData = metaData;}
	public ArrayList<MetaData> getMetaData() {return metaData;}
	
	public void setHeader(ArrayList<String> headers) {this.headers = headers;}
	public ArrayList<String> getHeader() {return headers;}
	
	public void setTimeSheetData(ArrayList<TimeSheetData> timeSheetData) {this.timeSheetData = timeSheetData;}
	public ArrayList<TimeSheetData> getTimeSheetData() {return timeSheetData;}
	
	public void setConfiguration(Configuration config) {this.config = config;}
	public Configuration getConfiguration() {return config;}
	
	public void setCaptionValueMap(Map<String, String> captionValue) {this.captionValue = captionValue;}
	public Map<String, String> getCaptionValueMap() {return captionValue;}
	
	public void setDate_WorkingHoursCollection(Map<Integer, Object> date_WorkingHoursCollection) {this.date_WorkingHoursCollection = date_WorkingHoursCollection;}
	public Map<Integer, Object> getDate_WorkingHoursCollection() {return date_WorkingHoursCollection;}
	
	public void setDate_TimeSheetCollection(Map<Integer, Object> date_TimeSheetCollection) {this.date_TimeSheetCollection = date_TimeSheetCollection;}
	public Map<Integer, Object> getDate_TimeSheetCollection() {return date_TimeSheetCollection;}
	
	public void setMonthCollection(Map<String, Object> monthCollection) {this.monthCollection = monthCollection;}
	public Map<String, Object> getMonthCollection() {return monthCollection;}
	
	public void setRecentlyUsedElements(int recentlyUsedElements) {this.recentlyUsedElements = recentlyUsedElements;}
	public int getRecentlyUsedElements() {return recentlyUsedElements;}
	
	public void setSearchResultsPerPage(int searchResults) {this.searchResults = searchResults;}
	public int getSearchResultsPerPage() {return searchResults;}
	
}