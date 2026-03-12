package com.ui.animatedmenu.showcase

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.*
import kotlinx.coroutines.delay

private val MedPurple = Color(0xFF7C4DFF)
private val MedPurpleDark = Color(0xFF4527A0)
private val MedBlue = Color(0xFF42A5F5)
private val MedBg = Color(0xFF0A0A1A)
private val MedSurface = Color(0xFF151530)
private val MedText = Color(0xFFE8E4F0)
private val MedGray = Color(0xFF8888AA)
private val MedAccent = Color(0xFF00E5FF)

data class MeditationSession(
    val title: String,
    val duration: String,
    val category: String,
    val hue: Float
)

private val sessions = listOf(
    MeditationSession("Deep Breathing", "10 min", "Breathwork", 260f),
    MeditationSession("Body Scan", "15 min", "Relaxation", 200f),
    MeditationSession("Focus Flow", "20 min", "Concentration", 180f),
    MeditationSession("Sleep Stories", "30 min", "Sleep", 280f),
    MeditationSession("Morning Rise", "5 min", "Morning", 40f),
    MeditationSession("Gratitude", "12 min", "Mindfulness", 150f),
)

@Composable
fun MeditationScreen(onBack: () -> Unit) {
    var isBreathing by remember { mutableStateOf(false) }
    var breathPhase by remember { mutableIntStateOf(0) } // 0=inhale, 1=hold, 2=exhale
    var breathTimer by remember { mutableIntStateOf(0) }

    val inf = rememberInfiniteTransition(label = "med")
    val cosmicPhase by inf.animateFloat(
        0f, 6.28f,
        infiniteRepeatable(tween(6000, easing = LinearEasing)),
        label = "cosmic"
    )
    val pulse by inf.animateFloat(
        0.95f, 1.05f,
        infiniteRepeatable(tween(2000, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "pulse"
    )

    // Breath animation
    val breathScale by animateFloatAsState(
        targetValue = when {
            !isBreathing -> 1f
            breathPhase == 0 -> 1.4f // inhale
            breathPhase == 1 -> 1.4f // hold
            else -> 0.9f // exhale
        },
        animationSpec = tween(
            durationMillis = when {
                !isBreathing -> 300
                breathPhase == 0 -> 4000
                breathPhase == 1 -> 2000
                else -> 4000
            },
            easing = FastOutSlowInEasing
        ),
        label = "breathScale"
    )

    // Breath cycle timer
    LaunchedEffect(isBreathing) {
        if (isBreathing) {
            while (isBreathing) {
                breathPhase = 0 // inhale
                breathTimer = 4
                for (i in 4 downTo 1) { breathTimer = i; delay(1000) }
                breathPhase = 1 // hold
                breathTimer = 2
                for (i in 2 downTo 1) { breathTimer = i; delay(1000) }
                breathPhase = 2 // exhale
                breathTimer = 4
                for (i in 4 downTo 1) { breathTimer = i; delay(1000) }
            }
        }
    }

    Box(
        modifier = Modifier.fillMaxSize().background(
            Brush.verticalGradient(listOf(MedBg, Color(0xFF0D0D2B)))
        )
    ) {
        // Cosmic background particles
        Canvas(modifier = Modifier.fillMaxSize()) {
            for (i in 0..40) {
                val x = (i * 73 + 17) % size.width.toInt()
                val y = (i * 89 + 31) % size.height.toInt()
                val a = 0.1f + sin(cosmicPhase + i * 0.5f).toFloat() * 0.1f
                val r = 1f + (i % 3)
                drawCircle(MedAccent.copy(alpha = a.coerceIn(0f, 1f)), r, Offset(x.toFloat(), y.toFloat()))
            }
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize().statusBarsPadding(),
            contentPadding = PaddingValues(bottom = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth().padding(20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, "Back", tint = MedText)
                    }
                    Text("Mindful", color = MedText, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    Box(
                        modifier = Modifier.size(40.dp).clip(CircleShape)
                            .background(MedPurple.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Filled.Settings, null, tint = MedPurple, modifier = Modifier.size(18.dp))
                    }
                }
            }

            // Breathing circle
            item {
                Spacer(Modifier.height(16.dp))

                Box(
                    modifier = Modifier.size(260.dp),
                    contentAlignment = Alignment.Center
                ) {
                    // Outer rings
                    Canvas(modifier = Modifier.fillMaxSize().scale(breathScale * pulse)) {
                        // Glow rings
                        for (ring in 3 downTo 0) {
                            val alpha = 0.05f + ring * 0.03f
                            val rad = size.width / 2 - ring * 15
                            drawCircle(
                                Brush.radialGradient(
                                    listOf(MedPurple.copy(alpha = alpha), Color.Transparent),
                                    radius = rad
                                ),
                                radius = rad
                            )
                        }

                        // Main ring
                        drawCircle(
                            Brush.sweepGradient(
                                listOf(MedPurple, MedBlue, MedAccent, MedPurple)
                            ),
                            radius = size.width / 2 - 50,
                            style = Stroke(width = 4f)
                        )

                        // Orbiting dots
                        for (i in 0..5) {
                            val angle = cosmicPhase + i * (6.28f / 6)
                            val rad2 = size.width / 2 - 50
                            val ox = center.x + cos(angle) * rad2
                            val oy = center.y + sin(angle) * rad2
                            drawCircle(MedAccent.copy(alpha = 0.6f), 4f, Offset(ox, oy))
                        }

                        // Inner animated gradient circle
                        drawCircle(
                            Brush.radialGradient(
                                listOf(
                                    MedPurple.copy(alpha = 0.4f),
                                    MedPurpleDark.copy(alpha = 0.2f),
                                    Color.Transparent
                                ),
                                radius = size.width / 2 - 60
                            ),
                            radius = size.width / 2 - 60
                        )
                    }

                    // Center text
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        if (isBreathing) {
                            Text(
                                when (breathPhase) {
                                    0 -> "Inhale"
                                    1 -> "Hold"
                                    else -> "Exhale"
                                },
                                color = MedAccent,
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                "$breathTimer",
                                color = MedText,
                                fontSize = 40.sp,
                                fontWeight = FontWeight.Bold
                            )
                        } else {
                            Text("Begin", color = MedText, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                            Text("Tap to start", color = MedGray, fontSize = 12.sp)
                        }
                    }

                    // Clickable overlay
                    Box(
                        modifier = Modifier.fillMaxSize().clip(CircleShape)
                            .clickable { isBreathing = !isBreathing }
                    )
                }

                Spacer(Modifier.height(8.dp))

                // Breath guide label
                if (isBreathing) {
                    Text(
                        "4-2-4 Breathing Pattern",
                        color = MedGray,
                        fontSize = 13.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Spacer(Modifier.height(24.dp))
            }

            // Stats row
            item {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    listOf(
                        Triple("12", "Sessions", MedPurple),
                        Triple("2.5h", "Total", MedBlue),
                        Triple("7", "Streak", MedAccent),
                    ).forEach { (value, label, color) ->
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .background(MedSurface)
                                .padding(horizontal = 20.dp, vertical = 14.dp)
                        ) {
                            Text(value, color = color, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                            Text(label, color = MedGray, fontSize = 12.sp)
                        }
                    }
                }
                Spacer(Modifier.height(24.dp))
            }

            // Sessions header
            item {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Sessions", color = MedText, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    Text("See all", color = MedPurple, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                }
                Spacer(Modifier.height(12.dp))
            }

            // Session cards
            items(sessions) { session ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 4.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(MedSurface)
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Mini mandala
                    Box(
                        modifier = Modifier.size(48.dp).clip(CircleShape)
                    ) {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            drawCircle(Color.hsl(session.hue, 0.5f, 0.15f))
                            // Concentric rings
                            for (r in 3 downTo 0) {
                                drawCircle(
                                    Color.hsl(session.hue, 0.6f, 0.4f + r * 0.1f).copy(alpha = 0.3f),
                                    radius = size.width / 2 - r * 6,
                                    style = Stroke(1.5f)
                                )
                            }
                            // Center dot
                            drawCircle(Color.hsl(session.hue, 0.7f, 0.6f), 4f)
                        }
                    }
                    Spacer(Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(session.title, color = MedText, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
                        Text("${session.category} • ${session.duration}", color = MedGray, fontSize = 12.sp)
                    }
                    Box(
                        modifier = Modifier.size(36.dp).clip(CircleShape)
                            .background(MedPurple.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Filled.PlayArrow, null, tint = MedPurple, modifier = Modifier.size(18.dp))
                    }
                }
            }
        }
    }
}
