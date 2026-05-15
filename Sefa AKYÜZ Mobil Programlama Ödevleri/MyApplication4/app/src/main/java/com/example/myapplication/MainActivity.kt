package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.ui.theme.MyApplicationTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

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

enum class TestState { IDLE, TESTING, DONE }

@Composable
fun SpeedTestScreen() {
    val bg = Color(0xFF0A0E1A)
    val accent = Color(0xFF3B82F6)
    val accentGreen = Color(0xFF22C55E)
    val accentOrange = Color(0xFFF97316)

    var testState by remember { mutableStateOf(TestState.IDLE) }
    var download by remember { mutableFloatStateOf(0f) }
    var upload by remember { mutableFloatStateOf(0f) }
    var ping by remember { mutableIntStateOf(0) }
    var currentSpeed by remember { mutableStateOf(0f) }
    var phase by remember { mutableStateOf("") }

    val scope = rememberCoroutineScope()

    fun startTest() {
        testState = TestState.TESTING
        download = 0f
        upload = 0f
        ping = 0
        currentSpeed = 0f

        scope.launch {
            // Ping phase
            phase = "PING"
            repeat(8) {
                delay(120)
                ping = Random.nextInt(8, 25)
            }

            // Download phase
            phase = "İNDİRME"
            val dlTarget = Random.nextFloat() * 60f + 40f
            var dl = 0f
            while (dl < dlTarget) {
                delay(80)
                dl = (dl + Random.nextFloat() * 4f).coerceAtMost(dlTarget)
                currentSpeed = dl
            }
            download = dl

            // Upload phase
            phase = "YÜKLEME"
            val ulTarget = Random.nextFloat() * 30f + 15f
            var ul = 0f
            while (ul < ulTarget) {
                delay(80)
                ul = (ul + Random.nextFloat() * 3f).coerceAtMost(ulTarget)
                currentSpeed = ul
            }
            upload = ul

            currentSpeed = 0f
            phase = ""
            testState = TestState.DONE
        }
    }

    Surface(modifier = Modifier.fillMaxSize(), color = bg) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding()
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(modifier = Modifier.height(24.dp))

            // ── Başlık ──────────────────────────────────────
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Hız Testi", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    Text("Bağlantı performansını ölç", color = Color.White.copy(0.45f), fontSize = 12.sp)
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.White.copy(0.06f))
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text("Türk Telekom", color = Color.White.copy(0.7f), fontSize = 12.sp)
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // ── Gösterge ─────────────────────────────────────
            Box(
                modifier = Modifier.size(240.dp),
                contentAlignment = Alignment.Center
            ) {
                SpeedGauge(
                    speed = currentSpeed,
                    maxSpeed = 100f,
                    phase = phase,
                    state = testState
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            // ── Metrik Kartlar ────────────────────────────────
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                MetricCard(
                    modifier = Modifier.weight(1f),
                    label = "İNDİRME",
                    value = if (download > 0f) "%.1f".format(download) else "—",
                    unit = "Mbps",
                    color = accent,
                    icon = "↓"
                )
                MetricCard(
                    modifier = Modifier.weight(1f),
                    label = "YÜKLEME",
                    value = if (upload > 0f) "%.1f".format(upload) else "—",
                    unit = "Mbps",
                    color = accentGreen,
                    icon = "↑"
                )
                MetricCard(
                    modifier = Modifier.weight(1f),
                    label = "PING",
                    value = if (ping > 0) ping.toString() else "—",
                    unit = "ms",
                    color = accentOrange,
                    icon = "◎"
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ── Bağlantı Bilgileri ────────────────────────────
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                InfoCard(modifier = Modifier.weight(1f), label = "IP ADRESİ", value = "192.168.1.1")
                InfoCard(modifier = Modifier.weight(1f), label = "YER", value = "İstanbul, TR")
            }

            Spacer(modifier = Modifier.weight(1f))

            // ── Başlat Butonu ─────────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .then(
                        if (testState == TestState.TESTING)
                            Modifier.background(Color.White.copy(0.06f))
                        else
                            Modifier.background(Brush.horizontalGradient(listOf(accent, Color(0xFF6366F1))))
                    )
                    .clickable(enabled = testState != TestState.TESTING) { startTest() },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = when (testState) {
                        TestState.IDLE -> "TESTI BAŞLAT"
                        TestState.TESTING -> "TEST EDİLİYOR..."
                        TestState.DONE -> "YENİDEN TEST ET"
                    },
                    color = if (testState == TestState.TESTING) Color.White.copy(0.4f) else Color.White,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
fun SpeedGauge(speed: Float, maxSpeed: Float, phase: String, state: TestState) {
    val darkBlue = Color(0xFF131929)
    val accent = Color(0xFF3B82F6)
    val accentGreen = Color(0xFF22C55E)

    val arcColor = if (phase == "YÜKLEME") accentGreen else accent

    val animatedSpeed by animateFloatAsState(
        targetValue = speed,
        animationSpec = tween(200),
        label = "speed"
    )

    val pulseAnim = rememberInfiniteTransition(label = "pulse")
    val pulseAlpha by pulseAnim.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.9f,
        animationSpec = infiniteRepeatable(tween(900), RepeatMode.Reverse),
        label = "alpha"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .clip(CircleShape)
            .background(darkBlue),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            val cx = size.width / 2
            val cy = size.height / 2
            val r = size.width / 2 * 0.88f

            // Arka plan yayı
            drawArc(
                color = Color.White.copy(0.08f),
                startAngle = 135f,
                sweepAngle = 270f,
                useCenter = false,
                style = Stroke(width = 18f, cap = StrokeCap.Round),
                size = Size(r * 2, r * 2),
                topLeft = Offset(cx - r, cy - r)
            )

            // İlerleme yayı
            val sweep = (animatedSpeed / maxSpeed * 270f).coerceIn(0f, 270f)
            if (sweep > 0f) {
                drawArc(
                    color = arcColor,
                    startAngle = 135f,
                    sweepAngle = sweep,
                    useCenter = false,
                    style = Stroke(width = 18f, cap = StrokeCap.Round),
                    size = Size(r * 2, r * 2),
                    topLeft = Offset(cx - r, cy - r)
                )
            }

            // İğne
            val needleAngleDeg = 135f + sweep
            val needleRad = Math.toRadians(needleAngleDeg.toDouble())
            val needleLen = r * 0.72f
            drawLine(
                color = Color.White.copy(0.9f),
                start = Offset(cx, cy),
                end = Offset(
                    cx + needleLen * cos(needleRad).toFloat(),
                    cy + needleLen * sin(needleRad).toFloat()
                ),
                strokeWidth = 3.5f,
                cap = StrokeCap.Round
            )

            // Merkez nokta
            drawCircle(color = Color.White, radius = 6f, center = Offset(cx, cy))
            drawCircle(color = arcColor, radius = 3f, center = Offset(cx, cy))
        }

        // Sayısal değer
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = if (speed > 0f) "%.1f".format(speed) else
                    if (state == TestState.DONE) "✓" else "—",
                color = Color.White,
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Mbps",
                color = Color.White.copy(0.45f),
                fontSize = 13.sp
            )
            if (phase.isNotEmpty()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = phase,
                    color = arcColor.copy(alpha = pulseAlpha),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    letterSpacing = 1.sp
                )
            }
        }
    }
}

@Composable
fun MetricCard(
    modifier: Modifier = Modifier,
    label: String,
    value: String,
    unit: String,
    color: Color,
    icon: String
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFF131929))
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(icon, color = color, fontSize = 14.sp)
            Spacer(modifier = Modifier.width(4.dp))
            Text(label, color = Color.White.copy(0.45f), fontSize = 9.sp, letterSpacing = 0.5.sp)
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(value, color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        Text(unit, color = color.copy(0.8f), fontSize = 10.sp)
    }
}

@Composable
fun InfoCard(modifier: Modifier = Modifier, label: String, value: String) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(Color(0xFF131929))
            .padding(horizontal = 12.dp, vertical = 10.dp)
    ) {
        Text(label, color = Color.White.copy(0.4f), fontSize = 10.sp, letterSpacing = 0.5.sp)
        Spacer(modifier = Modifier.height(3.dp))
        Text(value, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Medium)
    }
}

@Preview(showBackground = true)
@Composable
fun SpeedTestPreview() {
    MyApplicationTheme {
        SpeedTestScreen()
    }
}