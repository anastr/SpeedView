package com.github.anastr.speedviewapp

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import com.github.anastr.speedviewlib.Gauge
import com.github.anastr.speedviewlib.Speedometer

class SpeedTextPositionActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {

    private lateinit var speedometer: Speedometer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_speed_text_position)
        title = "Speed Text Position"

        speedometer = findViewById(R.id.speedometer)
        speedometer.speedTo(40f)
        val spinner = findViewById<Spinner>(R.id.spinner)!!
        spinner.onItemSelectedListener = this
        val categories: MutableList<String> = ArrayList()
        categories.add("TOP_LEFT")
        categories.add("TOP_CENTER")
        categories.add("TOP_RIGHT")
        categories.add("LEFT")
        categories.add("CENTER")
        categories.add("RIGHT")
        categories.add("BOTTOM_LEFT")
        categories.add("BOTTOM_CENTER")
        categories.add("BOTTOM_RIGHT")
        val dataAdapter =
            ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, categories)
        spinner.adapter = dataAdapter
        spinner.setSelection(7)

        findViewById<View>(R.id.b_unit_switch).setOnClickListener { unitUnderSpeedTextClick() }
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View, position: Int, id: Long) {
        speedometer.speedTextPosition = Gauge.Position.values()[position]
        // simple usage:
        // speedometer.setSpeedTextPosition(Speedometer.Position.TOP_LEFT);
    }

    override fun onNothingSelected(arg0: AdapterView<*>?) {}

    private fun unitUnderSpeedTextClick() {
        speedometer.unitUnderSpeedText = !speedometer.unitUnderSpeedText
    }
}