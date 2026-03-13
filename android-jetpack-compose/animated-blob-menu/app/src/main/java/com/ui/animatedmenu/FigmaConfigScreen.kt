package com.ui.animatedmenu

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.cos
import kotlin.math.sin

private val FigmaYellow = Color(0xFFF7D33D)
private val FigmaCoral = Color(0xFFE86A51)
private val FigmaBlue = Color(0xFF6C98FA)
private val FigmaPurple = Color(0xFFA259FF)
private val FigmaGreen = Color(0xFF0ACF83)
private val FigmaBg = Color(0xFF0A0A12)
private val FigmaCardBg = Color(0xFF14141E)
private val FigmaTextPrimary = Color(0xFFF5F5F5)

@Composable
fun FigmaConfigScreen(onBack: () -> Unit) {
    val infiniteTransition = rememberInfiniteTransition()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(FigmaBg)
            .statusBarsPadding()
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.ArrowBack, "Back", tint = FigmaTextPrimary)
            }
            Text(
                "Config 2020",
                color = FigmaTextPrimary,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Hero Section
            FigmaHeroSection(infiniteTransition)

            // Animated Components Grid
            FigmaComponentGrid(infiniteTransition)

            // Color Palette
            FigmaColorPalette(infiniteTransition)

            // Geometric Pattern
            FigmaGeometricPattern(infiniteTransition)

            Spacer(Modifier.height(16.dp))
        }
    }
}

@Composable
private fun FigmaHeroSection(infiniteTransition: InfiniteTransition) {
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 360f,
        animationSpec = infiniteRepeatable(tween(20000, easing = LinearEasing))
    )
    val pulse by infiniteTransition.animateFloat(
        initialValue = 0.9f, targetValue = 1.1f,
        animationSpec = infiniteRepeatable(tween(3000), RepeatMode.Reverse)
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(240.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(FigmaCardBg),
        contentAlignment = Alignment.Center
    ) {
        // Background gradient orbs
        Canvas(modifier = Modifier.fillMaxSize()) {
            val cx = size.width / 2
            val cy = size.height / 2

            // Orbiting color circles
            val orbs = listOf(
                Triple(FigmaYellow, 0f, 80.dp.toPx()),
                Triple(FigmaCoral, 72f, 90.dp.toPx()),
                Triple(FigmaBlue, 144f, 75.dp.toPx()),
                Triple(FigmaPurple, 216f, 85.dp.toPx()),
                Triple(FigmaGreen, 288f, 70.dp.toPx())
            )

            for ((color, baseAngle, radius) in orbs) {
                val angle = Math.toRadians((baseAngle + rotation).toDouble())
                val ox = cx + cos(angle).toFloat() * radius * pulse
                val oy = cy + sin(angle).toFloat() * radius * 0.6f * pulse
                drawCircle(
                    color = color.copy(alpha = 0.25f),
                    radius = 45.dp.toPx(),
                    center = Offset(ox, oy)
                )
                drawCircle(
                    color = color.copy(alpha = 0.08f),
                    radius = 80.dp.toPx(),
                    center = Offset(ox, oy)
                )
            }
        }

        // Title text
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                "CONFIG",
                color = FigmaTextPrimary,
                fontSize = 36.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 8.sp
            )
            Text(
                "2020",
                color = FigmaYellow,
                fontSize = 48.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 12.sp
            )
            Spacer(Modifier.height(8.dp))
            Text(
                "Design Systems Conference",
                color = FigmaTextPrimary.copy(alpha = 0.6f),
                fontSize = 12.sp,
                letterSpacing = 2.sp
            )
        }
    }
}

