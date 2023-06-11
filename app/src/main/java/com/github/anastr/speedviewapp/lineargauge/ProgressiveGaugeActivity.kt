package com.github.anastr.speedviewapp.lineargauge

import android.os.Bundle
import android.widget.Button
import android.widget.SeekBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.github.anastr.speedviewapp.R
import com.github.anastr.speedviewlib.ProgressiveGauge
import java.util.Locale

class ProgressiveGaugeActivity : AppCompatActivity() {

    private lateinit var progressiveGauge: ProgressiveGauge
    private lateinit var seekBar: SeekBar
    private lateinit var ok: Button
    private lateinit var textSpeed: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_progressive_gauge)

        title = "Progressive Gauge"
        progressiveGauge = findViewById(R.id.gauge)
        seekBar = findViewById(R.id.seekBar)
        ok = findViewById(R.id.ok)
        textSpeed = findViewById(R.id.textSpeed)
        ok.setOnClickListener {
            progressiveGauge.speedTo(
                seekBar.progress.toFloat()
            )
        }
        seekBar.setOnSeekBarChangeListener(
            object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                    textSpeed.text = String.format(Locale.getDefault(), "%d", progress)
                }

                override fun onStartTrackingTouch(seekBar: SeekBar) {}
                override fun onStopTrackingTouch(seekBar: SeekBar) {}
            }
        )
    }
}