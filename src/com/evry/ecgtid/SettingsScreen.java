package com.evry.ecgtid;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Locale;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
 
public class SettingsScreen extends PreferenceActivity implements OnSharedPreferenceChangeListener, OnPreferenceChangeListener {
	private String _url;
	
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
        setContentView(R.layout.settings);
        ListPreference languagePreference = (ListPreference) findPreference("Languages");
        languagePreference.setOnPreferenceChangeListener(this);
        Configuration config = DataExchange.getInstance().getConfiguration();
        
        if (config.locale.toString().equals("da"))
        	languagePreference.setValue("Dansk");
        else
        	languagePreference.setValue("English");
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        _url = sp.getString("URL", null);
        sp.registerOnSharedPreferenceChangeListener(this);
    }
    
    @Override
    protected void onPause() {
        super.onPause();
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        sp.unregisterOnSharedPreferenceChangeListener(this);
    }
    
    public void onSharedPreferenceChanged(SharedPreferences sp, String key) {
    	
        if (key.equalsIgnoreCase("URL"))
        {
            String urlString = sp.getString(key, null);

            try {
                URL url = new URL(urlString);
                _url = url.toString();
            
            } catch (MalformedURLException e) {
                sp.unregisterOnSharedPreferenceChangeListener(this);
                SharedPreferences.Editor editor = sp.edit();
                editor.putString("URL", _url);
                editor.commit();
                sp.registerOnSharedPreferenceChangeListener(this);
                
                String alertURL = getResources().getString(R.string.alert_url);
                String alertURLTitle = getResources().getString(R.string.alert_url_title);
                
                AlertDialog alertDialog = new AlertDialog.Builder(this).create();
                alertDialog.setTitle(alertURLTitle);
                alertDialog.setMessage(alertURL);
                alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                alertDialog.setIcon(R.drawable.ecg);
                alertDialog.show();
            }  
        }
         if (key.equalsIgnoreCase("Username"))
        {
            String username = sp.getString(key, "");
            DataExchange.getInstance().setUsername(username);
        }
        else if (key.equalsIgnoreCase("Password"))
        {
            String password = sp.getString(key, "");
            DataExchange.getInstance().setPassword(password);
        }
        else if (key.equalsIgnoreCase("CapacityRecentlyUsed"))
        {
        	String recentlyUsed = sp.getString(key, "");
        	int recentlyUsedElements = Integer.parseInt(recentlyUsed.substring(0, recentlyUsed.indexOf(" ")));
            DataExchange.getInstance().setRecentlyUsedElements(recentlyUsedElements);
        }
        else if (key.equalsIgnoreCase("CapacitySearchResults"))
        {
        	String searchResults = sp.getString(key, "");
        	int searchResultsPerPage = Integer.parseInt(searchResults.substring(0, searchResults.indexOf(" ")));
            DataExchange.getInstance().setSearchResultsPerPage(searchResultsPerPage);
        }
    }

	public boolean onPreferenceChange(Preference preference, Object newValue) {
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
		
    	if (newValue.toString().equals("English")){
    		Configuration c = new Configuration(getResources().getConfiguration());
    		c.locale = Locale.ENGLISH;
    		getResources().updateConfiguration(c, getResources().getDisplayMetrics());
    		DataExchange.getInstance().setConfiguration(c);
    		sp.edit().putString("en", "en").commit();
    		preference.setDefaultValue(newValue);
    		Intent intent = getBaseContext().getPackageManager()
    	             .getLaunchIntentForPackage( getBaseContext().getPackageName() );
    		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
    		startActivity(intent);
    		
    	}else if (newValue.toString().equals("Dansk")){
    		Configuration c = new Configuration(getResources().getConfiguration());
    		c.locale =  new Locale("da");
    		getResources().updateConfiguration(c, getResources().getDisplayMetrics());
    		DataExchange.getInstance().setConfiguration(c);
    		preference.setDefaultValue(newValue);
    		sp.edit().putString("da", "da").commit();
    		Intent intent = getBaseContext().getPackageManager()
    	             .getLaunchIntentForPackage( getBaseContext().getPackageName() );
    		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	    	startActivity(intent);
    	}
		return true;
	}
    
}