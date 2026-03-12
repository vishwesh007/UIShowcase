package com.ui.animatedmenu.showcase

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun TimerScreen(onBack: () -> Unit = {}) {
    val Bg = Color(0xFFF7F3EE)
    val Dark = Color(0xFF1A1A1A)
    val Coral = Color(0xFFE8624A)
    val CoralLight = Color(0xFFFF8A65)
    val Surface = Color(0xFFFFFFFF)
    val TextMuted = Color(0xFFBDBDBD)

    val inf = rememberInfiniteTransition(label = "timer")
    val secondsAngle by inf.animateFloat(0f, 360f,
        infiniteRepeatable(tween(60000, easing = LinearEasing)), label = "sec")
    val minuteAngle by inf.animateFloat(0f, 360f,
        infiniteRepeatable(tween(3600000, easing = LinearEasing)), label = "min")
    val pulseRing by inf.animateFloat(0.95f, 1f,
        infiniteRepeatable(tween(2000, easing = FastOutSlowInEasing), RepeatMode.Reverse), label = "pr")

    Column(modifier = Modifier.fillMaxSize().background(Bg).statusBarsPadding(),
        horizontalAlignment = Alignment.CenterHorizontally) {
        // Top bar
        Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) { Icon(Icons.Filled.ArrowBack, "Back", tint = Dark) }
            Spacer(Modifier.weight(1f))
            Text("TIMER", color = Dark, fontSize = 12.sp, fontWeight = FontWeight.Medium, letterSpacing = 3.sp)
            Spacer(Modifier.weight(1f))
            IconButton(onClick = {}) { Icon(Icons.Filled.MoreVert, null, tint = TextMuted) }
        }

        Spacer(Modifier.height(24.dp))

        // Clock face with shadow rings
        Box(modifier = Modifier.size(300.dp), contentAlignment = Alignment.Center) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val cx = size.width / 2; val cy = size.height / 2
                val radius = size.width / 2 - 24f

                // Outer decorative ring
                drawCircle(Coral.copy(alpha = 0.06f), radius + 12f, Offset(cx, cy))
                drawCircle(Coral.copy(alpha = 0.03f), radius + 20f, Offset(cx, cy))

                // Clock face
                drawCircle(Surface, radius, Offset(cx, cy))
                // Subtle shadow ring
                drawCircle(Color(0xFFE0D8CF), radius, Offset(cx, cy), style = Stroke(2f))

                // Inner progress arc
                drawArc(Coral.copy(alpha = 0.1f), 0f, 360f, false,
                    Offset(cx - radius + 20f, cy - radius + 20f),
                    Size((radius - 20f) * 2, (radius - 20f) * 2),
                    style = Stroke(6f, cap = StrokeCap.Round))
                drawArc(Coral, -90f, secondsAngle, false,
                    Offset(cx - radius + 20f, cy - radius + 20f),
                    Size((radius - 20f) * 2, (radius - 20f) * 2),
                    style = Stroke(6f, cap = StrokeCap.Round))

                // Hour markers
                for (i in 1..12) {
                    val angle = Math.toRadians((i * 30.0 - 90))
                    val isMajor = i % 3 == 0
                    val r1 = if (isMajor) radius - 22f else radius - 14f
                    val r2 = radius - 6f
                    drawLine(if (isMajor) Dark else Color(0xFFCCC4BA),
                        Offset((cx + r1 * cos(angle)).toFloat(), (cy + r1 * sin(angle)).toFloat()),
                        Offset((cx + r2 * cos(angle)).toFloat(), (cy + r2 * sin(angle)).toFloat()),
                        strokeWidth = if (isMajor) 3f else 1.5f, cap = StrokeCap.Round)
                }

                // Minute markers
                for (i in 0 until 60) {
                    if (i % 5 != 0) {
                        val angle = Math.toRadians((i * 6.0 - 90))
                        val r1 = radius - 8f
                        val r2 = radius - 4f
                        drawLine(Color(0xFFE0D8CF),
                            Offset((cx + r1 * cos(angle)).toFloat(), (cy + r1 * sin(angle)).toFloat()),
                            Offset((cx + r2 * cos(angle)).toFloat(), (cy + r2 * sin(angle)).toFloat()),
                            strokeWidth = 1f)
                    }
                }

                // Hour hand (short, thick)
                val hourAngle = Math.toRadians((minuteAngle / 12f - 90).toDouble())
                drawLine(Dark, Offset(cx, cy),
                    Offset((cx + (radius - 70f) * cos(hourAngle)).toFloat(),
                        (cy + (radius - 70f) * sin(hourAngle)).toFloat()),
                    strokeWidth = 6f, cap = StrokeCap.Round)

                // Minute hand
                val minRad = Math.toRadians((minuteAngle - 90).toDouble())
                drawLine(Dark, Offset(cx, cy),
                    Offset((cx + (radius - 40f) * cos(minRad)).toFloat(),
                        (cy + (radius - 40f) * sin(minRad)).toFloat()),
                    strokeWidth = 4f, cap = StrokeCap.Round)

                // Second hand
                val secRad = Math.toRadians((secondsAngle - 90).toDouble())
                // Counter-balance
                drawLine(Coral, Offset(cx, cy),
                    Offset((cx - 20f * cos(secRad)).toFloat(), (cy - 20f * sin(secRad)).toFloat()),
                    strokeWidth = 2f, cap = StrokeCap.Round)
                // Main
                drawLine(Coral, Offset(cx, cy),
                    Offset((cx + (radius - 28f) * cos(secRad)).toFloat(),
                        (cy + (radius - 28f) * sin(secRad)).toFloat()),
                    strokeWidth = 2f, cap = StrokeCap.Round)

                // Center dots
                drawCircle(Coral, 7f, Offset(cx, cy))
                drawCircle(Surface, 3f, Offset(cx, cy))
            }
        }

        Spacer(Modifier.height(32.dp))

        // Timer display card
        Box(modifier = Modifier.clip(RoundedCornerShape(28.dp))
            .background(Dark).padding(horizontal = 40.dp, vertical = 18.dp)) {
            Row(verticalAlignment = Alignment.Bottom) {
                Text("00", color = Surface, fontSize = 36.sp, fontWeight = FontWeight.Light)
                Text(" : ", color = Coral, fontSize = 36.sp, fontWeight = FontWeight.Light)
                Text("02", color = Surface, fontSize = 36.sp, fontWeight = FontWeight.Light)
                Text(" : ", color = Coral, fontSize = 36.sp, fontWeight = FontWeight.Light)
                Text("${(secondsAngle / 6).toInt().toString().padStart(2, '0')}",
                    color = Coral, fontSize = 36.sp, fontWeight = FontWeight.Medium)
            }
        }

        Spacer(Modifier.height(32.dp))

        // Controls
        Row(horizontalArrangement = Arrangement.spacedBy(20.dp),
            verticalAlignment = Alignment.CenterVertically) {
            // Stop
            Box(modifier = Modifier.size(52.dp).clip(CircleShape).background(Surface),
                contentAlignment = Alignment.Center) {
                Icon(Icons.Filled.Stop, "Stop", tint = Dark, modifier = Modifier.size(22.dp))
            }
            // Play (main)
            Box(modifier = Modifier.size(72.dp).clip(CircleShape)
                .background(Brush.linearGradient(listOf(Coral, CoralLight))),
                contentAlignment = Alignment.Center) {
                Icon(Icons.Filled.PlayArrow, "Play", tint = Color.White, modifier = Modifier.size(36.dp))
            }
            // Reset
            Box(modifier = Modifier.size(52.dp).clip(CircleShape).background(Surface),
                contentAlignment = Alignment.Center) {
                Icon(Icons.Filled.Refresh, "Reset", tint = Dark, modifier = Modifier.size(22.dp))
            }
        }

        Spacer(Modifier.height(24.dp))

        // Laps
        Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 32.dp),
            horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Laps", color = Dark, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
            Text("Best: 00:58", color = TextMuted, fontSize = 12.sp)
        }
        Spacer(Modifier.height(8.dp))
        Column(modifier = Modifier.padding(horizontal = 32.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)) {
            listOf("Lap 1" to "01:24", "Lap 2" to "00:58", "Lap 3" to "01:12").forEach { (lap, time) ->
                Row(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp))
                    .background(Surface).padding(horizontal = 16.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(lap, color = Dark, fontSize = 13.sp)
                    Text(time, color = if (time == "00:58") Coral else TextMuted,
                        fontSize = 13.sp, fontWeight = FontWeight.Medium)
                }
            }
        }
    }
}
