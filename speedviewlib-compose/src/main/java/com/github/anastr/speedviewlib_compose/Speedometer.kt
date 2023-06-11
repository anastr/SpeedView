package com.github.anastr.speedviewlib_compose

import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.anastr.speedviewlib_compose.components.Section
import com.github.anastr.speedviewlib_compose.components.cneter.CenterBox
import com.github.anastr.speedviewlib_compose.components.cneter.SvCenterCircle
import com.github.anastr.speedviewlib_compose.components.indicators.IndicatorBox
import com.github.anastr.speedviewlib_compose.components.indicators.NormalIndicator
import com.github.anastr.speedviewlib_compose.components.text.SpeedText
import com.github.anastr.speedviewlib_compose.components.text.SpeedUnitText
import com.github.anastr.speedviewlib_compose.components.ticks.Ticks
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

typealias SpeedometerDecoration =  @Composable SpeedometerScope.(
    sections: ImmutableList<Section>,
    ticks: ImmutableList<Float>,
) -> Unit

@Composable
fun Speedometer(
    modifier: Modifier = Modifier,
    decoration: SpeedometerDecoration,
    minSpeed: Float = 0f,
    maxSpeed: Float = 100f,
    speed: Float = minSpeed,
    startDegree: Int = 135,
    endDegree: Int = 405,
    unit: String = "Km",
    unitSpeedSpace: Dp = 2.dp,
    unitUnderSpeed: Boolean = false,
    indicator: @Composable BoxScope.() -> Unit = { NormalIndicator() },
    centerContent: @Composable BoxScope.() -> Unit = { SvCenterCircle() },
    speedText: @Composable () -> Unit = { SpeedText(speed = speed) },
    unitText: @Composable () -> Unit = { BasicText(text = unit) },
    sections: ImmutableList<Section> = persistentListOf(
        Section(0f, .6f, Color(0xFF00FF00.toInt())),
        Section(.6f, .87f, Color(0xFFFFFF00.toInt())),
        Section(.87f, 1f, Color(0xFFFF0000.toInt())),
    ),
    ticks: ImmutableList<Float> = persistentListOf(0f, 1f),
    tickPadding: Dp = 30.dp,
    tickRotate: Boolean = true,
    tickLabel: @Composable BoxScope.(index: Int, tickSpeed: Float) -> Unit = { _, tickSpeed ->
        SpeedText(
            speed = tickSpeed,
            style = TextStyle.Default.copy(fontSize = 10.sp),
        )
    },
) {
    BaseSpeedometer(
        modifier = modifier,
        minSpeed = minSpeed,
        maxSpeed = maxSpeed,
        startDegree = startDegree,
        endDegree = endDegree,
    ) {
        decoration(sections, ticks)

        Ticks(
            ticks = ticks,
            paddingTop = tickPadding,
            isRotate = tickRotate,
            label = tickLabel,
        )

        SpeedUnitText(
            modifier = Modifier.padding(10.dp),
            speedText = speedText,
            unitText = unitText,
            drawUnit = unit.isNotBlank(),
            spacer = unitSpeedSpace,
            unitUnderSpeed = unitUnderSpeed,
        )

        IndicatorBox(
            modifier = Modifier.fillMaxSize(),
            speed = speed,
            indicator = indicator,
        )

        CenterBox(
            modifier = Modifier.fillMaxSize(),
            center = centerContent,
        )
    }
}
