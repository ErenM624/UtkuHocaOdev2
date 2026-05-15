package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.ui.theme.MyApplicationTheme
import java.util.Locale
import kotlin.math.cos
import kotlin.math.sin

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                SpeedTestScreen()
            }
        }
    }
}

@Composable
fun SpeedTestScreen() {
    val backgroundColor = Color(0xFFF2F2F2)
    val darkBlue = Color(0xFF0C182F)

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = backgroundColor
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .safeDrawingPadding(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(40.dp))

            // Upper Section: Metrics and Gauge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Left: Metrics
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    MetricItem("DOWNLOAD", "DEĞER")
                    MetricItem("UPLOAD", "DEĞER")
                    MetricItem("PING", "DEĞER")
                }

                // Right: Gauge
                Box(
                    modifier = Modifier
                        .size(200.dp)
                        .background(darkBlue, RoundedCornerShape(4.dp))
                        .padding(8.dp)
                ) {
                    SpeedGauge(8.97f)
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Middle Section: Provider Info
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(modifier = Modifier.fillMaxWidth()) {
                    InfoItem("SAĞLAYICI İSMİ", modifier = Modifier.weight(1f))
                    InfoItem("ANA SAĞLAYICI İSMİ", modifier = Modifier.weight(1f))
                }
                Row(modifier = Modifier.fillMaxWidth()) {
                    InfoItem("IP ADRESİ", modifier = Modifier.weight(1f))
                    InfoItem("YER BİLGİSİ", modifier = Modifier.weight(1f))
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Bottom Section: Start Button
            Button(
                onClick = { /* Start Test Logic */ },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .border(1.dp, Color.Gray, RoundedCornerShape(4.dp)),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White,
                    contentColor = Color.Black
                ),
                shape = RoundedCornerShape(4.dp)
            ) {
                Text(
                    text = "START TEST",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun MetricItem(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = label,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = Color.DarkGray,
            modifier = Modifier.width(80.dp)
        )
        Text(
            text = value,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = Color.DarkGray
        )
    }
}

@Composable
fun InfoItem(label: String, modifier: Modifier = Modifier) {
    Text(
        text = label,
        fontSize = 14.sp,
        fontWeight = FontWeight.Medium,
        color = Color.Black,
        modifier = modifier
    )
}

@Composable
fun SpeedGauge(speed: Float) {
    Box(modifier = Modifier.fillMaxSize()) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(size.width / 2, size.height / 2)
            val radius = size.width / 2 * 0.85f
            
            // Draw the background arc (darker blue)
            drawArc(
                color = Color.White.copy(alpha = 0.1f),
                startAngle = 135f,
                sweepAngle = 270f,
                useCenter = false,
                style = Stroke(width = 15f, cap = StrokeCap.Round),
                size = Size(radius * 2, radius * 2),
                topLeft = Offset(center.x - radius, center.y - radius)
            )

            // Draw progress arc
            val sweepAngle = (speed / 100f) * 270f
            drawArc(
                color = Color(0xFF3D85F7),
                startAngle = 135f,
                sweepAngle = sweepAngle.coerceAtMost(270f),
                useCenter = false,
                style = Stroke(width = 15f, cap = StrokeCap.Round),
                size = Size(radius * 2, radius * 2),
                topLeft = Offset(center.x - radius, center.y - radius)
            )

            // Draw Needle
            val needleAngle = 135f + sweepAngle
            val needleLen = radius * 0.8f
            val endX = center.x + needleLen * cos(Math.toRadians(needleAngle.toDouble())).toFloat()
            val endY = center.y + needleLen * sin(Math.toRadians(needleAngle.toDouble())).toFloat()
            
            drawLine(
                color = Color.White,
                start = center,
                end = Offset(endX, endY),
                strokeWidth = 4f,
                cap = StrokeCap.Round
            )
        }
        
        // Gauge Labels (Simplified)
        GaugeLabel("0", 135f)
        GaugeLabel("10", 216f)
        GaugeLabel("20", 256.5f)
        GaugeLabel("30", 297f)
        GaugeLabel("50", 337.5f)
        GaugeLabel("100", 405f)

        // Digital Value Overlay
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.BottomCenter) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(bottom = 20.dp)) {
                Text(
                    text = String.format(Locale.getDefault(), "%.2f", speed),
                    color = Color.White,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "↑", // Simple arrow
                        color = Color.Gray,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Mbps",
                        color = Color.Gray,
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}

@Composable
fun GaugeLabel(text: String, angle: Float) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp),
        contentAlignment = Alignment.Center
    ) {
        val angleRad = Math.toRadians(angle.toDouble())
        val xOffset = (70 * cos(angleRad)).dp
        val yOffset = (70 * sin(angleRad)).dp
        
        Text(
            text = text,
            color = Color.White.copy(alpha = 0.7f),
            fontSize = 10.sp,
            modifier = Modifier.offset(x = xOffset, y = yOffset)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun SpeedTestPreview() {
    MyApplicationTheme {
        SpeedTestScreen()
    }
}
