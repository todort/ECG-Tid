package com.evry.ecgtid;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import android.app.Activity;

import com.evry.ecgtid.objects.DeleteRecordSeriliazer;
import com.evry.ecgtid.objects.NewRegistrationSeriliazer;
import com.evry.ecgtid.objects.SearchHelpResults;
import com.evry.ecgtid.objects.SearchHelpSeriliazer;
import com.evry.ecgtid.objects.UpdateRecord;
import com.evry.ecgtid.objects.UpdateRecordSeriliazer;
import com.evry.ecgtid.objects.MetaData;
import com.evry.ecgtid.objects.ResponseParser;
import com.evry.ecgtid.objects.TimeSheetData;
import com.evry.ecgtid.objects.Item;

public class SoapManager {

	Soap soap;
	Activity _activity;
	ResponseParser responseParser = new ResponseParser();
	Vector<Object> list = new Vector<Object>();

	public SoapManager(Activity activity) {
		_activity = activity;
		soap = new Soap(_activity);
		
	}

	public Map<Integer, PropertyInfo> getMetaDataMap(String deviceID) {

		PropertyInfo pi0 = new PropertyInfo();
		pi0.setName("IvDevId");
		pi0.setValue(deviceID);

		PropertyInfo pi1 = new PropertyInfo();
		pi1.setName("IvForceupdate");
		pi1.setValue("X");

		Map<Integer, PropertyInfo> map = new HashMap<Integer, PropertyInfo>();
		map.put(0, pi0);
		map.put(1, pi1);

		return map;
	}

	public Map<Integer, PropertyInfo> getHeaderMap() {

		PropertyInfo pi0 = new PropertyInfo();
		pi0.setName("IvFromMonth");
		pi0.setValue(CalendarView.getCurrentMonth());

		PropertyInfo pi1 = new PropertyInfo();
		pi1.setName("IvFromYear");
		pi1.setValue(CalendarView.getCurrentYear());

		PropertyInfo pi2 = new PropertyInfo();
		pi2.setName("IvToMonth");
		pi2.setValue(CalendarView.getCurrentMonth());

		PropertyInfo pi3 = new PropertyInfo();
		pi3.setName("IvToYear");
		pi3.setValue(CalendarView.getCurrentYear());

		Map<Integer, PropertyInfo> map = new HashMap<Integer, PropertyInfo>();
		map.put(0, pi0);
		map.put(1, pi1);
		map.put(2, pi2);
		map.put(3, pi3);

		return map;
	}

	public Map<Integer, PropertyInfo> getTimeSheetDataMap(String day) {

		PropertyInfo pi0 = new PropertyInfo();
		pi0.setName("IvWorkdate");
		pi0.setValue(day);

		Map<Integer, PropertyInfo> map = new HashMap<Integer, PropertyInfo>();
		map.put(0, pi0);

		return map;
	}
	
	public Map<Integer, PropertyInfo> getUpdateTimeSheetDataMap(String day,int rowNumber, int columnNumber, String value, int recordId) {
		
		
		
		PropertyInfo pi0 = new PropertyInfo();
		pi0.setName("ItTimesheet");
		

		
	//	PropertyInfo pi000 = new PropertyInfo();
	//	pi000.setName("Value");
	//	pi000.setValue(value);
		
	//	PropertyInfo pi001 = new PropertyInfo();
	//	pi001.setName("RecordId");
	//	pi001.setValue(recordId);
	
		UpdateRecordSeriliazer update = new UpdateRecordSeriliazer(rowNumber, columnNumber, value, recordId);
		
		list.add(update);
		
		
		pi0.setValue(list);
	
		
		PropertyInfo pi1 = new PropertyInfo();
		pi1.setName("IvWorkdate");
		pi1.setValue(day);

		Map<Integer, PropertyInfo> map = new HashMap<Integer, PropertyInfo>();
		map.put(0, pi0);
		map.put(1, pi1);

		return map;
	}
	
	public Map<Integer, PropertyInfo> getNewRegistrationDataMap(String day,int rowNumber, int columnNumber, String value) {

		PropertyInfo pi0 = new PropertyInfo();
		pi0.setName("ItTimesheet");
	
		NewRegistrationSeriliazer newRegistration = new NewRegistrationSeriliazer(rowNumber, columnNumber, value);
		
		list.add(newRegistration);
		
		
		pi0.setValue(list);
	
		
		PropertyInfo pi1 = new PropertyInfo();
		pi1.setName("IvWorkdate");
		pi1.setValue(day);

		Map<Integer, PropertyInfo> map = new HashMap<Integer, PropertyInfo>();
		map.put(0, pi0);
		map.put(1, pi1);

		return map;
	}

	public Map<Integer, PropertyInfo> getDeleteRecordMap(String day, int rowNumber) {
		
		
	
		PropertyInfo pi0 = new PropertyInfo();
		pi0.setName("ItTimesheet");

		DeleteRecordSeriliazer delete = new DeleteRecordSeriliazer(rowNumber);
		Item item = new Item();
		item.item = delete;
		
		pi0.setValue(item);
		pi0.setType(item.getClass());
		
		PropertyInfo pi1 = new PropertyInfo();
		pi1.setName("IvWorkdate");
		pi1.setValue(day);

		Map<Integer, PropertyInfo> map = new HashMap<Integer, PropertyInfo>();
		map.put(0, pi0);
		map.put(1, pi1);

		return map;
	}
	
	public Map<Integer, PropertyInfo> getWeekTotalMap(String date) {

		PropertyInfo pi0 = new PropertyInfo();
		pi0.setName("IvWorkdate");
		pi0.setValue(date);

		Map<Integer, PropertyInfo> map = new HashMap<Integer, PropertyInfo>();
		map.put(0, pi0);

		return map;
	}
	
