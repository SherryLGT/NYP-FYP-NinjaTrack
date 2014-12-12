package controller;

import model.Profile;
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
			//user.setAge(cursor.getString(cursor.getColumnIndex("age")));
			user.setContactNo(cursor.getInt(cursor.getColumnIndex("contact_no")));
			user.setEmail(cursor.getString(cursor.getColumnIndex("email")));
			//user.setStartDate(cursor.getString(cursor.getColumnIndex("start_date")));
		}
				
		return  user;
	}
}