@Composable
private fun FigmaComponentGrid(infiniteTransition: InfiniteTransition) {
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 360f,
        animationSpec = infiniteRepeatable(tween(8000, easing = LinearEasing))
    )
    val bounce by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(2000), RepeatMode.Reverse)
    )

    data class FigmaComp(val name: String, val color: Color)
    val components = listOf(
        FigmaComp("Frame", FigmaBlue),
        FigmaComp("Vector", FigmaGreen),
        FigmaComp("Text", FigmaPurple),
        FigmaComp("Shape", FigmaCoral)
    )

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        components.forEachIndexed { idx, comp ->
            val delay = idx * 500f
            val stagger by infiniteTransition.animateFloat(
                initialValue = 0.8f, targetValue = 1.2f,
                animationSpec = infiniteRepeatable(
                    tween(2000, delayMillis = (delay).toInt()),
                    RepeatMode.Reverse
                )
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(16.dp))
                    .background(FigmaCardBg)
                    .padding(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier.size(48.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val center = Offset(size.width / 2, size.height / 2)
                        when (idx) {
                            0 -> { // Frame — rotating rect
                                rotate(rotation * 0.5f, center) {
                                    drawRoundRect(
                                        color = comp.color,
                                        topLeft = Offset(8.dp.toPx(), 8.dp.toPx()),
                                        size = Size(
                                            size.width - 16.dp.toPx(),
                                            size.height - 16.dp.toPx()
                                        ),
                                        cornerRadius = CornerRadius(4.dp.toPx()),
                                        style = Stroke(2.5.dp.toPx())
                                    )
                                }
                            }
                            1 -> { // Vector — star/triangle
                                val path = Path().apply {
                                    val r = 18.dp.toPx() * stagger.coerceIn(0.5f, 1.5f)
                                    for (i in 0..4) {
                                        val angle = Math.toRadians((i * 72.0 - 90 + rotation * 0.3))
                                        val px = center.x + cos(angle).toFloat() * r
                                        val py = center.y + sin(angle).toFloat() * r
                                        if (i == 0) moveTo(px, py) else lineTo(px, py)
                                    }
                                    close()
                                }
                                drawPath(path, comp.color, style = Stroke(2.dp.toPx()))
                            }
                            2 -> { // Text — Aa
                                drawCircle(
                                    comp.color.copy(alpha = 0.2f),
                                    radius = 20.dp.toPx() * stagger.coerceIn(0.5f, 1.5f),
                                    center = center
                                )
                                drawCircle(
                                    comp.color,
                                    radius = 8.dp.toPx(),
                                    center = center
                                )
                            }
                            3 -> { // Shape — morphing rect/circle
                                val cRadius = (bounce * 20.dp.toPx()).coerceAtLeast(2.dp.toPx())
                                rotate(rotation * 0.25f, center) {
                                    drawRoundRect(
                                        color = comp.color,
                                        topLeft = Offset(6.dp.toPx(), 6.dp.toPx()),
                                        size = Size(
                                            size.width - 12.dp.toPx(),
                                            size.height - 12.dp.toPx()
                                        ),
                                        cornerRadius = CornerRadius(cRadius)
                                    )
                                }
                            }
                        }
                    }
                }
                Spacer(Modifier.height(8.dp))
                Text(
                    comp.name,
                    color = comp.color,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
private fun FigmaColorPalette(infiniteTransition: InfiniteTransition) {
    val wave by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 6.2832f,
        animationSpec = infiniteRepeatable(tween(3000, easing = LinearEasing))
    )

    val colors = listOf(
        "Yellow" to FigmaYellow,
        "Coral" to FigmaCoral,
        "Blue" to FigmaBlue,
        "Purple" to FigmaPurple,
        "Green" to FigmaGreen
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(FigmaCardBg)
            .padding(20.dp)
    ) {
        Text(
            "BRAND PALETTE",
            color = FigmaTextPrimary.copy(alpha = 0.5f),
            fontSize = 12.sp,
            letterSpacing = 2.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            colors.forEachIndexed { idx, (name, color) ->
                val scale = (1f + sin(wave + idx * 1.2f).toFloat() * 0.15f).coerceIn(0.5f, 1.5f)

                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .scale(scale)
                            .clip(CircleShape)
                            .background(color)
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        name,
                        color = color.copy(alpha = 0.8f),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Composable
private fun FigmaGeometricPattern(infiniteTransition: InfiniteTransition) {
    val phase by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 6.2832f,
        animationSpec = infiniteRepeatable(tween(4000, easing = LinearEasing))
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(FigmaCardBg)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val cols = 6
            val rows = 4
            val cellW = size.width / cols
            val cellH = size.height / rows
            val figColors = listOf(FigmaYellow, FigmaCoral, FigmaBlue, FigmaPurple, FigmaGreen)

            for (row in 0 until rows) {
                for (col in 0 until cols) {
                    val cx = cellW * col + cellW / 2
                    val cy = cellH * row + cellH / 2
                    val colorIdx = (row + col) % figColors.size
                    val color = figColors[colorIdx]
                    val offset = sin(phase + (row + col) * 0.6f).toFloat()
                    val radius = (8.dp.toPx() + offset * 4.dp.toPx()).coerceAtLeast(2.dp.toPx())
                    val alpha = (0.3f + offset * 0.25f).coerceIn(0.05f, 0.7f)

                    drawCircle(
                        color = color.copy(alpha = alpha),
                        radius = radius,
                        center = Offset(cx, cy + offset * 6.dp.toPx())
                    )
                }
            }
        }

        // Label
        Text(
            "GEOMETRIC MOTION",
            color = FigmaTextPrimary.copy(alpha = 0.4f),
            fontSize = 11.sp,
            letterSpacing = 3.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 12.dp)
        )
    }
}
