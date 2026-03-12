package com.ui.animatedmenu.showcase

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Settings
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
fun GaugeScreen(onBack: () -> Unit = {}) {
    val Bg = Color(0xFF06091A)
    val Surface = Color(0xFF0F1328)
    val Cyan = Color(0xFF00E5FF)
    val CyanDim = Color(0xFF0097A7)
    val Red = Color(0xFFFF1744)
    val Amber = Color(0xFFFFAB00)
    val TextPrimary = Color(0xFFF5F5F5)
    val TextSecondary = Color(0xFF78849E)

    val inf = rememberInfiniteTransition(label = "gauge")
    val glowPulse by inf.animateFloat(0.4f, 1f,
        infiniteRepeatable(tween(2500, easing = FastOutSlowInEasing), RepeatMode.Reverse), label = "gp")
    val needleOscillate by inf.animateFloat(-3f, 3f,
        infiniteRepeatable(tween(3000, easing = FastOutSlowInEasing), RepeatMode.Reverse), label = "no")

    // Needle entrance swing animation
    val needleEntry = remember { Animatable(0f) }
    LaunchedEffect(Unit) { needleEntry.animateTo(1f, spring(dampingRatio = 0.4f, stiffness = 80f)) }
    val gaugeValue = 68f * needleEntry.value + needleOscillate * needleEntry.value

    var isOn by remember { mutableStateOf(true) }

    Box(modifier = Modifier.fillMaxSize().background(Bg)) {
        // Background ambient glow
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawCircle(Cyan.copy(alpha = 0.04f * glowPulse), radius = 400f,
                center = Offset(size.width / 2, size.height * 0.35f))
        }

        Column(modifier = Modifier.fillMaxSize().statusBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally) {
            // Top bar
            Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack) { Icon(Icons.Filled.ArrowBack, "Back", tint = TextPrimary) }
                Spacer(Modifier.weight(1f))
                Text("NOISE METER", color = TextSecondary, fontSize = 12.sp,
                    fontWeight = FontWeight.Medium, letterSpacing = 3.sp)
                Spacer(Modifier.weight(1f))
                IconButton(onClick = {}) { Icon(Icons.Filled.Settings, null, tint = TextSecondary) }
            }

            Spacer(Modifier.height(20.dp))

            // Main gauge
            Box(modifier = Modifier.size(300.dp), contentAlignment = Alignment.Center) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val cx = size.width / 2; val cy = size.height / 2
                    val radius = size.width / 2 - 30f
                    val strokeW = 14f

                    // Outer glow ring
                    drawCircle(Cyan.copy(alpha = 0.03f * glowPulse), radius + 20f, Offset(cx, cy))

                    // Background arc (240° sweep from 150° start)
                    drawArc(Surface, 150f, 240f, false,
                        Offset(cx - radius, cy - radius), Size(radius * 2, radius * 2),
                        style = Stroke(strokeW, cap = StrokeCap.Round))

                    // Gradient progress arc
                    val sweepAngle = (gaugeValue / 100f) * 240f
                    val arcColor = when {
                        gaugeValue < 30 -> Cyan
                        gaugeValue < 60 -> Amber
                        else -> Red
                    }
                    drawArc(arcColor, 150f, sweepAngle, false,
                        Offset(cx - radius, cy - radius), Size(radius * 2, radius * 2),
                        style = Stroke(strokeW, cap = StrokeCap.Round))

                    // Glow at arc end
                    val endAngle = Math.toRadians((150.0 + sweepAngle))
                    val endX = cx + (radius * cos(endAngle)).toFloat()
                    val endY = cy + (radius * sin(endAngle)).toFloat()
                    drawCircle(arcColor.copy(alpha = 0.5f * glowPulse), 18f, Offset(endX, endY))

                    // Tick marks
                    val tickRadius = radius - 24f
                    for (i in 0..24) {
                        val angle = Math.toRadians((150.0 + i * 10))
                        val isMajor = i % 5 == 0
                        val r1 = if (isMajor) tickRadius - 14f else tickRadius - 8f
                        val r2 = tickRadius
                        val tickProgress = i.toFloat() / 24f
                        val tickColor = if (tickProgress <= gaugeValue / 100f) arcColor else Color(0xFF1E2440)
                        drawLine(tickColor,
                            Offset((cx + r1 * cos(angle)).toFloat(), (cy + r1 * sin(angle)).toFloat()),
                            Offset((cx + r2 * cos(angle)).toFloat(), (cy + r2 * sin(angle)).toFloat()),
                            strokeWidth = if (isMajor) 3f else 1.5f, cap = StrokeCap.Round)
                    }

                    // Needle
                    val needleAngle = Math.toRadians((150.0 + sweepAngle))
                    val needleLen = radius - 50f
                    drawLine(TextPrimary, Offset(cx, cy),
                        Offset((cx + needleLen * cos(needleAngle)).toFloat(),
                            (cy + needleLen * sin(needleAngle)).toFloat()),
                        strokeWidth = 2.5f, cap = StrokeCap.Round)
                    drawCircle(arcColor, 8f, Offset(cx, cy))
                    drawCircle(Bg, 4f, Offset(cx, cy))
                }

                // Center display
                Column(horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.offset(y = 20.dp)) {
                    Text("${gaugeValue.toInt()}", color = TextPrimary,
                        fontSize = 56.sp, fontWeight = FontWeight.Bold, letterSpacing = (-2).sp)
                    Text("dB", color = TextSecondary, fontSize = 14.sp,
                        fontWeight = FontWeight.Medium, letterSpacing = 2.sp)
                }
            }

            Spacer(Modifier.height(8.dp))

            // Status label
            val statusText = when { gaugeValue < 30 -> "Quiet" ; gaugeValue < 60 -> "Moderate" ; else -> "Loud" }
            val statusColor = when { gaugeValue < 30 -> Cyan ; gaugeValue < 60 -> Amber ; else -> Red }
            Box(modifier = Modifier.clip(RoundedCornerShape(20.dp))
                .background(statusColor.copy(alpha = 0.12f))
                .padding(horizontal = 20.dp, vertical = 6.dp)) {
                Text(statusText, color = statusColor, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
            }

            Spacer(Modifier.height(28.dp))

            // Level bars
            Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 32.dp),
                horizontalArrangement = Arrangement.SpaceEvenly) {
                GaugeBar2("Output", "${gaugeValue.toInt()}", "dB", gaugeValue / 100f, Cyan, Surface)
                GaugeBar2("Peak", "${(gaugeValue * 1.2f).toInt()}", "dB", (gaugeValue * 1.2f / 100f).coerceAtMost(1f), Amber, Surface)
                GaugeBar2("Avg", "${(gaugeValue * 0.7f).toInt()}", "dB", gaugeValue * 0.7f / 100f, CyanDim, Surface)
            }

            Spacer(Modifier.height(28.dp))

            // Toggle
            Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 32.dp)
                .clip(RoundedCornerShape(16.dp)).background(Surface).padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween) {
                Column {
                    Text("Noise Monitor", color = TextPrimary, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
                    Text(if (isOn) "Active • Monitoring" else "Paused", color = TextSecondary, fontSize = 12.sp)
                }
                Box(modifier = Modifier.width(52.dp).height(28.dp).clip(RoundedCornerShape(14.dp))
                    .background(if (isOn) Cyan else Color(0xFF2A3050))
                    .clickable { isOn = !isOn },
                    contentAlignment = if (isOn) Alignment.CenterEnd else Alignment.CenterStart) {
                    Box(modifier = Modifier.padding(3.dp).size(22.dp).clip(CircleShape)
                        .background(if (isOn) Bg else Color(0xFF5A6080)))
                }
            }
        }
    }
}

@Composable
private fun GaugeBar2(label: String, value: String, unit: String, fill: Float, color: Color, surface: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        Text(unit, color = Color(0xFF78849E), fontSize = 10.sp)
        Spacer(Modifier.height(8.dp))
        Box(modifier = Modifier.width(28.dp).height(100.dp)
            .clip(RoundedCornerShape(14.dp)).background(surface)) {
            Box(modifier = Modifier.fillMaxWidth().fillMaxHeight(fill.coerceIn(0f, 1f))
                .align(Alignment.BottomCenter)
                .clip(RoundedCornerShape(14.dp))
                .background(Brush.verticalGradient(listOf(color, color.copy(alpha = 0.4f)))))
        }
        Spacer(Modifier.height(6.dp))
        Text(label, color = Color(0xFF78849E), fontSize = 11.sp, fontWeight = FontWeight.Medium)
    }
}
