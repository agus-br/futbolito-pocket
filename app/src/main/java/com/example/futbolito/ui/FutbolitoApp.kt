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
import dev.ricknout.composesensors.accelerometer.isAccelerometerSensorAvailable
import dev.ricknout.composesensors.accelerometer.rememberAccelerometerSensorValueAsState
import kotlinx.coroutines.delay

val fieldColor = Color(0xFF0A662F)
val goalColor = Color(0xFFFFFFFF)
val ballColor = Color(0xFFE8B002)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FutbolitoApp() {
    if (isAccelerometerSensorAvailable()) {
        val sensorValue by rememberAccelerometerSensorValueAsState()
        val (sensorAceleracionX, sensorAceleracionY, _) = sensorValue.value

        var balonPosicionX by remember { mutableFloatStateOf(500f) }
        var balonPosicionY by remember { mutableFloatStateOf(800f) }
        var velocidadX by remember { mutableFloatStateOf(0f) }
        var velocidadY by remember { mutableFloatStateOf(0f) }
        var golesEquipoA by remember { mutableIntStateOf(0) }
        var golesEquipoB by remember { mutableIntStateOf(0) }

        val tamanoBalon = 25f
        val sensibilidad = 1.5f
        val friccion = 0.92f
        val restitucion = 0.9f

        // Movimiento basado en acelerómetro
        LaunchedEffect(sensorAceleracionX, sensorAceleracionY) {
            velocidadX += -sensorAceleracionX * sensibilidad
            velocidadY += sensorAceleracionY * sensibilidad

            velocidadX *= friccion
            velocidadY *= friccion

            balonPosicionX += velocidadX
            balonPosicionY += velocidadY

        }

        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            "Equipo A: $golesEquipoA",
                            style = MaterialTheme.typography.titleLarge,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                )
            },
            bottomBar = {
                BottomAppBar {
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        Text(
                            "Equipo B: $golesEquipoB",
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
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val anchoCanvas = size.width
                    val altoCanvas = size.height

                    val margen = 50f
                    val anchoCancha = anchoCanvas - (2 * margen)
                    val altoCancha = altoCanvas - (2 * margen)

                    // Dibujar cancha
                    drawRect(
                        color = fieldColor,
                        topLeft = Offset(0f, 0f),
                        size = Size(anchoCanvas, altoCanvas)
                    )
                    drawRect(
                        color = goalColor,
                        topLeft = Offset(margen, margen),
                        size = Size(anchoCancha, altoCancha),
                        style = Stroke(width = 10f)
                    )

                    // Dibujar porterías
                    val anchoPorteria = 150f
                    val altoPorteria = 50f
                    val coordenadaYPorteriaAbajo = altoCanvas - margen
                    val coordenadaXIzquierdaPorteria = (anchoCanvas - anchoPorteria) / 2
                    val coordenadaXDerechaPorteria = coordenadaXIzquierdaPorteria + anchoPorteria

                    drawRect(
                        color = goalColor,
                        topLeft = Offset(coordenadaXIzquierdaPorteria, 0f),
                        size = Size(anchoPorteria, altoPorteria),
                        style = Stroke(width = 10f)
                    )
                    drawRect(
                        color = goalColor,
                        topLeft = Offset(coordenadaXIzquierdaPorteria, altoCancha + altoPorteria),
                        size = Size(anchoPorteria, altoPorteria),
                        style = Stroke(width = 10f)
                    )

                    // Dibujar círculo central
                    drawCircle(
                        color = goalColor,
                        center = Offset(anchoCanvas / 2f, altoCanvas / 2f),
                        radius = anchoCancha * 0.1f,
                        style = Stroke(width = 10f)
                    )

                    // Línea central
                    drawLine(
                        color = goalColor,
                        start = Offset(margen, altoCanvas / 2f),
                        end = Offset(anchoCanvas - margen, altoCanvas / 2f),
                        strokeWidth = 10f
                    )

                    // Dibujar áreas de meta
                    val anchoMeta = 300f
                    val altoMeta = 100f
                    drawRect(
                        color = goalColor,
                        topLeft = Offset((anchoCanvas - anchoMeta) / 2, margen),
                        size = Size(anchoMeta, altoMeta),
                        style = Stroke(width = 10f)
                    )
                    drawRect(
                        color = goalColor,
                        topLeft = Offset((anchoCanvas - anchoMeta) / 2, altoCancha - altoMeta + margen),
                        size = Size(anchoMeta, altoMeta),
                        style = Stroke(width = 10f)
                    )


                    // Dibujar áreas de penalti
                    val anchoAreaPenal = 600f
                    val altoAreaPenal = 250f
                    drawRect(
                        color = goalColor,
                        topLeft = Offset((anchoCanvas - anchoAreaPenal) / 2, margen),
                        size = Size(anchoAreaPenal, altoAreaPenal),
                        style = Stroke(width = 10f)
                    )
                    drawRect(
                        color = goalColor,
                        topLeft = Offset((anchoCanvas - anchoAreaPenal) / 2, altoCancha - altoAreaPenal + margen),
                        size = Size(anchoAreaPenal, altoAreaPenal),
                        style = Stroke(width = 10f)
                    )

                    // Detectar si la pelota toca la portería
                    val golEquipoB = balonPosicionY - tamanoBalon <= margen && balonPosicionX in coordenadaXIzquierdaPorteria..coordenadaXDerechaPorteria
                    val golEquipoA = balonPosicionY + tamanoBalon >= coordenadaYPorteriaAbajo && balonPosicionX in coordenadaXIzquierdaPorteria..coordenadaXDerechaPorteria

                    if (golEquipoB) {
                        golesEquipoB++
                        balonPosicionX = anchoCanvas / 2
                        balonPosicionY = altoCanvas / 2
                        velocidadX = 0f
                        velocidadY = 0f
                    } else if (golEquipoA) {
                        golesEquipoA++
                        balonPosicionX = anchoCanvas / 2
                        balonPosicionY = altoCanvas / 2
                        velocidadX = 0f
                        velocidadY = 0f
                    }

                    // Rebotes en las paredes
                    if (balonPosicionX - tamanoBalon < margen || balonPosicionX + tamanoBalon > anchoCanvas - margen) {
                        velocidadX = -velocidadX * restitucion
                        balonPosicionX = balonPosicionX.coerceIn(margen + tamanoBalon, anchoCanvas - margen - tamanoBalon)
                    }
                    if (balonPosicionY - tamanoBalon < margen || balonPosicionY + tamanoBalon > altoCanvas - margen) {
                        velocidadY = -velocidadY * restitucion
                        balonPosicionY = balonPosicionY.coerceIn(margen + tamanoBalon, altoCanvas - margen - tamanoBalon)
                    }

                    drawCircle(
                        ballColor,
                        radius = tamanoBalon,
                        center = Offset(balonPosicionX, balonPosicionY)
                    )
                }
            }
        }
    } else {
        Text("Acelerómetro no disponible")
    }
}
