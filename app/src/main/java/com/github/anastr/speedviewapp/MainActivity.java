package com.github.anastr.speedviewapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.github.anastr.speedviewapp.lineargauge.ImageLinearGaugeActivity;
import com.github.anastr.speedviewapp.lineargauge.ProgressiveGaugeActivity;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ListView listView = findViewById(R.id.list);

        String[] values = new String[] {
                "1. Speed View",
                "2. Deluxe Speed View",
                "3. Awesome Speedometer View",
                "4. Ray Speedometer View",
                "5. Pointer Speedometer",
                "6. Tube Speedometer",
                "7. Image Speedometer",
                "1.. Progressive Gauge",
                "2.. Image Linear Gauge",
                "Work With Indicator",
                "Work With Marks",
                "Work With Note",
                "Create Speedometer Programmatically",
                "Work With Start and End Degree",
                "Work With Modes",
                "Speed Text Position",
                "Work With Ticks",
                "SpeedView with Recycler",
                "Sections"
        };

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, android.R.id.text1, values);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        Intent intent = new Intent ();
        switch (position) {
            case 0:
                intent.setClass(MainActivity.this, SpeedViewActivity.class);
                break;
            case 1:
                intent.setClass(MainActivity.this, DeluxeSpeedActivity.class);
                break;
            case 2:
                intent.setClass(MainActivity.this, AwesomeSpeedometerActivity.class);
                break;
            case 3:
                intent.setClass(MainActivity.this, RayActivity.class);
                break;
            case 4:
                intent.setClass(MainActivity.this, PointerActivity.class);
                break;
            case 5:
                intent.setClass(MainActivity.this, TubeSpeedometerActivity.class);
                break;
            case 6:
                intent.setClass(MainActivity.this, ImageSpeedometerActivity.class);
                break;
            case 7:
                intent.setClass(MainActivity.this, ProgressiveGaugeActivity.class);
                break;
            case 8:
                intent.setClass(MainActivity.this, ImageLinearGaugeActivity.class);
                break;
            case 9:
                intent.setClass(MainActivity.this, WorkWithIndicatorActivity.class);
                break;
            case 10:
                intent.setClass(MainActivity.this, MarksActivity.class);
                break;
            case 11:
                intent.setClass(MainActivity.this, WorkWithNoteActivity.class);
                break;
            case 12:
                intent.setClass(MainActivity.this, CreateProgrammatically.class);
                break;
            case 13:
                intent.setClass(MainActivity.this, StartEndDegreeActivity.class);
                break;
            case 14:
                intent.setClass(MainActivity.this, WorkWithModesActivity.class);
                break;
            case 15:
                intent.setClass(MainActivity.this, SpeedTextPositionActivity.class);
                break;
            case 16:
                intent.setClass(MainActivity.this, TickActivity.class);
                break;
            case 17:
                intent.setClass(MainActivity.this, RecyclerActivity.class);
                break;
            case 18:
                intent.setClass(MainActivity.this, SectionActivity.class);
                break;
        }
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.about_item:
                startActivity(new Intent(this, AboutActivity.class));
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
