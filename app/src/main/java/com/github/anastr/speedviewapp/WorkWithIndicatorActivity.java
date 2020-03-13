package com.github.anastr.speedviewapp;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.github.anastr.speedviewlib.Speedometer;
import com.github.anastr.speedviewlib.components.indicators.ImageIndicator;
import com.github.anastr.speedviewlib.components.indicators.Indicator;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class WorkWithIndicatorActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    Speedometer speedometer;
    TextView textWidth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_work_with_indicator);
        setTitle("Work With Indicator");

        speedometer = findViewById(R.id.speedometer);
        textWidth = findViewById(R.id.textWidth);

        assert speedometer != null;
        speedometer.speedTo(40);

        Spinner spinner = findViewById(R.id.spinner);
        assert spinner != null;
        spinner.setOnItemSelectedListener(this);

        List<String> categories = new ArrayList<>();
        categories.add("NoIndicator");
        categories.add("NormalIndicator");
        categories.add("NormalSmallIndicator");
        categories.add("TriangleIndicator");
        categories.add("SpindleIndicator");
        categories.add("LineIndicator");
        categories.add("HalfLineIndicator");
        categories.add("QuarterLineIndicator");
        categories.add("KiteIndicator");
        categories.add("NeedleIndicator");

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, categories);
        spinner.setAdapter(dataAdapter);
        spinner.setSelection(1);

        SeekBar seekBarWidth = findViewById(R.id.seekBar);
        assert seekBarWidth != null;
        seekBarWidth.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int width, boolean b) {
                speedometer.getIndicator().setWidth(speedometer.dpTOpx(width));
                textWidth.setText(String.format(Locale.getDefault(), "%ddp", width));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (position) {
            case 0:
                speedometer.setIndicator(Indicator.Indicators.NoIndicator);
                break;
            case 1:
                speedometer.setIndicator(Indicator.Indicators.NormalIndicator);
                break;
            case 2:
                speedometer.setIndicator(Indicator.Indicators.NormalSmallIndicator);
                break;
            case 3:
                speedometer.setIndicator(Indicator.Indicators.TriangleIndicator);
                break;
            case 4:
                speedometer.setIndicator(Indicator.Indicators.SpindleIndicator);
                break;
            case 5:
                speedometer.setIndicator(Indicator.Indicators.LineIndicator);
                break;
            case 6:
                speedometer.setIndicator(Indicator.Indicators.HalfLineIndicator);
                break;
            case 7:
                speedometer.setIndicator(Indicator.Indicators.QuarterLineIndicator);
                break;
            case 8:
                speedometer.setIndicator(Indicator.Indicators.KiteIndicator);
                break;
            case 9:
                speedometer.setIndicator(Indicator.Indicators.NeedleIndicator);
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> arg0) {
    }

    public void imageIndicator(View view) {
        ImageIndicator imageIndicator = new ImageIndicator(getApplicationContext()
                , ContextCompat.getDrawable(this, R.drawable.image_indicator1));
        speedometer.setIndicator(imageIndicator);
    }
}
