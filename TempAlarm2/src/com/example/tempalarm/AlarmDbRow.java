package com.example.tempalarm;

import android.content.ContentValues;
import android.util.Log;

public class AlarmDbRow {
	 String KEY_ALARM = "alarm"; //alarm,hour, minute, length all ints, the rest booleans
	 String KEY_HOUR = "hour";
	 String KEY_MINUTE = "minute";
	 String KEY_LENGTH = "length";
	 String KEY_MONDAY = "monday";
	 String KEY_TUESDAY = "tuesday";
	 String KEY_WEDNESDAY = "wednesday";
	 String KEY_THURSDAY = "thursday";
	 String KEY_FRIDAY = "friday";
	 String KEY_SATURDAY = "saturday";
	 String KEY_SUNDAY = "sunday";
	 String KEY_ACTIVE = "active";

	 int ALARM ; //alarm,hour, minute, length all ints, the rest booleans
	 int HOUR ;
	 int MINUTE ;
	 int LENGTH ;
	 Boolean MONDAY ;
	 Boolean TUESDAY ;
	 Boolean WEDNESDAY ;
	 Boolean THURSDAY ;
	 Boolean FRIDAY ;
	 Boolean SATURDAY ;
	 Boolean SUNDAY ;
	 Boolean ACTIVE ;

	 ContentValues alarmNumberArray = new ContentValues(); //size of 12 , 1-12 values to assign to the columns
	 ContentValues alarmColumns; 

	AlarmDbRow(){
		alarmNumberArray.put(KEY_ALARM, 1);
		alarmNumberArray.put(KEY_HOUR, 2);
		alarmNumberArray.put(KEY_MINUTE, 3);
		alarmNumberArray.put(KEY_LENGTH, 4);
		alarmNumberArray.put(KEY_MONDAY, 5);
		alarmNumberArray.put(KEY_TUESDAY, 6);
		alarmNumberArray.put(KEY_WEDNESDAY, 7);
		alarmNumberArray.put(KEY_THURSDAY, 8);
		alarmNumberArray.put(KEY_FRIDAY, 9);
		alarmNumberArray.put(KEY_SATURDAY, 10);
		alarmNumberArray.put(KEY_SUNDAY, 11);
		alarmNumberArray.put(KEY_ACTIVE, 12);

		alarmColumns = new ContentValues();

	}

	public void setColumn(String key, int value){
		alarmColumns.put(key, value);
	}

	public ContentValues getColumns(){
		Log.i("alarm columns", "alarm columns = " + alarmColumns);
		return alarmColumns;
	}



}