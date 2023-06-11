package com.github.anastr.speedviewlib_compose.components.cneter

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun SvCenterCircle(
    size: Dp = 40.dp,
    color: Color = Color(0xFF444444),
) {
    Box(
        modifier = Modifier
            .size(size)
            .background(color, CircleShape),
    )
}
