package com.github.anastr.speedviewapp

import android.os.Bundle
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_fulcrum.*

class FulcrumActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fulcrum)

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