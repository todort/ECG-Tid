package com.evry.ecgtid.objects;

import java.util.Hashtable;

import org.ksoap2.serialization.KvmSerializable;
import org.ksoap2.serialization.PropertyInfo;

public class SearchHelpSeriliazer implements KvmSerializable {

	public String TecName;
	public String Value;
	

	SearchHelpSeriliazer(){}
	
	public SearchHelpSeriliazer(String tecName, String value){

		TecName = tecName;
		Value = value;
	}
	
	public Object getProperty(int arg0) {
		
		switch(arg0){
		
		case 0:
			return TecName;
			
		case 1:
			return Value;
		
		}
		
		return null;
	}

	public int getPropertyCount() {
		
		return 2;
	}

	public void getPropertyInfo(int index, @SuppressWarnings("rawtypes") Hashtable arg1, PropertyInfo info) {
		
		switch(index){
		
		case 0:
			info.type = PropertyInfo.STRING_CLASS;
			info.name = "TecName";
		break;
			
		case 1:
			info.type = PropertyInfo.STRING_CLASS;
			info.name = "Value";
		break;
		
		default:break;
		}
		
	}

	public void setProperty(int index, Object value) {
		
		switch(index){
		
		case 0:
			TecName = value.toString();
		break;
		
		case 1:
			Value = value.toString();
		break;
		
		default:break;
		}
		
	}
}
