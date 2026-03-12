package com.ui.animatedmenu.showcase

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

private val SuccessGreen = Color(0xFF4CAF50)
private val ScreenBg = Color(0xFFFAFAFA)
private val TextDark = Color(0xFF212121)
private val TextMuted = Color(0xFF757575)

data class CelebrationParticle(
    val x: Float,
    val y: Float,
    val vx: Float,
    val vy: Float,
    val rotation: Float,
    val rotationSpeed: Float,
    val color: Color,
    val w: Float,
    val h: Float,
    val shape: Int,
    val alpha: Float = 1f
)

@Composable
fun ConfettiScreen(onBack: () -> Unit) {
    // Phase timeline:
    // 1 (0ms)     Ring stroke draws in
    // 2 (500ms)   Circle fills green
    // 3 (750ms)   Checkmark stroke draws
    // 4 (400ms)   Badge scale + spring bounce
    // 5 (1100ms)  Confetti burst
    // 6 (1300ms)  Title fades in
    // 7 (1600ms)  Details card slides up
    // 8 (2000ms)  Button appears

    val ringDraw = remember { Animatable(0f) }
    val circleFill = remember { Animatable(0f) }
    val checkDraw = remember { Animatable(0f) }
    val badgeScale = remember { Animatable(0f) }
    val titleAlpha = remember { Animatable(0f) }
    val detailSlide = remember { Animatable(0f) }
    val buttonAlpha = remember { Animatable(0f) }

    val inf = rememberInfiniteTransition(label = "float")
    val floatY by inf.animateFloat(-4f, 4f,
        infiniteRepeatable(tween(2500, easing = FastOutSlowInEasing), RepeatMode.Reverse), label = "fy")
    val glowPulse by inf.animateFloat(0.8f, 1f,
        infiniteRepeatable(tween(1500, easing = FastOutSlowInEasing), RepeatMode.Reverse), label = "gp")

    var particles by remember { mutableStateOf<List<CelebrationParticle>>(emptyList()) }
    var confettiTriggered by remember { mutableStateOf(false) }

    val confettiColors = listOf(
        Color(0xFFE53935), Color(0xFF43A047), Color(0xFF1E88E5),
        Color(0xFFFDD835), Color(0xFF8E24AA), Color(0xFFF4511E),
        Color(0xFFFF7043), Color(0xFF26C6DA), Color(0xFFAB47BC),
        Color(0xFFFFCA28), Color(0xFF66BB6A), Color(0xFFEF5350)
    )

    // Sequenced launch effects
    LaunchedEffect(Unit) {
        ringDraw.animateTo(1f, tween(600, easing = FastOutSlowInEasing))
    }
    LaunchedEffect(Unit) {
        delay(500)
        circleFill.animateTo(1f, tween(350, easing = FastOutSlowInEasing))
    }
    LaunchedEffect(Unit) {
        delay(750)
        checkDraw.animateTo(1f, tween(400, easing = FastOutSlowInEasing))
    }
    LaunchedEffect(Unit) {
        delay(400)
        badgeScale.animateTo(1f, spring(dampingRatio = 0.45f, stiffness = 250f))
    }
    LaunchedEffect(Unit) {
        delay(1100)
        confettiTriggered = true
    }
    LaunchedEffect(Unit) {
        delay(1300)
        titleAlpha.animateTo(1f, tween(400))
    }
    LaunchedEffect(Unit) {
        delay(1600)
        detailSlide.animateTo(1f, spring(dampingRatio = 0.7f, stiffness = 300f))
    }
    LaunchedEffect(Unit) {
        delay(2000)
        buttonAlpha.animateTo(1f, tween(400))
    }

    // Generate and animate confetti particles
    LaunchedEffect(confettiTriggered) {
        if (!confettiTriggered) return@LaunchedEffect
        val burst = mutableListOf<CelebrationParticle>()
        // Center burst (120 pieces)
        repeat(120) {
            val angle = Random.nextDouble(0.0, 2 * PI).toFloat()
            val speed = Random.nextFloat() * 28f + 8f
            burst.add(CelebrationParticle(
                x = 540f, y = 800f,
                vx = cos(angle) * speed,
                vy = sin(angle) * speed - Random.nextFloat() * 15f,
                rotation = Random.nextFloat() * 360f,
                rotationSpeed = Random.nextFloat() * 16f - 8f,
                color = confettiColors.random(),
                w = Random.nextFloat() * 14f + 6f,
                h = Random.nextFloat() * 8f + 4f,
                shape = Random.nextInt(3)
            ))
        }
        // Side cannons (40 pieces)
        repeat(40) {
            val fromLeft = it % 2 == 0
            val startX = if (fromLeft) 0f else 1080f
            val angle = if (fromLeft) Random.nextDouble(-PI * 0.4, PI * 0.1).toFloat()
                       else Random.nextDouble(PI * 0.9, PI * 1.4).toFloat()
            val speed = Random.nextFloat() * 20f + 12f
            burst.add(CelebrationParticle(
                x = startX, y = Random.nextFloat() * 400f + 600f,
                vx = cos(angle) * speed,
                vy = sin(angle) * speed - Random.nextFloat() * 10f,
                rotation = Random.nextFloat() * 360f,
                rotationSpeed = Random.nextFloat() * 12f - 6f,
                color = confettiColors.random(),
                w = Random.nextFloat() * 16f + 5f,
                h = Random.nextFloat() * 6f + 3f,
                shape = Random.nextInt(3)
            ))
        }
        particles = burst
        // Physics loop
        while (particles.isNotEmpty()) {
            delay(16)
            particles = particles.mapNotNull { p ->
                val newVy = p.vy + 0.5f
                val newX = p.x + p.vx * 0.99f
                val newY = p.y + newVy
                val newAlpha = p.alpha - 0.002f
                if (newY > 2400f || newAlpha <= 0f) null
                else p.copy(
                    x = newX, y = newY,
                    vy = newVy, vx = p.vx * 0.995f,
                    rotation = p.rotation + p.rotationSpeed,
                    alpha = newAlpha
                )
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize().background(ScreenBg)) {
        // Confetti canvas layer
        Canvas(modifier = Modifier.fillMaxSize()) {
            particles.forEach { p ->
                rotate(degrees = p.rotation, pivot = Offset(p.x, p.y)) {
                    when (p.shape) {
                        0 -> drawRect(
                            color = p.color.copy(alpha = p.alpha),
                            topLeft = Offset(p.x - p.w / 2, p.y - p.h / 2),
                            size = Size(p.w, p.h)
                        )
                        1 -> drawCircle(
                            color = p.color.copy(alpha = p.alpha),
                            radius = p.w / 2,
                            center = Offset(p.x, p.y)
                        )
                        2 -> {
                            val path = Path().apply {
                                moveTo(p.x, p.y)
                                cubicTo(
                                    p.x + p.w, p.y - p.h,
                                    p.x + p.w * 2, p.y + p.h,
                                    p.x + p.w * 2.5f, p.y
                                )
                            }
                            drawPath(path, p.color.copy(alpha = p.alpha),
                                style = Stroke(width = 3f, cap = StrokeCap.Round))
                        }
                    }
                }
            }
        }

        // Main content
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Top bar
            Row(
                modifier = Modifier.fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .statusBarsPadding(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(Icons.Filled.ArrowBack, "Back", tint = TextDark)
                }
                Spacer(Modifier.weight(1f))
                Text("Payment", fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = TextDark)
                Spacer(Modifier.weight(1f))
                Spacer(Modifier.width(48.dp))
            }

            Spacer(Modifier.weight(0.8f))

            // Green checkmark badge
            Box(
                modifier = Modifier.graphicsLayer {
                    scaleX = badgeScale.value; scaleY = badgeScale.value
                    translationY = floatY
                },
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.size(160.dp)) {
                    val cx = size.width / 2; val cy = size.height / 2
                    // Outer glow
                    drawCircle(SuccessGreen.copy(alpha = 0.1f * glowPulse), cx * 1.4f, Offset(cx, cy))
                    drawCircle(SuccessGreen.copy(alpha = 0.05f * glowPulse), cx * 1.8f, Offset(cx, cy))
                    // Ring stroke draws in
                    drawArc(
                        color = SuccessGreen, startAngle = -90f,
                        sweepAngle = 360f * ringDraw.value, useCenter = false,
                        style = Stroke(width = 8f, cap = StrokeCap.Round),
                        topLeft = Offset(cx - cx * 0.7f, cy - cy * 0.7f),
                        size = Size(cx * 1.4f, cy * 1.4f)
                    )
                    // Filled green circle
                    drawCircle(SuccessGreen.copy(alpha = circleFill.value), cx * 0.65f, Offset(cx, cy))
                    // Checkmark path that draws in
                    if (checkDraw.value > 0f) {
                        val sX = cx - cx * 0.22f; val sY = cy + cy * 0.02f
                        val mX = cx - cx * 0.04f; val mY = cy + cy * 0.2f
                        val eX = cx + cx * 0.28f; val eY = cy - cy * 0.18f
                        val checkPath = Path().apply {
                            moveTo(sX, sY)
                            if (checkDraw.value < 0.5f) {
                                val t = checkDraw.value * 2f
                                lineTo(sX + (mX - sX) * t, sY + (mY - sY) * t)
                            } else {
                                lineTo(mX, mY)
                                val t = (checkDraw.value - 0.5f) * 2f
                                lineTo(mX + (eX - mX) * t, mY + (eY - mY) * t)
                            }
                        }
                        drawPath(checkPath, Color.White,
                            style = Stroke(width = 10f, cap = StrokeCap.Round))
                    }
                }
            }

            Spacer(Modifier.height(32.dp))

            // Title
            Text("Payment Successful",
                modifier = Modifier.graphicsLayer {
                    alpha = titleAlpha.value
                    translationY = (1f - titleAlpha.value) * 15f
                },
                fontSize = 26.sp, fontWeight = FontWeight.Bold,
                color = TextDark, letterSpacing = (-0.5).sp)

            Spacer(Modifier.height(12.dp))

            Text("Your payment has been processed\nsuccessfully",
                modifier = Modifier.graphicsLayer {
                    alpha = titleAlpha.value * 0.8f
                    translationY = (1f - titleAlpha.value) * 20f
                },
                fontSize = 14.sp, color = TextMuted,
                textAlign = TextAlign.Center, lineHeight = 20.sp)

            Spacer(Modifier.height(36.dp))

            // Amount card
            Surface(
                modifier = Modifier.fillMaxWidth()
                    .padding(horizontal = 32.dp)
                    .graphicsLayer {
                        alpha = detailSlide.value
                        translationY = (1f - detailSlide.value) * 40f
                    },
                shape = RoundedCornerShape(20.dp),
                color = Color.White,
                shadowElevation = 4.dp,
                tonalElevation = 2.dp
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("AMOUNT PAID", fontSize = 12.sp, color = TextMuted,
                        fontWeight = FontWeight.Medium, letterSpacing = 1.5.sp)
                    Spacer(Modifier.height(8.dp))

                    val amountAnim = remember { Animatable(0f) }
                    LaunchedEffect(detailSlide.value > 0.5f) {
                        if (detailSlide.value > 0.5f) {
                            amountAnim.animateTo(1f, tween(800, easing = FastOutSlowInEasing))
                        }
                    }
                    @Suppress("DefaultLocale")
                    Text("$${String.format("%.2f", 2499.99f * amountAnim.value)}",
                        fontSize = 36.sp, fontWeight = FontWeight.Black, color = TextDark)

                    Spacer(Modifier.height(16.dp))
                    Divider(color = Color(0xFFEEEEEE))
                    Spacer(Modifier.height(16.dp))

                    Row(Modifier.fillMaxWidth()) {
                        Column {
                            Text("To", fontSize = 11.sp, color = TextMuted, letterSpacing = 1.sp)
                            Text("Apple Store", fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold, color = TextDark)
                        }
                        Spacer(Modifier.weight(1f))
                        Column(horizontalAlignment = Alignment.End) {
                            Text("Date", fontSize = 11.sp, color = TextMuted, letterSpacing = 1.sp)
                            Text("Mar 12, 2026", fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold, color = TextDark)
                        }
                    }
                    Spacer(Modifier.height(12.dp))
                    Row(Modifier.fillMaxWidth()) {
                        Column {
                            Text("Reference", fontSize = 11.sp, color = TextMuted, letterSpacing = 1.sp)
                            Text("#TXN-48291", fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold, color = TextDark)
                        }
                        Spacer(Modifier.weight(1f))
                        Column(horizontalAlignment = Alignment.End) {
                            Text("Status", fontSize = 11.sp, color = TextMuted, letterSpacing = 1.sp)
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(Modifier.size(8.dp).clip(CircleShape).background(SuccessGreen))
                                Spacer(Modifier.width(4.dp))
                                Text("Completed", fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold, color = SuccessGreen)
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.weight(1f))

            // Bottom button
            Button(
                onClick = onBack,
                modifier = Modifier.fillMaxWidth()
                    .padding(horizontal = 32.dp)
                    .padding(bottom = 16.dp)
                    .navigationBarsPadding()
                    .height(56.dp)
                    .graphicsLayer {
                        alpha = buttonAlpha.value
                        translationY = (1f - buttonAlpha.value) * 30f
                    },
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen)
            ) {
                Icon(Icons.Filled.Home, null, Modifier.size(20.dp))
                Spacer(Modifier.width(8.dp))
                Text("Back to Home", fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}
