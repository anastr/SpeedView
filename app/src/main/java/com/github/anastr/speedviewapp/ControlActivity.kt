package com.github.anastr.speedviewapp

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.CheckBox
import android.widget.CompoundButton
import android.widget.EditText
import android.widget.SeekBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.github.anastr.speedviewlib.SpeedView
import java.util.Locale

class ControlActivity : AppCompatActivity() {

    private lateinit var speedView: SpeedView
    private lateinit var seekBar: SeekBar
    private lateinit var maxSpeed: EditText
    private lateinit var speedometerWidth: EditText
    private lateinit var withTremble: CheckBox
    private lateinit var textSpeed: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_control)

        speedView = findViewById(R.id.awesomeSpeedometer)
        seekBar = findViewById(R.id.seekBar)
        textSpeed = findViewById(R.id.textSpeed)
        maxSpeed = findViewById(R.id.maxSpeed)
        speedometerWidth = findViewById(R.id.speedometerWidth)
        withTremble = findViewById(R.id.withTremble)
        withTremble.setOnCheckedChangeListener { _: CompoundButton?, isChecked: Boolean ->
            speedView.withTremble = isChecked
        }
        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                textSpeed.text = String.format(Locale.getDefault(), "%d", progress)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onStopTrackingTouch(seekBar: SeekBar) {}
        })
        speedView.speedTo(50f)

        findViewById<View>(R.id.b_set_speed).setOnClickListener { setSpeed() }
        findViewById<View>(R.id.b_set_max_speed).setOnClickListener { setMaxSpeed() }
        findViewById<View>(R.id.b_set_speedometer_width).setOnClickListener { setSpeedometerWidth() }
        findViewById<View>(R.id.b_set_speed_text_size).setOnClickListener { setSpeedTextSize() }
        findViewById<View>(R.id.b_set_indicator_color).setOnClickListener { setIndicatorColor() }
        findViewById<View>(R.id.b_set_center_circle_color).setOnClickListener { setCenterCircleColor() }
        findViewById<View>(R.id.b_set_low_speed_color).setOnClickListener { setLowSpeedColor() }
        findViewById<View>(R.id.b_set_medium_speed_color).setOnClickListener { setMediumSpeedColor() }
        findViewById<View>(R.id.b_set_high_speed_color).setOnClickListener { setHighSpeedColor() }
    }

    private fun setSpeed() {
        speedView.speedTo(seekBar.progress.toFloat())
    }

    private fun setMaxSpeed() {
        try {
            val max = maxSpeed.text.toString().toInt()
            seekBar.max = max
            speedView.maxSpeed = max.toFloat()
        } catch (e: Exception) {
            maxSpeed.error = e.message
        }
    }

    private fun setSpeedometerWidth() {
        try {
            val width = speedometerWidth.text.toString().toFloat()
            speedView.speedometerWidth = width
        } catch (e: Exception) {
            speedometerWidth.error = e.message
        }
    }

    private fun setSpeedTextSize() {
        val speedTextSize = findViewById<EditText>(R.id.speedTextSize)
        try {
            val size = speedTextSize.text.toString().toFloat()
            speedView.speedTextSize = size
        } catch (e: Exception) {
            speedTextSize.error = e.message
        }
    }

    private fun setIndicatorColor() {
        val indicatorColor = findViewById<EditText>(R.id.indicatorColor)
        try {
            speedView.indicator.color = Color.parseColor(indicatorColor.text.toString())
        } catch (e: Exception) {
            indicatorColor.error = e.message
        }
    }

    private fun setCenterCircleColor() {
        val centerCircleColor = findViewById<EditText>(R.id.centerCircleColor)
        try {
            speedView.centerCircleColor = Color.parseColor(centerCircleColor.text.toString())
        } catch (e: Exception) {
            centerCircleColor.error = e.message
        }
    }

    private fun setLowSpeedColor() {
        val lowSpeedColor = findViewById<EditText>(R.id.lowSpeedColor)
        try {
            speedView.sections[0].color = Color.parseColor(lowSpeedColor.text.toString())
        } catch (e: Exception) {
            lowSpeedColor.error = e.message
        }
    }

    private fun setMediumSpeedColor() {
        val mediumSpeedColor = findViewById<EditText>(R.id.mediumSpeedColor)
        try {
            speedView.sections[1].color = Color.parseColor(mediumSpeedColor.text.toString())
        } catch (e: Exception) {
            mediumSpeedColor.error = e.message
        }
    }

    private fun setHighSpeedColor() {
        val highSpeedColor = findViewById<EditText>(R.id.highSpeedColor)
        try {
            speedView.sections[2].color = Color.parseColor(highSpeedColor.text.toString())
        } catch (e: Exception) {
            highSpeedColor.error = e.message
        }
    }
}