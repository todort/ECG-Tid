package com.evry.ecgtid.objects;

import java.util.Hashtable;

import org.ksoap2.serialization.KvmSerializable;
import org.ksoap2.serialization.PropertyInfo;

public class UpdateRecordSeriliazer implements KvmSerializable {
	
	public int RowNumber;
	public int ColumnNumber;
	public String Value;
	public int RecordId;
	

	UpdateRecordSeriliazer(){}
	
	public UpdateRecordSeriliazer(int rowNumber, int columnNumber, String value, int recordId){

		RowNumber = rowNumber;
		ColumnNumber = columnNumber;
		Value = value;
		RecordId = recordId;	
	}
	
	public Object getProperty(int arg0) {
		
		switch(arg0){
		
		case 0:
			return RowNumber;
			
		case 1:
			return ColumnNumber;
		
		case 2:
			return Value;
			
		case 3:
			return RecordId;
		
		}
		
		return null;
	}

	public int getPropertyCount() {
		
		return 4;
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
			
		case 3:
			info.type = PropertyInfo.INTEGER_CLASS;
			info.name = "RecordId";
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
		
		case 3:
			RecordId = Integer.parseInt(value.toString());
		break;
		
		default:break;
		}
		
	}

}