	public Map<Integer, PropertyInfo> getSearchHelpMap(String tecName, String value, String fieldName, int from, int to) {

		PropertyInfo pi0 = new PropertyInfo();
		pi0.setName("ItSearchCriteria");
	
		SearchHelpSeriliazer searchHelp = new SearchHelpSeriliazer(tecName, value);
		
		list.add(searchHelp);
		
		
		pi0.setValue(list);
	
		
		PropertyInfo pi1 = new PropertyInfo();
		pi1.setName("IvFieldname");
		pi1.setValue(fieldName);
		
		PropertyInfo pi2 = new PropertyInfo();
		pi2.setName("IvResultsFrom");
		pi2.setValue(from);
		
		PropertyInfo pi3 = new PropertyInfo();
		pi3.setName("IvResultsTo");
		pi3.setValue(to);

		Map<Integer, PropertyInfo> map = new HashMap<Integer, PropertyInfo>();
		map.put(0, pi0);
		map.put(1, pi1);
		map.put(2, pi2);
		map.put(3, pi3);

		return map;
	}
	
	/**
	 * @throws Exception
	 ************************************************************************************************************************/

	public ArrayList<MetaData> getMetaData(String user, String pass, Map<Integer, PropertyInfo> map) throws Exception {

		SoapObject response = soap.makeSOAPCall(user, pass, "ZcatsValidateprofile", map);

		//ecg.metaData = responseParser.parseMetaData(response);
		
		return responseParser.parseMetaData(response);
	}

	public ArrayList<Date> getHeader(String user, String pass, Map<Integer, PropertyInfo> map) throws Exception {

		SoapObject response = soap.makeSOAPCall(user, pass, "ZcatsGetHeader", map);

		// responseParser.parseHeader(response);

		return responseParser.parseHeader(response);
	}

	public List<Map<Integer, Object>> getTimeSheetDataForMonth(String user, String pass) throws Exception {

		Map<Integer, Object> date_WorkingHoursCollection = new HashMap<Integer, Object>();
		Map<Integer, Object> date_TimeSheetCollection = new HashMap<Integer, Object>();
		List<Map<Integer, Object>> list = new LinkedList<Map<Integer,Object>>();

		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		

		ArrayList<Date> headers = getHeader(DataExchange.getInstance().getUsername(), DataExchange.getInstance().getPassword(), getHeaderMap());
		

		for (int i = 0; i < headers.size(); i++) {
		//	ArrayList<Float> workingHours = new ArrayList<Float>();
			String currentDay = sdf.format(headers.get(i));
			SoapObject response = soap.makeSOAPCall(DataExchange.getInstance().getUsername(), DataExchange.getInstance().getPassword(), "ZcatsGetTimesheet",
					getTimeSheetDataMap(currentDay));
			ArrayList<TimeSheetData> timeSheetData = new ArrayList<TimeSheetData>();
			timeSheetData = responseParser.parseTimeSheetData(response);
			float count = 0;
			
			for (int j = 0; j < timeSheetData.size(); j++) {

				TimeSheetData sheetData = timeSheetData.get(j);
				if (sheetData.RecordId != 0)
					count += Float.valueOf(sheetData.Value.replace(",", "."));
			}
			
			date_WorkingHoursCollection.put(headers.get(i).getDate(), count);
			date_TimeSheetCollection.put(headers.get(i).getDate(), timeSheetData);
			
		}
		list.add(date_TimeSheetCollection);
		list.add(date_WorkingHoursCollection);
		
		return list;
	}
	
	public ArrayList<TimeSheetData> getTimeSheetDataForDay(String user, String pass, String date) throws Exception {

		
		
		SoapObject response = soap.makeSOAPCall(DataExchange.getInstance().getUsername(), DataExchange.getInstance().getPassword(), "ZcatsGetTimesheet",
					getTimeSheetDataMap(date));
			
		ArrayList<TimeSheetData> timeSheetDataList = new ArrayList<TimeSheetData>();
		timeSheetDataList = responseParser.parseTimeSheetData(response);

		return timeSheetDataList;
	}
	
	public UpdateRecord getUpdateTimeSheetData(String user, String pass, Map<Integer, PropertyInfo> map) throws Exception {

		SoapObject response = soap.makeSOAPCall(user, pass, "ZcatsUpdateTimesheet", map);

		return responseParser.parseUpdateDeleteTimeSheetMsg(response);
	}
	
	public UpdateRecord getDeleteRecordMsg(String user, String pass, Map<Integer, PropertyInfo> map) throws Exception {

		SoapObject response = soap.makeSOAPCall(user, pass, "ZcatsDeleteRecord", map);

		return responseParser.parseUpdateDeleteTimeSheetMsg(response);
	}
	
	public float getWeekTotal(String user, String pass, Map<Integer, PropertyInfo> map) throws Exception {

		SoapObject response = soap.makeSOAPCall(user, pass, "ZcatsGetWeeklyTotal", map);

		return responseParser.parseWeekTotal(response);
	}
	
	public ArrayList<SearchHelpResults> getSearchHelpResults(String user, String pass, Map<Integer, PropertyInfo> map) throws Exception {

		SoapObject response = soap.makeSOAPCall(user, pass, "ZcatsSrchHelp", map);
	//	String serverError = responseParser.parseUpdateTimeSheetMsg(response, 1);
	//	if (serverError.length() > 0)
	//		throw new RuntimeException(serverError);
		
		return responseParser.parseSearchHelpResults(response);
	}
}