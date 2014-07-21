package com.example.tempalarm;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.io.*;
import java.net.URL;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.SystemClock;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

//~RT 04-02-2014 10:25 PM can't pull info form text files yet , alarm works sending reveiving on boot etc. prob need to remove
//repeating alarm


//~RT 03-25-2014 05:19 PM 
//I changed the alarm to am.set() from am.setRepeating() shouldn't cause boot to not repeat but if it does just change it back and
//figure out a way to cut it off through like am.cancel or something//

public class AlarmManagerBroadcastReceiver extends BroadcastReceiver {

	final public static String ONE_TIME = "onetime";
	final public static String HOUR  = "hour";
	final public static String MINUTES  = "minutes";
	final public static int MILLINDAY = 86400000; // milliseconds in a day
	final public static int MILLINHOUR = 3600000; // milliseconds in a hour
	final public static int MILLINMIN= 60000; // millisecoands in minute
	public Context globeContext;

	@Override
	public void onReceive(Context context, Intent intent) {
		 PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
         PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "YOUR TAG");
         wl.acquire();
         Toast.makeText(context, "Alarm received !", Toast.LENGTH_SHORT).show();
         if ("android.intent.action.BOOT_COMPLETED".equals(intent.getAction())){
        	 	sendAlarms(null, true, context);
         }
         globeContext = context;

         Calendar c = Calendar.getInstance(); 
         double c_min = c.get(Calendar.MINUTE), c_hour = c.get(Calendar.HOUR_OF_DAY); 
         double time = c_hour + (c_min/100);
         DatabaseHandler dbhand = new DatabaseHandler(context);
         String[] daysOfWeek = DatabaseHandler.DAYS;//new String[7];

         AudioManager mgr= (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
         Ringtone alarm_ring = AlarmManagerActivity.getRingtone(context);
         int alarm_stream_type = alarm_ring.getStreamType();
         int alarm_max_volume = mgr.getStreamMaxVolume(alarm_stream_type);
         Uri alert =  RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
         MediaPlayer mMediaPlayer = new MediaPlayer();
         try{
         mMediaPlayer.setDataSource(context, alert);
         mMediaPlayer.setAudioStreamType(AudioManager.STREAM_RING);
         mMediaPlayer.setLooping(true);
         mMediaPlayer.prepare();
         }catch(Exception e) {} //hasnt crashed yet
         
         int count = 0;
         String currentDay = daysOfWeek[c.get(Calendar.DAY_OF_WEEK) -1]; 
         int alarmFromDB = dbhand.getAlarmThatShouldBePlaying(time, currentDay); //returns an alarm that should be playing if there isn't one return -1
         if ( alarmFromDB != -1){
        	 ContentValues conVal = dbhand.getRowContent(alarmFromDB);
        	 double tempHour = conVal.getAsDouble(dbhand.KEY_HOUR),tempMinute =  conVal.getAsDouble(dbhand.KEY_MINUTE), 
        			 tempLength = conVal.getAsDouble(dbhand.KEY_LENGTH);
        	 double alarmStart = tempHour + (tempMinute/100); // ie: 9.2 for 9:20 AM, or 17.4 for 5:40 PM
        	 double alarmEnd = alarmStart + (tempLength/100); // from alarms start time to the duration like 9:20AM til 9:40 , cuz dur = 20
        	 //Toast.makeText(context, "temp hour = " + tempHour + " temp minute = " + tempMinute, Toast.LENGTH_LONG).show();
        	 Toast.makeText(context,"alarm # = " + alarmFromDB + "alarm start = " + alarmStart + " alarm end = " + 
        			 alarmEnd, Toast.LENGTH_SHORT).show();
        	 Log.i("in alarm recieved ", "temp hour  = " + tempHour + " temp minute = " + tempMinute);
        	 Log.i("in alarm received", "alarm# == " + alarmFromDB + " alarm start = " + alarmStart + " alarm end = " + alarmEnd);
        	 if (time>= alarmStart && time <alarmEnd){
                 alarmLoopThread.start();
                 Intent loop = new Intent(context, HUD.class);
                 context.startService(loop);
             }
         }
	}
        
