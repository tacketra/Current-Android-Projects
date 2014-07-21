package com.example.tempalarm;
import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.CancellationSignal;
import android.os.OperationCanceledException;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class DatabaseHandler extends SQLiteOpenHelper {
	
	public static AlarmManagerActivity alarmContext;
	// All Static variables
	public static final int GRAY = -7829368;
	public static final int BLUE = -16776961;
	
	public static final int DATABASE_VERSION = 1;
	public static final String DATABASE_NAME = "AlarmTestDb18";//
	
	public static final String TABLE_ALARMS = "alarms";
	public static final String KEY_ALARM = "alarm"; //alarm,hour, minute, length all ints, the rest booleans
	public static final String KEY_HOUR = "hour";
	public static final String KEY_MINUTE = "minute";
	public static final String KEY_LENGTH = "length";
	public static final String KEY_MONDAY = "monday";
	public static final String KEY_TUESDAY = "tuesday";
	public static final String KEY_WEDNESDAY = "wednesday";
	public static final String KEY_THURSDAY = "thursday";
	public static final String KEY_FRIDAY = "friday";//calender order starts with sunday, which is 1, so there monday is 2 and so on like below:
	public static final String KEY_SATURDAY = "saturday";  //2	       3            4               5          6             7            1
	public static final String KEY_SUNDAY = "sunday";//mine starts Monday = 0, then tuesday is 1 and ends w/ sunday .like below:
	public static final String KEY_ACTIVE = "active";     //0          1             2              3            4           5             6
	public static final String[] DAYS =  new String[] {KEY_MONDAY, KEY_TUESDAY, KEY_WEDNESDAY, KEY_THURSDAY, KEY_FRIDAY, KEY_SATURDAY, KEY_SUNDAY};
	public Boolean dbCreateCalled = false;

	public DatabaseHandler(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);

	}

	// Creating Tables
	@Override
	public void onCreate(SQLiteDatabase db) {
		//db = this.getWritableDatabase();
		//Cursor c = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);
		//if (!c.moveToFirst()) { // if database has no table lets create one
			String CREATE_ALARM_TABLE = "CREATE TABLE " + TABLE_ALARMS + "(" + KEY_ALARM + " INTEGER," 
					+ KEY_HOUR  + " INTEGER," + KEY_MINUTE + " INTEGER," + KEY_LENGTH  + " INTEGER," + KEY_MONDAY + 
					" BOOLEAN," + KEY_TUESDAY + " BOOLEAN," + KEY_WEDNESDAY + " BOOLEAN," + KEY_THURSDAY + 
					" BOOLEAN," + KEY_FRIDAY + " BOOLEAN," + KEY_SATURDAY + " BOOLEAN," + KEY_SUNDAY + " BOOLEAN," + 
					KEY_ACTIVE + " BOOLEAN" + ")";
			db.execSQL(CREATE_ALARM_TABLE);
			dbCreateCalled = true;
		//}
	}

	public Boolean dbCreate(){
		return dbCreateCalled;
	}

	// Upgrading database
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// Drop older table if existed

		//I HID THIS  db.execSQL("DROP TABLE IF EXISTS " + TABLE_CONTACTS);
		//
		//
		// Create tables again
		onCreate(db);
	}

	/**
	 * All CRUD(Create, Read, Update, Delete) Operations
	 */////

	// Adding new contact
	Boolean addAlarmRow(ContentValues values) {
		SQLiteDatabase db = this.getWritableDatabase();
		int alarmNumber = values.getAsInteger(KEY_ALARM);
		String tableCheck = KEY_ALARM + Integer.toString(alarmNumber);
		Boolean rowExists = false;
		if ( rowExists(alarmNumber ) ){ //~RT 04-12-2014 02:39 PM , removed the alarmNumber + 1 , just alarmNumber now
			db.update(TABLE_ALARMS, values, KEY_ALARM +"="+ Integer.toString(alarmNumber) , null);
			rowExists  = true;
		}
		else{
			db.insert(TABLE_ALARMS, null, values);
		}
		db.close(); 
		return rowExists;
	}
	
	

	/*
	public Boolean rowExists(int alarmNumber) throws OperationCanceledException {
		String dbStr = "";
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery("SELECT alarm FROM alarms", null);
		Boolean returnVal = cursor.moveToPosition(alarmNumber);
		cursor.close();
		return returnVal;
	}*/

	public Boolean rowExists(int alarmNumber) throws OperationCanceledException {
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor tempCursor = db.rawQuery("SELECT " + this.KEY_ALARM +  " FROM alarms", null);
		if (tempCursor.moveToFirst()){
			while(!tempCursor.isAfterLast()){
				if(tempCursor.getInt(0) ==  alarmNumber){
					return true;
				}
				tempCursor.moveToNext();
			}
		}
		tempCursor.close();
		return false;
	}

	public String readDatabase(){
		String dbStr = "";
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery("SELECT * FROM alarms", null);
		int count = 0;
				    while ( !cursor.isAfterLast() ) {
				        ///Toast.makeText(, "Table Name=> "+c.getString(0), Toast.LENGTH_LONG).show();
				    	Log.i("Cursor c", "table columns  as name = " + cursor.getColumnName(0));
				    	String[] arrayCol = cursor.getColumnNames();
				    	String tempor = "";
				    	Log.i(" array col length", " is arraycol.lenth casuing it?");
				    	for (int i = 0; i < arrayCol.length; i++){
				    		Log.i("array col in for", "in readdatabase for loop , i = " + i);
				    		//if ( i <=3 ){
				    			tempor+= arrayCol[i];
				    			Log.i("array col in for", "column name in for loop = " + arrayCol[i]);
				    			Cursor tempCursor = db.rawQuery("SELECT " + arrayCol[i] +  " FROM alarms", null);
				    			if (tempCursor.moveToPosition(count)){
					    			tempor+= ": " + tempCursor.getInt(0) +  " \n";
					    			Log.i("array col in for", "column INT in for loop = " + tempCursor.getInt(0));
				    			}
				    			tempCursor.close();
				    		//}
				    		//else{
				    			//tempor+= arrayCol[i] + ": Boolean ";
				    		//}
				    	}

				    	Log.i("tempor ", "\n " + tempor);
				    	dbStr+= " " + "[ " + tempor + " ]";
				        cursor.moveToNext();
				       count++;
				   }
				    Log.i("while loop readDatabase", "while loop in read database looped " + count + " times");
		cursor.close();
		return dbStr;
	}
	//
	/** 
	 * finds the next number that will be used for a new alarm, e.g. there are laready alarm 1,2 and 3, so return 4
	 * @param None
	 * @return the number, the int that the next alarm should be , e.g. there are laready alarm 1,2 and 3, so return 4
	 */
	public int nextRow(){
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery("SELECT alarm FROM alarms", null); // go through each alarm by number, till you get to the last
		int newAlarmRow = 0;
		cursor.moveToFirst();
	    while ( !cursor.isAfterLast() ) {
	    	newAlarmRow = cursor.getInt(0);//
	    	cursor.moveToNext();
	    }
		cursor.close();
		Toast.makeText(alarmContext, "the next alarm number we will use is " + newAlarmRow, Toast.LENGTH_SHORT).show();
		return newAlarmRow +1; //this will be the next alarm number for a new alarm that gets created, so one after current last
	}
	
	public String readRow(int alarmNumber){ // idk why this is called read table, change to row
		//if (rowExists(alarmNumber)){
			String dbStr = "";
			SQLiteDatabase db = this.getReadableDatabase();
			//Cursor cursor = db.rawQuery("SELECT * FROM alarms", null);
			int count = 0; //move til you find the correct row, equal to the alarm number
			Cursor tempCursor = db.rawQuery("SELECT " + this.KEY_ALARM +  " FROM alarms", null);
			if (tempCursor.moveToFirst()){
				while(!tempCursor.isAfterLast()){
					if(tempCursor.getInt(0) ==  alarmNumber){
						Cursor cursor = db.rawQuery("SELECT * FROM alarms", null);
						String[] arrayCol = cursor.getColumnNames();
						String tempor = "";//
						if (cursor.moveToPosition(count)){
							for (int i = 0; i < arrayCol.length; i++){
								tempor+= arrayCol[i] + ": " + cursor.getInt(i) + " ";//"\n "; 
								//cursor.moveToNext();
							}
							return " " + "[ " + tempor + " ]";
						}
						cursor.close();
					}
					tempCursor.moveToNext();
					count++;
				}
			}
			tempCursor.close();
			return " there is no alarm: " + alarmNumber;
		//}
		//return " there is no alarm: " + alarmNumber; //else
	}
	
	//get attribute of a particular alarm, ex: (1, KEY_TUESDAY), return t/f for whether alarm 1 is active tuesday, alarm 1 fires at hour 15 (3PM)
	public int getKeyFromAlarm(int alarmNum, String key){
		ContentValues conVal = getRowContent(alarmNum);
		return	conVal.getAsInteger(key);
	}
	
	//pass a alarm number, string, value and that will modify that alarms attribute, ex: pass KEY_ACTIVE and 0, and it will change
	//the alarm from its current active value to 0 (false)
	// updateAlarm(int alarm number,String key (to modify), int value (value to change the key to) )
	public void updateAlarm(int alarmNum, String key, int value){
		SQLiteDatabase db = this.getReadableDatabase();
		//below example, update alarms set active=1 where alarm=1. This would make alarm 1 active
		//Cursor tempCursor = db.rawQuery("update alarms set "+ key + "="+ value + " where alarm=" + alarmNum, null);
		ContentValues cv = this.getRowContent(alarmNum);
		cv.put(key, value);
		db.update(TABLE_ALARMS, cv, KEY_ALARM +"="+ alarmNum , null);
	}


	// make this return a dictionary object
	public ContentValues getRowContent(int alarmNumber){
		//if (rowExists(alarmNumber)){
			ContentValues cvals = new ContentValues();
			String dbStr = "";
			SQLiteDatabase db = this.getReadableDatabase();
			//Cursor cursor = db.rawQuery("SELECT * FROM alarms", null);
			int count = 0; //move til you find the correct row, equal to the alarm number
			Cursor tempCursor = db.rawQuery("SELECT " + this.KEY_ALARM +  " FROM alarms", null);
			if (tempCursor.moveToFirst()){
				while(!tempCursor.isAfterLast()){
					if(tempCursor.getInt(0) ==  alarmNumber){
						Cursor cursor = db.rawQuery("SELECT * FROM alarms", null);
						String[] arrayCol = cursor.getColumnNames();
						String tempor = "";//
						if (cursor.moveToPosition(count)){
							for (int i = 0; i < arrayCol.length; i++){
								tempor+= arrayCol[i] + ": " + cursor.getInt(i) +"\n ";
								cvals.put(arrayCol[i], cursor.getInt(i));
								//cursor.moveToNext();
							}
							return cvals;
						}
						cursor.close();
					}
					tempCursor.moveToNext();
					count++;
				}
			}
			tempCursor.close();
			return cvals; // need to either break or displpay  toast saying row empty
		//}
		//return " there is no alarm: " + alarmNumber; //else
	}


	public int getAlarmThatShouldBePlaying(double currentTime, String day){
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery("SELECT * FROM alarms", null);
		int count = 0;
		while ( !cursor.isAfterLast() ) {
			Cursor tempCursor = db.rawQuery("SELECT " + this.KEY_ACTIVE + "," + day+ ","+ this.KEY_HOUR + "," + this.KEY_MINUTE+ "," +  
					this.KEY_LENGTH + "," + this.KEY_ALARM + " FROM alarms", null);
			if (tempCursor.moveToPosition(count)){
				if (tempCursor.getInt(0) == 1 && tempCursor.getInt(1) == 1){
					//alarm is active on current day, otherwise no reason to get hours, minutes, and length
			        double rowHour = tempCursor.getInt(2), rowMinute = tempCursor.getInt(3), rowLength = tempCursor.getInt(4);
			        Log.i("rowHour", "rowHour = " + rowHour + " rowMinute = " + rowMinute);
					double alarmStart = rowHour + (rowMinute/100);
					double alarmEnd = alarmStart + (rowLength/100);
					Log.i("ALARMSTART alarm END", "alarmStart in getalarmshoyldbeplaying = " + alarmStart + " alarmEnd = "+alarmEnd 
							+ " currenTime = " + currentTime);
					if (alarmStart <= currentTime && currentTime <= alarmEnd){
						cursor.close();
						return tempCursor.getInt(5); // return alarm number that should be playing , ie: current time is 9:20, 
						//alarm 1 has an hour of 9 and minute of 20 and length 0f 10, so return 1.
					}
				} 
			}
			tempCursor.close();
			cursor.moveToNext();
			count++;
		}
		cursor.close();
		return -1; // there is no alarm that should be playing right now
	}
	
	/*fills the screen with all the alarms stored in the database, makes a row for each alarm with two buttons. The first button 
	 on the left gives the time of the alarm and if you click it you can edit the days the alarm is active, the time and duration.
	 The second button, the one on the right is simply to make the alarm active or not, clicking it will make it blue (active)
	 pass an alarm number if you want to update only one row, otherwise pass -1 to just update all rows*/
	public void populateGuiAlarmRows(AlarmManagerActivity _context, int alarmNum){
			alarmContext = _context;// without the context passed from alarmManager activity this would not put these buttons that
									//get generated in the correct location, the user wouldn't even see them.		
			SQLiteDatabase db = this.getReadableDatabase();
			String oneAlarm = "";
			//Toast.makeText(_context, "populateGui , alarmNum = " + alarmNum, Toast.LENGTH_SHORT).show();
			if (alarmNum != -1){oneAlarm = " where alarm=" + alarmNum;}
			Cursor cursor = db.rawQuery("SELECT * FROM alarms" + oneAlarm, null);
			int count = 0;
		    while ( !cursor.isAfterLast() ) {// each iteration of the while is for one alarm, while the for just loops through its columns
	    		Cursor tempCursor = db.rawQuery("SELECT " + KEY_ALARM +  " FROM alarms"+ oneAlarm, null);
	    		if (tempCursor.moveToPosition(count)){
	    			ContentValues convals = getRowContent(tempCursor.getInt(0));
	    			int hours = convals.getAsInteger(KEY_HOUR);
	    			String amPm = "AM ";
	    			if ( hours >=12){
	    				amPm = "PM ";
	    			}
	    			if(hours >=13){
	    				hours = hours - 12; //12PM should stil be 12 pm but 1pm (13 in military) should be 1,14 should be 2 etc.
	    			}
	    			else if (hours == 0){
	    				hours = 12; // 0:59 should be 12:59 AM, the hours are store din military time, so convert
	    			}
	    			String days = "";
	    			for (int i = 0; i < 7; i++){
	    				if (convals.getAsInteger(DAYS[i]) == 1){
	    					days+= DAYS[i].substring(0,2) + ", ";
	    				}
	    			}
	    			String row = hours +":"+ convals.getAsInteger(this.KEY_MINUTE) + amPm + " " + days;
	    			//String row = this.readRow(tempCursor.getInt(0)); //string of entire alarm
	    			Boolean act = false;
	    			if ( convals.getAsInteger(KEY_ACTIVE) == 1){act = true;}
	    			makeAlarmButtonRows( _context , row , tempCursor.getInt(0) , act); //make a button out of the string of that alarm
		    	}
	    		tempCursor.close();
		        cursor.moveToNext();
		       count++;
		   }
		   cursor.close();			
			/*
	        for (int i = 1; i < 10; i++) { 	
	        	Button b = new Button(alarmContext);
	        	b.setText("alarm " + i);
	        	b.setId(i);
	        	Button b2 = new Button(alarmContext);
	        	b2.setText("on/off");
	        	b2.setId(i*-1);
	        	RelativeLayout.LayoutParams rl = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
	        	RelativeLayout.LayoutParams rl2 = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
	        	rl2.addRule(RelativeLayout.RIGHT_OF, i);
	        	if (i > 1){
	        		rl.addRule(RelativeLayout.BELOW, i -1);
	        		rl2.addRule(RelativeLayout.BELOW, i -1);
	        	}
	        	b.setLayoutParams(rl);
	        	b2.setLayoutParams(rl2);
	        	((RelativeLayout) alarmContext.findViewById(com.example.tempalarm.R.id.relativeLayout)).addView(b);
	        	((RelativeLayout) alarmContext.findViewById(com.example.tempalarm.R.id.relativeLayout)).addView(b2);        	
	        }*/
			
	}
	
	private void makeAlarmButtonRows(AlarmManagerActivity _context, String row, final int rowNumber, final Boolean active){
		/*Button tempButton = (Button) this.alarmContext.findViewById(rowNumber);
		Button tempActiveButton = (Button) this.alarmContext.findViewById(rowNumber *-1);
		Boolean buttonsExist = true; // check whether we need to create buttons or not
		if (tempButton == null){ // findviewbyid returns null if view doesn't already exist, so we create new button
			tempButton = new Button(_context);
			tempActiveButton = new Button(_context); //if button with info for alarm doesn't exist either does the on/off button for it
			buttonsExist = false;
		} 
		Button b = tempButton; //button for editing the alarm//
		Button activeButton = tempActiveButton;// on off button, blue = on, gray = off 
		*/
		Button b = null;//(Button) this.alarmContext.findViewById(rowNumber);//
		Button activeButton = null;//(Button) this.alarmContext.findViewById(rowNumber *-1);
		Boolean buttonsExist = false; // check whether we need to create buttons or not
		if (b == null){ // findviewbyid returns null if view doesn't already exist, so we create new button
			b= new Button(_context);
			activeButton = new Button(_context); //if button with info for alarm doesn't exist either does the on/off button for it
			buttonsExist = false;
		} 
		
    	b.setText(row);
    	//Toast.makeText(_context, "in make alarmbuttonrows = " + rowNumber, Toast.LENGTH_SHORT).show();
    	if ( active){
    		activeButton.setText("on");
    		activeButton.setBackgroundColor(BLUE);//blue 
    	}
    	else{
    		activeButton.setText("off");
    		activeButton.setBackgroundColor(GRAY);
    	}
    	if (buttonsExist == false){  // we don't need to create id and make all of these layouts if the button already exists
	    	b.setId(rowNumber);
	    	activeButton.setId(rowNumber*-1);
	    	RelativeLayout.LayoutParams rl = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
	    	RelativeLayout.LayoutParams rl2 = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
	    	rl2.addRule(RelativeLayout.RIGHT_OF, rowNumber);
	    	if (rowNumber > 0){
	    		rl.addRule(RelativeLayout.BELOW, rowNumber -1);
	    		rl2.addRule(RelativeLayout.BELOW, rowNumber -1);
	    	}
	    	b.setLayoutParams(rl);
	    	activeButton.setLayoutParams(rl2);
	    	((RelativeLayout) alarmContext.findViewById(com.example.tempalarm.R.id.relativeLayout)).addView(b);
	    	((RelativeLayout) alarmContext.findViewById(com.example.tempalarm.R.id.relativeLayout)).addView(activeButton);   
    	}
    	activeButton.setOnClickListener(new View.OnClickListener() {
    	    @Override
    	    public void onClick(View v) {
    	    	Boolean tempBool = true;//
    	    	int activeToInt = 1;
    	    	if (active){activeToInt=0; tempBool = false;} // true = 1, false = 0 , so we can pass it to the database
    	    	
    	    	updateAlarm(rowNumber, KEY_ACTIVE, activeToInt);
    	    	populateGuiAlarmRows(alarmContext, rowNumber);
    	    	
    	    	if(tempBool){
    	    		/*alarmContext.sendAlarm2(getKeyFromAlarm(rowNumber, DatabaseHandler.KEY_HOUR), getKeyFromAlarm(rowNumber, 
    	    				DatabaseHandler.KEY_MINUTE), false, rowNumber);*/
    	    		alarmContext.sendAlarm(getRowContent(rowNumber), false);
    	    	}
    	        //Toast.makeText(v.getContext(), "alarm " + rowNumber + " changed to " + tempBool, Toast.LENGTH_SHORT).show();
    	    }
    	});
    	b.setOnClickListener(new View.OnClickListener() {
    	    @Override
    	    public void onClick(View v) {
    	    	goToSettings(rowNumber);
    	    }
    	});
	}
	
	public void goToSettings(int rowNumber){
		Intent settingsI = new Intent(alarmContext, Settings.class);
		ContentValues cVals = getRowContent(rowNumber);
		for(int i = 0; i < 7 ; i ++){
			settingsI.putExtra(DAYS[i], cVals.getAsInteger(DAYS[i]));
		}
		settingsI.putExtra("alarm", cVals.getAsInteger(KEY_ALARM));
		alarmContext.startActivity(settingsI);
	}
	
	public void deleteAlarmRow(int rowNum){
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(TABLE_ALARMS, "alarm=" + rowNum, null);
		Intent alarmI = new Intent(alarmContext, AlarmManagerActivity.class);
		alarmContext.startActivity(alarmI);
	}
	
	public void timeAlarmGoesOffToast(int rowNum){
		ContentValues cVals = getRowContent(rowNum);
		String rowStr = "";
		Toast.makeText(alarmContext, rowStr, Toast.LENGTH_SHORT).show();
	}

}




