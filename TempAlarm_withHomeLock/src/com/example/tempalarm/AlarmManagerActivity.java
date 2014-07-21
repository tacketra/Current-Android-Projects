package com.example.tempalarm;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.R;
import android.media.AudioManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.PixelFormat;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v4.app.NavUtils;


//trying to have custom home screen lock now at 8:44PM 5/25/14, creating new class called homeLock
//removing all the view part from read , see if it matters!!!!!!!!!!!!!!!!!!! 922PM march 31 2014//

//I need to fix the multiple alarms playing since I made the home option so that a user cannot bypass the apps alarm
//by removing the app before the on boot is called in slower booting phones, otherwise should still work normal
//also it makes the onBoot receiver not work if it is not set as home

public class AlarmManagerActivity extends homeLock implements OnKeyListener {


	public Ringtone r;
	public static AlarmManagerBroadcastReceiver alarm;
	private String file = "mydata11";
	private String data;//
	int line_number = 0;
	int alarm_number ;
	String str = "";
	String alarm_line = "";
	public static Context globalContext;
	AlarmDbRow alarmCV = new AlarmDbRow();
	public static DatabaseHandler db;
	EditText alarmGui;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.example.tempalarm.R.layout.activity_main);
        globalContext = getApplicationContext();

        //Toast.makeText(getApplicationContext(), getIntent().getComponent() + " ", Toast.LENGTH_SHORT).show();
        alarm = new AlarmManagerBroadcastReceiver();
        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        r = RingtoneManager.getRingtone(getApplicationContext(), notification);
        AudioManager mgr=null;
        //globalContext = getApplicationContext();
        mgr=(AudioManager) getSystemService(Context.AUDIO_SERVICE);
        
        db = new DatabaseHandler(this); // set up the database, where all alarms are stored
        db.populateGuiAlarmRows(this, -1);//passing -1 to this method just updates all rows instead of just the particular number given
        //need to give the databasehandler object (db) the right context so it can populate the gui
        Toast.makeText(getApplicationContext(), " on create called for alarmManagerActivity! ", Toast.LENGTH_SHORT).show();
        Boolean fromHome = getIntent().getBooleanExtra("from homeLock", false);
        if (/*AlarmManagerBroadcastReceiver.onBootCalled*/ true){
        //if (/*fromHome   hiding for now */ false){
        	Toast.makeText(getApplicationContext(), " it came from HOME !!!!!!!! ", Toast.LENGTH_SHORT).show();
            String[] daysOfWeek = DatabaseHandler.DAYS;
            Calendar c = Calendar.getInstance(); 
            int calendarDay = c.get(Calendar.DAY_OF_WEEK);//they start sunday = 1, monday = 2, mine starts w/ m = 0
            if ( calendarDay == 1){ calendarDay = 6; }//theres starts with sunday (1) mine ends with sun (6)
            else{ calendarDay = calendarDay -2;}
            String currentDay = daysOfWeek[calendarDay];//after getting the order of their array to match mine get day
            int alarmShould = -1;
            if(db != null){ 
                double c_min = c.get(Calendar.MINUTE), c_hour = c.get(Calendar.HOUR_OF_DAY); 
                double time = c_hour + (c_min/100);
            	alarmShould = db.getAlarmThatShouldBePlaying(time, currentDay); 
            	if (alarmShould != -1){
            		//alarm.alarmLoopThread.start();
                    //Intent loop = new Intent(alarm.globeContext, HUD.class);
                   // alarm.globeContext.startService(loop);
            		sendAlarm(db.getRowContent(alarmShould), false);
            	}
                else{
                    Intent loop = new Intent(homeLock.homeLockContext, HUD.class);
                    homeLock.homeLockContext.stopService(loop);
                    
                	Toast.makeText(getApplicationContext(), " no need to send an alarm, no alarm should be playing now "
                			, Toast.LENGTH_SHORT).show();
                }
            }
        }
        /*
        if (SystemClock.uptimeMillis() < 55000){
        	Toast.makeText(globalContext, " uptimeMilliis less than 45k, time is = " + SystemClock.uptimeMillis()
        			, Toast.LENGTH_LONG).show();
	        bootThread.start();
	        Intent loop = new Intent(globalContext, HUD2.class);
	        globalContext.startService(loop);
        }*/
        /*
        for (int i = 1; i < 10; i++) { 	
        	Button b = new Button(this);
        	b.setText("alarm " + i);
        	b.setId(i);
        	Button b2 = new Button(this);
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
        	((RelativeLayout) findViewById(com.example.tempalarm.R.id.relativeLayout)).addView(b);
        	((RelativeLayout) findViewById(com.example.tempalarm.R.id.relativeLayout)).addView(b2);        	
        }
        */
    }
    
    public static Ringtone getRingtone(Context context){
        Uri alarm_ring = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        return RingtoneManager.getRingtone(context, alarm_ring);
    }
    

    
    @Override
	protected void onStart() {
		super.onStart();
	}

    public void startRepeatingTimer(View view) {
    	Context context = this.getApplicationContext();
    	if(alarm != null){
    		alarm.SetAlarm(context);
    	}else{
    		//toast.makeText(context, "Alarm is null", Toast.LENGTH_SHORT).show();
    	}
    }
    
    public void cancelRepeatingTimer(View view){
    	Context context = this.getApplicationContext();
    	if(alarm != null){
    		alarm.CancelAlarm(context);
    	}else{
    		//toast.makeText(context, "Alarm is null", Toast.LENGTH_SHORT).show();
    		
    	}
    }
    
    public Context getAlarmContext(){
    	return this.getApplicationContext();
    }
    
    /*
    public void onetimeTimer(View view) {
    	Context context = this.getApplicationContext();
    	EditText hour = (EditText) findViewById(com.example.tempalarm.R.id.hour);
    	EditText minutes = (EditText) findViewById(com.example.tempalarm.R.id.minutes);
    	EditText alarm_num = (EditText) findViewById(com.example.tempalarm.R.id.alarm_number);
    	int hour_int = Integer.parseInt( hour.getText().toString() );
    	int minutes_int = Integer.parseInt( minutes.getText().toString() );
    	alarm_number = Integer.parseInt( alarm_num.getText().toString() );
    	//Log.i("past the string onversion", "past the string conversion");
    	
    	if(alarm != null){
    		//alarm.setOnetimeTimer(context, hour_int, minutes_int, false, alarm_number, getAlarmString(view));
    		//Toast.makeText(context, "minutes = " + minutes_int + " hours = " + hour_int, Toast.LENGTH_SHORT).show();
    		alarm.setOnetimeTimer(context, hour_int, minutes_int, false, alarm_number);
    	}else{
    		//toast.makeText(context, "Alarm is null", Toast.LENGTH_SHORT).show();
    	}
    }*/
    
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(com.example.tempalarm.R.menu.main, menu);
        return true;
    }

	public void sendAlarm(ContentValues cVals, Boolean from_boot){
		//alarm.sendAlarms(cVals, from_boot, getAlarmContext());
		alarm.sendAlarms(cVals, from_boot, globalContext, true);
	}
	
	public void newAlarm(View view){
		//makePrefered(this);
		AlarmDbRow alarmCV = new AlarmDbRow();
    	int alarmRow = db.nextRow();
    	alarmCV.setColumn(alarmCV.KEY_ALARM, alarmRow);
    	alarmCV.setColumn(alarmCV.KEY_HOUR, 12);
    	alarmCV.setColumn(alarmCV.KEY_MINUTE, 0);
    	alarmCV.setColumn(alarmCV.KEY_LENGTH, 0);
    	alarmCV.setColumn(alarmCV.KEY_MONDAY, 0);
    	alarmCV.setColumn(alarmCV.KEY_TUESDAY, 0);
    	alarmCV.setColumn(alarmCV.KEY_WEDNESDAY, 0);
    	alarmCV.setColumn(alarmCV.KEY_THURSDAY, 0);
    	alarmCV.setColumn(alarmCV.KEY_FRIDAY, 0);
    	alarmCV.setColumn(alarmCV.KEY_SATURDAY, 0);
    	alarmCV.setColumn(alarmCV.KEY_SUNDAY, 0);
    	alarmCV.setColumn(alarmCV.KEY_ACTIVE, 0);
    	
    	db.addAlarmRow(alarmCV.getColumns());
    	db.populateGuiAlarmRows(this, alarmRow);

	}
	
	Thread bootThread = new Thread(){
		@Override
		public void run(){
			while(SystemClock.uptimeMillis() < 55000){}
			Intent loop = new Intent(globalContext, HUD2.class);
			globalContext.stopService(loop);////
		}
	};
	
	public void setHome(View view){
		makePrefered(this.getAlarmContext());
	}
	
	public static void makePrefered(Context c) {
	       PackageManager p = c.getPackageManager();
	       ComponentName cN = new ComponentName(c, FakeHome.class);
	       p.setComponentEnabledSetting(cN, PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);

	       Intent selector = new Intent(Intent.ACTION_MAIN);
	       selector.addCategory(Intent.CATEGORY_HOME);  
	       selector.addFlags( Intent.FLAG_ACTIVITY_NEW_TASK); //
	       c.startActivity(selector);	       
	       p.setComponentEnabledSetting(cN, PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
	   }
	
	public static void removeHome(Context c) {
	       PackageManager p = c.getPackageManager();
	       ComponentName cN = new ComponentName(c, AlarmManagerActivity.class);
	       p.setComponentEnabledSetting(cN, PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
	       Intent selector = new Intent(Intent.ACTION_MAIN);   
	       selector.removeCategory(Intent.CATEGORY_HOME);
	       c.startActivity(selector);
	       //p.setComponentEnabledSetting(cN, PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
	   }
	
	boolean isMyLauncherDefault() {
	    final IntentFilter filter = new IntentFilter(Intent.ACTION_MAIN);
	    filter.addCategory(Intent.CATEGORY_HOME);

	    List<IntentFilter> filters = new ArrayList<IntentFilter>();
	    filters.add(filter);

	    final String myPackageName = getPackageName();
	    List<ComponentName> activities = new ArrayList<ComponentName>();
	    final PackageManager packageManager = (PackageManager) getPackageManager();

	    // You can use name of your package here as third argument
	    packageManager.getPreferredActivities(filters, activities, null);

	    for (ComponentName activity : activities) {
	        if (myPackageName.equals(activity.getPackageName())) {
	            return true;
	        }
	    }
	    return false;
	}
	
	@Override
	public boolean onKey(View v, int keyCode, KeyEvent event) {
		Toast.makeText(v.getContext(), "keyCode = " + keyCode + " , keyevent = " + event, Toast.LENGTH_SHORT).show();
		for (int i = 0 ; i < 8 ; i ++){
			Log.i("days", " onkey displayed :  + " + i);
		}
		return false;
	}
	    
	
	/*
	 * old methods below I no longer use
	 * 
	@SuppressLint("NewApi")
	public void setAlarmText(View view) throws UnsupportedEncodingException, IOException{//called by save alarm button , writes 
    	Context context = this.getApplicationContext();
    	EditText hour = (EditText) findViewById(com.example.tempalarm.R.id.hour);
    	EditText minutes = (EditText) findViewById(com.example.tempalarm.R.id.minutes);
    	EditText alarm_num = (EditText) findViewById(com.example.tempalarm.R.id.alarm_number);
    	EditText alarm_dur = (EditText) findViewById(com.example.tempalarm.R.id.alarm_duration);
    	// populate all the columns
    	int hour_int = Integer.parseInt( hour.getText().toString() );
    	int minutes_int = Integer.parseInt( minutes.getText().toString() );
    	int alarm_number = Integer.parseInt( alarm_num.getText().toString() );
    	int alarm_duration = Integer.parseInt( alarm_dur.getText().toString() );
    	alarmCV.setColumn(alarmCV.KEY_ALARM, alarm_number);
    	alarmCV.setColumn(alarmCV.KEY_HOUR, hour_int);
    	alarmCV.setColumn(alarmCV.KEY_MINUTE, minutes_int);
    	alarmCV.setColumn(alarmCV.KEY_LENGTH, alarm_duration);
    	alarmCV.setColumn(alarmCV.KEY_MONDAY, 1);
    	alarmCV.setColumn(alarmCV.KEY_TUESDAY, 1);
    	alarmCV.setColumn(alarmCV.KEY_WEDNESDAY, 1);
    	alarmCV.setColumn(alarmCV.KEY_THURSDAY, 1);
    	alarmCV.setColumn(alarmCV.KEY_FRIDAY, 1);
    	alarmCV.setColumn(alarmCV.KEY_SATURDAY, 1);
    	alarmCV.setColumn(alarmCV.KEY_SUNDAY, 1);
    	alarmCV.setColumn(alarmCV.KEY_ACTIVE, 1);
    	
        Boolean rowE = db.addAlarmRow(alarmCV.getColumns());
        if (rowE){
        	Toast.makeText(context, "addAlarmRow, the row did EXIST", Toast.LENGTH_LONG).show();
        }
        else{
        	Toast.makeText(context, "Alarm DID NOT exist, NEW alarm", Toast.LENGTH_LONG).show();
        }
        //Boolean dbool = db.dbCreate();
        //Toast.makeText(context, "Database Create called? = " + dbool,Toast.LENGTH_LONG ).show();
        //String dataStr = db.readDatabase();
        //Log.i("database to string", "database to string = " + dataStr);		
	}
	
	public void save(View view){
		      // data = et.getText().toString();
			  //String data = "alarmfile";
		      try {
		        FileOutputStream fOut = openFileOutput(file,MODE_WORLD_READABLE);
		    	  //FileOutputStream fOut = openFileOutput(file,MODE_APPEND);

		         fOut.write(data.getBytes(), 0 , data.getBytes().length);
		         //fOut.write(data.getBytes());

		         fOut.close();
		         //toast.makeText(getBaseContext(),"file saved",Toast.LENGTH_LONG).show();
		      } catch (Exception e) {
		         // TODO Auto-generated catch block
		    	  //toast.makeText(getBaseContext(),"EXCEPTION in save!!", Toast.LENGTH_SHORT).show();
		         e.printStackTrace();
		      }
	}

	public String read(Boolean showToast){ // figure out if this is the one that trigger son the touch, i think it is one
		    	EditText alarm_num = (EditText) findViewById(com.example.tempalarm.R.id.alarm_number);
		    	int alarm_number = Integer.parseInt( alarm_num.getText().toString() );
		    	String temp = db.readRow(alarm_number);
		    	if (showToast){
		    		Toast.makeText(getApplicationContext(), temp, Toast.LENGTH_LONG).show();
		    	}
		        return temp;
	}

	public void readCurrent(View view){
		read(true); // make a boolean parameter for read so that you can make a toast of all the saved alarms
		//this one should read from the database

	}

	//for use with setAlarmText, takes in an the new alarm that you want to add, the .txt file that contains all 
	//alarms, the alarm number that you want to add. If you add an alarm 3 it will look to see if there is already
	//an alarm 3 and if there is it will replace it with the new alarm, otherwise it will just add an alarm 3 to
	//the .txt that keeps all of the saved alarms.
	public String appendOrReplaceAlarm(String newAlarm, String oldFile, int alarmNumber){
		String alarmNumStr = "alarm" + Integer.toString(alarmNumber);
		if (oldFile.contains( alarmNumStr )){
			int start = oldFile.indexOf(alarmNumStr);
			int end = oldFile.indexOf("]", start); //get the full length of the old alarm string
			String oldAlarm = oldFile.substring(start, end +1);
			Log.i(" old and new", "oldAlarm = " + oldAlarm + " newAlarm = " + newAlarm);
			return oldFile.replace(oldAlarm, newAlarm);

		}
		else{
			//toast.makeText(getApplicationContext(), "we are appending !!", Toast.LENGTH_SHORT).show();
			return oldFile.concat(newAlarm);

		}

	}

	// whatever user currently has typed in for alarm number this function will take that number and return the 
	//alarm string associated with it
	public String getAlarmString(){
    	//EditText alarm_num = (EditText) findViewById(R.id.alarm_number);
    	//alarm_number = Integer.parseInt( alarm_num.getText().toString() );
    	alarm_number = 1;
    	String alarmNumStr = "alarm" + Integer.toString(alarm_number);
    	String oldFile = read(false);
		if (oldFile.contains( alarmNumStr )){
			int start = oldFile.indexOf(alarmNumStr);
			int end = oldFile.indexOf("]", start); //get the full length of the old alarm string
			return oldFile.substring(start, end); //alarm string we need to pull out
		}
		else{
			//toast.makeText(getApplicationContext(), "There is no Alarm " + alarm_number + " saved!", Toast.LENGTH_SHORT).show();
			return ""; // no alarm x saved
		}

	}*/

}
        