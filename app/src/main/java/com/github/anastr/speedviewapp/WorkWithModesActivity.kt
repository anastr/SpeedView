package com.github.anastr.speedviewapp

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import com.github.anastr.speedviewlib.Speedometer

class WorkWithModesActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {

    private lateinit var speedometer: Speedometer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_work_with_modes)

        title = "Work With Modes"
        speedometer = findViewById(R.id.speedometer)

        speedometer.speedTo(40f)
        val spinner = findViewById<Spinner>(R.id.spinner)!!
        spinner.onItemSelectedListener = this
        val categories: MutableList<String> = ArrayList()
        categories.add("NORMAL")
        categories.add("LEFT")
        categories.add("TOP")
        categories.add("RIGHT")
        categories.add("BOTTOM")
        categories.add("TOP_LEFT")
        categories.add("TOP_RIGHT")
        categories.add("BOTTOM_RIGHT")
        categories.add("BOTTOM_LEFT")
        val dataAdapter =
            ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, categories)
        spinner.adapter = dataAdapter
        spinner.setSelection(0)
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View, position: Int, id: Long) {
        speedometer.speedometerMode = Speedometer.Mode.values()[position]
        // simple usage:
        // speedometer.setSpeedometerMode(Speedometer.Mode.RIGHT);
    }

    override fun onNothingSelected(arg0: AdapterView<*>?) {}
}