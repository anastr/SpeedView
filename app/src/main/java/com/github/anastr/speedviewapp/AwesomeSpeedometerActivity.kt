package com.github.anastr.speedviewapp

import android.os.Bundle
import android.widget.Button
import android.widget.SeekBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.github.anastr.speedviewlib.AwesomeSpeedometer
import java.util.*

class AwesomeSpeedometerActivity : AppCompatActivity() {

    lateinit var textSpeed: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_awesome_speedometer)
        title = "Awesome Speedometer View"

        val awesomeSpeedometer = findViewById<AwesomeSpeedometer>(R.id.awesomeSpeedometer)
        val seekBar = findViewById<SeekBar>(R.id.seekBar)
        val speedTo = findViewById<Button>(R.id.speedTo)
        val realSpeedTo = findViewById<Button>(R.id.realSpeedTo)
        val stop = findViewById<Button>(R.id.stop)
        textSpeed = findViewById(R.id.textSpeed)
        awesomeSpeedometer.withTremble = false

        speedTo.setOnClickListener { awesomeSpeedometer.speedTo(seekBar.progress.toFloat()) }

        realSpeedTo.setOnClickListener { awesomeSpeedometer.realSpeedTo(seekBar.progress.toFloat()) }

        stop.setOnClickListener { awesomeSpeedometer.stop() }

        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                textSpeed.text = "%d".format(Locale.getDefault(), progress)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {}

            override fun onStopTrackingTouch(seekBar: SeekBar) {}
        })
    }
}
