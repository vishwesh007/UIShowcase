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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun WeatherScreen(onBack: () -> Unit = {}) {
    val DeepBlue = Color(0xFF0B1437)
    val MidBlue = Color(0xFF1A237E)
    val SkyBlue = Color(0xFF42A5F5)
    val GoldenSun = Color(0xFFFFD54F)
    val WarmOrange = Color(0xFFFF9800)
    val TextPrimary = Color(0xFFF5F5F5)
    val TextSecondary = Color(0xFFB0BEC5)
    val GlassWhite = Color.White.copy(alpha = 0.1f)
    val GlassBorder = Color.White.copy(alpha = 0.15f)

    val inf = rememberInfiniteTransition(label = "wx")
    val sunRot by inf.animateFloat(0f, 360f,
        infiniteRepeatable(tween(20000, easing = LinearEasing)), label = "sr")
    val sunGlow by inf.animateFloat(0.5f, 1f,
        infiniteRepeatable(tween(3000, easing = FastOutSlowInEasing), RepeatMode.Reverse), label = "sg")
    val cloudDrift by inf.animateFloat(-20f, 20f,
        infiniteRepeatable(tween(8000, easing = FastOutSlowInEasing), RepeatMode.Reverse), label = "cd")
    val wavePhase by inf.animateFloat(0f, 6.28f,
        infiniteRepeatable(tween(5000, easing = LinearEasing)), label = "wp")

    // Temperature count-up entrance
    val tempEntry = remember { Animatable(0f) }
    LaunchedEffect(Unit) { tempEntry.animateTo(1f, tween(1200, easing = FastOutSlowInEasing)) }
    val displayTemp = (24 * tempEntry.value).toInt()

    Box(modifier = Modifier.fillMaxSize()
        .background(Brush.verticalGradient(listOf(DeepBlue, MidBlue, SkyBlue, Color(0xFF87CEEB))))) {

        // Stars / particles in upper sky
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width; val h = size.height
            val starPositions = listOf(
                Offset(w * 0.1f, h * 0.05f), Offset(w * 0.3f, h * 0.08f),
                Offset(w * 0.7f, h * 0.03f), Offset(w * 0.85f, h * 0.07f),
                Offset(w * 0.15f, h * 0.12f), Offset(w * 0.55f, h * 0.06f),
                Offset(w * 0.9f, h * 0.14f), Offset(w * 0.4f, h * 0.15f),
            )
            starPositions.forEachIndexed { i, pos ->
                val alpha = (0.3f + sunGlow * 0.5f * if (i % 2 == 0) 1f else 0.6f)
                drawCircle(TextPrimary.copy(alpha = alpha), radius = if (i % 3 == 0) 2f else 1.2f, center = pos)
            }
        }

        Column(modifier = Modifier.fillMaxSize().statusBarsPadding()) {
            // Top bar
            Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack) { Icon(Icons.Filled.ArrowBack, "Back", tint = TextPrimary) }
                Spacer(Modifier.weight(1f))
                Icon(Icons.Filled.LocationOn, null, tint = GoldenSun, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(4.dp))
                Text("San Francisco, CA", color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                Spacer(Modifier.weight(1f))
                IconButton(onClick = {}) { Icon(Icons.Filled.Search, null, tint = TextSecondary) }
            }

            Spacer(Modifier.height(8.dp))

            // Sun with rays and glow
            Box(modifier = Modifier.fillMaxWidth().height(160.dp), contentAlignment = Alignment.Center) {
                Canvas(modifier = Modifier.size(160.dp)) {
                    val cx = size.width / 2; val cy = size.height / 2
                    // Outer glow
                    drawCircle(GoldenSun.copy(alpha = 0.08f * sunGlow), radius = 80f, center = Offset(cx, cy))
                    drawCircle(WarmOrange.copy(alpha = 0.06f * sunGlow), radius = 65f, center = Offset(cx, cy))
                    // Sun rays
                    for (i in 0 until 12) {
                        val angle = Math.toRadians(sunRot.toDouble() + i * 30.0)
                        val r1 = 38f; val r2 = 52f + sunGlow * 8f
                        drawLine(GoldenSun.copy(alpha = 0.5f),
                            Offset(cx + (r1 * cos(angle)).toFloat(), cy + (r1 * sin(angle)).toFloat()),
                            Offset(cx + (r2 * cos(angle)).toFloat(), cy + (r2 * sin(angle)).toFloat()),
                            strokeWidth = 2f, cap = StrokeCap.Round)
                    }
                    // Sun circle
                    drawCircle(Brush.radialGradient(listOf(GoldenSun, WarmOrange)), radius = 35f, center = Offset(cx, cy))
                }
            }

            // Temperature with count-up animation
            Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                Text("${displayTemp}°", color = TextPrimary, fontSize = 88.sp, fontWeight = FontWeight.Thin, letterSpacing = (-4).sp)
                Spacer(Modifier.height(2.dp))
                Text("Partly Cloudy", color = TextSecondary, fontSize = 16.sp, fontWeight = FontWeight.Light)
                Spacer(Modifier.height(4.dp))
                Text("H:28°  L:18°", color = TextSecondary.copy(alpha = 0.7f), fontSize = 13.sp)
            }

            Spacer(Modifier.height(20.dp))

            // Animated wave separator
            Canvas(modifier = Modifier.fillMaxWidth().height(24.dp)) {
                val w = size.width
                val path1 = Path().apply {
                    moveTo(0f, 12f)
                    for (x in 0..w.toInt() step 4) {
                        lineTo(x.toFloat(), 12f + sin(wavePhase + x / w * 8f * Math.PI.toFloat()) * 6f)
                    }
                }
                drawPath(path1, GlassBorder, style = Stroke(1.5f))
            }

            Spacer(Modifier.height(12.dp))

            // Stats - glass cards
            Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                GlassStatCard("Wind", "12", "km/h", Icons.Filled.Air, SkyBlue, Modifier.weight(1f))
                GlassStatCard("Humidity", "64", "%", Icons.Filled.WaterDrop, Color(0xFF26C6DA), Modifier.weight(1f))
                GlassStatCard("UV Index", "6", "High", Icons.Filled.WbSunny, WarmOrange, Modifier.weight(1f))
            }

            Spacer(Modifier.height(16.dp))

            // Hourly forecast - glass card
            Box(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(GlassWhite)
                .padding(16.dp)) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Hourly Forecast", color = TextPrimary, fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
                        Spacer(Modifier.weight(1f))
                        Text("See All", color = SkyBlue, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                    }
                    Spacer(Modifier.height(16.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                        HourItem2("Now", "24°", true, GoldenSun)
                        HourItem2("1PM", "26°", false, GoldenSun)
                        HourItem2("3PM", "25°", false, GoldenSun)
                        HourItem2("5PM", "22°", false, WarmOrange)
                        HourItem2("7PM", "19°", false, Color(0xFF5C6BC0))
                    }
                }
            }

            Spacer(Modifier.height(12.dp))

            // Sunrise/Sunset
            Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Box(modifier = Modifier.weight(1f).clip(RoundedCornerShape(16.dp))
                    .background(GlassWhite).padding(14.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.WbTwilight, null, tint = WarmOrange, modifier = Modifier.size(22.dp))
                        Spacer(Modifier.width(10.dp))
                        Column {
                            Text("Sunrise", color = TextSecondary, fontSize = 11.sp)
                            Text("6:42 AM", color = TextPrimary, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
                Box(modifier = Modifier.weight(1f).clip(RoundedCornerShape(16.dp))
                    .background(GlassWhite).padding(14.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.NightsStay, null, tint = Color(0xFF5C6BC0), modifier = Modifier.size(22.dp))
                        Spacer(Modifier.width(10.dp))
                        Column {
                            Text("Sunset", color = TextSecondary, fontSize = 11.sp)
                            Text("7:58 PM", color = TextPrimary, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun GlassStatCard(label: String, value: String, unit: String,
                          icon: androidx.compose.ui.graphics.vector.ImageVector, color: Color, modifier: Modifier) {
    Box(modifier = modifier.clip(RoundedCornerShape(18.dp))
        .background(Color.White.copy(alpha = 0.1f)).padding(14.dp),
        contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(icon, null, tint = color, modifier = Modifier.size(22.dp))
            Spacer(Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.Bottom) {
                Text(value, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 22.sp)
                Text(unit, color = Color.White.copy(alpha = 0.6f), fontSize = 11.sp,
                    modifier = Modifier.padding(start = 2.dp, bottom = 3.dp))
            }
            Text(label, color = Color.White.copy(alpha = 0.6f), fontSize = 11.sp)
        }
    }
}

@Composable
private fun HourItem2(time: String, temp: String, isNow: Boolean, iconColor: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(time, color = if (isNow) Color.White else Color.White.copy(alpha = 0.5f),
            fontSize = 12.sp, fontWeight = if (isNow) FontWeight.SemiBold else FontWeight.Normal)
        Spacer(Modifier.height(8.dp))
        Box(modifier = Modifier.size(32.dp).clip(CircleShape)
            .background(if (isNow) iconColor.copy(alpha = 0.25f) else Color.Transparent),
            contentAlignment = Alignment.Center) {
            Icon(Icons.Filled.WbSunny, null, tint = iconColor, modifier = Modifier.size(18.dp))
        }
        Spacer(Modifier.height(8.dp))
        Text(temp, color = Color.White, fontWeight = if (isNow) FontWeight.Bold else FontWeight.Normal,
            fontSize = if (isNow) 16.sp else 14.sp)
    }
}
