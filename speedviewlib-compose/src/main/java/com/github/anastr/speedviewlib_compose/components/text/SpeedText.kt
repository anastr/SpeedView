package com.github.anastr.speedviewlib_compose.components.text

import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import kotlin.math.roundToInt

@Composable
fun SpeedText(
    modifier: Modifier = Modifier,
    speed: Float,
    style: TextStyle = TextStyle.Default,
) {
    BasicText(
        modifier = modifier,
        text = speed.roundToInt().toString(),
        style = style,
    )
}
