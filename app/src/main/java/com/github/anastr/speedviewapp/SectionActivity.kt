package com.github.anastr.speedviewapp

import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import android.widget.SeekBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.github.anastr.speedviewlib.SpeedView
import com.github.anastr.speedviewlib.components.Style
import com.github.anastr.speedviewlib.util.doOnSections

class SectionActivity : AppCompatActivity() {

    private lateinit var speedView: SpeedView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_section)
        title = "Sections"

        speedView = findViewById(R.id.speedView)
        val textSpeed = findViewById<TextView>(R.id.textSpeed)
        val seekBar = findViewById<SeekBar>(R.id.seekBar)
        val buttonRandomColor = findViewById<Button>(R.id.button_random_color)

        seekBar.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                speedView.makeSections(progress, 0, Style.BUTT)
                textSpeed.text = "$progress"
                randomColors()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        buttonRandomColor.setOnClickListener { randomColors() }

        seekBar.progress = 5
        speedView.speedTo(50f)
    }

    private fun randomColors() {
        speedView.doOnSections { it.color = Color.rgb((0..255).random(), (0..255).random(), (0..255).random()) }

        /*
          the next code is slow, because if you call
          `section.color = ...`
          every time, it will redraw the speedometer and take a lot of time.
          sections are observable by its speedometer, so any change in section will redraw the speedometer.
         */
//        speedView.sections.forEach {
//            it.color = Color.rgb((0..255).random(), (0..255).random(), (0..255).random())
//        }
    }
}
