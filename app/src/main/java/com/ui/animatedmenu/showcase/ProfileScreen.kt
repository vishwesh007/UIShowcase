package com.ui.animatedmenu.showcase

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun ProfileScreen(onBack: () -> Unit = {}) {
    val Grad1 = Color(0xFF7C4DFF)
    val Grad2 = Color(0xFFE040FB)
    val Grad3 = Color(0xFFFF4081)
    val Bg = Color(0xFFF8F9FC)
    val TextDark = Color(0xFF1A1A2E)
    val TextMuted = Color(0xFF9E9E9E)

    val inf = rememberInfiniteTransition(label = "p")
    val ringRot by inf.animateFloat(0f, 360f,
        infiniteRepeatable(tween(12000, easing = LinearEasing)), label = "rr")

    // Entrance animations
    val headerEntry = remember { Animatable(0f) }
    val contentEntry = remember { Animatable(0f) }
    val statsEntry = remember { Animatable(0f) }
    val gridEntry = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        headerEntry.animateTo(1f, tween(600, easing = FastOutSlowInEasing))
    }
    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(300)
        contentEntry.animateTo(1f, spring(dampingRatio = 0.7f, stiffness = 200f))
    }
    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(500)
        statsEntry.animateTo(1f, tween(800, easing = FastOutSlowInEasing))
    }
    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(800)
        gridEntry.animateTo(1f, tween(600, easing = FastOutSlowInEasing))
    }

    Column(modifier = Modifier.fillMaxSize().background(Bg).verticalScroll(rememberScrollState())) {
        // Gradient header with particles
        Box(modifier = Modifier.fillMaxWidth().height(220.dp)
            .background(Brush.linearGradient(listOf(Grad1, Grad2, Grad3),
                start = Offset.Zero, end = Offset(1000f, 600f)))) {

            Canvas(modifier = Modifier.fillMaxSize()) {
                for (i in 0..15) {
                    val x = (i * 73 + 20) % size.width.toInt()
                    val y = (i * 47 + 30) % size.height.toInt()
                    drawCircle(Color.White.copy(alpha = 0.08f), radius = (15 + i * 3).toFloat(),
                        center = Offset(x.toFloat(), y.toFloat()))
                }
            }

            // Top actions
            Row(modifier = Modifier.fillMaxWidth().statusBarsPadding().padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack) { Icon(Icons.Filled.ArrowBack, "Back", tint = Color.White) }
                Spacer(Modifier.weight(1f))
                IconButton(onClick = {}) { Icon(Icons.Filled.MoreVert, null, tint = Color.White) }
            }
        }

        // Avatar overlapping header
        Column(modifier = Modifier.fillMaxWidth().offset(y = (-56).dp),
            horizontalAlignment = Alignment.CenterHorizontally) {

            Box(contentAlignment = Alignment.Center,
                modifier = Modifier.size(112.dp).graphicsLayer {
                    alpha = contentEntry.value
                    scaleX = 0.7f + contentEntry.value * 0.3f
                    scaleY = scaleX
                }) {
                // Animated gradient ring
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val r = size.minDimension / 2f
                    drawCircle(Color.White, radius = r)
                    drawArc(Brush.sweepGradient(listOf(Grad1, Grad2, Grad3, Grad1)),
                        startAngle = ringRot, sweepAngle = 360f, useCenter = false,
                        style = Stroke(width = 4.dp.toPx(), cap = StrokeCap.Round),
                        topLeft = Offset(4.dp.toPx(), 4.dp.toPx()),
                        size = androidx.compose.ui.geometry.Size(size.width - 8.dp.toPx(), size.height - 8.dp.toPx()))
                }
                Box(modifier = Modifier.size(96.dp).clip(CircleShape)
                    .background(Brush.radialGradient(listOf(Grad2.copy(alpha = 0.15f), Grad1.copy(alpha = 0.25f)))),
                    contentAlignment = Alignment.Center) {
                    Icon(Icons.Filled.Person, null, tint = Grad1, modifier = Modifier.size(48.dp))
                }
            }

            Spacer(Modifier.height(10.dp))
            Text("Sarah Mitchell", fontWeight = FontWeight.Bold, fontSize = 22.sp, color = TextDark,
                letterSpacing = (-0.3).sp)
            Text("UI/UX Designer  •  Creative Director", color = TextMuted, fontSize = 13.sp,
                letterSpacing = 0.5.sp)

            Spacer(Modifier.height(20.dp))

            // Stats in glass card with entrance animation
            Box(modifier = Modifier.padding(horizontal = 24.dp).fillMaxWidth()
                .graphicsLayer {
                    alpha = statsEntry.value
                    translationY = (1f - statsEntry.value) * 40f
                }
                .clip(RoundedCornerShape(20.dp))
                .background(Color.White).padding(vertical = 20.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                    AnimatedProfileStat(248, "Posts", statsEntry.value)
                    Box(modifier = Modifier.width(1.dp).height(36.dp).background(Color(0xFFEEEEEE)))
                    AnimatedProfileStat(12400, "Followers", statsEntry.value, format = "%.1fK")
                    Box(modifier = Modifier.width(1.dp).height(36.dp).background(Color(0xFFEEEEEE)))
                    AnimatedProfileStat(523, "Following", statsEntry.value)
                }
            }

            Spacer(Modifier.height(16.dp))

            // Action buttons
            Row(modifier = Modifier.padding(horizontal = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Button(onClick = {},
                    modifier = Modifier.weight(1f).height(44.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent),
                    contentPadding = PaddingValues(0.dp),
                    shape = RoundedCornerShape(14.dp)) {
                    Box(modifier = Modifier.fillMaxSize()
                        .background(Brush.horizontalGradient(listOf(Grad1, Grad2)),
                            shape = RoundedCornerShape(14.dp)),
                        contentAlignment = Alignment.Center) {
                        Text("Follow", color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                    }
                }
                OutlinedButton(onClick = {},
                    modifier = Modifier.weight(1f).height(44.dp),
                    shape = RoundedCornerShape(14.dp),
                    border = ButtonDefaults.outlinedButtonBorder) {
                    Text("Message", color = Grad1, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                }
            }

            Spacer(Modifier.height(24.dp))

            // Highlights
            Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.SpaceEvenly) {
                val highlights = listOf("Travel" to Icons.Filled.Flight, "Food" to Icons.Filled.Restaurant,
                    "Design" to Icons.Filled.Palette, "Music" to Icons.Filled.MusicNote)
                highlights.forEach { (label, icon) ->
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(modifier = Modifier.size(60.dp).clip(CircleShape)
                            .background(Brush.radialGradient(
                                listOf(Grad2.copy(alpha = 0.12f), Grad1.copy(alpha = 0.06f)))),
                            contentAlignment = Alignment.Center) {
                            Icon(icon, null, tint = Grad1, modifier = Modifier.size(24.dp))
                        }
                        Spacer(Modifier.height(4.dp))
                        Text(label, fontSize = 11.sp, color = TextMuted)
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

            // Recent Work header
            Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
                verticalAlignment = Alignment.CenterVertically) {
                Text("Recent Work", fontWeight = FontWeight.Bold, fontSize = 17.sp, color = TextDark)
                Spacer(Modifier.weight(1f))
                Text("See all", color = Grad1, fontSize = 13.sp, fontWeight = FontWeight.Medium)
            }
            Spacer(Modifier.height(12.dp))

            // Post grid
            val postGradients = listOf(
                listOf(Color(0xFFE8EAF6), Color(0xFFC5CAE9)),
                listOf(Color(0xFFFCE4EC), Color(0xFFF8BBD0)),
                listOf(Color(0xFFE0F7FA), Color(0xFFB2EBF2)),
                listOf(Color(0xFFF3E5F5), Color(0xFFE1BEE7)),
                listOf(Color(0xFFE8F5E9), Color(0xFFC8E6C9)),
                listOf(Color(0xFFFFF3E0), Color(0xFFFFE0B2))
            )
            val postIcons = listOf(Icons.Filled.Image, Icons.Filled.Palette, Icons.Filled.Brush,
                Icons.Filled.DesignServices, Icons.Filled.AutoAwesome, Icons.Filled.ColorLens)

            Column(modifier = Modifier.padding(horizontal = 16.dp).padding(bottom = 56.dp)
                .graphicsLayer {
                    alpha = gridEntry.value
                    translationY = (1f - gridEntry.value) * 60f
                }) {
                for (row in 0..1) {
                    Row(modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        for (col in 0..2) {
                            val idx = row * 3 + col
                            Box(modifier = Modifier.weight(1f).aspectRatio(1f)
                                .clip(RoundedCornerShape(16.dp))
                                .background(Brush.linearGradient(postGradients[idx])),
                                contentAlignment = Alignment.Center) {
                                Icon(postIcons[idx], null, tint = Grad1.copy(alpha = 0.35f),
                                    modifier = Modifier.size(32.dp))
                            }
                        }
                    }
                    if (row == 0) Spacer(Modifier.height(8.dp))
                }
            }
        }
    }
}

@Composable
private fun AnimatedProfileStat(targetValue: Int, label: String, progress: Float, format: String? = null) {
    val displayValue = (targetValue * progress).toInt()
    val displayText = if (format != null && targetValue >= 1000) {
        format.format(displayValue / 1000f)
    } else {
        "$displayValue"
    }
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(displayText, fontWeight = FontWeight.Bold, fontSize = 20.sp, color = Color(0xFF1A1A2E),
            letterSpacing = (-0.5).sp)
        Text(label, color = Color(0xFF9E9E9E), fontSize = 12.sp)
    }
}

@Composable
private fun ProfileStat(value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, fontWeight = FontWeight.Bold, fontSize = 20.sp, color = Color(0xFF1A1A2E),
            letterSpacing = (-0.5).sp)
        Text(label, color = Color(0xFF9E9E9E), fontSize = 12.sp)
    }
}
