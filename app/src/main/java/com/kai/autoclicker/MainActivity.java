package com.kai.autoclicker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;


public class MainActivity extends AppCompatActivity {

    private Button mStart;
    private EditText mInterval;
    private RadioButton mTapMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (!Settings.canDrawOverlays(this)) {                      //for android 6+,用來確認權限,是否能夠讓此app, overlay在別的app上面
            int REQUEST_CODE = 101;
            Intent myIntent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
            myIntent.setData(Uri.parse("package:" + getPackageName()));
            startActivityForResult(myIntent, REQUEST_CODE);

            int REQUEST_CODE_2 = 102;
            Intent myintent_2 = new Intent(android.provider.Settings.ACTION_ACCESSIBILITY_SETTINGS);
            startActivityForResult(myintent_2, REQUEST_CODE_2);
        }



        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mStart = findViewById(R.id.start);
        mInterval = findViewById(R.id.interval);
        mTapMode = findViewById(R.id.tap);
        mStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(MainActivity.this, AutoService.class);
                intent.putExtra(AutoService.ACTION, AutoService.SHOW);
                String interval = mInterval.getText().toString();
                intent.putExtra("interval", Integer.valueOf(!interval.isEmpty()?interval:"10"));
                intent.putExtra(AutoService.MODE, AutoService.TAP);

                startService(intent);

                moveTaskToBack(true);
            }
        });









    }
}