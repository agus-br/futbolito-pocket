package com.example.futbolito.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import dev.ricknout.composesensors.accelerometer.isAccelerometerSensorAvailable
import dev.ricknout.composesensors.accelerometer.rememberAccelerometerSensorValueAsState

// Definir colores personalizados
val fieldColor = Color(0xFF0A662F) // Verde oscuro para la cancha
val goalColor = Color(0xFFFFFFFF) // Blanco para las porterías
val ballColor = Color(0xFF1E90FF) // Azul brillante para la pelota

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FutbolitoApp() {
    if (isAccelerometerSensorAvailable()) {
        val sensorValue by rememberAccelerometerSensorValueAsState()
        val (x, y, _) = sensorValue.value

        var ballPosition by remember { mutableStateOf(Offset(500f, 800f)) }
        var scoreTeamA by remember { mutableIntStateOf(0) }
        var scoreTeamB by remember { mutableIntStateOf(0) }

        val ballSize = 20f

        // Ajustar la posición de la pelota según el acelerómetro
        LaunchedEffect(x, y) {
            ballPosition = Offset(
                (ballPosition.x - x * 10),
                (ballPosition.y + y * 10)
            )
        }

        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            "Equipo A: $scoreTeamA",
                            style = MaterialTheme.typography.titleLarge,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                )
            },
            bottomBar = {
                BottomAppBar {
                    Box(modifier = Modifier
                        .fillMaxWidth(),
                        contentAlignment = Alignment.Center) {
                        Text(
                            "Equipo B: $scoreTeamB",
                            style = MaterialTheme.typography.bodyLarge,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)

            ) {
                Canvas(modifier = Modifier
                    .fillMaxSize()
                ) {
                    val canvasWidth = size.width
                    val canvasHeight = size.height

                    // Definir área de juego dentro de la pantalla
                    val margin = 50f
                    val canchaWidth = canvasWidth - 2 * margin
                    val canchaHeight = canvasHeight - 2 * margin

                    // Dibujar la cancha
                    drawRect(
                        color = fieldColor,
                        topLeft = Offset(0f, 0f),
                        size = Size(canvasWidth, canvasHeight)
                    )

                    // Dibujar las líneas blancas del campo
                    drawRect(
                        color = goalColor,
                        topLeft = Offset(margin, margin),
                        size = Size(canchaWidth, canchaHeight),
                        style = Stroke(width = 10f)
                    )

                    // Dibujar porterías
                    val goalWidth = 150f
                    val goalHeight = 50f
                    val goalBottomY = canvasHeight - margin
                    val goalLeftX = (canvasWidth - goalWidth) / 2
                    val goalRightX = goalLeftX + goalWidth

                    drawRect(
                        color = goalColor,
                        topLeft = Offset((canvasWidth - goalWidth) / 2 , 0f),
                        size = Size(goalWidth, goalHeight),
                        style = Stroke(width = 10f)
                    )
                    drawRect(
                        color = goalColor,
                        topLeft = Offset((canvasWidth - goalWidth) / 2, canchaHeight + goalHeight),
                        size = Size(goalWidth, goalHeight),
                        style = Stroke(width = 10f)
                    )

                    // Dibujar círculo central
                    drawCircle(
                        color = goalColor,
                        center = Offset(canvasWidth / 2f, canvasHeight / 2f),
                        radius = canchaWidth * 0.1f,
                        style = Stroke(width = 10f)
                    )

                    // Línea central
                    drawLine(
                        color = goalColor,
                        start = Offset(margin, canvasHeight / 2f),
                        end = Offset(canvasWidth - margin, canvasHeight / 2f),
                        strokeWidth = 10f
                    )

                    // Dibujar áreas de meta
                    val areaWidth = 300f
                    val areaHeight = 100f
                    drawRect(
                        color = goalColor,
                        topLeft = Offset((canvasWidth - areaWidth) / 2, margin),
                        size = Size(areaWidth, areaHeight),
                        style = Stroke(width = 10f)
                    )
                    drawRect(
                        color = goalColor,
                        topLeft = Offset((canvasWidth - areaWidth) / 2, canchaHeight - areaHeight + margin),
                        size = Size(areaWidth, areaHeight),
                        style = Stroke(width = 10f)
                    )


                    // Dibujar áreas de penalti
                    val areaPenalWidth = 600f
                    val areaPenalHeight = 250f
                    drawRect(
                        color = goalColor,
                        topLeft = Offset((canvasWidth - areaPenalWidth) / 2, margin),
                        size = Size(areaPenalWidth, areaPenalHeight),
                        style = Stroke(width = 10f)
                    )
                    drawRect(
                        color = goalColor,
                        topLeft = Offset((canvasWidth - areaPenalWidth) / 2, canchaHeight - areaPenalHeight + margin),
                        size = Size(areaPenalWidth, areaPenalHeight),
                        style = Stroke(width = 10f)
                    )


                    // Dibujar la pelota y asegurar que no salga de la cancha
                    val adjustedBallX = ballPosition.x.coerceIn(margin + ballSize, canvasWidth - margin - ballSize)
                    val adjustedBallY = ballPosition.y.coerceIn(margin + ballSize, canvasHeight - margin - ballSize)

                    drawCircle(
                        ballColor,
                        radius = ballSize,
                        center = Offset(adjustedBallX, adjustedBallY)
                    )

                    // Detectar si la pelota toca la línea de la portería
                    val ballTouchGoalTop = adjustedBallY - ballSize <= margin && adjustedBallX in goalLeftX..goalRightX
                    val ballTouchGoalBottom = adjustedBallY + ballSize >= goalBottomY && adjustedBallX in goalLeftX..goalRightX

                    if (ballTouchGoalTop) {
                        scoreTeamA++
                        ballPosition = Offset(canvasWidth / 2, canvasHeight / 2) // Reiniciar posición
                    } else if (ballTouchGoalBottom) {
                        scoreTeamB++
                        ballPosition = Offset(canvasWidth / 2, canvasHeight / 2) // Reiniciar posición
                    }
                }
            }
        }
    } else {
        Text("Acelerómetro no disponible")
    }
}
