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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.PI
import kotlin.math.sin

private val CreamBg = Color(0xFFF5E6D3)
private val WarmBeige = Color(0xFFEDD5BD)
private val DarkNavy = Color(0xFF1A1A2E)
private val AccentPink = Color(0xFFE91E63)
private val WalkerBlue = Color(0xFF5C8EC6)
private val WalkerPurple = Color(0xFF3D2F6E)
private val MountainLight = Color(0xFFE0CDB7)
private val MountainDark = Color(0xFFCDBBA5)
private val MapBlue = Color(0xFF2962FF)

data class JournalActivity(
    val time: String,
    val title: String,
    val detail: String,
    val iconColor: Color
)

private val journalActivities = listOf(
    JournalActivity("10:42", "Morning Walk", "2km in 30mins", Color(0xFFFF8C00)),
    JournalActivity("07:15", "Yoga Session", "45mins", Color(0xFF4CAF50)),
    JournalActivity("14:30", "Running", "5km in 25mins", Color(0xFFE91E63)),
)

@Composable
fun JournalScreen(onBack: () -> Unit) {
    var currentPage by remember { mutableIntStateOf(0) }
    val inf = rememberInfiniteTransition(label = "journal")
    val walkCycle by inf.animateFloat(0f, 6.28f,
        infiniteRepeatable(tween(2000, easing = LinearEasing)), label = "walk")
    val breathe by inf.animateFloat(0.98f, 1.02f,
        infiniteRepeatable(tween(3000, easing = FastOutSlowInEasing), RepeatMode.Reverse), label = "breathe")

    Box(modifier = Modifier.fillMaxSize().background(CreamBg)) {
        when (currentPage) {
            0 -> JournalHomePage(walkCycle, breathe, onBack, onActivityClick = { currentPage = 1 })
            1 -> JournalDetailPage(walkCycle, onBack = { currentPage = 0 })
        }
    }
}

