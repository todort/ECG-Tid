package com.evry.ecgtid.objects;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import org.ksoap2.serialization.SoapObject;

import android.app.Activity;

/**
 * Ksoap2 for android - output parser This class parses an input soap message
 * 
 * @author tamas.beres@helloandroid com, akos.birtha@helloandroid com
 * 
 */
public class ResponseParser {

	/**
	 * Parses a single business object containing primitive types from the
	 * response
	 * 
	 * @param input
	 *            soap message, one element at a time
	 * @param theClass
	 *            your class object, that contains the same member names and
	 *            types for the response soap object
	 * @return the values parsed
	 * @throws NumberFormatException
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 * @throws ParseException 
	 */
	Activity _activity;

	/*public ResponseParser(Activity activity){
		_activity = activity;
	}*/
	
	@SuppressWarnings("rawtypes")
	public static void parseBusinessObject(String input, Object output, boolean flag) throws NumberFormatException, IllegalArgumentException, IllegalAccessException,
			InstantiationException, ParseException {

		Class theClass = output.getClass();
		Field[] fields = theClass.getDeclaredFields();

		for (int i = 0; i < fields.length; i++) {
			Type type = fields[i].getType();
			fields[i].setAccessible(true);

			// detect String
			if (fields[i].getType().equals(String.class)) {
				String tag = fields[i].getName() + "="; // "s" is for String in
				// the above soap
				// response example +
				// field name for
				// example Name =
				// "sName"
				if (input.contains(tag)) {
					String strValue = input.substring(input.indexOf(tag) + tag.length(), input.indexOf(";", input.indexOf(tag)));
					if (tag.equals("Value=") && flag)
						strValue = strValue.substring(strValue.lastIndexOf(" ") + 1, strValue.length());
					if (strValue.length() != 0) {
						if (strValue.equalsIgnoreCase("anyType{}"))
							fields[i].set(output, null);
						else
							fields[i].set(output, strValue);
					}
				}
			}

			// detect int or Integer
			if (type.equals(Integer.TYPE) || type.equals(Integer.class)) {
				String tag = fields[i].getName() + "="; // "i" is for Integer or
				// int in the above soap
				// response example+
				// field name for
				// example Goals =
				// "iGoals"
				if (input.contains(tag)) {
					String strValue = input.substring(input.indexOf(tag) + tag.length(), input.indexOf(";", input.indexOf(tag)));
					if (strValue.length() != 0) {
						fields[i].setInt(output, Integer.valueOf(strValue));
					}
				}
			}

			// detect float or Float
			if (type.equals(Float.TYPE) || type.equals(Float.class)) {
				String tag = fields[i].getName() + "=";
				if (input.contains(tag)) {
					String strValue = input.substring(input.indexOf(tag) + tag.length(), input.indexOf(";", input.indexOf(tag)));
					strValue = strValue.replace(",", ".");
					if (strValue.length() != 0) {
						fields[i].setFloat(output, Float.valueOf(strValue));
					}
				}
			}

			if (type.equals(Boolean.TYPE) || type.equals(Boolean.class)) {
				String tag = fields[i].getName() + "=";
				if (input.contains(tag)) {
					String strValue = input.substring(input.indexOf(tag) + tag.length(), input.indexOf(";", input.indexOf(tag)));
					if (strValue.length() != 0) {
						if (strValue.equals("1"))
							fields[i].setBoolean(output, true);
						else
							fields[i].setBoolean(output, false);
					}
				}
			}
			if (type.equals(java.util.Date.class)) { 
				
				String tag = fields[i].getName() + "=";
				if (input.contains(tag)) {
					String strValue = input.substring(input.indexOf(tag) + tag.length(), input.indexOf(";", input.indexOf(tag)));
					if (strValue.length() != 0) {
						
						SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
						fields[i].set(output, sdf.parse(strValue));
					}
				}
			}
		}
	}

