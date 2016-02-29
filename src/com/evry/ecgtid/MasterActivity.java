package com.evry.ecgtid;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

public class MasterActivity extends Activity {
    
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.menu_prefs, menu);
	    return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle item selection
	    switch (item.getItemId()) {
	    	
	    	case R.id.prefs:
	    		Intent intent = new Intent(this, SettingsScreen.class);
	    		startActivity(intent);
	    	break;
	    
	        default:
	            return super.onOptionsItemSelected(item);
	    }
		return true;
	}
	
	public void alarmAlert(String title, String msg){
		final AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setTitle(title);
        alertDialog.setMessage(msg);
        alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            	alertDialog.dismiss();
            	MasterActivity.this.finish();
            	Intent intent = new Intent(MasterActivity.this, LoginScreen.class);
                startActivity(intent);
            }
        });
        alertDialog.setIcon(R.drawable.logo);
        alertDialog.setCancelable(false);
        alertDialog.show();
        alertDialog.setCancelable(false);
        alertDialog.setCanceledOnTouchOutside(false);
       
	}
	
	public void alarmAlertUpdateDelete(String title, String msg, final Intent intent){
		final AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setTitle(title);
        alertDialog.setMessage(msg);
        alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            	alertDialog.dismiss();
            	MasterActivity.this.finish();
				startActivity(intent);	
            }
        });
        alertDialog.setIcon(R.drawable.logo);
        alertDialog.setCancelable(false);
        alertDialog.show();
        alertDialog.setCancelable(false);
        alertDialog.setCanceledOnTouchOutside(false);
       
	}
	
	public void alarmAlertWithoutIntent(String title, String msg){
		final AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setTitle(title);
        alertDialog.setMessage(msg);
        alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            	MasterActivity.this.finish();
            	alertDialog.dismiss();
            }
        });
        alertDialog.setIcon(R.drawable.logo);
        alertDialog.setCancelable(false);
        alertDialog.show();
        alertDialog.setCancelable(false);
        alertDialog.setCanceledOnTouchOutside(false);
       
	}
	
	public static Object readObjectFromFile(Context context, String filename) {

        ObjectInputStream objectIn = null;
        Object object = null;
        try {

            FileInputStream fileIn = context.getApplicationContext().openFileInput(filename);
            objectIn = new ObjectInputStream(fileIn);
            object = objectIn.readObject();

        } catch (FileNotFoundException e) {
            // Do nothing
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            if (objectIn != null) {
                try {
                    objectIn.close();
                } catch (IOException e) {
                    // do nowt
                }
            }
        }

        return object;
    }
}