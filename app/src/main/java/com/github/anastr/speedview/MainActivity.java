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

    public void openRaySpeedometerActivity(View view) {
        Intent intent = new Intent (MainActivity.this, RayActivity.class);
        startActivity(intent);
    }

    public void openPointerSpeedometerActivity(View view) {
        Intent intent = new Intent (MainActivity.this, PointerActivity.class);
        startActivity(intent);
    }

    public void openTubeSpeedometerActivity(View view) {
        Intent intent = new Intent (MainActivity.this, TubeSpeedometerActivity.class);
        startActivity(intent);
    }

    public void openImageSpeedometerActivity(View view) {
        Intent intent = new Intent (MainActivity.this, ImageSpeedometerActivity.class);
        startActivity(intent);
    }

    public void openWorkWithNoteActivity(View view) {
        Intent intent = new Intent (MainActivity.this, WorkWithNoteActivity.class);
        startActivity(intent);
    }

    public void openCreateProgrammatically(View view) {
        Intent intent = new Intent (MainActivity.this, CreateProgrammatically.class);
        startActivity(intent);
    }
}
