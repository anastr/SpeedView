package com.github.anastr.speedviewapp

import android.os.Bundle
import android.widget.Button
import android.widget.SeekBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.github.anastr.speedviewlib.RaySpeedometer
import java.util.Locale

class RayActivity : AppCompatActivity() {

    private lateinit var raySpeedometer: RaySpeedometer
    private lateinit var seekBarSpeed: SeekBar
    private lateinit var seekBarDegree: SeekBar
    private lateinit var seekBarWidth: SeekBar
    private lateinit var ok: Button
    private lateinit var textSpeed: TextView
    private lateinit var textDegree: TextView
    private lateinit var textWidth: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ray)

        title = "Ray Speedometer View"
        raySpeedometer = findViewById(R.id.raySpeedometer)
        seekBarSpeed = findViewById(R.id.seekBarSpeed)
        seekBarDegree = findViewById(R.id.seekBarDegree)
        seekBarWidth = findViewById(R.id.seekBarWidth)
        ok = findViewById(R.id.ok)
        textSpeed = findViewById(R.id.textSpeed)
        textDegree = findViewById(R.id.textDegree)
        textWidth = findViewById(R.id.textWidth)

        ok.setOnClickListener {
            raySpeedometer.speedTo(
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
        seekBarDegree.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                textDegree.text = String.format(Locale.getDefault(), "%d", progress)
                raySpeedometer.setDegreeBetweenMark(progress)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onStopTrackingTouch(seekBar: SeekBar) {}
        })
        seekBarWidth.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                textWidth.text = String.format(Locale.getDefault(), "%ddp", progress)
                raySpeedometer.rayMarkWidth =
                    raySpeedometer.dpTOpx(progress.toFloat()).toInt().toFloat()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onStopTrackingTouch(seekBar: SeekBar) {}
        })
    }
}