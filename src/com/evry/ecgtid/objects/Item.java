package com.evry.ecgtid.objects;

import java.util.Hashtable;
import org.ksoap2.serialization.KvmSerializable;
import org.ksoap2.serialization.PropertyInfo;

public class Item implements KvmSerializable {
	
	public Object item;
	
	public Item(Object item){
		this.item = item;
	}

	public Item() {
		
	}

	public Object getProperty(int arg0) {
		
		switch(arg0){
		
		case 0:
			return item;
		}
		
		return null;
	}

	public int getPropertyCount() {
		
		return 1;
	}

	public void getPropertyInfo(int index, @SuppressWarnings("rawtypes") Hashtable arg1, PropertyInfo info) {
		
		switch(index){
		
		case 0:
			info.type = PropertyInfo.OBJECT_CLASS;
			info.name = "item";
		break;
		
		default:break;
		}
		
	}

	public void setProperty(int index, Object value) {
		
		switch(index){
		
		case 0:
			item = value;
		break;
		
		default:break;
		}
	}

	
}
