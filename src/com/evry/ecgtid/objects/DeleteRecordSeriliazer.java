package com.evry.ecgtid.objects;

import java.util.Hashtable;

import org.ksoap2.serialization.KvmSerializable;
import org.ksoap2.serialization.PropertyInfo;

public class DeleteRecordSeriliazer implements KvmSerializable {
	
	public int RowNumber;
	
	

	DeleteRecordSeriliazer(){}
	
	public DeleteRecordSeriliazer(int rowNumber){

		RowNumber = rowNumber;
		
	}
	
	public Object getProperty(int arg0) {
		
		switch(arg0){
		
		case 0:
			return RowNumber;
			
		
		
		}
		
		return null;
	}

	public int getPropertyCount() {
		
		return 1;
	}

	public void getPropertyInfo(int index, @SuppressWarnings("rawtypes") Hashtable arg1, PropertyInfo info) {
		
		switch(index){
		
		case 0:
			info.type = PropertyInfo.INTEGER_CLASS;
			info.name = "RowNumber";
		break;
			
		
		default:break;
		}
		
	}

	public void setProperty(int index, Object value) {
		
		switch(index){
		
		case 0:
			RowNumber = Integer.parseInt(value.toString());
		break;
		
		
		
		default:break;
		}
		
	}

}
