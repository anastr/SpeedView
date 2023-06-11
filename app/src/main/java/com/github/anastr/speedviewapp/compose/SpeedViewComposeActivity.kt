package com.github.anastr.speedviewapp.compose

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Slider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.github.anastr.speedviewlib_compose.SpeedViewDecoration
import com.github.anastr.speedviewlib_compose.Speedometer

class SpeedViewComposeActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        title = "Compose"
        setContent { 
            MaterialTheme {
                Content()
            }
        }
    }
}

@Composable
private fun Content() {
    Column(
        modifier = Modifier.fillMaxWidth().padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        var speed by remember { mutableStateOf(0f) }
        val currentSpeed by animateFloatAsState(
            targetValue = speed,
            animationSpec = tween(durationMillis = 2000, easing = FastOutSlowInEasing),
        )
        Speedometer(
            modifier = Modifier.size(250.dp),
            speed = currentSpeed,
            decoration = SpeedViewDecoration,
        )
        Spacer(modifier = Modifier.height(8.dp))

        Row {
            var sliderPosition by remember { mutableStateOf(0f) }
            Slider(
                modifier = Modifier.weight(1f),
                value = sliderPosition,
                onValueChange = { sliderPosition = it },
                valueRange = 0f..100f,
            )
            Button(onClick = { speed = sliderPosition }) {
                Text(text = "Go")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SpeedometerTest() {
    MaterialTheme {
        Content()
    }
}
