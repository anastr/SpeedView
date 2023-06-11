package com.github.anastr.speedviewapp

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.SeekBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.github.anastr.speedviewlib.AwesomeSpeedometer
import com.github.anastr.speedviewlib.DeluxeSpeedView
import com.github.anastr.speedviewlib.ImageSpeedometer
import com.github.anastr.speedviewlib.PointerSpeedometer
import com.github.anastr.speedviewlib.RaySpeedometer
import com.github.anastr.speedviewlib.SpeedView
import com.github.anastr.speedviewlib.Speedometer
import com.github.anastr.speedviewlib.TubeSpeedometer
import com.github.anastr.speedviewlib.components.indicators.Indicator
import java.util.Locale
import java.util.Random

class CreateProgrammatically : AppCompatActivity() {

    private lateinit var rootSpeedometer: LinearLayout
    private var speedometer: Speedometer? = null
    private lateinit var seekBar: SeekBar
    private lateinit var ok: Button
    private lateinit var textSpeed: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_programmatically)

        title = "Create Speedometer Programmatically"
        rootSpeedometer = findViewById(R.id.root_speedometer)
        seekBar = findViewById(R.id.seekBar)
        ok = findViewById(R.id.ok)
        textSpeed = findViewById(R.id.textSpeed)

        ok.setOnClickListener {
            speedometer?.speedTo(seekBar.progress.toFloat())
        }
        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                textSpeed.text = String.format(Locale.getDefault(), "%d", progress)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onStopTrackingTouch(seekBar: SeekBar) {}
        })

        findViewById<View>(R.id.b_create_speedometer).setOnClickListener { addRandomSpeedometer() }
    }

    private fun addRandomSpeedometer() {
        val mad = Random()
        when (mad.nextInt(7)) {
            0 -> speedometer = SpeedView(this)
            1 -> speedometer = DeluxeSpeedView(this)
            2 -> speedometer = AwesomeSpeedometer(this)
            3 -> speedometer = RaySpeedometer(this)
            4 -> speedometer = PointerSpeedometer(this)
            5 -> speedometer = TubeSpeedometer(this)
            6 -> {
                speedometer = ImageSpeedometer(this)
                speedometer?.setIndicator(Indicator.Indicators.HalfLineIndicator)
                speedometer?.indicator?.width = speedometer?.dpTOpx(5f) ?: 0f
                speedometer?.speedTextColor = Color.WHITE
                speedometer?.unitTextColor = Color.WHITE
                (speedometer as ImageSpeedometer).setImageSpeedometer(R.drawable.for_image_speedometer)
            }
        }
        rootSpeedometer.removeAllViews()
        rootSpeedometer.addView(speedometer)
    }
}