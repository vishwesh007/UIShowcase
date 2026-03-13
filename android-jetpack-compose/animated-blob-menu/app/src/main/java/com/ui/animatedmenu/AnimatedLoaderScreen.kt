package com.ui.animatedmenu

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.cos
import kotlin.math.sin

// Loader colors from video thumbnail
private val LoaderPurple = Color(0xFFD125EB)
private val LoaderCyan = Color(0xFF36E4ED)
private val LoaderLime = Color(0xFFA4F443)
private val LoaderGold = Color(0xFFF3C742)
private val LoaderBg = Color(0xFF0A0A0A)
private val LoaderCardBg = Color(0xFF1A1A1A)

@Composable
fun AnimatedLoaderScreen(onBack: () -> Unit = {}) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(LoaderBg)
            .statusBarsPadding()
    ) {
        // Header
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.Filled.ArrowBack, "Back", tint = Color.White)
            }
            Spacer(Modifier.width(8.dp))
            Column {
                Text(
                    "Animated Loaders",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    "6 Loading Animations",
                    color = LoaderCyan.copy(alpha = 0.7f),
                    fontSize = 12.sp
                )
            }
        }

        // Grid of loader cards
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item { LoaderCard("Bouncing Dots") { BouncingDotsLoader() } }
            item { LoaderCard("Spinning Ring") { SpinningRingLoader() } }
            item { LoaderCard("Pulsing Glow") { PulsingCirclesLoader() } }
            item { LoaderCard("Color Wave") { ColorWaveLoader() } }
            item { LoaderCard("Orbit") { OrbitDotsLoader() } }
            item { LoaderCard("Typing") { TypingDotsLoader() } }
        }
    }
}

@Composable
private fun LoaderCard(title: String, content: @Composable () -> Unit) {
    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(LoaderCardBg)
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp),
            contentAlignment = Alignment.Center
        ) { content() }
        Spacer(Modifier.height(8.dp))
        Text(title, color = Color(0xFFAAAAAA), fontSize = 12.sp, fontWeight = FontWeight.Medium)
    }
}

// 1. Bouncing Dots — 4 colored dots bounce up/down with staggered delay
@Composable
private fun BouncingDotsLoader() {
    val colors = listOf(LoaderPurple, LoaderCyan, LoaderLime, LoaderGold)
    val inf = rememberInfiniteTransition(label = "bounce")
    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        colors.forEachIndexed { index, color ->
            val offset by inf.animateFloat(
                0f, 1f,
                infiniteRepeatable(
                    tween(600, delayMillis = index * 120, easing = FastOutSlowInEasing),
                    RepeatMode.Reverse
                ),
                label = "dot_$index"
            )
            Box(
                modifier = Modifier
                    .size(14.dp)
                    .offset(y = (-offset * 24f).dp)
                    .clip(CircleShape)
                    .background(color)
            )
        }
    }
}

// 2. Spinning Ring — gradient arc that rotates continuously
@Composable
private fun SpinningRingLoader() {
    val inf = rememberInfiniteTransition(label = "spin")
    val rotation by inf.animateFloat(
        0f, 360f,
        infiniteRepeatable(tween(1200, easing = LinearEasing)),
        label = "spinAngle"
    )
    Canvas(modifier = Modifier.size(60.dp)) {
        drawArc(
            Brush.sweepGradient(listOf(LoaderCyan, LoaderPurple, Color.Transparent)),
            startAngle = rotation,
            sweepAngle = 270f,
            useCenter = false,
            style = Stroke(width = 6.dp.toPx(), cap = StrokeCap.Round)
        )
    }
}

// 3. Pulsing Circles — concentric circles pulse outward and fade
@Composable
private fun PulsingCirclesLoader() {
    val inf = rememberInfiniteTransition(label = "pulse")
    val colors = listOf(LoaderPurple, LoaderCyan, LoaderLime)
    Box(contentAlignment = Alignment.Center) {
        colors.forEachIndexed { index, color ->
            val scale by inf.animateFloat(
                0.3f, 1.2f,
                infiniteRepeatable(
                    tween(1500, delayMillis = index * 300, easing = FastOutSlowInEasing),
                    RepeatMode.Reverse
                ),
                label = "pulse_$index"
            )
            val alpha = ((1.2f - scale) / 0.9f).coerceIn(0f, 0.6f)
            Box(
                modifier = Modifier
                    .size(70.dp)
                    .scale(scale)
                    .clip(CircleShape)
                    .background(color.copy(alpha = alpha))
            )
        }
    }
}

// 4. Color Wave — vertical bars that animate height like an equalizer
@Composable
private fun ColorWaveLoader() {
    val inf = rememberInfiniteTransition(label = "wave")
    val phase by inf.animateFloat(
        0f, 6.28f,
        infiniteRepeatable(tween(2000, easing = LinearEasing)),
        label = "wavePhase"
    )
    val colors = listOf(LoaderPurple, LoaderCyan, LoaderLime, LoaderGold, LoaderPurple, LoaderCyan, LoaderLime)
    Row(
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        colors.forEachIndexed { index, color ->
            val h = (16f + 36f * sin(phase + index * 0.9f)).coerceAtLeast(8f)
            Box(
                modifier = Modifier
                    .width(8.dp)
                    .height(h.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(color)
            )
        }
    }
}

// 5. Orbit Dots — 4 colored dots orbit a center point
@Composable
private fun OrbitDotsLoader() {
    val inf = rememberInfiniteTransition(label = "orbit")
    val angle by inf.animateFloat(
        0f, 360f,
        infiniteRepeatable(tween(2000, easing = LinearEasing)),
        label = "orbitAngle"
    )
    Canvas(modifier = Modifier.size(70.dp)) {
        val colors = listOf(LoaderPurple, LoaderCyan, LoaderLime, LoaderGold)
        val orbitRadius = 24.dp.toPx()
        val dotRadius = 5.dp.toPx()
        colors.forEachIndexed { index, color ->
            val a = ((angle + index * 90f) * Math.PI / 180.0).toFloat()
            val x = center.x + cos(a) * orbitRadius
            val y = center.y + sin(a) * orbitRadius
            drawCircle(color, dotRadius, Offset(x, y))
        }
        drawCircle(Color.White.copy(alpha = 0.2f), 3.dp.toPx(), center)
    }
}

// 6. Typing Dots — 3 dots scale up/down sequentially (chat indicator style)
@Composable
private fun TypingDotsLoader() {
    val inf = rememberInfiniteTransition(label = "typing")
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        repeat(3) { index ->
            val scale by inf.animateFloat(
                0.5f, 1.3f,
                infiniteRepeatable(
                    tween(500, delayMillis = index * 180, easing = FastOutSlowInEasing),
                    RepeatMode.Reverse
                ),
                label = "type_$index"
            )
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .scale(scale)
                    .clip(CircleShape)
                    .background(LoaderCyan)
            )
        }
    }
}
