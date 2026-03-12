package com.ui.animatedmenu

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun AnimatedSplashScreen(onFinished: () -> Unit) {
    val Purple1 = Color(0xFF7C4DFF)
    val Purple2 = Color(0xFF651FFF)
    val Pink = Color(0xFFE040FB)
    val Cyan = Color(0xFF00E5FF)
    val Deep = Color(0xFF0D0015)

    // Phase 1: Background gradient morphs (0-500ms)
    val bgEntry = remember { Animatable(0f) }
    // Phase 2: Concentric ripple circles expand (300-1200ms)
    val ripple1 = remember { Animatable(0f) }
    val ripple2 = remember { Animatable(0f) }
    val ripple3 = remember { Animatable(0f) }
    // Phase 3: Logo scales in with spring (600-1000ms)
    val logoScale = remember { Animatable(0f) }
    val logoRotation = remember { Animatable(-30f) }
    // Phase 4: Text fades in (900-1300ms)
    val textAlpha = remember { Animatable(0f) }
    val textY = remember { Animatable(30f) }
    // Phase 5: Subtitle (1100-1500ms)
    val subAlpha = remember { Animatable(0f) }
    // Phase 6: Particles burst (1200-2000ms)
    val particleBurst = remember { Animatable(0f) }
    // Phase 7: Exit (2500ms)
    val exitAlpha = remember { Animatable(1f) }
    val exitScale = remember { Animatable(1f) }

    // Continuous shimmer
    val inf = rememberInfiniteTransition(label = "sp")
    val shimmer by inf.animateFloat(0f, 6.28f,
        infiniteRepeatable(tween(3000, easing = LinearEasing)), label = "sh")

    LaunchedEffect(Unit) {
        // Phase 1: Background
        bgEntry.animateTo(1f, tween(800, easing = FastOutSlowInEasing))
    }
    LaunchedEffect(Unit) {
        delay(300)
        ripple1.animateTo(1f, tween(900, easing = FastOutSlowInEasing))
    }
    LaunchedEffect(Unit) {
        delay(500)
        ripple2.animateTo(1f, tween(900, easing = FastOutSlowInEasing))
    }
    LaunchedEffect(Unit) {
        delay(700)
        ripple3.animateTo(1f, tween(900, easing = FastOutSlowInEasing))
    }
    LaunchedEffect(Unit) {
        delay(600)
        logoScale.animateTo(1f, spring(dampingRatio = 0.5f, stiffness = 200f))
    }
    LaunchedEffect(Unit) {
        delay(600)
        logoRotation.animateTo(0f, spring(dampingRatio = 0.6f, stiffness = 150f))
    }
    LaunchedEffect(Unit) {
        delay(900)
        textAlpha.animateTo(1f, tween(400, easing = FastOutSlowInEasing))
    }
    LaunchedEffect(Unit) {
        delay(900)
        textY.animateTo(0f, spring(dampingRatio = 0.7f, stiffness = 300f))
    }
    LaunchedEffect(Unit) {
        delay(1100)
        subAlpha.animateTo(1f, tween(400))
    }
    LaunchedEffect(Unit) {
        delay(1200)
        particleBurst.animateTo(1f, tween(800, easing = FastOutSlowInEasing))
    }
    // Exit after animation
    LaunchedEffect(Unit) {
        delay(2800)
        exitAlpha.animateTo(0f, tween(400))
        onFinished()
    }
    LaunchedEffect(Unit) {
        delay(2800)
        exitScale.animateTo(1.15f, tween(400, easing = FastOutSlowInEasing))
    }

    Box(modifier = Modifier.fillMaxSize()
        .graphicsLayer { alpha = exitAlpha.value; scaleX = exitScale.value; scaleY = exitScale.value }
        .background(Brush.radialGradient(
            listOf(Purple2.copy(alpha = bgEntry.value), Deep),
            center = Offset(540f, 1100f), radius = 1200f * bgEntry.value.coerceAtLeast(0.001f)
        ))) {

        // Animated background orbs
        Canvas(modifier = Modifier.fillMaxSize()) {
            val cx = size.width / 2; val cy = size.height / 2

            // Morphing gradient orbs
            val orbAlpha = bgEntry.value * 0.08f
            drawCircle(Pink.copy(alpha = orbAlpha),
                radius = 200f + sin(shimmer).toFloat() * 30f,
                center = Offset(cx * 0.3f, cy * 0.4f))
            drawCircle(Cyan.copy(alpha = orbAlpha * 0.7f),
                radius = 160f + cos(shimmer).toFloat() * 20f,
                center = Offset(cx * 1.6f, cy * 0.6f))
            drawCircle(Purple1.copy(alpha = orbAlpha * 0.5f),
                radius = 120f + sin(shimmer * 1.3f).toFloat() * 15f,
                center = Offset(cx * 0.8f, cy * 1.5f))

            // Expanding ripple circles
            if (ripple1.value > 0f) {
                drawCircle(Purple1.copy(alpha = 0.15f * (1f - ripple1.value)),
                    radius = ripple1.value * 350f, center = Offset(cx, cy))
            }
            if (ripple2.value > 0f) {
                drawCircle(Pink.copy(alpha = 0.12f * (1f - ripple2.value)),
                    radius = ripple2.value * 500f, center = Offset(cx, cy))
            }
            if (ripple3.value > 0f) {
                drawCircle(Cyan.copy(alpha = 0.08f * (1f - ripple3.value)),
                    radius = ripple3.value * 700f, center = Offset(cx, cy))
            }

            // Particle burst - 16 particles radiate outward
            if (particleBurst.value > 0f) {
                for (i in 0 until 16) {
                    val angle = Math.toRadians(i * 22.5 + shimmer.toDouble() * 5)
                    val dist = particleBurst.value * (120f + i * 15f)
                    val px = cx + (dist * cos(angle)).toFloat()
                    val py = cy + (dist * sin(angle)).toFloat()
                    val pAlpha = (1f - particleBurst.value) * 0.6f
                    val pColor = if (i % 3 == 0) Cyan else if (i % 3 == 1) Pink else Purple1
                    drawCircle(pColor.copy(alpha = pAlpha),
                        radius = 3f + (1f - particleBurst.value) * 5f,
                        center = Offset(px, py))
                }
            }
        }

        // Center content
        Column(modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center) {

            // Logo with spring scale + rotation
            Box(modifier = Modifier.graphicsLayer {
                    scaleX = logoScale.value
                    scaleY = logoScale.value
                    rotationZ = logoRotation.value
                    alpha = logoScale.value
                }) {
                // Outer glow
                Box(modifier = Modifier.size(120.dp).clip(CircleShape)
                    .background(Brush.radialGradient(listOf(
                        Purple1.copy(alpha = 0.3f), Color.Transparent))),
                    contentAlignment = Alignment.Center) {
                    // Inner circle
                    Box(modifier = Modifier.size(88.dp).clip(CircleShape)
                        .background(Brush.linearGradient(listOf(Purple1, Pink))),
                        contentAlignment = Alignment.Center) {
                        Icon(Icons.Filled.AutoAwesome, null,
                            tint = Color.White, modifier = Modifier.size(40.dp))
                    }
                }
            }

            Spacer(Modifier.height(32.dp))

            // Title text
            Text("UI Showcase",
                modifier = Modifier.graphicsLayer {
                    alpha = textAlpha.value
                    translationY = textY.value
                },
                color = Color.White, fontSize = 28.sp,
                fontWeight = FontWeight.Bold, letterSpacing = (-0.5).sp)

            Spacer(Modifier.height(8.dp))

            // Subtitle
            Text("Crafted with Compose",
                modifier = Modifier.graphicsLayer { alpha = subAlpha.value },
                color = Color.White.copy(alpha = 0.5f), fontSize = 14.sp,
                fontWeight = FontWeight.Light, letterSpacing = 2.sp)
        }

        // Bottom tagline
        Text("by AI Designer",
            modifier = Modifier.align(Alignment.BottomCenter)
                .padding(bottom = 48.dp)
                .graphicsLayer { alpha = subAlpha.value * 0.6f },
            color = Color.White.copy(alpha = 0.3f), fontSize = 12.sp,
            letterSpacing = 1.sp)
    }
}
