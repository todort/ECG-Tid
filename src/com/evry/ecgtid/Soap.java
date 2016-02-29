package com.evry.ecgtid;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.kobjects.base64.Base64;
import org.ksoap2.HeaderProperty;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
import android.util.Log;

public class Soap {
	Activity _activity;

	Soap(Activity activity) {
		_activity = activity;
	}

	public SoapObject makeSOAPCall(String user, String pass, String methodName, Map<Integer, PropertyInfo> map) throws Exception {
		SoapObject request, so = null;

		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(_activity);

		String URL = prefs.getString("URL", "");
		String METHOD_NAME = methodName;
		String SOAP_ACTION = URL + "/" + METHOD_NAME;
		String NAMESPACE = "urn:sap-com:document:sap:soap:functions:mc-style";

		HttpTransportSE http = new HttpTransportSE(URL);

		http.debug = true;
		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
		request = new SoapObject(NAMESPACE, METHOD_NAME);

		for (int i = 0; i < map.size(); i++) {

			request.addProperty((PropertyInfo) map.get(i));
		}

		System.setProperty("proxyHost", "http://sapds4.teamr3.com");
		System.setProperty("proxyPort", "8080");

		envelope.setOutputSoapObject(request);
		http.setXmlVersionTag("<?xml version=\"1.0\" encoding= \"UTF-8\"?>");

		String login = Base64.encode((user + ":" + pass).getBytes());
		List<HeaderProperty> headerProperties = new LinkedList<HeaderProperty>();
		headerProperties.add(new HeaderProperty("Authorization", "Basic " + login));
		//headerProperties.add(new HeaderProperty("Defaults", "Logon Language " + "DA"));

		try {

			http.call(SOAP_ACTION, envelope, headerProperties);
			so = (SoapObject) envelope.bodyIn;

		} catch (IOException e) {
			if (e != null) {
				Log.e("Soap", e.toString());
				throw new Exception(e);
			}

		} catch (XmlPullParserException e) {
			if (e != null) {
				Log.e("Soap", e.toString());
				throw new Exception(e);
			}
		} catch (RuntimeException e) {
			if (e != null) {
				Log.e("Soap", e.toString());
				throw new Exception(e);
			}
		}
		return so;
	}

	public boolean isOnline() {
		ConnectivityManager cm = (ConnectivityManager) _activity.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = cm.getActiveNetworkInfo();
		if (netInfo != null && netInfo.isConnectedOrConnecting() && netInfo.isAvailable()) {
			return true;
		} else
			return false;
	}
}