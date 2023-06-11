package com.github.anastr.speedviewapp

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import android.view.View
import android.widget.Button
import android.widget.SeekBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.github.anastr.speedviewlib.SpeedView
import com.github.anastr.speedviewlib.components.note.ImageNote
import com.github.anastr.speedviewlib.components.note.Note
import com.github.anastr.speedviewlib.components.note.TextNote
import java.util.Locale

class WorkWithNoteActivity : AppCompatActivity() {

    private lateinit var speedView: SpeedView
    private lateinit var seekBar: SeekBar
    private lateinit var ok: Button
    private lateinit var textSpeed: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_work_with_note)

        title = "Work With Note"
        speedView = findViewById(R.id.speedView)
        seekBar = findViewById(R.id.seekBar)
        ok = findViewById(R.id.ok)
        textSpeed = findViewById(R.id.textSpeed)

        speedView.sections[0].color = Color.parseColor("#64DD17")
        speedView.sections[1].color = Color.parseColor("#FFAB00")
        speedView.sections[2].color = Color.parseColor("#F44336")
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

        findViewById<View>(R.id.b_center).setOnClickListener { noteCenter() }
        findViewById<View>(R.id.b_center_indicator).setOnClickListener { noteCenterIndicator() }
        findViewById<View>(R.id.b_top_center).setOnClickListener { noteTopIndicator() }
        findViewById<View>(R.id.b_image).setOnClickListener { noteImageNote() }
        findViewById<View>(R.id.b_spannable).setOnClickListener { noteSpannableString() }
    }

    private fun noteCenter() {
        val type = Typeface.createFromAsset(assets, "fonts/lhandw.ttf")
        val note = TextNote(applicationContext, "Wait !")
            .setPosition(Note.Position.CenterSpeedometer)
            .setTextTypeFace(type)
            .setTextSize(speedView.dpTOpx(20f))
        speedView.addNote(note, 2000)
    }

    private fun noteCenterIndicator() {
        val note = TextNote(applicationContext, "Stop !!")
            .setPosition(Note.Position.CenterIndicator)
            .setTextTypeFace(Typeface.create(Typeface.DEFAULT, Typeface.BOLD))
            .setTextSize(speedView.dpTOpx(13f))
        speedView.addNote(note, 2000)
    }

    private fun noteTopIndicator() {
        val note = TextNote(applicationContext, "TOP")
            .setPosition(Note.Position.TopIndicator)
            .setAlign(Note.Align.Bottom)
            .setTextSize(speedView.dpTOpx(13f))
        speedView.addNote(note, 2000)
    }

    private fun noteImageNote() {
        val imageNote = ImageNote(
            applicationContext, R.mipmap.ic_launcher
        )
            .setPosition(Note.Position.BottomIndicator)
        speedView.addNote(imageNote, 2000)
    }

    private fun noteSpannableString() {
        val s = SpannableString("Speedometer")
        s.setSpan(RelativeSizeSpan(1.2f), 0, 11, 0)
        s.setSpan(RelativeSizeSpan(1.7f), 0, 1, 0)
        s.setSpan(ForegroundColorSpan(Color.parseColor("#64DD17")), 0, 1, 0)
        s.setSpan(ForegroundColorSpan(Color.parseColor("#FF1744")), 1, 5, 0)
        s.setSpan(ForegroundColorSpan(Color.parseColor("#AA00FF")), 5, 6, 0)
        s.setSpan(RelativeSizeSpan(1.4f), 5, 6, 0)
        s.setSpan(ForegroundColorSpan(Color.parseColor("#2196F3")), 6, 11, 0)
        val note = TextNote(applicationContext, s)
            .setBackgroundColor(Color.parseColor("#EEFF41"))
            .setPosition(Note.Position.QuarterSpeedometer)
            .setTextSize(speedView.dpTOpx(10f))
        speedView.addNote(note, 2000)
    }
}