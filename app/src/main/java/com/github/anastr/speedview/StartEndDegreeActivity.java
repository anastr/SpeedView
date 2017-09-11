package com.github.anastr.speedview;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.SeekBar;
import android.widget.TextView;

import com.github.anastr.speedviewlib.Speedometer;

import java.util.Locale;

public class StartEndDegreeActivity extends AppCompatActivity {

    Speedometer speedometer;
    SeekBar seekBarStartDegree, seekBarEndDegree;
    TextView textStartDegree, textEndDegree;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_end_degree);

        speedometer = (Speedometer) findViewById(R.id.speedometer);
        seekBarStartDegree = (SeekBar) findViewById(R.id.seekBarStartDegree);
        seekBarEndDegree = (SeekBar) findViewById(R.id.seekBarEndDegree);
        textStartDegree = (TextView) findViewById(R.id.textStartDegree);
        textEndDegree = (TextView) findViewById(R.id.textEndDegree);

        speedometer.speedPercentTo(50);

        seekBarStartDegree.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int value, boolean b) {
                int degree = value + 90;
                speedometer.setStartDegree(degree);
                textStartDegree.setText(String.format(Locale.getDefault(), "%d", degree));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        seekBarEndDegree.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int value, boolean b) {
                int degree = value + 270;
                speedometer.setEndDegree(degree);
                textEndDegree.setText(String.format(Locale.getDefault(), "%d", degree));
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
