package com.github.anastr.speedviewapp;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.github.anastr.speedviewlib.SpeedView;
import com.github.anastr.speedviewlib.components.Section;
import com.github.anastr.speedviewlib.components.Style;

import java.util.Locale;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function2;

public class SpeedViewActivity extends AppCompatActivity {

    SpeedView speedView;
    SeekBar seekBar;
    Button ok;
    TextView textSpeed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_speed_view);
        setTitle("Speed View");

        speedView = findViewById(R.id.speedView);
        seekBar = findViewById(R.id.seekBar);
        ok = findViewById(R.id.ok);
        textSpeed = findViewById(R.id.textSpeed);

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

        speedView.setSpeedTextListener(new Function1<Float, CharSequence>() {
            @Override
            public CharSequence invoke(Float speed) {
                return String.format(Locale.getDefault(), "lol%.0f", speed);
            }
        });

        speedView.getSections().clear();
        speedView.addSections(new Section(0f, .1f, Color.LTGRAY, speedView.dpTOpx(30f))
                , new Section(.1f, .2f, Color.GRAY, speedView.dpTOpx(30f), Style.ROUND)
                , new Section(.2f, .3f, Color.DKGRAY, speedView.dpTOpx(30f))
                , new Section(.3f, .4f, Color.BLACK, speedView.dpTOpx(30f))
                , new Section(.4f, .5f, Color.CYAN, speedView.dpTOpx(30f), Style.ROUND)
                , new Section(.5f, .6f, Color.BLUE, speedView.dpTOpx(30f))
                , new Section(.6f, .7f, Color.GREEN, speedView.dpTOpx(30f))
                , new Section(.7f, .8f, Color.YELLOW, speedView.dpTOpx(30f))
                , new Section(.8f, .9f, Color.MAGENTA, speedView.dpTOpx(30f)));
//        speedView.getSections().add(new Section(1f, Color.RED));

        speedView.setOnSectionChangeListener(new Function2<Section, Section, Unit>() {
            @Override
            public Unit invoke(Section previousSection, Section newSection) {
                if (previousSection != null) {
//                    previousSection.setPadding(10);
                    previousSection.setWidth(speedView.dpTOpx(30f));
                }
                if (newSection != null) {
//                    newSection.setPadding(0);
                    newSection.setWidth(speedView.dpTOpx(35f));
                }
//                if(newSection != null && newSection.getEndOffset() > .4f){}
//                if (previousSection != null)
//                    previousSection.setColor(Color.RED);
                return Unit.INSTANCE;
            }
        });
    }

    public void openControlActivity(View view) {
        Intent intent = new Intent(SpeedViewActivity.this, ControlActivity.class);
        startActivity(intent);
    }
}
