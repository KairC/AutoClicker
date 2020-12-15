package com.kai.autoclicker;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.GestureDescription;
import android.content.Intent;
import android.graphics.Path;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.widget.Toast;

public class AutoService extends AccessibilityService {
    public static final String ACTIVITY_TAG = "AutoService";
    public static final String ACTION = "action";
    public static final String SHOW = "show";
    public static final String HIDE = "hide";
    public static final String PLAY = "play";
    public static final String STOP = "stop";
    public static final String MODE = "mode";
    public static final String TAP = "tap";
    public static final String SWIPE = "swipe";
    private FloatingView mFloatingView;
    private int mInterval;
    private int mX;
    private int mY;
    private String mMode;

    private IntervalRunnable mRunnable;
    private Handler mHandler;

    @Override
    public void onCreate(){
        super.onCreate();

        mFloatingView = new FloatingView(this);
        HandlerThread handlerThread = new HandlerThread("auto-handler");
        handlerThread.start();
        mHandler = new Handler(handlerThread.getLooper());

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId){

        if (intent != null) {
            String action = intent.getStringExtra(ACTION);
            if (SHOW.equals(action)) {
                mInterval = intent.getIntExtra("interval", 10) * 1000;
                mMode = intent.getStringExtra(MODE);
                mFloatingView.show();
                Toast.makeText(getBaseContext(), "Your click interval is "+mInterval/1000+" sec.", Toast.LENGTH_LONG).show();
            } else if (HIDE.equals(action)) {
                mFloatingView.hide();
                mHandler.removeCallbacksAndMessages(null);
                Toast.makeText(getBaseContext(), "AutoClicker closed.", Toast.LENGTH_LONG).show();

            } else if (PLAY.equals(action)) {
                mX = intent.getIntExtra("x", 0);
                mY = intent.getIntExtra("y", 0);
                if (mRunnable == null) {
                    mRunnable = new IntervalRunnable();
                }
                mHandler.postDelayed(mRunnable, mInterval);
                Toast.makeText(getBaseContext(), "Start tapping", Toast.LENGTH_LONG).show();
            } else if (STOP.equals(action)) {
                mHandler.removeCallbacksAndMessages(null);
                Toast.makeText(getBaseContext(), "Stop tapping", Toast.LENGTH_LONG).show();
            }
        }
        return super.onStartCommand(intent, flags, startId); //returning super.onStartCommand() is equivalent to returning START_STICKY
    }
    private void Tapping(int x, int y) {
        Path path = new Path();
        path.moveTo(x, y);
        GestureDescription.Builder builder = new GestureDescription.Builder();
        GestureDescription.StrokeDescription clickstroke = new GestureDescription.StrokeDescription(path, 0, 50);
        builder.addStroke(clickstroke);
        GestureDescription gestureDescription = builder.build();
        Log.e(ACTIVITY_TAG,"built.");
        boolean isDispatch = this.dispatchGesture(gestureDescription, new AccessibilityService.GestureResultCallback(){
            @Override
            public void onCompleted(GestureDescription gestureDescription) {
                Log.e(ACTIVITY_TAG,"touch success.");
                super.onCompleted(gestureDescription);
                mHandler.postDelayed(mRunnable, mInterval);
                Log.e(ACTIVITY_TAG,"Just a test.");
            }

            @Override
            public void onCancelled(GestureDescription gestureDescription) {
                Log.e(ACTIVITY_TAG,"Didn't touch anything.");
                super.onCancelled(gestureDescription);
            }
        }, null);

        Log.e(ACTIVITY_TAG,"The result is " + isDispatch);
    }

    private class IntervalRunnable implements Runnable {
        @Override
        public void run() {
                Tapping(mX, mY);
        }
    }


    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {

    }

    @Override
    public void onInterrupt() {

    }
}