@Composable
private fun JournalHomePage(
    walkCycle: Float,
    breathe: Float,
    onBack: () -> Unit,
    onActivityClick: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize().padding(top = 48.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("JOURNAL", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = DarkNavy,
                letterSpacing = 2.sp)
            IconButton(onClick = onBack) {
                Icon(Icons.Filled.Menu, "menu", tint = DarkNavy)
            }
        }

        Spacer(Modifier.height(8.dp))

        // Date section
        Row(
            modifier = Modifier.padding(start = 24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Filled.ChevronLeft, "prev", tint = DarkNavy.copy(alpha = 0.5f),
                modifier = Modifier.size(20.dp))
            Text("13", fontSize = 72.sp, fontWeight = FontWeight.Bold, color = DarkNavy,
                modifier = Modifier.padding(start = 4.dp))
        }
        Text("July 2020", fontSize = 14.sp, color = DarkNavy.copy(alpha = 0.6f),
            modifier = Modifier.padding(start = 48.dp))

        Spacer(Modifier.height(8.dp))

        // Walking illustration with mountains
        Box(
            modifier = Modifier.fillMaxWidth().height(280.dp),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val w = size.width
                val h = size.height

                // Mountain silhouettes (background)
                val mountain1 = Path().apply {
                    moveTo(w * 0.1f, h * 0.8f)
                    lineTo(w * 0.35f, h * 0.25f)
                    lineTo(w * 0.6f, h * 0.8f)
                    close()
                }
                drawPath(mountain1, MountainLight.copy(alpha = 0.4f))

                val mountain2 = Path().apply {
                    moveTo(w * 0.35f, h * 0.8f)
                    lineTo(w * 0.55f, h * 0.3f)
                    lineTo(w * 0.8f, h * 0.8f)
                    close()
                }
                drawPath(mountain2, MountainDark.copy(alpha = 0.35f))

                val mountain3 = Path().apply {
                    moveTo(w * 0.5f, h * 0.8f)
                    lineTo(w * 0.75f, h * 0.2f)
                    lineTo(w * 1f, h * 0.8f)
                    close()
                }
                drawPath(mountain3, MountainLight.copy(alpha = 0.3f))

                // Ground line
                drawLine(
                    color = MountainDark.copy(alpha = 0.3f),
                    start = Offset(0f, h * 0.82f),
                    end = Offset(w, h * 0.82f),
                    strokeWidth = 2f
                )

                // Walking person
                val personX = w * 0.5f
                val personY = h * 0.45f
                val legSwing = sin(walkCycle) * 15f
                val armSwing = sin(walkCycle + PI.toFloat()) * 12f
                val bodyBob = sin(walkCycle * 2) * 3f

                // Shadow
                drawOval(
                    color = Color.Black.copy(alpha = 0.1f),
                    topLeft = Offset(personX - 30f, h * 0.78f),
                    size = Size(60f, 12f)
                )

                // Left leg
                drawLine(WalkerPurple, Offset(personX - 5f, personY + 60f + bodyBob),
                    Offset(personX - 15f + legSwing, personY + 110f), strokeWidth = 8f, cap = StrokeCap.Round)
                // Left foot
                drawCircle(Color(0xFF7C3AED), 6f, Offset(personX - 15f + legSwing, personY + 113f))

                // Right leg
                drawLine(WalkerPurple, Offset(personX + 5f, personY + 60f + bodyBob),
                    Offset(personX + 15f - legSwing, personY + 110f), strokeWidth = 8f, cap = StrokeCap.Round)
                // Right foot
                drawCircle(Color(0xFF7C3AED), 6f, Offset(personX + 15f - legSwing, personY + 113f))

                // Body (torso)
                drawLine(WalkerBlue, Offset(personX, personY + bodyBob),
                    Offset(personX, personY + 65f + bodyBob), strokeWidth = 14f, cap = StrokeCap.Round)

                // Left arm
                drawLine(WalkerBlue.copy(alpha = 0.9f), Offset(personX - 8f, personY + 15f + bodyBob),
                    Offset(personX - 25f + armSwing, personY + 50f + bodyBob), strokeWidth = 7f, cap = StrokeCap.Round)

                // Right arm
                drawLine(WalkerBlue.copy(alpha = 0.9f), Offset(personX + 8f, personY + 15f + bodyBob),
                    Offset(personX + 25f - armSwing, personY + 50f + bodyBob), strokeWidth = 7f, cap = StrokeCap.Round)

                // Head
                drawCircle(Color(0xFFFDBB9D), 18f, Offset(personX, personY - 12f + bodyBob))
                // Hair (blue-ish)
                val hairPath = Path().apply {
                    moveTo(personX - 18f, personY - 15f + bodyBob)
                    cubicTo(personX - 15f, personY - 35f + bodyBob,
                        personX + 15f, personY - 35f + bodyBob,
                        personX + 20f, personY - 10f + bodyBob)
                    cubicTo(personX + 22f, personY - 25f + bodyBob,
                        personX + 5f, personY - 40f + bodyBob,
                        personX - 18f, personY - 15f + bodyBob)
                }
                drawPath(hairPath, Color(0xFF64B5F6))
            }
        }

        Spacer(Modifier.height(16.dp))

        // Activity card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .clickable { onActivityClick() },
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(4.dp)
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Map thumbnail
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(MapBlue)
                ) {
                    Canvas(modifier = Modifier.fillMaxSize().padding(6.dp)) {
                        // Map route lines
                        val path = Path().apply {
                            moveTo(0f, size.height * 0.3f)
                            cubicTo(size.width * 0.3f, 0f,
                                size.width * 0.6f, size.height,
                                size.width, size.height * 0.5f)
                        }
                        drawPath(path, Color.White.copy(alpha = 0.6f),
                            style = Stroke(2f, cap = StrokeCap.Round))
                        // Route dots
                        drawCircle(Color.White, 3f, Offset(0f, size.height * 0.3f))
                        drawCircle(Color(0xFFFF5252), 3f, Offset(size.width, size.height * 0.5f))
                    }
                }

                Spacer(Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text("10:42", fontSize = 12.sp, color = DarkNavy.copy(alpha = 0.5f))
                    Text("Morning Walk", fontSize = 16.sp, fontWeight = FontWeight.SemiBold,
                        color = DarkNavy)
                    Text("2km in 30mins", fontSize = 12.sp, color = DarkNavy.copy(alpha = 0.5f))
                }

                // Walking icon
                Canvas(modifier = Modifier.size(24.dp)) {
                    val cx = size.width / 2
                    val cy = size.height / 2
                    // Head
                    drawCircle(Color(0xFFFF8C00), 4f, Offset(cx, cy - 8f))
                    // Body
                    drawLine(Color(0xFFFF8C00), Offset(cx, cy - 4f), Offset(cx, cy + 4f), 2f)
                    // Legs
                    drawLine(Color(0xFFFF8C00), Offset(cx, cy + 4f), Offset(cx - 4f, cy + 10f), 2f)
                    drawLine(Color(0xFFFF8C00), Offset(cx, cy + 4f), Offset(cx + 4f, cy + 10f), 2f)
                    // Arms
                    drawLine(Color(0xFFFF8C00), Offset(cx, cy - 2f), Offset(cx - 5f, cy + 3f), 2f)
                    drawLine(Color(0xFFFF8C00), Offset(cx, cy - 2f), Offset(cx + 5f, cy + 3f), 2f)
                }
            }
        }

        Spacer(Modifier.weight(1f))

        // Bottom navigation
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 48.dp, vertical = 24.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Chart icon
            IconButton(onClick = {}) {
                Icon(Icons.Filled.ShowChart, "stats", tint = DarkNavy.copy(alpha = 0.4f),
                    modifier = Modifier.size(28.dp))
            }
            // Clock icon (selected)
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(AccentPink.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Filled.Schedule, "history", tint = AccentPink,
                    modifier = Modifier.size(28.dp))
            }
            // Profile icon
            IconButton(onClick = {}) {
                Icon(Icons.Filled.PersonOutline, "profile", tint = DarkNavy.copy(alpha = 0.4f),
                    modifier = Modifier.size(28.dp))
            }
        }
    }
}

