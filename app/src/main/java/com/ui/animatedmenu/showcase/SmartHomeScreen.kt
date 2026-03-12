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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.sin

private data class DeviceState(val name: String, val icon: ImageVector, val isOn: Boolean,
                               val color: Color, val subtitle: String = "")

@Composable
fun SmartHomeScreen(onBack: () -> Unit = {}) {
    val Bg = Color(0xFF0A1628)
    val Surface = Color(0xFF132039)
    val SurfaceLight = Color(0xFF1A2D50)
    val Cyan = Color(0xFF00E5FF)
    val Amber = Color(0xFFFFAB00)
    val Pink = Color(0xFFFF4081)
    val Green = Color(0xFF00E676)
    val Purple = Color(0xFF7C4DFF)
    val TextPrimary = Color(0xFFF5F5F5)
    val TextSecondary = Color(0xFF6B7FA3)

    var devices by remember {
        mutableStateOf(listOf(
            DeviceState("Living Light", Icons.Filled.LightMode, true, Amber, "85%"),
            DeviceState("Air Conditioner", Icons.Filled.AcUnit, true, Cyan, "22°C"),
            DeviceState("Speaker", Icons.Filled.Speaker, false, Pink, "Off"),
            DeviceState("Smart TV", Icons.Filled.Tv, true, Purple, "Netflix"),
            DeviceState("Security", Icons.Filled.CameraAlt, true, Green, "Active"),
            DeviceState("Thermostat", Icons.Filled.Thermostat, false, Color(0xFFFF5722), "Off"),
        ))
    }

    val inf = rememberInfiniteTransition(label = "home")
    val tempAnim by inf.animateFloat(21.5f, 23.5f,
        infiniteRepeatable(tween(5000, easing = FastOutSlowInEasing), RepeatMode.Reverse), label = "tp")
    val glowPulse by inf.animateFloat(0.3f, 1f,
        infiniteRepeatable(tween(3000, easing = FastOutSlowInEasing), RepeatMode.Reverse), label = "gp")
    val energyWave by inf.animateFloat(0f, 6.28f,
        infiniteRepeatable(tween(4000, easing = LinearEasing)), label = "ew")

    Box(modifier = Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(Bg, Color(0xFF061020))))) {
        // Background glow
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawCircle(Cyan.copy(alpha = 0.03f * glowPulse), 300f,
                Offset(size.width * 0.8f, size.height * 0.15f))
            drawCircle(Purple.copy(alpha = 0.03f * glowPulse), 250f,
                Offset(size.width * 0.2f, size.height * 0.6f))
        }

        Column(modifier = Modifier.fillMaxSize().statusBarsPadding()) {
            // Top bar
            Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack) { Icon(Icons.Filled.ArrowBack, "Back", tint = TextPrimary) }
                Column(modifier = Modifier.weight(1f).padding(start = 4.dp)) {
                    Text("Good Evening", color = TextSecondary, fontSize = 12.sp)
                    Text("Smart Home", color = TextPrimary, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                }
                Box(modifier = Modifier.size(40.dp).clip(CircleShape).background(Surface),
                    contentAlignment = Alignment.Center) {
                    Icon(Icons.Filled.Notifications, null, tint = Cyan, modifier = Modifier.size(18.dp))
                    // Notification dot
                    Box(modifier = Modifier.align(Alignment.TopEnd).offset(x = (-6).dp, y = 6.dp)
                        .size(8.dp).clip(CircleShape).background(Pink))
                }
            }

            Spacer(Modifier.height(12.dp))

            // Temperature card with energy visualization
            Box(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(Brush.linearGradient(listOf(
                    Cyan.copy(alpha = 0.12f), Surface, Purple.copy(alpha = 0.08f)
                )))) {
                // Energy wave at bottom
                Canvas(modifier = Modifier.fillMaxWidth().height(100.dp).align(Alignment.BottomCenter)) {
                    val w = size.width; val h = size.height
                    for (x in 0..w.toInt() step 3) {
                        val y = h * 0.6f + sin(energyWave + x / w * 12f) * 8f
                        drawCircle(Cyan.copy(alpha = 0.05f), 2f, Offset(x.toFloat(), y))
                    }
                }

                Row(modifier = Modifier.fillMaxWidth().padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Room Temperature", color = TextSecondary, fontSize = 12.sp)
                        Spacer(Modifier.height(4.dp))
                        Row(verticalAlignment = Alignment.Top) {
                            Text("%.1f".format(tempAnim), color = TextPrimary,
                                fontSize = 48.sp, fontWeight = FontWeight.Bold, letterSpacing = (-2).sp)
                            Text("°C", color = Cyan, fontSize = 16.sp,
                                fontWeight = FontWeight.Medium, modifier = Modifier.padding(top = 12.dp))
                        }
                        Spacer(Modifier.height(4.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(Green))
                            Spacer(Modifier.width(6.dp))
                            Text("Humidity 45%  •  Good", color = TextSecondary, fontSize = 11.sp)
                        }
                    }
                    // Circular temp indicator
                    Box(modifier = Modifier.size(72.dp), contentAlignment = Alignment.Center) {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val cx = size.width / 2; val cy = size.height / 2
                            drawCircle(Cyan.copy(alpha = 0.1f), size.width / 2 - 2f, Offset(cx, cy))
                            drawArc(Cyan, -90f, 280f,  false,
                                Offset(4f, 4f), androidx.compose.ui.geometry.Size(size.width - 8f, size.height - 8f),
                                style = androidx.compose.ui.graphics.drawscope.Stroke(4f,
                                    cap = androidx.compose.ui.graphics.StrokeCap.Round))
                        }
                        Icon(Icons.Filled.Thermostat, null, tint = Cyan, modifier = Modifier.size(28.dp))
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            // Room selector
            Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                listOf("Living" to true, "Bedroom" to false, "Kitchen" to false, "Office" to false).forEach { (room, sel) ->
                    Box(modifier = Modifier.clip(RoundedCornerShape(12.dp))
                        .background(if (sel) Cyan else Surface)
                        .padding(horizontal = 16.dp, vertical = 8.dp)) {
                        Text(room, color = if (sel) Bg else TextSecondary,
                            fontSize = 12.sp, fontWeight = if (sel) FontWeight.SemiBold else FontWeight.Normal)
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically) {
                Text("Devices", color = TextPrimary, fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
                Spacer(Modifier.weight(1f))
                Text("${devices.count { it.isOn }} active", color = Cyan, fontSize = 12.sp)
            }

            Spacer(Modifier.height(10.dp))

            // Device grid
            Column(modifier = Modifier.padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)) {
                for (row in devices.chunked(2)) {
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        for (device in row) {
                            SmartDeviceCard2(device, onToggle = {
                                val idx = devices.indexOf(device)
                                if (idx >= 0) devices = devices.toMutableList().apply {
                                    set(idx, device.copy(isOn = !device.isOn))
                                }
                            }, glowPulse = glowPulse, modifier = Modifier.weight(1f))
                        }
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            // Energy usage bar
            Box(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
                .clip(RoundedCornerShape(16.dp)).background(Surface).padding(14.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.FlashOn, null, tint = Amber, modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(10.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Energy Usage", color = TextSecondary, fontSize = 11.sp)
                        Text("2.4 kWh today", color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                    }
                    Box(modifier = Modifier.width(80.dp).height(6.dp)
                        .clip(RoundedCornerShape(3.dp)).background(SurfaceLight)) {
                        Box(modifier = Modifier.fillMaxWidth(0.6f).fillMaxHeight()
                            .background(Brush.horizontalGradient(listOf(Green, Cyan))))
                    }
                }
            }
        }
    }
}

@Composable
private fun SmartDeviceCard2(device: DeviceState, onToggle: () -> Unit,
                             glowPulse: Float, modifier: Modifier) {
    val scaleAnim by animateFloatAsState(
        if (device.isOn) 1f else 0.97f,
        spring(Spring.DampingRatioMediumBouncy), label = "sc")

    Box(modifier = modifier.scale(scaleAnim).clip(RoundedCornerShape(20.dp))
        .background(if (device.isOn) device.color.copy(alpha = 0.1f) else Color(0xFF132039))
        .clickable { onToggle() }.padding(16.dp)) {

        // Subtle glow when on
        if (device.isOn) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                drawCircle(device.color.copy(alpha = 0.06f * glowPulse), 60f,
                    Offset(size.width * 0.8f, size.height * 0.2f))
            }
        }

        Column {
            Row(modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(42.dp).clip(RoundedCornerShape(12.dp))
                    .background(if (device.isOn) device.color.copy(alpha = 0.2f) else Color(0xFF1A2D50)),
                    contentAlignment = Alignment.Center) {
                    Icon(device.icon, null, tint = if (device.isOn) device.color else Color(0xFF4A5F80),
                        modifier = Modifier.size(20.dp))
                }
                // Toggle
                Box(modifier = Modifier.width(42.dp).height(24.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (device.isOn) device.color else Color(0xFF1A2D50)),
                    contentAlignment = if (device.isOn) Alignment.CenterEnd else Alignment.CenterStart) {
                    Box(modifier = Modifier.padding(2.dp).size(20.dp).clip(CircleShape)
                        .background(if (device.isOn) Color.White else Color(0xFF3A5070)))
                }
            }
            Spacer(Modifier.height(14.dp))
            Text(device.name, color = if (device.isOn) Color.White else Color(0xFF4A5F80),
                fontSize = 13.sp, fontWeight = FontWeight.Medium)
            Text(device.subtitle, color = if (device.isOn) device.color else Color(0xFF2A3F60),
                fontSize = 11.sp)
        }
    }
}
