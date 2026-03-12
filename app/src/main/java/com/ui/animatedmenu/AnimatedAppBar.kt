package com.ui.animatedmenu

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Animated App Bar with organic wave/blob decoration
 * Matches the video's aesthetic: dark bar, amber accents, smooth curves
 */

private val AppBarBackground = Color(0xFF1A1A1A)
private val AppBarAccent = Color(0xFFFFC107)
private val AppBarTextColor = Color(0xFFF5F5F5)

@Composable
fun AnimatedAppBar(
    title: String,
    onMenuClick: () -> Unit = {},
    onSearchClick: () -> Unit = {},
    onMoreClick: () -> Unit = {}
) {
    // Subtle breathing animation on the bar's decorative wave
    val infiniteTransition = rememberInfiniteTransition(label = "appBarWave")
    val waveOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = EaseInOutCubic),
            repeatMode = RepeatMode.Reverse
        ),
        label = "wavePhase"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
    ) {
        // Bottom decorative wave
        Canvas(
            modifier = Modifier.fillMaxSize()
        ) {
            val w = size.width
            val h = size.height
            val waveHeight = 20f + (waveOffset * 8f) // subtle breathing

            val path = Path().apply {
                moveTo(0f, 0f)
                lineTo(w, 0f)
                lineTo(w, h - waveHeight * 2)

                // Organic wave bottom edge
                cubicTo(
                    w * 0.75f, h - waveHeight * 0.5f,
                    w * 0.5f, h - waveHeight * 3f,
                    w * 0.25f, h - waveHeight * 1.5f
                )
                cubicTo(
                    w * 0.1f, h - waveHeight * 0.8f,
                    0f, h - waveHeight * 2f,
                    0f, h - waveHeight * 2.5f
                )
                close()
            }

            drawPath(
                path = path,
                color = AppBarBackground
            )

            // Accent line at bottom of wave
            val accentPath = Path().apply {
                moveTo(w * 0.15f, h - waveHeight * 2.2f)
                cubicTo(
                    w * 0.35f, h - waveHeight * 3.2f,
                    w * 0.55f, h - waveHeight * 1.2f,
                    w * 0.85f, h - waveHeight * 2f
                )
            }
            drawPath(
                path = accentPath,
                color = AppBarAccent.copy(alpha = 0.3f),
                style = androidx.compose.ui.graphics.drawscope.Stroke(width = 3f)
            )
        }

        // Content
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onMenuClick) {
                Icon(
                    imageVector = Icons.Filled.Menu,
                    contentDescription = "Menu",
                    tint = AppBarTextColor
                )
            }

            Text(
                text = title,
                color = AppBarTextColor,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 12.dp)
            )

            IconButton(onClick = onSearchClick) {
                Icon(
                    imageVector = Icons.Filled.Search,
                    contentDescription = "Search",
                    tint = AppBarAccent
                )
            }

            IconButton(onClick = onMoreClick) {
                Icon(
                    imageVector = Icons.Filled.MoreVert,
                    contentDescription = "More",
                    tint = AppBarTextColor
                )
            }
        }
    }
}
