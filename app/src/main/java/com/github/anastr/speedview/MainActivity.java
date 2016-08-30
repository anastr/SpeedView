package com.github.anastr.speedview;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void openSpeedViewActivity(View view) {
        Intent intent = new Intent (MainActivity.this, SpeedViewActivity.class);
        startActivity(intent);
    }

    public void openDeluxeSpeedActivity(View view) {
        Intent intent = new Intent (MainActivity.this, DeluxeSpeedActivity.class);
        startActivity(intent);
    }

    public void openAwesomeSpeedometerActivity(View view) {
        Intent intent = new Intent (MainActivity.this, AwesomeSpeedometerActivity.class);
        startActivity(intent);
    }
}
