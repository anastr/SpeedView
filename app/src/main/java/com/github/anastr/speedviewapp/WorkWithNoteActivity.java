package com.github.anastr.speedviewapp;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.github.anastr.speedviewlib.SpeedView;
import com.github.anastr.speedviewlib.components.note.ImageNote;
import com.github.anastr.speedviewlib.components.note.Note;
import com.github.anastr.speedviewlib.components.note.TextNote;

import java.util.Locale;

public class WorkWithNoteActivity extends AppCompatActivity {

    SpeedView speedView;
    SeekBar seekBar;
    Button ok;
    TextView textSpeed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_work_with_note);
        setTitle("Work With Note");

        speedView = findViewById(R.id.speedView);
        seekBar = findViewById(R.id.seekBar);
        ok = findViewById(R.id.ok);
        textSpeed = findViewById(R.id.textSpeed);

        speedView.getSections().get(0).setColor(Color.parseColor("#64DD17"));
        speedView.getSections().get(1).setColor(Color.parseColor("#FFAB00"));
        speedView.getSections().get(2).setColor(Color.parseColor("#F44336"));

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                speedView.speedTo(seekBar.getProgress());
            }
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                textSpeed.setText(String.format(Locale.getDefault(), "%d", progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    public void noteCenter(View view) {
        Typeface type = Typeface.createFromAsset(getAssets(),"fonts/lhandw.ttf");
        TextNote note = new TextNote(getApplicationContext(), "Wait !")
                .setPosition(Note.Position.CenterSpeedometer)
                .setTextTypeFace(type)
                .setTextSize(speedView.dpTOpx(20f));
        speedView.addNote(note, 2000);
    }

    public void noteCenterIndicator(View view) {
        TextNote note = new TextNote(getApplicationContext(), "Stop !!")
                .setPosition(Note.Position.CenterIndicator)
                .setTextTypeFace(Typeface.create(Typeface.DEFAULT, Typeface.BOLD))
                .setTextSize(speedView.dpTOpx(13f));
        speedView.addNote(note, 2000);
    }

    public void noteTopIndicator(View view) {
        TextNote note = new TextNote(getApplicationContext(), "TOP")
                .setPosition(Note.Position.TopIndicator)
                .setAlign(Note.Align.Bottom)
                .setTextSize(speedView.dpTOpx(13f));
        speedView.addNote(note, 2000);
    }

    public void noteImageNote(View view) {
        ImageNote imageNote = new ImageNote(getApplicationContext()
                , R.mipmap.ic_launcher )
                .setPosition(Note.Position.BottomIndicator);

        speedView.addNote(imageNote, 2000);
    }

    public void noteSpannableString(View view) {
        SpannableString s = new SpannableString("Speedometer");
        s.setSpan(new RelativeSizeSpan(1.2f), 0, 11, 0);
        s.setSpan(new RelativeSizeSpan(1.7f), 0, 1, 0);
        s.setSpan(new ForegroundColorSpan(Color.parseColor("#64DD17")), 0, 1, 0);
        s.setSpan(new ForegroundColorSpan(Color.parseColor("#FF1744")), 1, 5, 0);
        s.setSpan(new ForegroundColorSpan(Color.parseColor("#AA00FF")), 5, 6, 0);
        s.setSpan(new RelativeSizeSpan(1.4f), 5, 6, 0);
        s.setSpan(new ForegroundColorSpan(Color.parseColor("#2196F3")), 6, 11, 0);

        TextNote note = new TextNote(getApplicationContext(), s)
                .setBackgroundColor(Color.parseColor("#EEFF41"))
                .setPosition(Note.Position.QuarterSpeedometer)
                .setTextSize(speedView.dpTOpx(10f));
        speedView.addNote(note, 2000);
    }
}
