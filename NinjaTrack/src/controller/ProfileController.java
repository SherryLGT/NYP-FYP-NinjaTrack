package controller;

import model.Profile;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class ProfileController {
	
	private SQLiteDatabase database;
	private String database_table = "Profile";
	
	public Profile retrieveProfile() {
		Profile user = new Profile();
		
		Cursor cursor = database.query(database_table, new String[] {"id", "name", "age", "contact_no", "email", "start_date"}, null, null, null, null, null, null);
		if(cursor != null) {
			user.setId(cursor.getInt(cursor.getColumnIndex("id")));
			user.setName(cursor.getString(cursor.getColumnIndex("name")));
			user.setAge(SQLiteController.parseDateTime(cursor.getString(cursor.getColumnIndex("age"))));
			user.setContactNo(cursor.getInt(cursor.getColumnIndex("contact_no")));
			user.setEmail(cursor.getString(cursor.getColumnIndex("email")));
			user.setStartDate(SQLiteController.parseDateTime(cursor.getString(cursor.getColumnIndex("start_date"))));
		}
				
		return user;
	}
	
	public long updateProfile(Profile profile) {
		ContentValues cv = new ContentValues();
		cv.put("name", profile.getName());
		cv.put("age", SQLiteController.setDateTime(profile.getAge()));
		cv.put("contact_no", profile.getContactNo());
		cv.put("email", profile.getEmail());
		cv.put("start_date", SQLiteController.setDateTime(profile.getStartDate()));
		
		return database.update(database_table, cv, "id= " + profile.getId(), null);
	}
}
