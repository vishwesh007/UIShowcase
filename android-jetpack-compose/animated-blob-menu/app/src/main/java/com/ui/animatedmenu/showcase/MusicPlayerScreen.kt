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
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun MusicPlayerScreen(onBack: () -> Unit = {}) {
    val Bg = Color(0xFF0D0015)
    val Surface = Color(0xFF1A0A2E)
    val Accent = Color(0xFFBB86FC)
    val AccentPink = Color(0xFFE040FB)
    val AccentHot = Color(0xFFFF4081)
    val TextPrimary = Color(0xFFF5F5F5)
    val TextSecondary = Color(0xFF9E9E9E)

    var isPlaying by remember { mutableStateOf(true) }

    val inf = rememberInfiniteTransition(label = "music")
    val discRot by inf.animateFloat(0f, 360f,
        infiniteRepeatable(tween(10000, easing = LinearEasing)), label = "rot")
    val wavePhase by inf.animateFloat(0f, 6.2832f,
        infiniteRepeatable(tween(1800, easing = LinearEasing)), label = "wp")
    val glowPulse by inf.animateFloat(0.4f, 1f,
        infiniteRepeatable(tween(2500, easing = FastOutSlowInEasing), RepeatMode.Reverse), label = "gp")
    val orbFloat by inf.animateFloat(0f, 1f,
        infiniteRepeatable(tween(6000, easing = FastOutSlowInEasing), RepeatMode.Reverse), label = "of")
    val progressAnim by inf.animateFloat(0f, 1f,
        infiniteRepeatable(tween(30000, easing = LinearEasing)), label = "prog")

    Box(modifier = Modifier.fillMaxSize().background(Bg)) {
        // Animated background orbs
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width; val h = size.height
            drawCircle(Accent.copy(alpha = 0.06f * glowPulse), radius = 280f,
                center = Offset(w * 0.2f, h * 0.15f + orbFloat * 40f))
            drawCircle(AccentPink.copy(alpha = 0.05f * glowPulse), radius = 220f,
                center = Offset(w * 0.85f, h * 0.35f - orbFloat * 30f))
            drawCircle(AccentHot.copy(alpha = 0.04f * glowPulse), radius = 180f,
                center = Offset(w * 0.5f, h * 0.85f + orbFloat * 20f))
        }

        Column(
            modifier = Modifier.fillMaxSize().statusBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Top bar
            Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack) {
                    Icon(Icons.Filled.ArrowBack, "Back", tint = TextPrimary)
                }
                Spacer(Modifier.weight(1f))
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("NOW PLAYING", color = TextSecondary, fontSize = 11.sp,
                        fontWeight = FontWeight.Medium, letterSpacing = 2.sp)
                }
                Spacer(Modifier.weight(1f))
                IconButton(onClick = {}) { Icon(Icons.Filled.MoreVert, null, tint = TextSecondary) }
            }

            Spacer(Modifier.height(16.dp))

            // Vinyl disc with glow
            Box(modifier = Modifier.size(280.dp), contentAlignment = Alignment.Center) {
                // Outer glow ring
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val cx = size.width / 2; val cy = size.height / 2
                    drawCircle(Accent.copy(alpha = 0.12f * glowPulse), radius = size.width / 2, center = Offset(cx, cy))
                    drawCircle(AccentPink.copy(alpha = 0.08f * glowPulse), radius = size.width / 2 - 15f, center = Offset(cx, cy))
                }

                // Vinyl disc
                Box(modifier = Modifier.size(250.dp).rotate(if (isPlaying) discRot else 0f)
                    .clip(CircleShape)
                    .background(Brush.radialGradient(listOf(
                        Color(0xFF1A1A2E), Color(0xFF16162A), Color(0xFF0F0F1A), Color(0xFF1A1A2E)
                    ))),
                    contentAlignment = Alignment.Center) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val cx = size.width / 2; val cy = size.height / 2
                        for (i in 1..20) {
                            val r = size.width / 2 * (0.25f + i * 0.035f)
                            val alpha = if (i % 3 == 0) 0.08f else 0.03f
                            drawCircle(Color.White.copy(alpha = alpha), r, Offset(cx, cy), style = Stroke(0.5f))
                        }
                        // Highlight streak
                        drawArc(Color.White.copy(alpha = 0.04f), 45f, 90f, false,
                            topLeft = Offset(20f, 20f), size = Size(size.width - 40f, size.height - 40f),
                            style = Stroke(40f))
                    }
                    // Center hub
                    Box(modifier = Modifier.size(90.dp).clip(CircleShape)
                        .background(Brush.sweepGradient(listOf(Accent, AccentPink, AccentHot, Accent))),
                        contentAlignment = Alignment.Center) {
                        Box(modifier = Modifier.size(82.dp).clip(CircleShape)
                            .background(Brush.radialGradient(listOf(Color(0xFF2D1B69), Color(0xFF1A0A2E)))),
                            contentAlignment = Alignment.Center) {
                            Icon(Icons.Filled.MusicNote, null, tint = Accent, modifier = Modifier.size(28.dp))
                        }
                    }
                }
            }

            Spacer(Modifier.height(28.dp))

            // Track info
            Text("Midnight Dreams", color = TextPrimary, fontSize = 24.sp,
                fontWeight = FontWeight.Bold, letterSpacing = 0.5.sp)
            Spacer(Modifier.height(4.dp))
            Text("Aurora  •  Synthwave Album  •  2024", color = TextSecondary, fontSize = 13.sp)

            Spacer(Modifier.height(28.dp))

            // Waveform visualizer with gradient
            Canvas(modifier = Modifier.fillMaxWidth().height(56.dp).padding(horizontal = 28.dp)) {
                val w = size.width; val h = size.height
                val barCount = 50; val barW = w / barCount * 0.6f; val sp = w / barCount
                val progressX = progressAnim * w

                for (i in 0 until barCount) {
                    val x = i * sp
                    val wave1 = sin(wavePhase + i * 0.4f) * 0.3f + 0.5f
                    val wave2 = sin(wavePhase * 1.3f + i * 0.6f) * 0.2f
                    val amp = ((wave1 + wave2).coerceIn(0.1f, 1f)) * h * 0.85f
                    val isPast = x < progressX
                    val barBrush = if (isPast) Brush.verticalGradient(listOf(AccentPink, Accent))
                        else Brush.verticalGradient(listOf(Color(0xFF2A1A4E), Color(0xFF1A0A2E)))
                    drawRoundRect(brush = barBrush, topLeft = Offset(x, (h - amp) / 2),
                        size = Size(barW, amp), cornerRadius = CornerRadius(3f))
                }
            }

            // Progress time
            Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 28.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween) {
                Text("${(progressAnim * 3).toInt()}:${"%02d".format(((progressAnim * 225) % 60).toInt())}",
                    color = Accent, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                Text("3:45", color = TextSecondary, fontSize = 12.sp)
            }

            // Progress bar
            Box(modifier = Modifier.fillMaxWidth().padding(horizontal = 28.dp).height(3.dp)
                .clip(RoundedCornerShape(2.dp)).background(Surface)) {
                Box(modifier = Modifier.fillMaxWidth(progressAnim).fillMaxHeight()
                    .background(Brush.horizontalGradient(listOf(Accent, AccentPink))))
                // Scrubber dot
                Box(modifier = Modifier.fillMaxWidth(progressAnim)) {
                    Box(modifier = Modifier.size(10.dp).align(Alignment.CenterEnd)
                        .clip(CircleShape).background(AccentPink))
                }
            }

            Spacer(Modifier.height(28.dp))

            // Controls
            Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 32.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = {}) {
                    Icon(Icons.Filled.Shuffle, null, tint = TextSecondary, modifier = Modifier.size(22.dp))
                }
                Box(modifier = Modifier.size(48.dp).clip(CircleShape)
                    .background(Surface), contentAlignment = Alignment.Center) {
                    Icon(Icons.Filled.SkipPrevious, null, tint = TextPrimary, modifier = Modifier.size(28.dp))
                }
                // Play/Pause with glow
                Box(contentAlignment = Alignment.Center) {
                    Canvas(modifier = Modifier.size(76.dp)) {
                        drawCircle(Accent.copy(alpha = 0.25f * glowPulse), radius = size.width / 2)
                    }
                    Box(modifier = Modifier.size(68.dp).clip(CircleShape)
                        .background(Brush.linearGradient(listOf(Accent, AccentPink)))
                        .clickable { isPlaying = !isPlaying },
                        contentAlignment = Alignment.Center) {
                        Icon(if (isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                            null, tint = Color.White, modifier = Modifier.size(34.dp))
                    }
                }
                Box(modifier = Modifier.size(48.dp).clip(CircleShape)
                    .background(Surface), contentAlignment = Alignment.Center) {
                    Icon(Icons.Filled.SkipNext, null, tint = TextPrimary, modifier = Modifier.size(28.dp))
                }
                IconButton(onClick = {}) {
                    Icon(Icons.Filled.Repeat, null, tint = TextSecondary, modifier = Modifier.size(22.dp))
                }
            }

            Spacer(Modifier.height(20.dp))

            // Bottom action bar
            Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 48.dp),
                horizontalArrangement = Arrangement.SpaceEvenly) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Filled.FavoriteBorder, null, tint = AccentHot, modifier = Modifier.size(20.dp))
                    Text("Like", color = TextSecondary, fontSize = 10.sp)
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Filled.QueueMusic, null, tint = TextSecondary, modifier = Modifier.size(20.dp))
                    Text("Queue", color = TextSecondary, fontSize = 10.sp)
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Filled.Share, null, tint = TextSecondary, modifier = Modifier.size(20.dp))
                    Text("Share", color = TextSecondary, fontSize = 10.sp)
                }
            }
        }
    }
}
