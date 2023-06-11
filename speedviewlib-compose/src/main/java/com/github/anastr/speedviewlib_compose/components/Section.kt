package com.github.anastr.speedviewlib_compose.components

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Immutable
data class Section(
    val startOffset: Float,
    val endOffset: Float,
    val color: Color,
    val width: Dp = 30.dp,
    val style: StrokeCap = StrokeCap.Butt,
) {

    init {
        require(startOffset in 0f..1f) { "startOffset must be between [0f, 1f]" }
        require(endOffset in 0f..1f) { "endOffset must be between [0f, 1f]" }
        require(endOffset > startOffset) { "endOffset must be bigger than startOffset" }
    }
}
