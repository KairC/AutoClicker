package com.kai.autoclicker;

import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

public class FloatingView extends FrameLayout implements View.OnClickListener {
    private static final String ACTIVITY_TAG="FloatingView";
    private Context mContext;
    private View mView;
    private View mView2;
    private ImageView mPlayView;
    private ImageView mStopView;
    private ImageView mCloseView;
    private int mTouchStartX, mTouchStartY;
    private WindowManager.LayoutParams mParams;
    private WindowManager.LayoutParams mParams2;
  private WindowManager mWindowManager;
    private String mCurState;
    public FloatingView(Context context) {
        super(context);
        mContext = context.getApplicationContext();
        LayoutInflater mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mView = mLayoutInflater.inflate(R.layout.floating_view, null);

        /***************************************/
        mView2 = mLayoutInflater.inflate(R.layout.floating_view_2, null);
        /***************************************/


        mPlayView = (ImageView) mView2.findViewById(R.id.play);
        mStopView = (ImageView) mView2.findViewById(R.id.stop);
        mCloseView = (ImageView) mView2.findViewById(R.id.close);
        mPlayView.setOnClickListener(this);
        mStopView.setOnClickListener(this);
        mCloseView.setOnClickListener(this);
        mView.setOnTouchListener(mOnTouchListener);
        mWindowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
    }

    public void show() {
        /*************** the floating view for tapping ********************/
        mParams = new WindowManager.LayoutParams();
        mParams.gravity = Gravity.CENTER;
        mParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        mParams.format = PixelFormat.RGBA_8888;
        mParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL |
                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN | WindowManager.LayoutParams.FLAG_LAYOUT_INSET_DECOR |
                WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH;
        mParams.width = LayoutParams.WRAP_CONTENT;
        mParams.height = LayoutParams.WRAP_CONTENT;
        mWindowManager.addView(mView, mParams);
        /*************** option bar which to play,stop,close ***************/
        mParams2 = new WindowManager.LayoutParams();
        mParams2.gravity = Gravity.LEFT;
        mParams2.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        mParams2.format = PixelFormat.RGBA_8888;
        mParams2.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL |
                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN | WindowManager.LayoutParams.FLAG_LAYOUT_INSET_DECOR;
        mParams2.width = LayoutParams.WRAP_CONTENT;
        mParams2.height = LayoutParams.WRAP_CONTENT;
        mWindowManager.addView(mView2, mParams2);
        /********************************************************************/
        Log.e(ACTIVITY_TAG,"addView");
        Log.e(ACTIVITY_TAG,"The beginning position is : (" + mParams.x + "," + mParams.y+ ")");

    }

    public void hide() {
        mWindowManager.removeView(mView);
        mWindowManager.removeView(mView2);
    }

    private OnTouchListener mOnTouchListener = new OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent event) {
            switch (event.getAction()) {

                case MotionEvent.ACTION_DOWN:

                    mTouchStartX = (int) event.getRawX();       //getRawX():相對於螢幕左邊的距離
                    mTouchStartY = (int) event.getRawY();       //getRawY():相對於螢幕頂部的距離
                    Log.e(ACTIVITY_TAG,"Now You touch on (" + mTouchStartX + "," + mTouchStartY + ")");

                    int[] location = new int[2];
                    mView.getLocationOnScreen(location);
                    Log.e(ACTIVITY_TAG,"Now the view position is : (" + location[0]  + "," + location[1]  + ")");
                    Log.e(ACTIVITY_TAG,"Now the view's width & height is : (" + mView.getWidth()/2  + "," + mView.getHeight()/2  + ")");
                    Log.e(ACTIVITY_TAG,"Now the center position is : (" + (location[0] + mView.getWidth()/2) + "," + (location[1] + mView.getHeight()/2) + ")");

                    break;

                case MotionEvent.ACTION_MOVE:

                    if (!AutoService.PLAY.equals(mCurState)) {  //當mCurState還在PLAY 不給移動
                        mParams.x += (int) event.getRawX() - mTouchStartX;      //mParams.x (mParams.y) 當有給定gravity時，起始位置為原點(0,0)，代表與View起始位置的相對距離
                        mParams.y += (int) event.getRawY() - mTouchStartY;      //e.g. gravity為CENTER，螢幕正中間位置為原點，當mParams.x與mParams.y各自+1，View會往右下方移動
                        //各自-1則往左上方移動，以此類推。
                        Log.e(ACTIVITY_TAG,"event.getRawX is :" + event.getRawX());
                        Log.e(ACTIVITY_TAG,"event.getRawY is :" + event.getRawY());
                        Log.e(ACTIVITY_TAG,"mTouchStartX :" + mTouchStartX);
                        Log.e(ACTIVITY_TAG,"mTouchStartY :" + mTouchStartY);
                        Log.e(ACTIVITY_TAG,"Coordinate now is: (" + mParams.x + "," + mParams.y + ")");
                        mWindowManager.updateViewLayout(mView,mParams);
                        mTouchStartX = (int) event.getRawX();
                        mTouchStartY = (int) event.getRawY();
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    break;
            }
            return true;
        }
    };

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(getContext(), AutoService.class);
        switch (v.getId()) {
            case R.id.play:

                mParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN |
                        WindowManager.LayoutParams.FLAG_LAYOUT_INSET_DECOR |
                        WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH;  //把mView設為FLAG_NOT_TOUCHABLE，就能讓Touch Event穿過該image view，而不會被攔截
                mWindowManager.updateViewLayout(mView,mParams);
                mCurState = AutoService.PLAY;
                int[] location = new int[2];
                mView.getLocationOnScreen(location);
                Log.e(ACTIVITY_TAG,"Now the center position of the target is : (" + (location[0] + mView.getWidth()/2) + "," + (location[1] + mView.getHeight()/2) + ")");

                intent.putExtra(AutoService.ACTION, AutoService.PLAY);
                intent.putExtra("x", location[0] + mView.getWidth()/2);
                intent.putExtra("y", location[1] + mView.getHeight()/2);
                break;
            case R.id.stop:
                mParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL |
                        WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN | WindowManager.LayoutParams.FLAG_LAYOUT_INSET_DECOR |
                        WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH;
                mWindowManager.updateViewLayout(mView,mParams);
                mCurState = AutoService.STOP;
                intent.putExtra(AutoService.ACTION, AutoService.STOP);
                break;
            case R.id.close:
                intent.putExtra(AutoService.ACTION, AutoService.HIDE);

                break;
        }
        getContext().startService(intent);
    }
}
