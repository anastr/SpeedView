package com.github.anastr.speedviewapp

import android.os.Bundle
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.widget.CheckBox
import android.widget.SeekBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.github.anastr.speedviewlib.Speedometer
import java.util.Locale

class TickActivity : AppCompatActivity() {

    private lateinit var speedometer: Speedometer
    private lateinit var withRotation: CheckBox
    private lateinit var seekBarTickNumbers: SeekBar
    private lateinit var seekBarTickPadding: SeekBar
    private lateinit var textTicks: TextView
    private lateinit var textTickPadding: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tick)

        title = "Work With Ticks"
        speedometer = findViewById(R.id.speedometer)
        withRotation = findViewById(R.id.cb_withRotation)
        seekBarTickNumbers = findViewById(R.id.seekBarStartDegree)
        seekBarTickPadding = findViewById(R.id.seekBarTickPadding)
        textTicks = findViewById(R.id.textTickNumber)
        textTickPadding = findViewById(R.id.textTickPadding)

        speedometer.speedPercentTo(53)
        speedometer.onPrintTickLabel = label@{ _, tick ->
            if (tick == 0f) {
                val s = SpannableString(String.format(Locale.getDefault(), "%.1f", tick))
                s.setSpan(ForegroundColorSpan(-0xeee9), 0, 1, 0)
                return@label s
            }
            null
        }
        withRotation.setOnCheckedChangeListener { _, b ->
            speedometer.isTickRotation = b
        }
        seekBarTickNumbers.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, value: Int, b: Boolean) {
                speedometer.tickNumber = value
                textTicks.text = String.format(Locale.getDefault(), "%d", value)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onStopTrackingTouch(seekBar: SeekBar) {}
        })
        seekBarTickPadding.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, value: Int, b: Boolean) {
                speedometer.tickPadding = speedometer.dpTOpx(value.toFloat()).toInt().toFloat()
                textTickPadding.text = String.format(Locale.getDefault(), "%d dp", value)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onStopTrackingTouch(seekBar: SeekBar) {}
        })
    }
}