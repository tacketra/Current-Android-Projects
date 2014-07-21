package com.example.tempalarm;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.TimePicker;
import android.widget.Toast;


public class Settings extends AlarmManagerActivity {

	//protected AlarmManagerActivity alarmContext2;
	ContentValues rowVals ;
	int alarmRow = 0;
	String[] DAYS = DatabaseHandler.DAYS;
	Context context;
	public static final int BLUE = DatabaseHandler.BLUE;
	public static final int GRAY = DatabaseHandler.GRAY;
	public static final String[] shortDays = new String[] {"Mo", "Tu", "We", "Th", "Fr", "Sa", "Su"};
	public ContentValues dayOrder = new ContentValues();
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.example.tempalarm.R.layout.settings);

        Bundle extras = getIntent().getExtras();
        String temp ="";
        String temp2 = "";
        
        alarmRow = extras.getInt(DatabaseHandler.KEY_ALARM); // alarm number this settings is working with
        for (int i = 0; i < 7; i++){
        	temp+= shortDays[i] + ":" + extras.getBoolean(shortDays[i]) + " , ";
        	temp2+= shortDays[i] + ":" + extras.getInt(shortDays[i]) + " , ";
        	Log.i("days", "int, " + DAYS[i] + ": " + getIntent().getIntExtra(DAYS[i], -1));
        	Log.i("days", "bool, " + DAYS[i] + ": " + getIntent().getBooleanExtra(DAYS[i], false));
        	
        }
        
        final Context con = getApplicationContext();
        
        Log.i("days", "temp = " + temp);
        Log.i("days", "temp2 = " + temp2);
        NumberPicker np3 = (NumberPicker) findViewById(R.id.wheel_duration);
        np3.setMinValue(1);
        np3.setMaxValue(40);//
        np3.setWrapSelectorWheel(true);
        //np3.setValue(extras.getInt(DatabaseHandler.KEY_LENGTH));
        np3.setValue(db.getKeyFromAlarm(alarmRow, DatabaseHandler.KEY_LENGTH));
        np3.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
			
			@Override
			public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
				//Toast.makeText(globalContext, "old val = " + oldVal + ", newVal = " + newVal, Toast.LENGTH_SHORT).show();
				db.updateAlarm(alarmRow, DatabaseHandler.KEY_LENGTH, newVal);
				//Toast.makeText(con, db.getKeyFromAlarm(alarmRow, DatabaseHandler.KEY_LENGTH), Toast.LENGTH_SHORT).show();
			}
		});
        
        //Button deleteButton = (Button) findViewById(R.id.delete_alarm);
        //deleteButton.setOnClickListener(l);

        
        
        
        TimePicker time = (TimePicker) findViewById(R.id.timeWheel);
        time.setCurrentHour(db.getKeyFromAlarm(alarmRow, DatabaseHandler.KEY_HOUR));
        time.setCurrentMinute(db.getKeyFromAlarm(alarmRow, DatabaseHandler.KEY_MINUTE));
        time.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener(){
        	@Override
        	public void onTimeChanged(TimePicker view, int hourOfDay, int minute){
        		//Toast.makeText(con, hourOfDay + ":"+minute, Toast.LENGTH_SHORT).show();//
        		db.updateAlarm(alarmRow, DatabaseHandler.KEY_HOUR, hourOfDay);
        		db.updateAlarm(alarmRow, DatabaseHandler.KEY_MINUTE, minute);//
        	}
        });
        
        //updateAlarm and getRowContent are what I need
        
        for(int i=0; i<7; i++) {
        	dayOrder.put(shortDays[i], i); // populate day order with Mo,0 Tu,1 etc. makes life easier in the onclick when we grab
        	//which day we are working with by the shorter names
    	    String buttonID = "button_" + DAYS[i];
    	    final int resID = getResources().getIdentifier(buttonID, "id", "com.example.tempalarm");
    	    /*
    	    if ( getIntent().getIntExtra(DAYS[i], -1) == 1){
    	    	((Button) findViewById(resID)).setBackgroundColor(BLUE); //if alarm is active for this day make the button for it blue
    	    	Log.i("Days", " the day " + DAYS[i] + " should be blue");
    	    }*/
    	    if (db.getKeyFromAlarm(alarmRow, DAYS[i]) == 1){
    	    	((Button) findViewById(resID)).setBackgroundColor(BLUE);
    	    }
    	    ((Button) findViewById(resID)).setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					//Toast.makeText(context, "the day is " + ((Button) findViewById(resID)).getId(), Toast.LENGTH_SHORT).show();
					String currentDay = (String) ((Button) findViewById(resID)).getText(); //get current day in short day, like Mo, Tu
					currentDay = DAYS[dayOrder.getAsInteger(currentDay)]; // dayorder(Mo) would = 0, DAYS[0] would = monday
					Log.i("days", "the day is " + currentDay);
		    	    if (db.getKeyFromAlarm(alarmRow, currentDay) == 1){
		    	    	((Button) findViewById(resID)).setBackgroundColor(GRAY);
		    	    	db.updateAlarm(alarmRow, currentDay, 0);
		    	    }
		    	    else{
		    	    	((Button) findViewById(resID)).setBackgroundColor(BLUE);
		    	    	db.updateAlarm(alarmRow, currentDay, 1);
		    	    }
					/*
		    	    if ( getIntent().getIntExtra(currentDay, -1) == 1){
		    	    	((Button) findViewById(resID)).setBackgroundColor(GRAY); //if alarm is active for this day make the button for it blue
		    	    	
		    	    }
		    	    else{
		    	    	((Button) findViewById(resID)).setBackgroundColor(BLUE);
		    	    }*/
				}
			});
        }
        
    }
    
    public void deleteCurrentAlarm(View view){
    	//db.alarmContext.removeHome(db.alarmContext);
    	Toast.makeText(this.getApplicationContext(), "deleting alarm " + alarmRow, Toast.LENGTH_SHORT).show();
    	//setContentView(com.example.tempalarm.R.layout.activity_main);
    	db.deleteAlarmRow(alarmRow);
    	//AlarmManagerActivity context;
    	//setContentView(com.example.tempalarm.R.layout.activity_main);
    	
    }
    
    @Override
    public void onBackPressed(){
    	if (db.getKeyFromAlarm(alarmRow, db.KEY_ACTIVE) == 1){
    		db.alarmContext.sendAlarm(db.getRowContent(alarmRow), false);
    	}//if the alarm is active send its alarms so they keep just loop and yyou don't have to manuall resend the alarms
		Intent alarmI = new Intent(db.alarmContext, AlarmManagerActivity.class);
		db.alarmContext.startActivity(alarmI);
    }
}
