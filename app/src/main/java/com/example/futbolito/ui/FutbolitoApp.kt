package com.example.futbolito.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.ricknout.composesensors.accelerometer.isAccelerometerSensorAvailable
import dev.ricknout.composesensors.accelerometer.rememberAccelerometerSensorValueAsState

@Composable
fun FutbolitoApp() {
    if (isAccelerometerSensorAvailable()) {
        val sensorValue by rememberAccelerometerSensorValueAsState()
        val (x, y, z) = sensorValue.value

        var ballPosition by remember { mutableStateOf(Offset(500f, 500f)) }

        // Ajustar la posición de la pelota según el acelerómetro
        LaunchedEffect(x, y) {
            ballPosition = Offset(
                (ballPosition.x - x * 10).coerceIn(0f, 1000f), // Limita dentro de los bordes
                (ballPosition.y + y * 10).coerceIn(0f, 2000f)
            )
        }

        Column(modifier = Modifier.fillMaxSize()) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                drawCircle(Color.Blue, radius = 50f, center = ballPosition)
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text("X: $x")
            Text("Y: $y")
            Text("Z: $z")
        }
    } else {
        Text("Acelerómetro no disponible")
    }
}