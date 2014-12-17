package controller;

import model.Profile;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SQLiteController {
	private static final String database_name = "NinjaTrackDB.db";
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
	
	private static class DBHelper extends SQLiteOpenHelper {
		public DBHelper(Context context) {
			super(context, database_name, null, database_version);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			// Create Profile table
			db.execSQL("CREATE TABLE Profile (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT NOT NULL, age TEXT NOT NULL, contact_no INTEGER NOT NULL, email TEXT NOT NULL, start_date TEXT NOT NULL, image TEXT)");
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			db.execSQL("DROP TABLE IF EXISTS " + database_name);
			onCreate(db);
		}
	}
	
	public void createProfile(Profile profile) {
		ContentValues cv = new ContentValues();
		cv.put("name", profile.getName());
		cv.put("age", Utility.parseDateToString(profile.getAge(), Utility.FORMAT_DD_MMM_YYYY));
		cv.put("contact_no", profile.getContactNo());
		cv.put("email", profile.getEmail());
		cv.put("start_date", Utility.parseDateToString(Utility.getTodayDate(), Utility.FORMAT_DD_MMM_YYYY));
		cv.put("image", profile.getImage());
		
		database.insert("Profile", null, cv);
	}
	
	public Profile retrieveProfile() {
		Profile profile = new Profile();
		
		Cursor cursor = database.query("Profile", new String[] {"id", "name", "age", "contact_no", "email", "start_date", "image"}, null, null, null, null, null, null);
		if(cursor != null) {
			if(cursor.moveToFirst()){
				profile.setId(cursor.getInt(cursor.getColumnIndex("id")));
				profile.setName(cursor.getString(cursor.getColumnIndex("name")));
				profile.setAge(Utility.parseDateFromString(cursor.getString(cursor.getColumnIndex("age")), Utility.FORMAT_DD_MMM_YYYY));
				profile.setContactNo(cursor.getInt(cursor.getColumnIndex("contact_no")));
				profile.setEmail(cursor.getString(cursor.getColumnIndex("email")));
				profile.setStartDate(Utility.parseDateFromString(cursor.getString(cursor.getColumnIndex("start_date")), Utility.FORMAT_DD_MMM_YYYY));
				profile.setImage(cursor.getString(cursor.getColumnIndex("image")));
			}
		}
		
		return profile;
	}
	
	public void updateProfile(Profile profile) {
		ContentValues cv = new ContentValues();
		cv.put("name", profile.getName());
		cv.put("age", Utility.parseDateToString(profile.getAge(), Utility.FORMAT_DD_MMM_YYYY));
		cv.put("contact_no", profile.getContactNo());
		cv.put("email", profile.getEmail());
		cv.put("start_date", Utility.parseDateToString(profile.getStartDate(), Utility.FORMAT_DD_MMM_YYYY));
		cv.put("image", profile.getImage());
		
		database.update("Profile", cv, "id= " + profile.getId(), null);
	}
}
