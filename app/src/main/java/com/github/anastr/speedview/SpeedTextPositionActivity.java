package com.github.anastr.speedview;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.github.anastr.speedviewlib.Speedometer;

import java.util.ArrayList;
import java.util.List;

public class SpeedTextPositionActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    Speedometer speedometer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_speed_text_position);

        speedometer = findViewById(R.id.speedometer);

        assert speedometer != null;
        speedometer.speedTo(40);

        Spinner spinner = findViewById(R.id.spinner);
        assert spinner != null;
        spinner.setOnItemSelectedListener(this);

        List<String> categories = new ArrayList<>();
        categories.add("TOP_LEFT");
        categories.add("TOP_CENTER");
        categories.add("TOP_RIGHT");
        categories.add("LEFT");
        categories.add("CENTER");
        categories.add("RIGHT");
        categories.add("BOTTOM_LEFT");
        categories.add("BOTTOM_CENTER");
        categories.add("BOTTOM_RIGHT");

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, categories);
        spinner.setAdapter(dataAdapter);
        spinner.setSelection(7);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        speedometer.setSpeedTextPosition(Speedometer.Position.values()[position]);
        // simple usage:
        // speedometer.setSpeedTextPosition(Speedometer.Position.TOP_LEFT);
    }

    @Override
    public void onNothingSelected(AdapterView<?> arg0) {
    }

    public void unitUnderSpeedTextClick(View view) {
        if (speedometer.isUnitUnderSpeedText())
            speedometer.setUnitUnderSpeedText(false);
        else
            speedometer.setUnitUnderSpeedText(true);
    }
}
