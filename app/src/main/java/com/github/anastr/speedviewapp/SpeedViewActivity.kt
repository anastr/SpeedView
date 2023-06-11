package com.github.anastr.speedviewapp

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.SeekBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.github.anastr.speedviewlib.SpeedView
import com.github.anastr.speedviewlib.components.Section
import com.github.anastr.speedviewlib.components.Style
import java.util.Locale

class SpeedViewActivity : AppCompatActivity() {

    private lateinit var speedView: SpeedView
    private lateinit var seekBar: SeekBar
    private lateinit var ok: Button
    private lateinit var textSpeed: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_speed_view)

        title = "Speed View"
        speedView = findViewById(R.id.speedView)
        seekBar = findViewById(R.id.seekBar)
        ok = findViewById(R.id.ok)
        textSpeed = findViewById(R.id.textSpeed)
        ok.setOnClickListener {
            speedView.speedTo(
                seekBar.progress.toFloat()
            )
        }
        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                textSpeed.text = String.format(Locale.getDefault(), "%d", progress)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onStopTrackingTouch(seekBar: SeekBar) {}
        })
        speedView.speedTextListener =
            { speed: Float? -> String.format(Locale.getDefault(), "lol%.0f", speed) }
        speedView.clearSections()
        speedView.addSections(
            Section(0f, .1f, Color.LTGRAY, speedView.dpTOpx(30f)),
            Section(.1f, .2f, Color.GRAY, speedView.dpTOpx(30f), Style.ROUND),
            Section(.2f, .3f, Color.DKGRAY, speedView.dpTOpx(30f)),
            Section(.3f, .4f, Color.BLACK, speedView.dpTOpx(30f)),
            Section(.4f, .5f, Color.CYAN, speedView.dpTOpx(30f), Style.ROUND),
            Section(.5f, .6f, Color.BLUE, speedView.dpTOpx(30f)),
            Section(.6f, .7f, Color.GREEN, speedView.dpTOpx(30f)),
            Section(.7f, .8f, Color.YELLOW, speedView.dpTOpx(30f)),
            Section(.8f, .9f, Color.MAGENTA, speedView.dpTOpx(30f))
        )
        //        speedView.getSections().add(new Section(1f, Color.RED));
        speedView.onSectionChangeListener = { previousSection: Section?, newSection: Section? ->
            if (previousSection != null) {
//                    previousSection.setPadding(10);
                previousSection.width = speedView.dpTOpx(30f)
            }
            if (newSection != null) {
//                    newSection.setPadding(0);
                newSection.width = speedView.dpTOpx(35f)
            }
            Unit
        }

        findViewById<View>(R.id.b_open_control).setOnClickListener { openControlActivity() }
    }

    private fun openControlActivity() {
        val intent = Intent(this@SpeedViewActivity, ControlActivity::class.java)
        startActivity(intent)
    }
}