package com.github.anastr.speedview;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import com.github.anastr.speedviewlib.Gauge;
import com.github.anastr.speedviewlib.PointerSpeedometer;
import com.github.anastr.speedviewlib.util.OnSpeedChangeListener;

import java.util.Locale;

public class PointerActivity extends AppCompatActivity {

    PointerSpeedometer pointerSpeedometer;
    SeekBar seekBarSpeed;
    Button ok;
    TextView textSpeed, textSpeedChange;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pointer);

        pointerSpeedometer = findViewById(R.id.pointerSpeedometer);
        seekBarSpeed = findViewById(R.id.seekBarSpeed);
        ok = findViewById(R.id.ok);
        textSpeed = findViewById(R.id.textSpeed);
        textSpeedChange = findViewById(R.id.textSpeedChange);

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pointerSpeedometer.speedTo(seekBarSpeed.getProgress());
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

        pointerSpeedometer.setOnSpeedChangeListener(new OnSpeedChangeListener() {
            @Override
            public void onSpeedChange(Gauge gauge, boolean isSpeedUp, boolean isByTremble) {
                textSpeedChange.setText(String.format(Locale.getDefault(), "onSpeedChange %d"
                        , gauge.getCurrentIntSpeed()));
            }
        });
    }
}
