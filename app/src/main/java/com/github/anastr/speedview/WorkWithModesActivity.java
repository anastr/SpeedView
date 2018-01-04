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

public class WorkWithModesActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    Speedometer speedometer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_work_with_modes);

        speedometer = findViewById(R.id.speedometer);

        assert speedometer != null;
        speedometer.speedTo(40);

        Spinner spinner = findViewById(R.id.spinner);
        assert spinner != null;
        spinner.setOnItemSelectedListener(this);

        List<String> categories = new ArrayList<>();
        categories.add("NORMAL");
        categories.add("LEFT");
        categories.add("TOP");
        categories.add("RIGHT");
        categories.add("BOTTOM");
        categories.add("TOP_LEFT");
        categories.add("TOP_RIGHT");
        categories.add("BOTTOM_RIGHT");
        categories.add("BOTTOM_LEFT");

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, categories);
        spinner.setAdapter(dataAdapter);
        spinner.setSelection(0);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        speedometer.setSpeedometerMode(Speedometer.Mode.values()[position]);
        // simple usage:
        // speedometer.setSpeedometerMode(Speedometer.Mode.RIGHT);
    }

    @Override
    public void onNothingSelected(AdapterView<?> arg0) {
    }
}
