package com.github.anastr.speedviewapp

import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.SeekBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.github.anastr.speedviewlib.DeluxeSpeedView
import java.util.Locale

class DeluxeSpeedActivity : AppCompatActivity() {

    private lateinit var deluxeSpeedView: DeluxeSpeedView
    private lateinit var seekBar: SeekBar
    private lateinit var ok: Button
    private lateinit var textSpeed: TextView
    private lateinit var withTremble: CheckBox
    private lateinit var withEffects: CheckBox

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_deluxe_speed)
        title = "Deluxe Speed View"

        deluxeSpeedView = findViewById(R.id.deluxeSpeedView)
        seekBar = findViewById(R.id.seekBar)
        ok = findViewById(R.id.ok)
        textSpeed = findViewById(R.id.textSpeed)
        withTremble = findViewById(R.id.withTremble)
        withEffects = findViewById(R.id.withEffects)

        ok.setOnClickListener {
            deluxeSpeedView.speedTo(
                seekBar.progress.toFloat()
            )
        }
        withTremble.setOnCheckedChangeListener { _, isChecked ->
            deluxeSpeedView.withTremble = isChecked
        }
        withEffects.setOnCheckedChangeListener { _, isChecked ->
            deluxeSpeedView.isWithEffects = isChecked
        }
        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                textSpeed.text = String.format(Locale.getDefault(), "%d", progress)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onStopTrackingTouch(seekBar: SeekBar) {}
        })
    }
}