package com.github.anastr.speedview;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.TextView;

import com.github.anastr.speedviewlib.DeluxeSpeedView;

import java.util.Locale;

public class DeluxeSpeedActivity extends AppCompatActivity {

    DeluxeSpeedView deluxeSpeedView;
    SeekBar seekBar;
    Button ok;
    TextView textSpeed;
    CheckBox withTremble, withEffects;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deluxe_speed);

        deluxeSpeedView = findViewById(R.id.deluxeSpeedView);
        seekBar = findViewById(R.id.seekBar);
        ok = findViewById(R.id.ok);
        textSpeed = findViewById(R.id.textSpeed);
        withTremble = findViewById(R.id.withTremble);
        withEffects = findViewById(R.id.withEffects);

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deluxeSpeedView.speedTo(seekBar.getProgress());
            }
        });

        withTremble.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                deluxeSpeedView.setWithTremble(isChecked);
            }
        });

        withEffects.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                deluxeSpeedView.setWithEffects(isChecked);
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