	public ArrayList<MetaData> parseMetaData(SoapObject response) throws Exception {
		ArrayList<MetaData> list = new ArrayList<MetaData>();
	//	MetaData metaData = new MetaData();
	//	MetaData F4 = new MetaData();
	//	MetaData resulTab = new MetaData();
        SoapObject so = (SoapObject) response.getProperty(0);
        
    	
    	//ResponseParser.parseBusinessObject(so.getProperty(0).toString(), metaData);
      /*  SoapObject full = null;
    	try{
	    	full = (SoapObject) so.getProperty("item");
    	}catch(RuntimeException e){
    		SoapObject errorSo = (SoapObject) response.getProperty(1);
            SoapError soapError = new SoapError();
            
            parseBusinessObject(errorSo.getProperty("item").toString(), soapError);
            throw new Exception(soapError.Message,e);
    		
    	}*/

		for (int i = 0; i < so.getPropertyCount(); i++) {
			MetaData metaData = new MetaData();
			parseBusinessObject(so.getProperty(i).toString(), metaData, false);
			SoapObject SO = (SoapObject) so.getProperty(i);
			
			
			SoapObject F4SrchCrit = (SoapObject) SO.getProperty("F4SrchCrit");
	    	SoapObject ResultTabCols = (SoapObject) SO.getProperty("ResultTabCols");
			
			if (metaData.HasF4){
				for (int j = 0; j < F4SrchCrit.getPropertyCount(); j++) {
					MetaData F4 = new MetaData();
		    		parseBusinessObject(F4SrchCrit.getProperty(j).toString(), F4, false);
		        	metaData.F4SrchCrit.add(F4);
				}
		    	
		    	for (int k = 0; k < ResultTabCols.getPropertyCount(); k++) {
		    		MetaData resulTab = new MetaData();
		    		parseBusinessObject(ResultTabCols.getProperty(k).toString(), resulTab, false);
		        	metaData.ResultTabCols.add(resulTab);
		        	
				}
			}
			
			list.add(metaData);
		}
		return list;
	}

	public ArrayList<Date> parseHeader(SoapObject response) throws NumberFormatException, IllegalArgumentException, IllegalAccessException,
			InstantiationException, ParseException {
		ArrayList<Date> list = new ArrayList<Date>();
		
		
		
		
		SoapObject res = (SoapObject) response.getProperty(0);
		
		for (int i = 0; i < res.getPropertyCount(); i++) {
			Header recordings = new Header();
			ResponseParser.parseBusinessObject(res.getProperty(i).toString(), recordings, false);
		//	String day = recordings.Date.substring(recordings.Date.lastIndexOf("-") + 1, recordings.Date.length());
		//	days.add(day);
			list.add(recordings.Date);
			
			

		}
		
		return list;
	}

	public ArrayList<TimeSheetData> parseTimeSheetData(SoapObject response) throws NumberFormatException, IllegalArgumentException, IllegalAccessException,
			InstantiationException, ParseException {
		ArrayList<TimeSheetData> list = new ArrayList<TimeSheetData>();
		SoapObject dataRes = (SoapObject) response.getProperty(1);

		for (int j = 0; j < dataRes.getPropertyCount(); j++) {
			TimeSheetData timeSheetData = new TimeSheetData();
			ResponseParser.parseBusinessObject(dataRes.getProperty(j).toString(), timeSheetData, true);

			list.add(timeSheetData);

		}
		return list;
	}
	
	/*public String parseUpdateTimeSheetMsg(SoapObject Response) throws NumberFormatException, IllegalArgumentException, IllegalAccessException,
	InstantiationException, ParseException {
		String result = "";
		StringBuilder sb = new StringBuilder();
		SoapObject response = (SoapObject) Response.getProperty(0);
		for (int i = 0; i < response.getPropertyCount(); i++) {

			UpdateRecord updateRecord = new UpdateRecord();
			ResponseParser.parseBusinessObject(response.getProperty(i).toString(), updateRecord, false);
			
			sb.append(updateRecord.Message + ". ");
		}
		result = sb.toString();

		
		return result;
	}*/
	
	public UpdateRecord parseUpdateDeleteTimeSheetMsg(SoapObject Response) throws NumberFormatException, IllegalArgumentException, IllegalAccessException,
	InstantiationException, ParseException {
		
		
		SoapObject response = (SoapObject) Response.getProperty(0);
		UpdateRecord updateRecord = null;
		for (int i = 0; i < response.getPropertyCount(); i++) {

			updateRecord = new UpdateRecord();
			ResponseParser.parseBusinessObject(response.getProperty(i).toString(), updateRecord, false);
			
			
		}

		return updateRecord;
	}
	
	public float parseWeekTotal(SoapObject Response) throws NumberFormatException, IllegalArgumentException, IllegalAccessException,
	InstantiationException, ParseException {
		
		WeeklyTotal weekTotal = new WeeklyTotal();
		ResponseParser.parseBusinessObject(Response.toString(), weekTotal, false);

		return weekTotal.EvWeeklyTotal;
	}
	
	public ArrayList<SearchHelpResults> parseSearchHelpResults(SoapObject response) throws NumberFormatException, IllegalArgumentException, IllegalAccessException,
		InstantiationException, ParseException {
		
		
		ArrayList<SearchHelpResults> list = new ArrayList<SearchHelpResults>();
		SoapObject dataRes = (SoapObject) response.getProperty(0);

		for (int j = 0; j < dataRes.getPropertyCount(); j++) {
			SearchHelpResults searchHelp = new SearchHelpResults();
			ResponseParser.parseBusinessObject(dataRes.getProperty(j).toString(), searchHelp, false);
			
		
			list.add(searchHelp);
	
		
		}
		return list;
	}

}