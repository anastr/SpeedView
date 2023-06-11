package com.github.anastr.speedviewapp

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.SeekBar
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.github.anastr.speedviewlib.Speedometer
import com.github.anastr.speedviewlib.components.indicators.ImageIndicator
import com.github.anastr.speedviewlib.components.indicators.Indicator
import java.util.Locale

class WorkWithIndicatorActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {

    private lateinit var speedometer: Speedometer
    private lateinit var textWidth: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_work_with_indicator)

        title = "Work With Indicator"
        speedometer = findViewById(R.id.speedometer)
        textWidth = findViewById(R.id.textWidth)

        speedometer.speedTo(40f)
        val spinner = findViewById<Spinner>(R.id.spinner)!!
        spinner.onItemSelectedListener = this
        val categories: MutableList<String> = ArrayList()
        categories.add("NoIndicator")
        categories.add("NormalIndicator")
        categories.add("NormalSmallIndicator")
        categories.add("TriangleIndicator")
        categories.add("SpindleIndicator")
        categories.add("LineIndicator")
        categories.add("HalfLineIndicator")
        categories.add("QuarterLineIndicator")
        categories.add("KiteIndicator")
        categories.add("NeedleIndicator")
        val dataAdapter =
            ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, categories)
        spinner.adapter = dataAdapter
        spinner.setSelection(1)
        val seekBarWidth = findViewById<SeekBar>(R.id.seekBar)!!
        seekBarWidth.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, width: Int, b: Boolean) {
                speedometer.indicator.width = speedometer.dpTOpx(width.toFloat())
                textWidth.text = String.format(Locale.getDefault(), "%ddp", width)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onStopTrackingTouch(seekBar: SeekBar) {}
        })

        findViewById<View>(R.id.b_image_indicator).setOnClickListener { imageIndicator() }
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View, position: Int, id: Long) {
        when (position) {
            0 -> speedometer.setIndicator(Indicator.Indicators.NoIndicator)
            1 -> speedometer.setIndicator(Indicator.Indicators.NormalIndicator)
            2 -> speedometer.setIndicator(Indicator.Indicators.NormalSmallIndicator)
            3 -> speedometer.setIndicator(Indicator.Indicators.TriangleIndicator)
            4 -> speedometer.setIndicator(Indicator.Indicators.SpindleIndicator)
            5 -> speedometer.setIndicator(Indicator.Indicators.LineIndicator)
            6 -> speedometer.setIndicator(Indicator.Indicators.HalfLineIndicator)
            7 -> speedometer.setIndicator(Indicator.Indicators.QuarterLineIndicator)
            8 -> speedometer.setIndicator(Indicator.Indicators.KiteIndicator)
            9 -> speedometer.setIndicator(Indicator.Indicators.NeedleIndicator)
        }
    }

    override fun onNothingSelected(arg0: AdapterView<*>?) {}

    private fun imageIndicator() {
        val imageIndicator = ImageIndicator(
            applicationContext, ContextCompat.getDrawable(this, R.drawable.image_indicator1)!!
        )
        speedometer.indicator = imageIndicator
    }
}