/*
SQLiteDatabase db = this.getWritableDatabase();
//Cursor c = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);
Cursor c = db.rawQuery("SELECT alarm from alarms where alarm =" + Integer.toString(alarmNumber), null);
if (c.moveToFirst()) {
	String tableCheck = KEY_ALARM + Integer.toString(alarmNumber);
    while ( !c.isAfterLast() ) {
        //Toast.makeText(, "Table Name=> "+c.getString(0), Toast.LENGTH_LONG).show();
        if (c.getString(0) == tableCheck){return true;}
        c.moveToNext();
    }
}
return false; // no table name matched the new alarm we want to add, ie: rowExists(2) , couldn't find alarm2 table so return false

SQLiteDatabase db = this.getWritableDatabase();
Cursor c = db.rawQuery("SELECT alarm from alarms where alarm =" + Integer.toString(alarmNumber), null);
return c.moveToFirst(); //returns false if empty, true if not
}
*/

/*
void addAlarmRow(ContentValues values) {
SQLiteDatabase db = this.getWritableDatabase();
int alarmNumber = values.getAsInteger(KEY_ALARM);
String tableCheck = KEY_ALARM + Integer.toString(alarmNumber);
if ( rowExists(alarmNumber) ){
	db.update(tableCheck, values, null, null);
}
else{
	db.insert(TABLE_ALARMS, null, values);
}
db.close(); 
}
*/
