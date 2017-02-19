package com.github.anastr.speedviewlib.base;

import android.graphics.Color;

import com.github.anastr.speedviewlib.components.Indicators.Indicator;

/**
 * this Library build By Anas Altair
 * see it on <a href="https://github.com/anastr/SpeedView">GitHub</a>
 */
public class SpeedometerDefault {

    public Speedometer.Mode speedometerMode;

    public Indicator indicator;

    public int startDegree = 135, endDegree = 135+270;

    public float speedometerWidth = -1;

    public int centerCircleColor = Color.DKGRAY
            , markColor = Color.WHITE
            , lowSpeedColor = Color.GREEN
            , mediumSpeedColor = Color.YELLOW
            , highSpeedColor = Color.RED
            , backgroundCircleColor = Color.WHITE;
}
