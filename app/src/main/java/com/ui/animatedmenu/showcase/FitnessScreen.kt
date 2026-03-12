package com.ui.animatedmenu.showcase

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
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
fun FitnessScreen(onBack: () -> Unit = {}) {
    val Bg = Color(0xFF000000)
    val Surface = Color(0xFF1C1C1E)
    val RedRing = Color(0xFFFF453A)
    val GreenRing = Color(0xFF30D158)
    val BlueRing = Color(0xFF0A84FF)
    val TextPrimary = Color(0xFFF5F5F5)
    val TextSecondary = Color(0xFF8E8E93)

    val inf = rememberInfiniteTransition(label = "fit")
    val glowPulse by inf.animateFloat(0.3f, 1f,
        infiniteRepeatable(tween(2000, easing = FastOutSlowInEasing), RepeatMode.Reverse), label = "gp")
    val ringBreath by inf.animateFloat(0f, 1f,
        infiniteRepeatable(tween(6000, easing = FastOutSlowInEasing), RepeatMode.Reverse), label = "rb")

    // Entrance fill animation — rings animate from 0 to target value
    val ringFill = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        ringFill.animateTo(1f, tween(1800, easing = FastOutSlowInEasing))
    }
    val ringProgress = ringFill.value

    Column(modifier = Modifier.fillMaxSize().background(Bg).statusBarsPadding()) {
        // Top bar
        Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) { Icon(Icons.Filled.ArrowBack, "Back", tint = TextPrimary) }
            Spacer(Modifier.weight(1f))
            Text("Activity", color = TextPrimary, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.weight(1f))
            Text("Today", color = BlueRing, fontSize = 14.sp, fontWeight = FontWeight.Medium)
            Spacer(Modifier.width(8.dp))
        }

        Spacer(Modifier.height(12.dp))

        // Activity rings with glow
        Box(modifier = Modifier.fillMaxWidth().height(260.dp), contentAlignment = Alignment.Center) {
            Canvas(modifier = Modifier.size(240.dp)) {
                val cx = size.width / 2; val cy = size.height / 2
                val strokeW = 22f; val gap = 10f

                // Move ring (outer - red)
                val moveR = size.width / 2 - strokeW / 2
                // Glow
                drawCircle(RedRing.copy(alpha = 0.06f * glowPulse), moveR + 8f, Offset(cx, cy))
                // Background
                drawCircle(RedRing.copy(alpha = 0.15f), moveR, Offset(cx, cy), style = Stroke(strokeW, cap = StrokeCap.Round))
                // Progress
                val moveSweep = 310f * ringProgress
                drawArc(RedRing, -90f, moveSweep, false,
                    Offset(cx - moveR, cy - moveR), Size(moveR * 2, moveR * 2),
                    style = Stroke(strokeW, cap = StrokeCap.Round))
                // End dot glow
                val moveEndAngle = Math.toRadians((-90.0 + moveSweep))
                val moveEndX = cx + (moveR * cos(moveEndAngle)).toFloat()
                val moveEndY = cy + (moveR * sin(moveEndAngle)).toFloat()
                drawCircle(RedRing.copy(alpha = 0.4f * glowPulse), 16f, Offset(moveEndX, moveEndY))

                // Exercise ring (middle - green)
                val exR = moveR - strokeW - gap
                drawCircle(GreenRing.copy(alpha = 0.05f * glowPulse), exR + 6f, Offset(cx, cy))
                drawCircle(GreenRing.copy(alpha = 0.15f), exR, Offset(cx, cy), style = Stroke(strokeW, cap = StrokeCap.Round))
                val exSweep = 240f * ringProgress
                drawArc(GreenRing, -90f, exSweep, false,
                    Offset(cx - exR, cy - exR), Size(exR * 2, exR * 2),
                    style = Stroke(strokeW, cap = StrokeCap.Round))
                val exEndAngle = Math.toRadians((-90.0 + exSweep))
                drawCircle(GreenRing.copy(alpha = 0.4f * glowPulse), 14f,
                    Offset(cx + (exR * cos(exEndAngle)).toFloat(), cy + (exR * sin(exEndAngle)).toFloat()))

                // Stand ring (inner - blue)
                val stR = exR - strokeW - gap
                drawCircle(BlueRing.copy(alpha = 0.04f * glowPulse), stR + 4f, Offset(cx, cy))
                drawCircle(BlueRing.copy(alpha = 0.15f), stR, Offset(cx, cy), style = Stroke(strokeW, cap = StrokeCap.Round))
                val stSweep = 350f * ringProgress
                drawArc(BlueRing, -90f, stSweep, false,
                    Offset(cx - stR, cy - stR), Size(stR * 2, stR * 2),
                    style = Stroke(strokeW, cap = StrokeCap.Round))
                val stEndAngle = Math.toRadians((-90.0 + stSweep))
                drawCircle(BlueRing.copy(alpha = 0.4f * glowPulse), 12f,
                    Offset(cx + (stR * cos(stEndAngle)).toFloat(), cy + (stR * sin(stEndAngle)).toFloat()))
            }

            // Center text
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("${(480 * ringProgress).toInt()}", color = TextPrimary,
                    fontSize = 36.sp, fontWeight = FontWeight.Bold)
                Text("KCAL", color = TextSecondary, fontSize = 11.sp,
                    letterSpacing = 2.sp, fontWeight = FontWeight.Medium)
            }
        }

        Spacer(Modifier.height(8.dp))

        // Ring legend
        Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 32.dp),
            horizontalArrangement = Arrangement.SpaceEvenly) {
            RingLegend2("Move", "${(480 * ringProgress).toInt()}/600", "kcal", RedRing)
            RingLegend2("Exercise", "${(32 * ringProgress).toInt()}/30", "min", GreenRing)
            RingLegend2("Stand", "${(10 * ringProgress).toInt()}/12", "hrs", BlueRing)
        }

        Spacer(Modifier.height(24.dp))

        // Activity cards
        Column(modifier = Modifier.padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)) {
            FitnessCard2("Steps", "${(8432 * ringProgress).toInt()}", "/ 10,000",
                Icons.Filled.DirectionsWalk, GreenRing, Surface,
                ringProgress * 0.84f)
            FitnessCard2("Distance", "%.1f km".format(5.2f * ringProgress), "",
                Icons.Filled.Timeline, BlueRing, Surface,
                ringProgress * 0.52f)
            FitnessCard2("Heart Rate", "${72 + (ringProgress * 10).toInt()} bpm", "Resting",
                Icons.Filled.Favorite, RedRing, Surface, null)
        }
    }
}

