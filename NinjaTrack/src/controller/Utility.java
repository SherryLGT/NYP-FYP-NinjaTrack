package controller;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;
import java.util.Locale;

public class Utility {
	
	public final static String FORMAT_DD_MMM_YYYY = "dd-MMM-yyyy";

	// Convert String to GregorianCalendar
	public static GregorianCalendar parseDateFromString(String date, String format) {
		SimpleDateFormat dateFormat = new SimpleDateFormat(format, Locale.getDefault());
		GregorianCalendar cal = new GregorianCalendar();
		try {
			cal.setTime(dateFormat.parse(date));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return cal;
	}
	
	// Convert GregorianCalendar to String
	public static String parseDateToString(GregorianCalendar date, String format) {
		SimpleDateFormat dateFormat = new SimpleDateFormat(format, Locale.getDefault());
		return dateFormat.format(date.getTime());
	}
	
	// Convert integers to GregorianCalendar
	public static GregorianCalendar parseDateFromIntegers(int day, int month, int year) {
		GregorianCalendar cal = new GregorianCalendar();
		cal.set(year, month, day);		
		return cal;
	}
}
