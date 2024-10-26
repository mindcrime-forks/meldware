package org.buni.meldware.webmail;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class UTCDateFormat {

	private SimpleDateFormat parser = new SimpleDateFormat("yyyyMMdd'T'HHmmssZ");
	private static int DATE_LENGHT= (4+2+2+1+2+2+2+1);
	
	public Date parse(String dateString) throws ParseException
	{
		if(dateString.length()!=DATE_LENGHT)
			throw new ParseException("Date is not expected size:" + DATE_LENGHT,dateString.length());
		// cheat to force the TZ
		String gmtFormated = dateString.replace("Z", "-0000");
		return parser.parse(gmtFormated);
	}
	
	public String format(Date date)
	{
		NumberFormat twoDigit = new DecimalFormat("00");
		NumberFormat fourDigit = new DecimalFormat("0000");
		String result = "";
		TimeZone tz = TimeZone.getTimeZone("UTC");
		Calendar calendar = Calendar.getInstance(tz);
		calendar.setTime(date);
		result+=fourDigit.format(calendar.get(Calendar.YEAR));
		result+=twoDigit.format(calendar.get(Calendar.MONTH)+1);
		result+=twoDigit.format(calendar.get(Calendar.DATE));
		result+="T";
		result+=twoDigit.format(calendar.get(Calendar.HOUR_OF_DAY));
		result+=twoDigit.format(calendar.get(Calendar.MINUTE));
		result+=twoDigit.format(calendar.get(Calendar.SECOND))+"Z";
		return result;
	}
}
