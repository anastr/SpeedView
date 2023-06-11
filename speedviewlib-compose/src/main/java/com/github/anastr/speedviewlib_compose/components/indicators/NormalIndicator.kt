package com.github.anastr.speedviewlib_compose.components.indicators

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.center
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun NormalIndicator(
    modifier: Modifier = Modifier,
    color: Color = Color(0XFF2196F3),
    width: Dp = 12.dp,
) {
    Canvas(modifier = modifier.fillMaxSize()) {
        val center = size.center
        val indicatorPath = Path()
        indicatorPath.reset()
        indicatorPath.moveTo(center.x, 0f)
        val bottomY = size.height * 2f / 3f
        indicatorPath.lineTo(center.x - width.toPx(), bottomY)
        indicatorPath.lineTo(center.x + width.toPx(), bottomY)
        val rectF = Rect(center.x - width.toPx(), bottomY - width.toPx(), center.x + width.toPx(), bottomY + width.toPx())
        indicatorPath.addArc(rectF, 0f, 180f)

        drawPath(
            path = indicatorPath,
            color = color,
        )
    }
}
