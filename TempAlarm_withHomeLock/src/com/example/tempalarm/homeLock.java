package com.example.tempalarm;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Set;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Toast;

public class homeLock extends Activity{
	//protected Intent loop = new Intent(getApplicationContext(), HUD.class);//AMA class can't call correctly, don't need protected
	public static Context homeLockContext;
	final static Object signal = new Object();
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.example.tempalarm.R.layout.home_lock);       
        homeLockContext = getApplicationContext();
        Toast.makeText(homeLockContext, "in home lock " + getIntent().getAction(), Toast.LENGTH_SHORT).show();
        
        if (isMyAppLauncherDefault()) {
        	Toast.makeText(homeLockContext, "alarm is set as HOME ", Toast.LENGTH_SHORT).show();	
	        Intent in = new Intent(homeLockContext, AlarmManagerActivity.class);
	        in.addFlags( Intent.FLAG_ACTIVITY_NEW_TASK);
	        in.putExtra("from homeLock", true);//
	        //Toast.makeText(homeLockContext, "Action = " + getIntent().getAction() + " ", Toast.LENGTH_SHORT).show();
	        if ( getIntent().getAction() != null){
	        	homeLockContext.startActivity(in );
	        	Intent loop = new Intent(homeLockContext, HUD.class);
	            homeLockContext.startService(loop);
	        }
        
        }
        
        //Toast.makeText(homeLockContext, " just created homelock ", Toast.LENGTH_SHORT).show();
        //while(System.currentTimeMillis() < sup + 7000){}

        /*
        AlarmManagerActivity ama = new AlarmManagerActivity();
        String[] daysOfWeek = DatabaseHandler.DAYS;
        Calendar c = Calendar.getInstance(); 
        int calendarDay = c.get(Calendar.DAY_OF_WEEK);//they start sunday = 1, monday = 2, mine starts w/ m = 0
        if ( calendarDay == 1){ calendarDay = 6; }//theres starts with sunday (1) mine ends with sun (6)
        else{ calendarDay = calendarDay -2;}
        String currentDay = daysOfWeek[calendarDay];//after getting the order of their array to match mine get day
        DatabaseHandler dataB = ama.db;
        int alarmShould = -1;
        if(dataB != null){ alarmShould = dataB.getAlarmThatShouldBePlaying(System.currentTimeMillis(), currentDay); }
        Toast.makeText(homeLockContext, "in home lock, the alarm that should be play is " + alarmShould, 
        		Toast.LENGTH_SHORT).show();
        if (alarmShould != -1){
	        homeLockContext = getApplicationContext();
	        
	        alarmLoopThread.start();
	        loop = new Intent(homeLockContext, HUD2.class);
	        homeLockContext.startService(loop);
        }
        
        else{
        	//ama.sendAlarm(dataB.getRowContent(alarmShould), true);//what should really be, below is for testing
        	if (dataB == null){
        		ama.sendAlarm(dataB.getRowContent(1), true);//
        	}
        	else{
        		//ama.sendAlarm(dataB.getRowContent(1), true);
        		dataB.alarmContext.sendAlarm(dataB.getRowContent(1), true);
        	}
        	//removeHomeLock(homeLockContext);
        	
        }
        */
        /*
	    PackageManager p = c.getPackageManager();
	    ComponentName cN = new ComponentName(c, homeLock.class);
	    p.setComponentEnabledSetting(cN, PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);

	    Intent selector = new Intent(Intent.ACTION_MAIN);
	    selector.addCategory(Intent.CATEGORY_HOME);            
	    c.startActivity(selector);	       
	    p.setComponentEnabledSetting(cN, PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
	    */  
	       
        /*
        homeLockContext = getApplicationContext();
        alarmLoopThread.start();
        Intent loop = new Intent(homeLockContext, HUD2.class);
        homeLockContext.startService(loop);
        AlarmManagerActivity ama = new AlarmManagerActivity();
        String[] daysOfWeek = DatabaseHandler.DAYS;
        Calendar c = Calendar.getInstance(); 
        String currentDay = daysOfWeek[c.get(Calendar.DAY_OF_WEEK) -1];
        DatabaseHandler dataB = ama.db;
        if(dataB == null){ Toast.makeText(homeLockContext, "database is nul yo", Toast.LENGTH_SHORT).show();}
        int alarmShould = dataB.getAlarmThatShouldBePlaying(System.currentTimeMillis(), currentDay);
        ama.sendAlarm(ama.db.getRowContent(1), true);
       	*/
        /*if (alarmShould != -1){
    	    ama.sendAlarm(ama.db.getRowContent(alarmShould), true);//
        }*/
    }
    
    /*homeLock(boolean bool){
    	homeLockContext = getApplicationContext();
    }*/
    
	public static void makePrefered(Context c) {
	       PackageManager p = c.getPackageManager();
	       ComponentName cN = new ComponentName(c, FakeHome.class);
	       p.setComponentEnabledSetting(cN, PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);

	       Intent selector = new Intent(Intent.ACTION_MAIN);
	       selector.addCategory(Intent.CATEGORY_HOME);            
	       c.startActivity(selector);	       
	       p.setComponentEnabledSetting(cN, PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
	   }
	
	public static void removeHomeLock(Context c) {
	       PackageManager p = c.getPackageManager();
	       ComponentName cN = new ComponentName(c, homeLock.class);
	       p.setComponentEnabledSetting(cN, PackageManager.COMPONENT_ENABLED_STATE_DISABLED, 0);
	       //Intent selector = new Intent(Intent.ACTION_MAIN);   
	       //selector.removeCategory(Intent.CATEGORY_HOME);
	       //c.startActivity(selector);
			System.exit(0);
	       //p.setComponentEnabledSetting(cN, PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
	   }
	
	public static void removeHud2(Context c) {
			Toast.makeText(c, "remove HUD2 ! ", Toast.LENGTH_SHORT).show();
	       PackageManager p = c.getPackageManager();
	       ComponentName cN = new ComponentName(c, HUD2.class);
	       //p.setComponentEnabledSetting(cN, PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
	       p.setComponentEnabledSetting(cN, PackageManager.COMPONENT_ENABLED_STATE_DISABLED, 0 );
	       //c.startActivity(selector);
	       //p.setComponentEnabledSetting(cN, PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
	   }
	
	Thread alarmLoopThread = new Thread()
	{
	    @Override
	    public void run() {
	    	 /*
	    	 synchronized(signal) {
	    		    try {
						signal.wait();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
	    		}*/
	    	 long tempT = System.currentTimeMillis();
	    	 while (System.currentTimeMillis() < tempT + 7000){}
	         Intent loop = new Intent(homeLockContext, HUD2.class);
	         Toast.makeText(homeLockContext, "stopping the lock ", Toast.LENGTH_LONG).show();
	         homeLockContext.stopService(loop);
	         removeHud2(homeLockContext);
	         
	         removeHomeLock(homeLockContext);
	         //Intent i = new Intent(homeLockContext, AlarmManagerActivity.class);
	         //homeLockContext.startActivity(i);
	    }
	};
	
	public boolean isMyAppLauncherDefault() {
		// http://www.android-ios-tutorials.com/225/get-default-launcher-pogrammatically-android/
	    IntentFilter filter = new IntentFilter(Intent.ACTION_MAIN);
	    filter.addCategory(Intent.CATEGORY_HOME);
	 
	    List<IntentFilter> filters = new ArrayList<IntentFilter>();
	    filters.add(filter);
	 
	    // the packageName of your application
	    String packageName = getPackageName();
	    List<ComponentName> preferredActivities = new ArrayList<ComponentName>();
	    final PackageManager packageManager = (PackageManager) getPackageManager();
	 
	    // You can use name of your package here as third argument
	    packageManager.getPreferredActivities(filters, preferredActivities, packageName);
	 
	    if (preferredActivities != null && preferredActivities.size()> 0) {
	        return true;
	    }
	    return false;
	}
	
	/*    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        //setContentView(com.example.tempalarm.R.layout.home_lock);
        //
        //homeLockContext = getApplicationContext();
        alarmLoopThread.start();
        Intent loop = new Intent(homeLockContext, HUD.class);
        homeLockContext.startService(loop);
      // AlarmManagerActivity ama = new AlarmManagerActivity();
       //String[] daysOfWeek = DatabaseHandler.DAYS;
      // Calendar c = Calendar.getInstance(); 
      // String currentDay = daysOfWeek[c.get(Calendar.DAY_OF_WEEK) -1];
      // DatabaseHandler dataB = ama.db;
      // if(dataB == null){ Toast.makeText(homeLockContext, "database is nul yo", Toast.LENGTH_SHORT).show();}
     //  int alarmShould = dataB.getAlarmThatShouldBePlaying(System.currentTimeMillis(), currentDay);
       
       if (alarmShould != -1){
    	   ama.sendAlarm(ama.db.getRowContent(alarmShould), true);//
       }
    //   ama.sendAlarm(ama.db.getRowContent(1), true);//
       
    }*/
}