@Composable
private fun RingLegend2(label: String, value: String, unit: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(color))
        Spacer(Modifier.height(6.dp))
        Text(label, color = color, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
        Text(value, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Medium)
    }
}

@Composable
private fun FitnessCard2(title: String, value: String, suffix: String,
                         icon: androidx.compose.ui.graphics.vector.ImageVector,
                         color: Color, surface: Color, progress: Float?) {
    Box(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)).background(surface)) {
        // Progress bar background
        if (progress != null) {
            Box(modifier = Modifier.fillMaxWidth(progress).fillMaxHeight()
                .background(color.copy(alpha = 0.08f)))
        }
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(44.dp).clip(RoundedCornerShape(12.dp))
                .background(color.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center) {
                Icon(icon, null, tint = color, modifier = Modifier.size(22.dp))
            }
            Spacer(Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(title, color = Color(0xFF8E8E93), fontSize = 12.sp)
                Row(verticalAlignment = Alignment.Bottom) {
                    Text(value, color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                    if (suffix.isNotEmpty()) {
                        Text("  $suffix", color = Color(0xFF8E8E93), fontSize = 12.sp,
                            modifier = Modifier.padding(bottom = 3.dp))
                    }
                }
            }
            Icon(Icons.Filled.ChevronRight, null, tint = Color(0xFF3A3A3C), modifier = Modifier.size(20.dp))
        }
    }
}
