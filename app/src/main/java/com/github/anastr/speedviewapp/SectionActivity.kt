package com.github.anastr.speedviewapp

import android.graphics.Color
import android.os.Bundle
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import com.github.anastr.speedviewlib.components.Style
import com.github.anastr.speedviewlib.util.doOnSections
import kotlinx.android.synthetic.main.activity_section.*

class SectionActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_section)
        title = "Sections"

        seekBar.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                speedView.makeSections(progress, 0, Style.BUTT)
                textSpeed.text = "$progress"
                randomColors()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        button_random_color.setOnClickListener { randomColors() }

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
