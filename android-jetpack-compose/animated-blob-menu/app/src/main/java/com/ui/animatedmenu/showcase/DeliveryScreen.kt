package com.ui.animatedmenu.showcase

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun DeliveryScreen(onBack: () -> Unit = {}) {
    val Bg = Color(0xFFF0F4FF)
    val Blue = Color(0xFF2962FF)
    val BlueDark = Color(0xFF1A46B5)
    val Green = Color(0xFF00C853)
    val Surface = Color(0xFFFFFFFF)
    val TextDark = Color(0xFF1A1A1A)
    val TextMuted = Color(0xFF9E9E9E)

    val inf = rememberInfiniteTransition(label = "del")
    val pulseSize by inf.animateFloat(0.7f, 1.2f,
        infiniteRepeatable(tween(1500), RepeatMode.Reverse), label = "ps")
    val truckPos by inf.animateFloat(0.2f, 0.75f,
        infiniteRepeatable(tween(4000, easing = FastOutSlowInEasing), RepeatMode.Reverse), label = "tp")
    val dotPhase by inf.animateFloat(0f, 1f,
        infiniteRepeatable(tween(2000, easing = LinearEasing)), label = "dp")

    val steps = listOf(
        Triple("Order Placed", "Jan 15, 10:00 AM", true),
        Triple("Picked Up", "Jan 15, 2:30 PM", true),
        Triple("In Transit", "Expected Jan 16", true),
        Triple("Delivered", "Pending", false),
    )

    Column(modifier = Modifier.fillMaxSize().background(Bg).statusBarsPadding()) {
        // Top bar
        Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) { Icon(Icons.Filled.ArrowBack, "Back", tint = TextDark) }
            Spacer(Modifier.weight(1f))
            Text("TRACK ORDER", color = TextDark, fontSize = 12.sp,
                fontWeight = FontWeight.Medium, letterSpacing = 2.sp)
            Spacer(Modifier.weight(1f))
            IconButton(onClick = {}) { Icon(Icons.Filled.Notifications, null, tint = TextMuted) }
        }

        // Map with animated route
        Box(modifier = Modifier.fillMaxWidth().height(180.dp).padding(horizontal = 16.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(Brush.linearGradient(listOf(Blue.copy(alpha = 0.08f), Blue.copy(alpha = 0.15f))))) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val w = size.width; val h = size.height
                // Grid lines
                for (i in 1..8) {
                    drawLine(Color(0xFF2962FF).copy(alpha = 0.04f),
                        Offset(w * i / 9f, 0f), Offset(w * i / 9f, h), strokeWidth = 1f)
                    drawLine(Color(0xFF2962FF).copy(alpha = 0.04f),
                        Offset(0f, h * i / 6f), Offset(w, h * i / 6f), strokeWidth = 1f)
                }
                // Route path
                val routeY = h * 0.5f
                drawLine(Blue.copy(alpha = 0.3f),
                    Offset(w * 0.1f, routeY), Offset(w * 0.9f, routeY),
                    strokeWidth = 3f, cap = StrokeCap.Round,
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 8f), dotPhase * 20f))
                // Start point
                drawCircle(Green, 8f, Offset(w * 0.1f, routeY))
                drawCircle(Color.White, 4f, Offset(w * 0.1f, routeY))
                // End point
                drawCircle(Blue, 8f, Offset(w * 0.9f, routeY))
                drawCircle(Color.White, 4f, Offset(w * 0.9f, routeY))
                // Truck position
                val truckX = w * (0.1f + truckPos * 0.8f)
                drawCircle(Blue.copy(alpha = 0.15f * pulseSize), 24f, Offset(truckX, routeY))
                drawCircle(Blue, 10f, Offset(truckX, routeY))
                drawCircle(Color.White, 5f, Offset(truckX, routeY))
            }

            // Labels
            Text("Warehouse", color = Blue.copy(alpha = 0.5f), fontSize = 10.sp,
                modifier = Modifier.align(Alignment.BottomStart).padding(start = 20.dp, bottom = 12.dp))
            Text("Your Address", color = Blue.copy(alpha = 0.5f), fontSize = 10.sp,
                modifier = Modifier.align(Alignment.BottomEnd).padding(end = 20.dp, bottom = 12.dp))
        }

        Spacer(Modifier.height(16.dp))

        // Package info card
        Box(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
            .clip(RoundedCornerShape(20.dp)).background(Surface).padding(18.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(48.dp).clip(RoundedCornerShape(14.dp))
                    .background(Blue.copy(alpha = 0.1f)), contentAlignment = Alignment.Center) {
                    Icon(Icons.Filled.LocalShipping, null, tint = Blue, modifier = Modifier.size(24.dp))
                }
                Spacer(Modifier.width(14.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text("Package #TRK-4829", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = TextDark)
                    Spacer(Modifier.height(2.dp))
                    Text("Express Delivery • Est. 2h", color = TextMuted, fontSize = 12.sp)
                }
                Box(modifier = Modifier.clip(RoundedCornerShape(8.dp))
                    .background(Green.copy(alpha = 0.1f))
                    .padding(horizontal = 10.dp, vertical = 4.dp)) {
                    Text("In Transit", color = Green, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        // Progress steps
        Box(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
            .clip(RoundedCornerShape(20.dp)).background(Surface).padding(18.dp)) {
            Column {
                Text("Tracking Details", color = TextDark, fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
                Spacer(Modifier.height(16.dp))

                steps.forEachIndexed { index, (label, subtitle, done) ->
                    val stepEntry = remember { Animatable(0f) }
                    LaunchedEffect(Unit) {
                        kotlinx.coroutines.delay(index * 250L + 400L)
                        stepEntry.animateTo(1f, spring(dampingRatio = 0.7f, stiffness = 300f))
                    }
                    Row(verticalAlignment = Alignment.Top,
                        modifier = Modifier.graphicsLayer {
                            alpha = stepEntry.value
                            translationX = (1f - stepEntry.value) * -40f
                        }) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Box(modifier = Modifier.size(28.dp).clip(CircleShape)
                                .background(if (done) Blue else Color(0xFFE0E0E0)),
                                contentAlignment = Alignment.Center) {
                                if (done) Icon(Icons.Filled.Check, null, tint = Color.White,
                                    modifier = Modifier.size(14.dp))
                                else Text("${index + 1}", color = TextMuted, fontSize = 11.sp)
                            }
                            if (index < steps.lastIndex) {
                                Box(modifier = Modifier.width(2.dp).height(36.dp)
                                    .background(if (done) Blue.copy(alpha = 0.3f) else Color(0xFFE0E0E0)))
                            }
                        }
                        Spacer(Modifier.width(14.dp))
                        Column {
                            Text(label, fontWeight = if (done) FontWeight.SemiBold else FontWeight.Normal,
                                color = if (done) TextDark else TextMuted, fontSize = 14.sp)
                            Text(subtitle, color = if (done && index == 2) Blue else TextMuted, fontSize = 11.sp)
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(14.dp))

        // Info cards
        Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            DeliveryInfo2("Distance", "12.4 km", Icons.Filled.NearMe, Blue, Modifier.weight(1f))
            DeliveryInfo2("Time Left", "~2 hrs", Icons.Filled.AccessTime, Green, Modifier.weight(1f))
            DeliveryInfo2("Weight", "2.3 kg", Icons.Filled.FitnessCenter, Color(0xFFFF6D00), Modifier.weight(1f))
        }
    }
}

@Composable
private fun DeliveryInfo2(label: String, value: String,
                          icon: androidx.compose.ui.graphics.vector.ImageVector,
                          color: Color, modifier: Modifier) {
    Box(modifier = modifier.clip(RoundedCornerShape(16.dp))
        .background(Color.White).padding(14.dp)) {
        Column(horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()) {
            Box(modifier = Modifier.size(36.dp).clip(RoundedCornerShape(10.dp))
                .background(color.copy(alpha = 0.1f)), contentAlignment = Alignment.Center) {
                Icon(icon, null, tint = color, modifier = Modifier.size(18.dp))
            }
            Spacer(Modifier.height(8.dp))
            Text(value, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Color(0xFF1A1A1A))
            Text(label, color = Color(0xFF9E9E9E), fontSize = 11.sp)
        }
    }
}
