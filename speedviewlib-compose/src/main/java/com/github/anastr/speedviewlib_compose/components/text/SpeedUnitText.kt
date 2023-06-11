package com.github.anastr.speedviewlib_compose.components.text

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun SpeedUnitText(
    modifier: Modifier = Modifier,
    alignment: Alignment = Alignment.BottomCenter,
    speedText: @Composable () -> Unit,
    unitText: @Composable () -> Unit,
    drawUnit: Boolean,
    unitUnderSpeed: Boolean = false,
    spacer: Dp = 2.dp,
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = alignment,
    ) {
        if (unitUnderSpeed) {
            Column(
                modifier = modifier,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                speedText()
                if (drawUnit) {
                    Spacer(modifier = Modifier.height(spacer))
                    unitText()
                }
            }
        } else {
            Row(
                modifier = modifier,
                verticalAlignment = Alignment.Bottom,
            ) {
                speedText()
                if (drawUnit) {
                    Spacer(modifier = Modifier.width(spacer))
                    unitText()
                }
            }
        }
    }
}
