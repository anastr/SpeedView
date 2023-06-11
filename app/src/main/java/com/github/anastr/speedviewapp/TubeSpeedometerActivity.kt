package com.github.anastr.speedviewapp

import android.os.Bundle
import android.widget.Button
import android.widget.SeekBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.github.anastr.speedviewlib.TubeSpeedometer
import java.util.Locale

class TubeSpeedometerActivity : AppCompatActivity() {

    private lateinit var tubeSpeedometer: TubeSpeedometer
    private lateinit var seekBar: SeekBar
    private lateinit var ok: Button
    private lateinit var textSpeed: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tube_speedometer)

        title = "Tube Speedometer"
        tubeSpeedometer = findViewById(R.id.speedometer)
        seekBar = findViewById(R.id.seekBar)
        ok = findViewById(R.id.ok)
        textSpeed = findViewById(R.id.textSpeed)

        ok.setOnClickListener {
            tubeSpeedometer.speedTo(
                seekBar.progress.toFloat()
            )
        }
        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                textSpeed.text = String.format(Locale.getDefault(), "%d", progress)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onStopTrackingTouch(seekBar: SeekBar) {}
        })
    }
}