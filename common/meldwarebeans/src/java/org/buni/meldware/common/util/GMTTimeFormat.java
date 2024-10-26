package org.buni.meldware.common.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

public class GMTTimeFormat {

	private DateFormat dateFormat = null; 
	private static final GMTTimeFormat instance = new GMTTimeFormat();
	
	private GMTTimeFormat(){
		dateFormat = new SimpleDateFormat("yyyyMMdd'T'HHmmss'Z'");
		dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
	}
	
	public static final DateFormat getFormat()
	{
		return GMTTimeFormat.instance.dateFormat;
	}
}
