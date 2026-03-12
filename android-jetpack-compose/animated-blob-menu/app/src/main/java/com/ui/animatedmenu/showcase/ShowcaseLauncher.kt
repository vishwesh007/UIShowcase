package com.ui.animatedmenu.showcase

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.sin

data class ShowcaseItem(
    val title: String,
    val subtitle: String,
    val icon: ImageVector,
    val gradientStart: Color,
    val gradientEnd: Color
)

val showcaseItems = listOf(
    ShowcaseItem("Gauge / Noise", "Motion Design", Icons.Filled.Speed, Color(0xFFE53935), Color(0xFF1A1A1A)),
    ShowcaseItem("E-Commerce", "Sunglasses Store", Icons.Filled.ShoppingBag, Color(0xFF2E7D32), Color(0xFFC8E6C9)),
    ShowcaseItem("Timer", "Analog Clock", Icons.Filled.Timer, Color(0xFFE8624A), Color(0xFFF5F0EB)),
    ShowcaseItem("Delivery", "Shipment Tracker", Icons.Filled.LocalShipping, Color(0xFF2196F3), Color(0xFFE3F2FD)),
    ShowcaseItem("Weather", "Sky Dashboard", Icons.Filled.WbSunny, Color(0xFF1565C0), Color(0xFF4FC3F7)),
    ShowcaseItem("Music", "Vinyl Player", Icons.Filled.MusicNote, Color(0xFFBB86FC), Color(0xFF121212)),
    ShowcaseItem("Fitness", "Activity Rings", Icons.Filled.FitnessCenter, Color(0xFFFF453A), Color(0xFF0A0A0A)),
    ShowcaseItem("Chat", "Messaging UI", Icons.Filled.Chat, Color(0xFF6C63FF), Color(0xFFF8F9FA)),
    ShowcaseItem("Profile", "Social Media", Icons.Filled.Person, Color(0xFF7C4DFF), Color(0xFFFF4081)),
    ShowcaseItem("Smart Home", "IoT Controls", Icons.Filled.Home, Color(0xFF00BCD4), Color(0xFF101820)),
    ShowcaseItem("Nike Product", "Hover Animation", Icons.Filled.ShoppingCart, Color(0xFFFA5B30), Color(0xFFF5F5F5)),
    ShowcaseItem("Confetti", "Celebration FX", Icons.Filled.Celebration, Color(0xFF673AB7), Color(0xFFFFC107)),
)

@Composable
fun ShowcaseLauncher(onNavigate: (Int) -> Unit, onBack: () -> Unit) {
    val inf = rememberInfiniteTransition(label = "bg")
    val bgFloat by inf.animateFloat(0f, 1f,
        infiniteRepeatable(tween(8000, easing = FastOutSlowInEasing), RepeatMode.Reverse), label = "bf")
    val shimmer by inf.animateFloat(0f, 6.28f,
        infiniteRepeatable(tween(4000, easing = LinearEasing)), label = "sh")

    Box(modifier = Modifier.fillMaxSize()
        .background(Brush.verticalGradient(listOf(Color(0xFF0D1117), Color(0xFF161B22))))) {

        // Animated background particles
        Canvas(modifier = Modifier.fillMaxSize()) {
            for (i in 0..20) {
                val x = (i * 97 + 50) % size.width.toInt()
                val baseY = (i * 113 + 30) % size.height.toInt()
                val y = baseY + sin(shimmer + i * 0.8f).toFloat() * 15f
                val alpha = 0.03f + (sin(shimmer + i * 1.2f).toFloat() + 1f) * 0.03f
                val r = (8 + i % 5 * 6).toFloat()
                drawCircle(Color(0xFF7C4DFF).copy(alpha = alpha), radius = r,
                    center = Offset(x.toFloat(), y))
            }
        }

        Column(modifier = Modifier.fillMaxSize().statusBarsPadding()) {
            // Header with animated gradient text background
            Row(modifier = Modifier.fillMaxWidth().padding(16.dp),
                verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack) {
                    Icon(Icons.Filled.ArrowBack, "Back", tint = Color.White)
                }
                Column(modifier = Modifier.padding(start = 8.dp)) {
                    Text("UI Showcase", color = Color.White, fontSize = 24.sp,
                        fontWeight = FontWeight.Bold, letterSpacing = (-0.5).sp)
                    Text("10 Dribbble-Inspired Designs", color = Color(0xFF7C4DFF),
                        fontSize = 13.sp, fontWeight = FontWeight.Medium, letterSpacing = 0.5.sp)
                }
                Spacer(Modifier.weight(1f))
                Box(modifier = Modifier.size(40.dp).clip(CircleShape)
                    .background(Color(0xFF7C4DFF).copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center) {
                    Text("10", color = Color(0xFF7C4DFF), fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
            }

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                itemsIndexed(showcaseItems) { index, item ->
                    ShowcaseCard(item = item, index = index + 1, globalPhase = shimmer,
                        onClick = { onNavigate(if (index >= 10) index + 1 else index) })
                }
            }
        }
    }
}

@Composable
private fun ShowcaseCard(item: ShowcaseItem, index: Int, globalPhase: Float, onClick: () -> Unit) {
    // Staggered entrance animation
    val enterAnim = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay((index - 1) * 80L)
        enterAnim.animateTo(1f, tween(500, easing = FastOutSlowInEasing))
    }

    val cardFloat = sin(globalPhase + index * 0.7f).toFloat() * 3f

    Box(modifier = Modifier.fillMaxWidth().aspectRatio(0.85f)
        .graphicsLayer {
            alpha = enterAnim.value
            translationY = (1f - enterAnim.value) * 60f + cardFloat
            scaleX = 0.9f + enterAnim.value * 0.1f
            scaleY = 0.9f + enterAnim.value * 0.1f
        }
        .clip(RoundedCornerShape(20.dp))
        .background(Brush.verticalGradient(listOf(
            item.gradientStart.copy(alpha = 0.25f),
            item.gradientEnd.copy(alpha = 0.15f))))
        .clickable { onClick() }
        .padding(16.dp)
    ) {
        // Subtle inner glow
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawCircle(item.gradientStart.copy(alpha = 0.08f), radius = size.width * 0.5f,
                center = Offset(size.width * 0.8f, size.height * 0.2f))
        }

        Column(modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween) {
            Row(modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(48.dp).clip(CircleShape)
                    .background(item.gradientStart.copy(alpha = 0.25f)),
                    contentAlignment = Alignment.Center) {
                    Icon(item.icon, null, tint = item.gradientStart, modifier = Modifier.size(24.dp))
                }
                Box(modifier = Modifier.clip(RoundedCornerShape(8.dp))
                    .background(Color.White.copy(alpha = 0.06f))
                    .padding(horizontal = 8.dp, vertical = 3.dp)) {
                    Text("#$index", color = Color.White.copy(alpha = 0.5f),
                        fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }

            Column {
                Text(item.title, color = Color.White, fontSize = 16.sp,
                    fontWeight = FontWeight.Bold, letterSpacing = (-0.3).sp)
                Spacer(Modifier.height(2.dp))
                Text(item.subtitle, color = Color.White.copy(alpha = 0.5f), fontSize = 12.sp,
                    letterSpacing = 0.3.sp)
                Spacer(Modifier.height(8.dp))
                // Mini progress bar decoration
                Box(modifier = Modifier.fillMaxWidth(0.4f).height(2.dp)
                    .clip(RoundedCornerShape(1.dp))
                    .background(Brush.horizontalGradient(listOf(item.gradientStart, item.gradientStart.copy(alpha = 0.2f)))))
            }
        }
    }
}
