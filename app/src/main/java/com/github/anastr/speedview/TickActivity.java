package com.github.anastr.speedview;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.TextView;

import com.github.anastr.speedviewlib.Speedometer;

import java.util.Locale;

public class TickActivity extends AppCompatActivity {

    Speedometer speedometer;
    CheckBox withRotation;
    SeekBar seekBarTickNumbers, seekBarTickPadding;
    TextView textTicks, textTickPadding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tick);

        speedometer = (Speedometer) findViewById(R.id.speedometer);
        withRotation = (CheckBox) findViewById(R.id.cb_withRotation);
        seekBarTickNumbers = (SeekBar) findViewById(R.id.seekBarStartDegree);
        seekBarTickPadding = (SeekBar) findViewById(R.id.seekBarTickPadding);
        textTicks = (TextView) findViewById(R.id.textTickNumber);
        textTickPadding = (TextView) findViewById(R.id.textTickPadding);

        speedometer.speedPercentTo(53);

        withRotation.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                speedometer.setTickRotation(b);
            }
        });

        seekBarTickNumbers.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int value, boolean b) {
                speedometer.setTickNumber(value);
                textTicks.setText(String.format(Locale.getDefault(), "%d", value));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        seekBarTickPadding.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int value, boolean b) {
                speedometer.setTickPadding((int) speedometer.dpTOpx(value));
                textTickPadding.setText(String.format(Locale.getDefault(), "%d dp", value));
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
