package com.github.anastr.speedviewapp.lineargauge

import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.SeekBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.github.anastr.speedviewapp.R
import com.github.anastr.speedviewlib.ImageLinearGauge
import com.github.anastr.speedviewlib.LinearGauge
import java.util.Locale

class ImageLinearGaugeActivity : AppCompatActivity() {

    private lateinit var imageLinearGauge: ImageLinearGauge
    private lateinit var seekBar: SeekBar
    private lateinit var ok: Button
    private lateinit var textSpeed: TextView
    private lateinit var checkBoxOrientation: CheckBox

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image_linear_gauge)

        title = "Image Linear Gauge"
        imageLinearGauge = findViewById(R.id.gauge)
        seekBar = findViewById(R.id.seekBar)
        ok = findViewById(R.id.ok)
        textSpeed = findViewById(R.id.textSpeed)
        checkBoxOrientation = findViewById(R.id.cb_orientation)

        ok.setOnClickListener { imageLinearGauge.speedTo(seekBar.progress.toFloat()) }
        seekBar.setOnSeekBarChangeListener(
            object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                    textSpeed.text = String.format(Locale.getDefault(), "%d", progress)
                }

                override fun onStartTrackingTouch(seekBar: SeekBar) {}
                override fun onStopTrackingTouch(seekBar: SeekBar) {}
            }
        )
        checkBoxOrientation.setOnCheckedChangeListener { _, b ->
            if (b) imageLinearGauge.orientation =
                LinearGauge.Orientation.VERTICAL else imageLinearGauge.orientation =
                LinearGauge.Orientation.HORIZONTAL
        }
    }
}