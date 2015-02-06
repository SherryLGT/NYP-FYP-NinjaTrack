package controller;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;

public class Utility {
	
	public final static String FORMAT_DD_MMM_YYYY = "dd-MMM-yyyy";
	
	// Get Today's date
	public static GregorianCalendar getTodayDate() {
		GregorianCalendar cal = new GregorianCalendar();
		cal = parseDateFromIntegers(cal.get(Calendar.DAY_OF_MONTH), cal.get(Calendar.MONTH), cal.get(Calendar.YEAR));
		return cal;
	}

	// Convert String to GregorianCalendar
	public static GregorianCalendar parseDateFromString(String date, String format) {
		SimpleDateFormat dateFormat = new SimpleDateFormat(format, Locale.getDefault());
		GregorianCalendar cal = new GregorianCalendar();
		try {
			System.out.println("DATE : " + dateFormat.parse(date));
			cal.setTime(dateFormat.parse(date));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return cal;
	}
	
	// Convert Integers to GregorianCalendar
	public static GregorianCalendar parseDateFromIntegers(int day, int month, int year) {
		GregorianCalendar cal = new GregorianCalendar();
		cal.set(year, month, day);		
		return cal;
	}
	
	// Convert GregorianCalendar to String
	public static String parseDateToString(GregorianCalendar date, String format) {
		SimpleDateFormat dateFormat = new SimpleDateFormat(format, Locale.getDefault());
		return dateFormat.format(date.getTime());
	}
}
