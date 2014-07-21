package com.example.tempalarm;




import java.util.concurrent.TimeUnit;

import android.app.Service;
import android.app.WallpaperManager;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.MotionEvent.PointerProperties;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.Toast;

public class HUD extends Service implements OnTouchListener, OnKeyListener {

	//770,950
	long down_time_start;
	long down_time_end;
	String name = "";
	int count = 0;
	float track_x = 0;//
	float track_y = 0;
	WindowManager.LayoutParams correct_peram = null;

    Button mButton;
    @Override
    public IBinder
    onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        count = 0;//dont think i need this
        mButton = new Button(this);
        //Toast.makeText(this,"HUD starting up!", Toast.LENGTH_SHORT).show();
        mButton.setText(" WAKE UP!! ");
        mButton.setOnTouchListener(this);
        
        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
        
		        WindowManager.LayoutParams.WRAP_CONTENT,
		        WindowManager.LayoutParams.WRAP_CONTENT,
		        WindowManager.LayoutParams.TYPE_PHONE, // chaned to phone 2/4/14 , from system overlay
		        //WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY,
		        WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
		        | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH 
		        ,PixelFormat.TRANSLUCENT);
        		//above is the test

        //params.gravity = Gravity.RIGHT | Gravity.TOP;
        params.gravity = Gravity.FILL; 
        params.setTitle("Load Average");
        WindowManager wm = (WindowManager) getSystemService(WINDOW_SERVICE);
        wm.addView(mButton, params);
        correct_peram = params; //save this for use in on touch   
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(mButton != null)
        {
            ((WindowManager) getSystemService(WINDOW_SERVICE)).removeView(mButton);
            mButton = null;
        }
    }


	public boolean onTouch(View v, MotionEvent event) {
		//Toast.makeText(this,"  WAKE UP!  ", Toast.LENGTH_SHORT).show();
		int action_type_int = event.getActionMasked(); //int constant for motion type, 1 is motion down etc.
		String action_type = ""; //motion down, motion up etc. defined by int constants in switch below
		if (action_type_int == 0){action_type = "Touch Down";}
		else if((action_type_int == 1)){action_type = "Touch Up";}
		else if((action_type_int == 2)){action_type = "Touch Move";}
		else if((action_type_int == 4)){action_type = "Touch Outside";}
		else if((action_type_int == 8)){action_type = "Touch Scroll";}
		else{action_type = "Touch Unknown";}

		if (action_type == "Touch Down"){
			down_time_start = //event.getDownTime();
					SystemClock.elapsedRealtime();		
		}
		else if (action_type == "Touch Up"){
			down_time_end = SystemClock.elapsedRealtime();
		}

		int pointer_index = event.getActionIndex(); //location where event above occured
		int pointer_id = event.getPointerId(pointer_index); //pointer id of the pointer_index
		float x_cord = event.getX(pointer_index); 
		float y_cord = event.getY(pointer_index);//

		if (action_type == "Touch Down"){
			//Toast.makeText(this,  action_type + " at{" + x_cord + "," + y_cord + "}" , Toast.LENGTH_SHORT).show();
				track_x = x_cord;
				track_y = y_cord;
		}
		else if (action_type == "Touch Up"){
			if (down_time_end - down_time_start >= 1000){  //only used when debugging so i can get past the brick
				//System.runFinalizersOnExit(true);
				//System.exit(0); 
				//stopSelf();
			}
		}
		else if (action_type == "Touch Outside"){
			//Toast.makeText(this,action_type + " at{" + x_cord + "," + y_cord + "}" + " {" + event.getX() + "," + event.getY() + "}", Toast.LENGTH_SHORT).show();
		}

		return false;
	}
    
	@Override
	public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
		Toast.makeText(this, "keycode = " + keyCode + " KeyEvent = " + event+" DialogInterface = "+dialog, Toast.LENGTH_SHORT).show();
		return false;
	}
    
}
