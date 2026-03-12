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
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.sin

data class LogoItem(
    val label: String,
    val shortLabel: String,
    val bgColor: Color,
    val textColor: Color = Color.White
)

@Composable
fun LogoSplashScreen(onBack: () -> Unit) {
    val logos = remember {
        listOf(
            LogoItem("Photoshop", "Ps", Color(0xFF001E36), Color(0xFF31A8FF)),
            LogoItem("After Effects", "Ae", Color(0xFF00005B), Color(0xFF9999FF)),
            LogoItem("Illustrator", "Ai", Color(0xFF330000), Color(0xFFFF9A00)),
            LogoItem("Figma", "Fi", Color(0xFF1E1E1E), Color(0xFFA259FF)),
            LogoItem("Creative Cloud", "Cc", Color(0xFF2A0020), Color(0xFFFF3366)),
            LogoItem("InDesign", "Id", Color(0xFF49021F), Color(0xFFFF3366)),
            LogoItem("Premiere Pro", "Pr", Color(0xFF00005B), Color(0xFF9999FF)),
            LogoItem("XD", "Xd", Color(0xFF470137), Color(0xFFFF61F6)),
            LogoItem("Lightroom", "Lr", Color(0xFF001D26), Color(0xFF31A8FF)),
        )
    }

    // --- Animation timeline ---
    // Phase 0: Black screen (0-300ms)
    // Phase 1: Logos stagger in with scale+rotation (300ms-1800ms, ~170ms apart per logo)
    // Phase 2: Hold (1800ms-3000ms)
    // Phase 3: Title text fades in (3000ms-3600ms)
    // Phase 4: Shimmer sweep across all logos (3600ms-4600ms)
    // Phase 5: Loop idle with subtle floating

    val logoAnimatables = remember { List(9) { Animatable(0f) } }
    val titleAlpha = remember { Animatable(0f) }
    val subtitleAlpha = remember { Animatable(0f) }

    // Shimmer phase
    val shimmerProgress = remember { Animatable(0f) }

    // Continuous floating
    val infiniteTransition = rememberInfiniteTransition(label = "float")
    val floatPhase by infiniteTransition.animateFloat(
        0f, 6.28f,
        infiniteRepeatable(tween(6000, easing = LinearEasing)),
        label = "fp"
    )

    // Global glow pulse
    val glowPulse by infiniteTransition.animateFloat(
        0.3f, 0.7f,
        infiniteRepeatable(tween(3000, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "gp"
    )

    LaunchedEffect(Unit) {
        delay(300)
        // Stagger logo entrances
        for (i in logoAnimatables.indices) {
            launch {
                logoAnimatables[i].animateTo(
                    1f,
                    spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = 300f)
                )
            }
            delay(120)
        }
        delay(1200)
        // Title text
        titleAlpha.animateTo(1f, tween(500, easing = FastOutSlowInEasing))
        delay(200)
        subtitleAlpha.animateTo(1f, tween(500, easing = FastOutSlowInEasing))
        delay(400)
        // Shimmer sweep
        shimmerProgress.animateTo(1f, tween(1200, easing = FastOutSlowInEasing))
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        // Background subtle grid lines
        Canvas(modifier = Modifier.fillMaxSize()) {
            val gridColor = Color.White.copy(alpha = 0.03f)
            val spacing = 60f
            for (x in 0..(size.width / spacing).toInt()) {
                drawLine(
                    gridColor, Offset(x * spacing, 0f),
                    Offset(x * spacing, size.height), strokeWidth = 0.5f
                )
            }
            for (y in 0..(size.height / spacing).toInt()) {
                drawLine(
                    gridColor, Offset(0f, y * spacing),
                    Offset(size.width, y * spacing), strokeWidth = 0.5f
                )
            }
        }

        // Radial glow behind logos
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawCircle(
                Brush.radialGradient(
                    listOf(
                        Color(0xFF7C4DFF).copy(alpha = glowPulse * 0.08f),
                        Color.Transparent
                    )
                ),
                radius = size.minDimension * 0.6f,
                center = Offset(size.width / 2f, size.height * 0.4f)
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
        ) {
            // Back button
            IconButton(
                onClick = onBack,
                modifier = Modifier.padding(8.dp)
            ) {
                Icon(Icons.Filled.ArrowBack, "Back", tint = Color.White.copy(alpha = 0.7f))
            }

            Spacer(Modifier.weight(0.3f))

            // 3x3 Logo Grid
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 40.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                for (row in 0..2) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterHorizontally)
                    ) {
                        for (col in 0..2) {
                            val idx = row * 3 + col
                            val logo = logos[idx]
                            val entry = logoAnimatables[idx].value
                            val floatOffset = sin(floatPhase + idx * 0.7f).toFloat() * 4f
                            val shimmerVal = shimmerProgress.value

                            // Per-logo shimmer: diagonal sweep based on position
                            val logoShimmerPhase = ((idx % 3 + idx / 3) * 0.12f)
                            val shimmerAlpha = if (shimmerVal > logoShimmerPhase && shimmerVal < logoShimmerPhase + 0.3f) {
                                ((shimmerVal - logoShimmerPhase) / 0.15f).coerceIn(0f, 1f) *
                                        (1f - ((shimmerVal - logoShimmerPhase - 0.15f) / 0.15f).coerceIn(0f, 1f))
                            } else 0f

                            Box(
                                modifier = Modifier
                                    .size(88.dp)
                                    .graphicsLayer {
                                        scaleX = entry
                                        scaleY = entry
                                        alpha = entry
                                        rotationZ = (1f - entry) * -45f
                                        translationY = floatOffset
                                    }
                            ) {
                                Canvas(modifier = Modifier.fillMaxSize()) {
                                    // Logo background rounded rect
                                    drawRoundRect(
                                        color = logo.bgColor,
                                        cornerRadius = CornerRadius(20f, 20f),
                                        size = size
                                    )

                                    // Border glow
                                    drawRoundRect(
                                        color = logo.textColor.copy(alpha = 0.3f),
                                        cornerRadius = CornerRadius(20f, 20f),
                                        size = size,
                                        style = Stroke(width = 1.5f)
                                    )

                                    // Shimmer overlay
                                    if (shimmerAlpha > 0f) {
                                        drawRoundRect(
                                            color = Color.White.copy(alpha = shimmerAlpha * 0.2f),
                                            cornerRadius = CornerRadius(20f, 20f),
                                            size = size
                                        )
                                    }

                                    // Inner highlight (top-left gradient)
                                    drawRoundRect(
                                        brush = Brush.linearGradient(
                                            listOf(
                                                Color.White.copy(alpha = 0.06f),
                                                Color.Transparent
                                            ),
                                            start = Offset(0f, 0f),
                                            end = Offset(size.width, size.height)
                                        ),
                                        cornerRadius = CornerRadius(20f, 20f),
                                        size = size
                                    )
                                }

                                // Logo text label
                                Text(
                                    text = logo.shortLabel,
                                    color = logo.textColor,
                                    fontSize = 28.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.align(Alignment.Center),
                                    letterSpacing = (-1).sp
                                )

                                // App name label below
                                Text(
                                    text = logo.label,
                                    color = Color.White.copy(alpha = 0.5f),
                                    fontSize = 8.sp,
                                    modifier = Modifier
                                        .align(Alignment.BottomCenter)
                                        .padding(bottom = 6.dp),
                                    textAlign = TextAlign.Center,
                                    maxLines = 1
                                )
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(40.dp))

            // Title text
            Text(
                text = "Creative Suite",
                color = Color.White,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = (-1).sp,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .graphicsLayer {
                        alpha = titleAlpha.value
                        translationY = (1f - titleAlpha.value) * 30f
                    }
            )

            Spacer(Modifier.height(8.dp))

            Text(
                text = "Design · Create · Inspire",
                color = Color(0xFF7C4DFF),
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                letterSpacing = 2.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .graphicsLayer {
                        alpha = subtitleAlpha.value
                        translationY = (1f - subtitleAlpha.value) * 20f
                    }
            )

            Spacer(Modifier.weight(0.5f))

            // Bottom indicator dots
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 40.dp)
                    .graphicsLayer { alpha = subtitleAlpha.value },
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                repeat(3) { i ->
                    val dotAlpha = if (i == 0) 1f else 0.3f
                    Box(
                        modifier = Modifier
                            .padding(horizontal = 4.dp)
                            .size(if (i == 0) 24.dp else 8.dp, 8.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF7C4DFF).copy(alpha = dotAlpha))
                    )
                }
            }
        }
    }
}
