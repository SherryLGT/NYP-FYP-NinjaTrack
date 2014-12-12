package controller;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class SQLiteController {
	private static final String database_name = "NinjaTrackDB";
	private static final int database_version = 1;
	
	private DBHelper helper;
	private final Context context;
	private SQLiteDatabase database;
	
	public SQLiteController(Context context) {
		this.context = context;
	}
	
	public SQLiteController open() throws SQLException {
		helper = new DBHelper(context);
		database = helper.getWritableDatabase();
		
		return this;
	}
	
	public void close() {
		helper.close();
	}

	public static String setDateTime(GregorianCalendar date) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
		return dateFormat.format(date);
	}

	public static GregorianCalendar parseDateTime(String date) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
		GregorianCalendar cal = new GregorianCalendar();
		try {
			cal.setTime(dateFormat.parse(date));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return cal;
	}
	
	private static class DBHelper extends SQLiteOpenHelper {
		public DBHelper(Context context) {
			super(context, database_name, null, database_version);
			Log.d(null, "Created");
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			// Create Profile table
			db.execSQL("CREATE TABLE Profile (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT NOT NULL, age DATE NOT NULL, contact_no INTEGER NOT NULL, email TEXT NOT NULL, start_date DATE NOT NULL)");
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			db.execSQL("DROP TABLE IF EXISTS " + database_name);
			onCreate(db);
		}
	}
}
