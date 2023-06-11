package com.github.anastr.speedviewlib_compose.components.indicators

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import com.github.anastr.speedviewlib_compose.SpeedometerScope

@Composable
internal fun SpeedometerScope.IndicatorBox(
    modifier: Modifier = Modifier,
    speed: Float,
    indicator: @Composable BoxScope.() -> Unit,
) {
    Box(
        modifier = modifier.rotate(90f + degreeAtSpeed(speed)),
        contentAlignment = Alignment.Center,
    ) {
        indicator()
    }
}
