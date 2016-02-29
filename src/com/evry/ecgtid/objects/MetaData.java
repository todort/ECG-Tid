package com.evry.ecgtid.objects;

import java.util.ArrayList;

public class MetaData {

	public String TecName;
	public String Caption;
	public String Tooltip;
	public String DataType;
	public boolean HasF4;
	public String ColType;
	public int Width;
	public int Length;
	public ArrayList<MetaData> F4SrchCrit = new ArrayList<MetaData>();
	public ArrayList<MetaData> ResultTabCols = new ArrayList<MetaData>();
}
