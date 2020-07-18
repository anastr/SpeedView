package com.github.anastr.speedviewapp;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.github.anastr.speedviewlib.RaySpeedometer;

import java.util.Locale;

public class RayActivity extends AppCompatActivity {

    RaySpeedometer raySpeedometer;
    SeekBar seekBarSpeed, seekBarDegree, seekBarWidth;
    Button ok;
    TextView textSpeed, textDegree, textWidth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ray);
        setTitle("Ray Speedometer View");

        raySpeedometer = findViewById(R.id.raySpeedometer);
        seekBarSpeed = findViewById(R.id.seekBarSpeed);
        seekBarDegree = findViewById(R.id.seekBarDegree);
        seekBarWidth = findViewById(R.id.seekBarWidth);
        ok = findViewById(R.id.ok);
        textSpeed = findViewById(R.id.textSpeed);
        textDegree = findViewById(R.id.textDegree);
        textWidth = findViewById(R.id.textWidth);

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                raySpeedometer.speedTo(seekBarSpeed.getProgress());
            }
        });

        seekBarSpeed.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
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

        seekBarDegree.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                textDegree.setText(String.format(Locale.getDefault(), "%d", progress));
                raySpeedometer.setDegreeBetweenMark(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        seekBarWidth.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                textWidth.setText(String.format(Locale.getDefault(), "%ddp", progress));

                raySpeedometer.setRayMarkWidth((int) raySpeedometer.dpTOpx(progress));
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
