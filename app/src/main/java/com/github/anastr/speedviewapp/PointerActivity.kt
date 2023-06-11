package com.github.anastr.speedviewapp

import android.os.Bundle
import android.widget.Button
import android.widget.SeekBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.github.anastr.speedviewlib.Gauge
import com.github.anastr.speedviewlib.PointerSpeedometer
import java.util.Locale

class PointerActivity : AppCompatActivity() {

    private lateinit var pointerSpeedometer: PointerSpeedometer
    private lateinit var seekBarSpeed: SeekBar
    private lateinit var ok: Button
    private lateinit var textSpeed: TextView
    private lateinit var textSpeedChange: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pointer)

        title = "Pointer Speedometer"
        pointerSpeedometer = findViewById(R.id.pointerSpeedometer)
        seekBarSpeed = findViewById(R.id.seekBarSpeed)
        ok = findViewById(R.id.ok)
        textSpeed = findViewById(R.id.textSpeed)
        textSpeedChange = findViewById(R.id.textSpeedChange)

        ok.setOnClickListener {
            pointerSpeedometer.speedTo(
                seekBarSpeed.progress.toFloat()
            )
        }
        seekBarSpeed.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                textSpeed.text = String.format(Locale.getDefault(), "%d", progress)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onStopTrackingTouch(seekBar: SeekBar) {}
        })
        pointerSpeedometer.onSpeedChangeListener =
            { gauge: Gauge, _: Boolean?, _: Boolean? ->
                textSpeedChange.text = String.format(
                    Locale.getDefault(), "onSpeedChange %d", gauge.currentIntSpeed
                )
            }
    }
}