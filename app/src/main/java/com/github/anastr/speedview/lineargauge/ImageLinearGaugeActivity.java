package com.github.anastr.speedview.lineargauge;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.TextView;

import com.github.anastr.speedview.R;
import com.github.anastr.speedviewlib.ImageLinearGauge;
import com.github.anastr.speedviewlib.LinearGauge;

import java.util.Locale;

public class ImageLinearGaugeActivity extends AppCompatActivity {

    ImageLinearGauge imageLinearGauge;
    SeekBar seekBar;
    Button ok;
    TextView textSpeed;
    CheckBox checkBoxOrientation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_linear_gauge);

        imageLinearGauge = (ImageLinearGauge) findViewById(R.id.gauge);
        seekBar = (SeekBar) findViewById(R.id.seekBar);
        ok = (Button) findViewById(R.id.ok);
        textSpeed = (TextView) findViewById(R.id.textSpeed);
        checkBoxOrientation = (CheckBox) findViewById(R.id.cb_orientation);

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageLinearGauge.speedTo(seekBar.getProgress());
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

        checkBoxOrientation.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b)
                    imageLinearGauge.setOrientation(LinearGauge.Orientation.VERTICAL);
                else
                    imageLinearGauge.setOrientation(LinearGauge.Orientation.HORIZONTAL);
            }
        });
    }
}
