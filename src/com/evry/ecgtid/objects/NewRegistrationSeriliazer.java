package com.evry.ecgtid.objects;

import java.util.Hashtable;

import org.ksoap2.serialization.KvmSerializable;
import org.ksoap2.serialization.PropertyInfo;

public class NewRegistrationSeriliazer implements KvmSerializable {

	public int RowNumber;
	public int ColumnNumber;
	public String Value;
	public int RecordId;
	

	NewRegistrationSeriliazer(){}
	
	public NewRegistrationSeriliazer(int rowNumber, int columnNumber, String value){

		RowNumber = rowNumber;
		ColumnNumber = columnNumber;
		Value = value;
	}
	
	public Object getProperty(int arg0) {
		
		switch(arg0){
		
		case 0:
			return RowNumber;
			
		case 1:
			return ColumnNumber;
		
		case 2:
			return Value;
		
		}
		
		return null;
	}

	public int getPropertyCount() {
		
		return 3;
	}

	public void getPropertyInfo(int index, @SuppressWarnings("rawtypes") Hashtable arg1, PropertyInfo info) {
		
		switch(index){
		
		case 0:
			info.type = PropertyInfo.INTEGER_CLASS;
			info.name = "RowNumber";
		break;
			
		case 1:
			info.type = PropertyInfo.INTEGER_CLASS;
			info.name = "ColumnNumber";
		break;
		
		case 2:
			info.type = PropertyInfo.STRING_CLASS;
			info.name = "Value";
		break;
			
		default:break;
		}
		
	}

	public void setProperty(int index, Object value) {
		
		switch(index){
		
		case 0:
			RowNumber = Integer.parseInt(value.toString());
		break;
		
		case 1:
			ColumnNumber = Integer.parseInt(value.toString());
		break;
		
		case 2:
			Value = value.toString();
		break;
	
		default:break;
		}
		
	}

}


