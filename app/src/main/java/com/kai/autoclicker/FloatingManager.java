package com.kai.autoclicker;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

public class FloatingManager {
    private WindowManager mWindowManager;
    private static FloatingManager mInstance;
    private Context mContext;
    private static final String ACTIVITY_TAG="FloatingManager";

    private FloatingManager(Context context){
        mContext = context;
        mWindowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        Log.e(ACTIVITY_TAG,"getWindowService");
    }

    public static FloatingManager getInstance(Context context){
        if(mInstance == null){
            mInstance = new FloatingManager(context);
            Log.e(ACTIVITY_TAG,"First time create mInstance.");
        }
        else {
            Log.e(ACTIVITY_TAG, "mInastance is already exist.");
        }
        return mInstance;
    }

    protected boolean addView(View view, WindowManager.LayoutParams params) {
        try {
            mWindowManager.addView(view, params); //show floatingView
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    protected boolean removeView(View view) {
        try {
            mWindowManager.removeView(view);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    protected boolean updateView(View view, WindowManager.LayoutParams params) {
        try {
            mWindowManager.updateViewLayout(view, params);

            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }



}
