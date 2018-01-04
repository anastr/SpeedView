package com.github.anastr.speedview;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import com.github.anastr.speedviewlib.AwesomeSpeedometer;

import java.util.Locale;

public class AwesomeSpeedometerActivity extends AppCompatActivity {

    AwesomeSpeedometer awesomeSpeedometer;
    SeekBar seekBar;
    Button speedTo, realSpeedTo, stop;
    TextView textSpeed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_awesome_speedometer);

        awesomeSpeedometer = findViewById(R.id.awesomeSpeedometer);
        seekBar = findViewById(R.id.seekBar);
        speedTo = findViewById(R.id.speedTo);
        realSpeedTo = findViewById(R.id.realSpeedTo);
        stop = findViewById(R.id.stop);
        textSpeed = findViewById(R.id.textSpeed);

        speedTo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                awesomeSpeedometer.speedTo(seekBar.getProgress());
            }
        });

        realSpeedTo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                awesomeSpeedometer.realSpeedTo(seekBar.getProgress());
            }
        });

        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                awesomeSpeedometer.stop();
            }
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                textSpeed.setText(String.format(Locale.getDefault(), "%d", progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
    }
}
