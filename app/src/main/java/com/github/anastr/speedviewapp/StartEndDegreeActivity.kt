package com.github.anastr.speedviewapp

import android.os.Bundle
import android.widget.SeekBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.github.anastr.speedviewlib.Speedometer
import java.util.Locale

class StartEndDegreeActivity : AppCompatActivity() {

    private lateinit var speedometer: Speedometer
    private lateinit var seekBarStartDegree: SeekBar
    private lateinit var seekBarEndDegree: SeekBar
    private lateinit var textStartDegree: TextView
    private lateinit var textEndDegree: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start_end_degree)

        title = "Work With Start and End Degree"
        speedometer = findViewById(R.id.speedometer)
        seekBarStartDegree = findViewById(R.id.seekBarStartDegree)
        seekBarEndDegree = findViewById(R.id.seekBarEndDegree)
        textStartDegree = findViewById(R.id.textStartDegree)
        textEndDegree = findViewById(R.id.textEndDegree)

        speedometer.speedPercentTo(50)
        seekBarStartDegree.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, value: Int, b: Boolean) {
                val degree = value + 90
                speedometer.setStartDegree(degree)
                textStartDegree.text = String.format(Locale.getDefault(), "%d", degree)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onStopTrackingTouch(seekBar: SeekBar) {}
        })
        seekBarEndDegree.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, value: Int, b: Boolean) {
                val degree = value + 270
                speedometer.setEndDegree(degree)
                textEndDegree.text = String.format(Locale.getDefault(), "%d", degree)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onStopTrackingTouch(seekBar: SeekBar) {}
        })
    }
}