	public void SetAlarm(Context context)
    {
        AlarmManager am=(AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmManagerBroadcastReceiver.class);
        intent.putExtra(HOUR, "hours");
        intent.putExtra(ONE_TIME, Boolean.FALSE);
        intent.putExtra(HOUR, "hours");
        intent.putExtra(MINUTES, MINUTES);
        PendingIntent pi = PendingIntent.getBroadcast(context, 0, intent, 0);
        //After after 30 seconds
        am.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 1000 * 5 , pi); 
    }

    public void CancelAlarm(Context context)
    {
        Intent intent = new Intent(context, AlarmManagerBroadcastReceiver.class);
        PendingIntent sender = PendingIntent.getBroadcast(context, 0, intent, 0);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(sender);
    }

    /**
     * Sends an alarm that will be received at some point in the future from onReceive(). It will send an alarm for each
     * day that has an active alarm, assuming the alarm itself is active at all. FIX NOT CHANGING FOR something like day 364
     * alarm getting sent on day 2 of the next year
     * @example
     * e.g. alarm 1 is passed to sendAlarms and it didn't come from boot, it is currently active and it has Saturday and Sunday
     * as active days. If today is Wednesday this sendAlarm method will send alarms for alarm1's time and duration for both 
     * Saturday via AlarmManager.set(the time (72 hours), pending intent) and Sunday. If the user still has that alarm as 
     * active and those days as active and the alarm for the same time when it is received , it will start the alarm. 
     * @param cVals - ContentValues of the alarm that will be sent
     * @param from_boot - did the sendAlarm get called from the phone just booting on? this call only happens from onReceive    
     * @return return nothing, void
     * 	  
     */
	public void sendAlarms(ContentValues cVals, Boolean from_boot, Context context) {
		Toast.makeText(context, "IN BROADCAStRECEIVER, send ALARMS", Toast.LENGTH_SHORT).show();
		AlarmManager am=(AlarmManager)context.getSystemService(Context.ALARM_SERVICE); 	
	    Intent intent = new Intent( context , AlarmManagerBroadcastReceiver.class);	
	    DatabaseHandler dbhand = new DatabaseHandler(context);
	    String fullDBStr = dbhand.readDatabase();
	    
	    intent.putExtra(ONE_TIME, true);
	    intent.putExtra(HOUR, cVals.getAsInteger(DatabaseHandler.KEY_HOUR));
	    intent.putExtra(MINUTES, cVals.getAsInteger(DatabaseHandler.KEY_MINUTE));  
	    int alarmRow = cVals.getAsInteger(DatabaseHandler.KEY_ALARM);
	    
	    if (from_boot){
	    	PendingIntent pi = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_ONE_SHOT);
	    	am.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() , pi);
	    }
	    else{
		    
		    Calendar c = Calendar.getInstance(); 
		    double c_min = c.get(Calendar.MINUTE), c_hour = c.get(Calendar.HOUR_OF_DAY);
		    int currentDay = c.get(Calendar.DAY_OF_YEAR); // January 1st = 1, February 22 = 53 (total days in January(31) + 22)
		    long alarm_hour = cVals.getAsInteger(DatabaseHandler.KEY_HOUR), alarm_minutes = cVals.getAsInteger(DatabaseHandler.KEY_MINUTE);	    
		    int seconds = c.get(Calendar.SECOND) *1000; // seconds converted to milliseconds
		    
	    	String DAYS[] = DatabaseHandler.DAYS;
	    	int dayIntValue = 0; // if today is wednesday , value of 162, sunday will be166 and Monday will be 167 etc.
		    for (int i = 0; i < 7; i++){
		    	int day = c.get(Calendar.DAY_OF_WEEK);
		    	if (day == 1){ day = 6;} // they start with sunday (sunday =1), i end with sunday , my sunday = 6
		    	else{
		    		day = day -2; // there monday is 2 mine is 1, there saturday is 7, mine is 5 
		    	}
		    	if(day == i){ dayIntValue = i;}
		    	//if ( cVals.getAsInteger(DAYS[i]) == 1){} // if alarm is active for particular day it will = 1, else = 0
		    }
		    int dayCount = 0; // 7 days of the week, we start on wednesday we want to end on tuesday, wrapping around
		    long currentMill = (currentDay * this.MILLINDAY) + (c.get(Calendar.HOUR_OF_DAY) * this.MILLINHOUR) + 
		    		(c.get(Calendar.MINUTE) * this.MILLINMIN) + seconds; //total time this exact second is in milliseconds
		    //Toast.makeText(context, "day of the year is " + currentDay, Toast.LENGTH_SHORT).show();
		    //Toast.makeText(context, "the alarm will send at " + alarm_hour + ":" + alarm_minutes, Toast.LENGTH_SHORT).show();
		    int firstDay = 0; // send a toast to the user to let them know when this alarm will send, only for the next one , not all days
		    while (dayCount != 7){ // loop through the days if starting at wednesday you will go to sunday, then mon , finally tues
		    	if ( cVals.getAsInteger(DAYS[dayIntValue]) == 1){  		
		    		long timeFuture =( (currentDay + dayCount) * this.MILLINDAY) + (alarm_hour* this.MILLINHOUR) + 
				    		(alarm_minutes * this.MILLINMIN); //time in the future this will fire 
		    		//long timeToSend = timeFuture - currentMill; //if today is monday 160thday 8:00AM and alarm is on wednesday 5:10PM
		    		long timeToSend = timeFuture - currentMill;
		    		if(timeFuture < currentMill){ timeToSend+= 7*this.MILLINDAY; }
		    		//above is for same day alarm but earlier than current hour, e.g. today is sunday 6PM and alarm is sunday at 8AM the alarm
		    		//would say that it will send in -10 hours, but really it should be (1 week from now - 10 hours)
		    		if (firstDay == 0){
		    			firstDay++;// we don't want to send any more toasts, just one for the first alarm for this alarm
		    			String willSend = "This alarm is set for, " + (timeToSend/this.MILLINDAY) + " days, " + (timeToSend/this.MILLINHOUR)%24
		    					+ " hours, " + (timeToSend/this.MILLINMIN)%60 + " minutes." ; 
		    			//the modulo is for like if it were going to send 98 hours from now we want to say 4 days, 2 hours not 4 days 98 hours
		    			Toast.makeText(context, "" + willSend, Toast.LENGTH_SHORT).show();
		    		}
		    		//we will send it in 162days 17 hours 10 minutes - 160days 8 hours 0 minutes , all in milliseconds
		    		// days form now this will send, in milliseconds, total time til this point is currentMill + days+hours+mins from now
		    		Log.i("days", " timeToSend = " + timeToSend);
		    		Log.i("days", "systemcurrentTimeMillis() = " +  System.currentTimeMillis());
		    		PendingIntent pi = PendingIntent.getBroadcast(context, dayIntValue/*"alarm " + alarmRow + " , day " + DAYS[dayIntValue]*/
		    				, intent, PendingIntent.FLAG_ONE_SHOT); //have to have one shot flag and unique id, otherwise these alarms
		    				//would all get overwritten
			        am.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + timeToSend, pi);
			        //am.setExact(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() , pi);
		    	} 
		    	dayIntValue = (dayIntValue+1) %7; // sunday is 6, monday is 0, wednesday is 2 etc. we don't always start at monday
		    	dayCount++;
		    }
	    }
	}
    
    //RT 5:52PM 04/18/14 hid looping of audio setloop(true)
	Thread alarmLoopThread = new Thread()
	{
	    @Override
	    public void run() {
	    	 Context context = globeContext;
	         Calendar c = Calendar.getInstance(); 
	         double c_min = c.get(Calendar.MINUTE), c_hour = c.get(Calendar.HOUR_OF_DAY); 
	         double time = c_hour + (c_min/100);
	         DatabaseHandler dbhand = new DatabaseHandler(context);
	         String[] daysOfWeek = DatabaseHandler.DAYS;

	         AudioManager mgr= (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
	         Ringtone alarm_ring = AlarmManagerActivity.getRingtone(context);
	         int alarm_stream_type = alarm_ring.getStreamType();
	         int alarm_max_volume = mgr.getStreamMaxVolume(alarm_stream_type);
	         Uri alert =  RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
	         MediaPlayer mMediaPlayer = new MediaPlayer();
	         try{
	         mMediaPlayer.setDataSource(context, alert);
	         mMediaPlayer.setAudioStreamType(AudioManager.STREAM_RING);
	         //mMediaPlayer.setLooping(true);
	         mMediaPlayer.prepare();
	         }catch(Exception e) {} //hasnt crashed yet

	         int count = 0;//
	         String currentDay = daysOfWeek[c.get(Calendar.DAY_OF_WEEK) -1]; 
	         int alarmFromDB = dbhand.getAlarmThatShouldBePlaying(time, currentDay); //returns an alarm that should be playing if there isn't one return -1
	         if ( alarmFromDB != -1){
	        	 ContentValues conVal = dbhand.getRowContent(alarmFromDB);
	        	 double tempHour = conVal.getAsDouble(dbhand.KEY_HOUR),tempMinute =  conVal.getAsDouble(dbhand.KEY_MINUTE), 
	        			 tempLength = conVal.getAsDouble(dbhand.KEY_LENGTH);
	        	 double alarmStart = tempHour + (tempMinute/100); // ie: 9.2 for 9:20 AM, or 17.4 for 5:40 PM
	        	 double alarmEnd = alarmStart + (tempLength/100); // from alarms start time to the duration like 9:20AM til 9:40 , cuz dur = 20
	        	 Log.i("in alarm recieved ", "temp hour  = " + tempHour + " temp minute = " + tempMinute);
	        	 Log.i("in alarm received", "alarm# == " + alarmFromDB + " alarm start = " + alarmStart + " alarm end = " + alarmEnd);


	        	 while(time>= alarmStart && time <alarmEnd){
		        	 if (count == 0){
		        	 	mMediaPlayer.start(); //only start once obv
		        	 } 

		        	 if (mgr.getStreamVolume(alarm_stream_type) != alarm_max_volume){ //stop trying to lower the volume sleepy stupid idiot
		        		 
		        		 //UNHIDE BELOW , JUST FOR TESTING/////
		        		 mgr.setStreamVolume(alarm_stream_type, alarm_max_volume, 0);
		        		 
		        	 }

		        	 c = c.getInstance();
		        	 c_min = c.get(Calendar.MINUTE); c_hour = c.get(Calendar.HOUR_OF_DAY);
		        	 time = c_hour + (c_min/100); 
		        	 count++;	 
		        	 Log.i("AMBR", "in thread ambr, time = " + time + " alarmStart = " + alarmStart + " alarmEnd = " + alarmEnd);
		        	 Log.i("AMBR", "in thread ambr, hour = " + c_hour + " minute = " + c_min);
		        	 if (mMediaPlayer.getCurrentPosition() >=mMediaPlayer.getDuration()/2 ){
		        		//Toast.makeText(context, "BEFORE sending new alarm", //Toast.LENGTH_SHORT).show();
		        		Log.i(" starting new ALARM", "mMediaPlayer.getcurpos = " + mMediaPlayer.getCurrentPosition() + " video duration = " + 
		        				mMediaPlayer.getDuration());
		                mgr =null;//
		                mMediaPlayer.stop();
		                //mMediaPlayer.release();
		                mMediaPlayer.reset();
		                mMediaPlayer = null;
		   	            mgr= (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
			            alarm_ring = AlarmManagerActivity.getRingtone(context);
			            alarm_stream_type = alarm_ring.getStreamType();
			            alarm_max_volume = mgr.getStreamMaxVolume(alarm_stream_type);
			            alert =  RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
			            mMediaPlayer = new MediaPlayer();
			            try{
			         	   mMediaPlayer.setDataSource(context, alert);
			         	   mMediaPlayer.setAudioStreamType(AudioManager.STREAM_RING);
			         	   //mMediaPlayer.setLooping(true);
			         	   mMediaPlayer.prepare();
			            }catch(Exception e) {} //hasnt crashed yet
			            count = 0;

		        	 } 
		         }
	        	 mMediaPlayer.stop();
	        	 mMediaPlayer.release();
                 Intent loop = new Intent(context, HUD.class);
                 context.stopService(loop);
                 sendAlarms(dbhand.getRowContent(alarmFromDB), false, globeContext);

	         }
	    }
	};
      
   /*
	public void setOnetimeTimer(Context context, int hour, int minutes, Boolean from_boot, 
			int alarm_number) {
		AlarmManager am=(AlarmManager)context.getSystemService(Context.ALARM_SERVICE); 	
		AlarmManagerActivity ama = new AlarmManagerActivity();
	    Intent intent = new Intent(context, AlarmManagerBroadcastReceiver.class);
	
	    DatabaseHandler dbhand = new DatabaseHandler(context);
	    String fullDBStr = dbhand.readDatabase();
	    //Toast.makeText(context, fullDBStr, Toast.LENGTH_LONG).show();//
	    
	    Log.i("full db in setOnetime", fullDBStr);
	    
	    intent.putExtra(ONE_TIME, true);
	    intent.putExtra(HOUR, hour);
	    intent.putExtra(MINUTES, minutes);        
	    PendingIntent pi = PendingIntent.getBroadcast(context, 0, intent, 0);
	    
	    Calendar c = Calendar.getInstance(); 
	    double c_min = c.get(Calendar.MINUTE), c_hour = c.get(Calendar.HOUR_OF_DAY);
	    double alarm_hour = hour, alarm_minutes = minutes;//int total = (hour*3600000) + (minutes*60000);
	    c.get(Calendar.DAY_OF_YEAR);
	    if (( alarm_hour+(alarm_minutes/100) ) < ( c_hour+(c_min/100) ) ){//if alarmtime < current , alarm is tomorrow
	    	double c_hourD = (double) c_hour;
	    	double c_minD = (double) c_min;
	    	double time_til_day_over = 24.0d - (c_hourD +(c_minD/100));
	    	alarm_hour += Math.floor(time_til_day_over); //hours left in the day plus hour alarm fires tomorrow
	    	alarm_minutes+= time_til_day_over - Math.floor(time_til_day_over);//minutes left in day plus mins tomorrow
	    }
	    else{
	    	alarm_hour -=c_hour; //hours left til alarm fires today
	    	alarm_minutes -=c_min; //minutes left til alarm fres today
	    }
	
	    long alarm_hour_long = (long) (alarm_hour);
	    long alarm_minutes_long = (long) (alarm_minutes);
	    Long temp = ( System.currentTimeMillis() + (alarm_hour_long*3600000) + (alarm_minutes_long*60000) )
	    		- System.currentTimeMillis() ;
	    Log.i(" times milleseconds ",  " time til alarm  = " + temp);
	    Log.i("alarm_hour + alarm mins", (alarm_hour_long*3600000) + (alarm_minutes_long*60000) + " ");
	    int seconds = c.get(Calendar.SECOND) *1000;
	
	    if (from_boot){
	    	am.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() , pi);
	    }
	    else{
	    	 //~RT 03-25-2014 05:15 PM 
	    	Log.i("time stuff", "hour = " + hour + " minutes = " + minutes);
	    	Toast.makeText(context, "in setonetimetimer(), hour = " + hour + " min = "+ minutes, Toast.LENGTH_SHORT).show();
	    	Bundle extras = intent.getExtras();
	    	//Toast.makeText(context, "getEXTRASSS in setonetimetimer, = " + extras.getInt(HOUR) +
	    		//" min = "+ extras.getInt(MINUTES), Toast.LENGTH_SHORT).show();
	    	long tempor = System.currentTimeMillis() + (alarm_hour_long*3600000) + 
	        		(alarm_minutes_long*60000) - seconds;
   		Log.i("days", " in ONETIME TIMERR, timeToSend = " + tempor);
   		Log.i("days", "systemcurrentTimeMillis() = " +  System.currentTimeMillis());
	        am.set(AlarmManager.RTC_WAKEUP, tempor, pi);
	        //am.cancel(null);
	    }
	}*/
   
}