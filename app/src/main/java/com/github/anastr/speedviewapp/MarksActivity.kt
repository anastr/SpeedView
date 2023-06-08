package com.github.anastr.speedviewapp

import android.os.Bundle
import android.widget.CheckBox
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import androidx.appcompat.app.AppCompatActivity
import com.github.anastr.speedviewlib.SpeedView
import com.github.anastr.speedviewlib.components.Style

class MarksActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_marks)

        val speedometer = findViewById<SpeedView>(R.id.speedometer)
        val styleCheckbox = findViewById<CheckBox>(R.id.styleCheckbox)
        val seekBarMarks = findViewById<SeekBar>(R.id.seekBarMarks)
        val seekBarHeight = findViewById<SeekBar>(R.id.seekBarHeight)
        val seekBarWidth = findViewById<SeekBar>(R.id.seekBarWidth)

        speedometer.speedTo(60f)

        styleCheckbox.setOnCheckedChangeListener { _, isChecked ->
            speedometer.markStyle = if(isChecked) Style.ROUND else Style.BUTT
        }

        seekBarMarks.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                speedometer.marksNumber = progress
            }
            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onStopTrackingTouch(seekBar: SeekBar) {}
        })

        seekBarHeight.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                speedometer.markHeight = speedometer.dpTOpx(progress.toFloat())
            }
            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onStopTrackingTouch(seekBar: SeekBar) {}
        })

        seekBarWidth.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                speedometer.markWidth = speedometer.dpTOpx(progress.toFloat())
            }
            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onStopTrackingTouch(seekBar: SeekBar) {}
        })
    }
}