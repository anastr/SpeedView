package com.github.anastr.speedviewapp

import android.os.Bundle
import android.widget.SeekBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.github.anastr.speedviewlib.SpeedView

class FulcrumActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fulcrum)

        val speedometer = findViewById<SpeedView>(R.id.speedometer)
        val seekBarFulcrumX = findViewById<SeekBar>(R.id.seekBarFulcrumX)
        val seekBarFulcrumY = findViewById<SeekBar>(R.id.seekBarFulcrumY)
        val textFulcrumX = findViewById<TextView>(R.id.textFulcrumX)
        val textFulcrumY = findViewById<TextView>(R.id.textFulcrumY)

        speedometer.speedTo(50f)

        seekBarFulcrumX.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                val fulcrumX = progress.toFloat() / 100f
                speedometer.setFulcrum(fulcrumX, speedometer.fulcrumY)
                textFulcrumX.text = String.format("%.2f", fulcrumX)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
        seekBarFulcrumY.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                val fulcrumY = progress.toFloat() / 100f
                speedometer.setFulcrum(speedometer.fulcrumX, fulcrumY)
                textFulcrumY.text = String.format("%.2f", fulcrumY)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
    }
}