@Composable
private fun JournalDetailPage(walkCycle: Float, onBack: () -> Unit) {
    val inf = rememberInfiniteTransition(label = "detail")
    val pulse by inf.animateFloat(0.95f, 1.05f,
        infiniteRepeatable(tween(2000, easing = FastOutSlowInEasing), RepeatMode.Reverse), label = "pulse")

    Column(
        modifier = Modifier.fillMaxSize().padding(top = 48.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.Filled.ArrowBack, "back", tint = DarkNavy)
            }
            Spacer(Modifier.weight(1f))
            Text("Activity Details", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = DarkNavy)
            Spacer(Modifier.weight(1f))
            Spacer(Modifier.size(48.dp))
        }

        Spacer(Modifier.height(24.dp))

        // Map view
        Card(
            modifier = Modifier.fillMaxWidth().height(200.dp).padding(horizontal = 24.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MapBlue)
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                // Grid lines
                for (i in 0..8) {
                    val y = size.height * i / 8f
                    drawLine(Color.White.copy(alpha = 0.1f), Offset(0f, y), Offset(size.width, y), 1f)
                    val x = size.width * i / 8f
                    drawLine(Color.White.copy(alpha = 0.1f), Offset(x, 0f), Offset(x, size.height), 1f)
                }

                // Route path
                val route = Path().apply {
                    moveTo(size.width * 0.15f, size.height * 0.7f)
                    cubicTo(size.width * 0.25f, size.height * 0.3f,
                        size.width * 0.4f, size.height * 0.5f,
                        size.width * 0.55f, size.height * 0.25f)
                    cubicTo(size.width * 0.7f, size.height * 0.1f,
                        size.width * 0.8f, size.height * 0.4f,
                        size.width * 0.85f, size.height * 0.6f)
                }
                drawPath(route, Color.White, style = Stroke(4f, cap = StrokeCap.Round,
                    join = StrokeJoin.Round))

                // Start point
                drawCircle(Color(0xFF4CAF50), 8f, Offset(size.width * 0.15f, size.height * 0.7f))
                drawCircle(Color.White, 4f, Offset(size.width * 0.15f, size.height * 0.7f))

                // End point
                drawCircle(Color(0xFFFF5252), 8f, Offset(size.width * 0.85f, size.height * 0.6f))
                drawCircle(Color.White, 4f, Offset(size.width * 0.85f, size.height * 0.6f))
            }
        }

        Spacer(Modifier.height(24.dp))

        // Stats row
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            StatCard("Distance", "2.0 km", Icons.Filled.SocialDistance)
            StatCard("Duration", "30 min", Icons.Filled.Timer)
            StatCard("Calories", "120 kcal", Icons.Filled.LocalFireDepartment)
        }

        Spacer(Modifier.height(24.dp))

        // Activity history
        Text("Today's Activities", fontSize = 18.sp, fontWeight = FontWeight.Bold,
            color = DarkNavy, modifier = Modifier.padding(horizontal = 24.dp))
        Spacer(Modifier.height(12.dp))

        journalActivities.forEach { activity ->
            Card(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 4.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(activity.iconColor.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Filled.DirectionsWalk, "walk",
                            tint = activity.iconColor, modifier = Modifier.size(20.dp))
                    }
                    Spacer(Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(activity.title, fontSize = 14.sp, fontWeight = FontWeight.SemiBold,
                            color = DarkNavy)
                        Text(activity.detail, fontSize = 12.sp, color = DarkNavy.copy(alpha = 0.5f))
                    }
                    Text(activity.time, fontSize = 12.sp, color = DarkNavy.copy(alpha = 0.5f))
                }
            }
        }
    }
}

@Composable
private fun StatCard(label: String, value: String, icon: androidx.compose.ui.graphics.vector.ImageVector) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(3.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(icon, label, tint = AccentPink, modifier = Modifier.size(24.dp))
            Spacer(Modifier.height(8.dp))
            Text(value, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = DarkNavy)
            Text(label, fontSize = 11.sp, color = DarkNavy.copy(alpha = 0.5f))
        }
    }
}
