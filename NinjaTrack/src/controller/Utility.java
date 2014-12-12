package controller;

import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;
import java.util.Locale;

public class Utility {

	private static String getDateTime(GregorianCalendar date) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault());
		return dateFormat.format(date);
	}
}
