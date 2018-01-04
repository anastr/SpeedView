package com.github.anastr.speedview;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;

import com.github.anastr.speedviewlib.SpeedView;

import java.util.Locale;

public class ControlActivity extends AppCompatActivity {

    SpeedView speedView;
    SeekBar seekBar;
    EditText maxSpeed, speedometerWidth;
    CheckBox withTremble;
    TextView textSpeed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_control);

        speedView = findViewById(R.id.awesomeSpeedometer);
        seekBar = findViewById(R.id.seekBar);
        textSpeed = findViewById(R.id.textSpeed);
        maxSpeed = findViewById(R.id.maxSpeed);
        speedometerWidth = findViewById(R.id.speedometerWidth);
        withTremble = findViewById(R.id.withTremble);

        withTremble.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                speedView.setWithTremble(isChecked);
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

        speedView.speedTo(50);
    }

    public void setSpeed(View view) {
        speedView.speedTo(seekBar.getProgress());
    }

    public void setMaxSpeed(View view) {
        try {
            int max = Integer.parseInt(maxSpeed.getText().toString());
            seekBar.setMax(max);
            speedView.setMaxSpeed(max);
        }
        catch (Exception e) {
            maxSpeed.setError(e.getMessage());
        }
    }

    public void setSpeedometerWidth(View view) {
        try {
            float width = Float.parseFloat(speedometerWidth.getText().toString());
            speedView.setSpeedometerWidth(width);
        }
        catch (Exception e) {
            speedometerWidth.setError(e.getMessage());
        }
    }

    public void setSpeedTextSize(View view) {
        EditText speedTextSize = findViewById(R.id.speedTextSize);
        try {
            float size = Float.parseFloat(speedTextSize.getText().toString());
            speedView.setSpeedTextSize(size);
        }
        catch (Exception e) {
            speedTextSize.setError(e.getMessage());
        }
    }

    public void setIndicatorColor(View view) {
        EditText indicatorColor = findViewById(R.id.indicatorColor);
        try{
            speedView.setIndicatorColor(Color.parseColor(indicatorColor.getText().toString()));
        } catch (Exception e) {
            indicatorColor.setError(e.getMessage());
        }
    }

    public void setCenterCircleColor(View view) {
        EditText centerCircleColor = findViewById(R.id.centerCircleColor);
        try{
            speedView.setCenterCircleColor(Color.parseColor(centerCircleColor.getText().toString()));
        } catch (Exception e) {
            centerCircleColor.setError(e.getMessage());
        }
    }

    public void setLowSpeedColor(View view) {
        EditText lowSpeedColor = findViewById(R.id.lowSpeedColor);
        try{
            speedView.setLowSpeedColor(Color.parseColor(lowSpeedColor.getText().toString()));
        } catch (Exception e) {
            lowSpeedColor.setError(e.getMessage());
        }
    }

    public void setMediumSpeedColor(View view) {
        EditText mediumSpeedColor = findViewById(R.id.mediumSpeedColor);
        try{
            speedView.setMediumSpeedColor(Color.parseColor(mediumSpeedColor.getText().toString()));
        } catch (Exception e) {
            mediumSpeedColor.setError(e.getMessage());
        }
    }

    public void setHighSpeedColor(View view) {
        EditText highSpeedColor = findViewById(R.id.highSpeedColor);
        try{
            speedView.setHighSpeedColor(Color.parseColor(highSpeedColor.getText().toString()));
        } catch (Exception e) {
            highSpeedColor.setError(e.getMessage());
        }
    }
}
