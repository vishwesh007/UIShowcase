package com.ui.animatedmenu.showcase

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.*

private val TravelOrange = Color(0xFFFF6B35)
private val TravelBlue = Color(0xFF1565C0)
private val TravelBg = Color(0xFFF8F9FE)
private val TravelDark = Color(0xFF1A1A2E)
private val TravelGray = Color(0xFF9E9E9E)
private val TravelWhite = Color(0xFFFFFFFF)

data class Destination(
    val name: String,
    val country: String,
    val price: String,
    val rating: Float,
    val days: Int,
    val hue1: Float,
    val hue2: Float
)

private val destinations = listOf(
    Destination("Santorini", "Greece", "$1,299", 4.8f, 7, 200f, 230f),
    Destination("Bali", "Indonesia", "$899", 4.7f, 5, 120f, 160f),
    Destination("Kyoto", "Japan", "$1,450", 4.9f, 6, 340f, 20f),
    Destination("Amalfi", "Italy", "$1,650", 4.6f, 8, 30f, 60f),
    Destination("Maldives", "Indian Ocean", "$2,100", 4.9f, 10, 180f, 200f),
    Destination("Patagonia", "Argentina", "$1,850", 4.7f, 9, 150f, 190f),
)

data class TravelCategory(val name: String, val emoji: String)

private val travelCategories = listOf(
    TravelCategory("Beach", "🏖"),
    TravelCategory("Mountain", "🏔"),
    TravelCategory("City", "🏙"),
    TravelCategory("Culture", "🏛"),
    TravelCategory("Adventure", "🧗"),
)

@Composable
fun TravelScreen(onBack: () -> Unit) {
    var selectedCat by remember { mutableIntStateOf(0) }
    var selectedDest by remember { mutableStateOf<Destination?>(null) }

    val inf = rememberInfiniteTransition(label = "trav")
    val wave by inf.animateFloat(
        0f, 6.28f,
        infiniteRepeatable(tween(3000, easing = LinearEasing)),
        label = "wave"
    )

    Box(modifier = Modifier.fillMaxSize().background(TravelBg)) {
        if (selectedDest != null) {
            // Detail view
            DestinationDetail(selectedDest!!, wave) { selectedDest = null }
        } else {
            // Home view
            LazyColumn(
                modifier = Modifier.fillMaxSize().statusBarsPadding(),
                contentPadding = PaddingValues(bottom = 80.dp)
            ) {
                item {
                    // Header
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(20.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = onBack) {
                            Icon(Icons.Filled.ArrowBack, "Back", tint = TravelDark)
                        }
                        Text("Explore", color = TravelDark, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                        Box(
                            modifier = Modifier.size(40.dp).clip(CircleShape)
                                .background(TravelOrange.copy(alpha = 0.1f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Filled.Search, null, tint = TravelOrange, modifier = Modifier.size(20.dp))
                        }
                    }
                }

                // Hero banner with animated gradient landscape
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp)
                            .height(180.dp)
                            .clip(RoundedCornerShape(24.dp))
                    ) {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            // Sunset sky gradient
                            drawRect(
                                Brush.verticalGradient(
                                    listOf(
                                        Color(0xFF1A237E),
                                        Color(0xFF5C6BC0),
                                        Color(0xFFFF8A65),
                                        Color(0xFFFFAB40)
                                    )
                                )
                            )

                            // Sun
                            val sunY = size.height * 0.55f + sin(wave * 0.5f).toFloat() * 5f
                            drawCircle(Color(0xFFFFD54F), radius = 30f, center = Offset(size.width * 0.7f, sunY))
                            drawCircle(Color(0xFFFFE082).copy(alpha = 0.3f), radius = 50f, center = Offset(size.width * 0.7f, sunY))

                            // Mountains
                            val mountainPath = Path().apply {
                                moveTo(0f, size.height)
                                lineTo(0f, size.height * 0.5f)
                                lineTo(size.width * 0.15f, size.height * 0.35f)
                                lineTo(size.width * 0.3f, size.height * 0.55f)
                                lineTo(size.width * 0.45f, size.height * 0.3f)
                                lineTo(size.width * 0.65f, size.height * 0.5f)
                                lineTo(size.width * 0.8f, size.height * 0.38f)
                                lineTo(size.width, size.height * 0.45f)
                                lineTo(size.width, size.height)
                                close()
                            }
                            drawPath(mountainPath, Color(0xFF1A1A2E).copy(alpha = 0.6f))

                            // Water with wave
                            val waterPath = Path().apply {
                                moveTo(0f, size.height)
                                for (x in 0..size.width.toInt() step 4) {
                                    val waveY = size.height * 0.72f + sin(wave + x * 0.03f).toFloat() * 4f
                                    lineTo(x.toFloat(), waveY)
                                }
                                lineTo(size.width, size.height)
                                close()
                            }
                            drawPath(
                                waterPath,
                                Brush.verticalGradient(
                                    listOf(Color(0xFF1565C0).copy(alpha = 0.7f), Color(0xFF0D47A1).copy(alpha = 0.9f))
                                )
                            )
                        }

                        // Text overlay
                        Column(
                            modifier = Modifier.align(Alignment.BottomStart).padding(20.dp)
                        ) {
                            Text("Discover", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                            Text("your next adventure", color = Color.White.copy(alpha = 0.8f), fontSize = 14.sp)
                        }
                    }
                    Spacer(Modifier.height(20.dp))
                }

                // Categories
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp)
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        travelCategories.forEachIndexed { idx, cat ->
                            val selected = idx == selectedCat
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(if (selected) TravelOrange else TravelWhite)
                                    .clickable { selectedCat = idx }
                                    .padding(horizontal = 16.dp, vertical = 10.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(cat.emoji, fontSize = 16.sp)
                                    Spacer(Modifier.width(6.dp))
                                    Text(
                                        cat.name,
                                        color = if (selected) Color.White else TravelDark,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }
                        }
                    }
                    Spacer(Modifier.height(20.dp))
                }

                // Popular destinations label
                item {
                    Text(
                        "Popular Destinations",
                        color = TravelDark,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 20.dp)
                    )
                    Spacer(Modifier.height(12.dp))
                }

                // Horizontal card carousel
                item {
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 20.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(destinations) { dest ->
                            DestinationCard(dest, wave) { selectedDest = dest }
                        }
                    }
                    Spacer(Modifier.height(24.dp))
                }

                // Trending section
                item {
                    Text(
                        "Trending Trips",
                        color = TravelDark,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 20.dp)
                    )
                    Spacer(Modifier.height(12.dp))
                }

                items(destinations.take(3)) { dest ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 4.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(TravelWhite)
                            .clickable { selectedDest = dest }
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Mini landscape canvas
                        Box(
                            modifier = Modifier.size(56.dp).clip(RoundedCornerShape(14.dp))
                        ) {
                            Canvas(modifier = Modifier.fillMaxSize()) {
                                drawRect(
                                    Brush.verticalGradient(
                                        listOf(
                                            Color.hsl(dest.hue1, 0.6f, 0.5f),
                                            Color.hsl(dest.hue2, 0.7f, 0.6f)
                                        )
                                    )
                                )
                                // Mini mountain
                                val mp = Path().apply {
                                    moveTo(0f, size.height)
                                    lineTo(size.width * 0.3f, size.height * 0.3f)
                                    lineTo(size.width * 0.6f, size.height * 0.5f)
                                    lineTo(size.width, size.height * 0.35f)
                                    lineTo(size.width, size.height)
                                    close()
                                }
                                drawPath(mp, Color.Black.copy(alpha = 0.3f))
                            }
                        }
                        Spacer(Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(dest.name, color = TravelDark, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
                            Text(
                                "${dest.country} • ${dest.days} days",
                                color = TravelGray, fontSize = 12.sp
                            )
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text(dest.price, color = TravelOrange, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Filled.Star, null, tint = Color(0xFFFFB300), modifier = Modifier.size(14.dp))
                                Text("${dest.rating}", color = TravelDark, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                            }
                        }
                    }
                }
            }
        }

        // Bottom nav
        if (selectedDest == null) {
            Row(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                    .background(TravelWhite)
                    .padding(horizontal = 24.dp, vertical = 12.dp)
                    .navigationBarsPadding(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                listOf(
                    Pair(Icons.Filled.Explore, "Explore"),
                    Pair(Icons.Filled.FavoriteBorder, "Saved"),
                    Pair(Icons.Filled.CalendarMonth, "Trips"),
                    Pair(Icons.Filled.Person, "Profile"),
                ).forEachIndexed { i, (icon, label) ->
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(icon, null, tint = if (i == 0) TravelOrange else TravelGray, modifier = Modifier.size(24.dp))
                        Text(label, color = if (i == 0) TravelOrange else TravelGray, fontSize = 11.sp)
                    }
                }
            }
        }
    }
}

@Composable
private fun DestinationCard(dest: Destination, wave: Float, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .width(200.dp)
            .height(260.dp)
            .clip(RoundedCornerShape(20.dp))
            .clickable { onClick() }
    ) {
        // Landscape background
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawRect(
                Brush.verticalGradient(
                    listOf(
                        Color.hsl(dest.hue1, 0.65f, 0.45f),
                        Color.hsl(dest.hue2, 0.7f, 0.55f),
                        Color.hsl(dest.hue2, 0.6f, 0.7f)
                    )
                )
            )

            // Clouds
            for (i in 0..2) {
                val cx = (size.width * (0.2f + i * 0.3f) + sin(wave + i).toFloat() * 10f)
                val cy = size.height * (0.15f + i * 0.08f)
                drawCircle(Color.White.copy(alpha = 0.25f), radius = 18f, center = Offset(cx, cy))
                drawCircle(Color.White.copy(alpha = 0.2f), radius = 14f, center = Offset(cx + 16, cy - 4))
                drawCircle(Color.White.copy(alpha = 0.2f), radius = 12f, center = Offset(cx - 14, cy + 2))
            }

            // Mountain silhouette
            val mPath = Path().apply {
                moveTo(0f, size.height)
                lineTo(size.width * 0.1f, size.height * 0.5f)
                lineTo(size.width * 0.25f, size.height * 0.35f)
                lineTo(size.width * 0.4f, size.height * 0.55f)
                lineTo(size.width * 0.55f, size.height * 0.3f)
                lineTo(size.width * 0.7f, size.height * 0.45f)
                lineTo(size.width * 0.85f, size.height * 0.38f)
                lineTo(size.width, size.height * 0.52f)
                lineTo(size.width, size.height)
                close()
            }
            drawPath(mPath, Color.Black.copy(alpha = 0.25f))

            // Water
            val wp = Path().apply {
                moveTo(0f, size.height)
                for (x in 0..size.width.toInt() step 3) {
                    val wy = size.height * 0.7f + sin(wave * 1.5f + x * 0.04f).toFloat() * 3f
                    lineTo(x.toFloat(), wy)
                }
                lineTo(size.width, size.height)
                close()
            }
            drawPath(wp, Color.White.copy(alpha = 0.15f))
        }

        // Gradient overlay at bottom
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(Color.Transparent, Color.Black.copy(alpha = 0.6f)),
                        startY = 200f
                    )
                )
        )

        // Info
        Column(
            modifier = Modifier.align(Alignment.BottomStart).padding(16.dp)
        ) {
            Text(dest.name, color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Text(dest.country, color = Color.White.copy(alpha = 0.8f), fontSize = 12.sp)
            Spacer(Modifier.height(6.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Filled.Star, null, tint = Color(0xFFFFB300), modifier = Modifier.size(14.dp))
                Spacer(Modifier.width(4.dp))
                Text("${dest.rating}", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Spacer(Modifier.width(8.dp))
                Text(dest.price, color = TravelOrange, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            }
        }

        // Bookmark
        Box(
            modifier = Modifier.align(Alignment.TopEnd).padding(12.dp)
                .size(32.dp).clip(CircleShape)
                .background(Color.White.copy(alpha = 0.2f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Filled.FavoriteBorder, null, tint = Color.White, modifier = Modifier.size(16.dp))
        }
    }
}

@Composable
private fun DestinationDetail(dest: Destination, wave: Float, onClose: () -> Unit) {
    val enterAnim = remember { Animatable(0f) }
    LaunchedEffect(Unit) { enterAnim.animateTo(1f, tween(500, easing = FastOutSlowInEasing)) }

    Column(
        modifier = Modifier.fillMaxSize().statusBarsPadding()
            .graphicsLayer { alpha = enterAnim.value; translationY = (1f - enterAnim.value) * 100f }
    ) {
        // Large landscape
        Box(
            modifier = Modifier.fillMaxWidth().height(300.dp)
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                drawRect(
                    Brush.verticalGradient(
                        listOf(
                            Color.hsl(dest.hue1, 0.7f, 0.4f),
                            Color.hsl(dest.hue1, 0.6f, 0.55f),
                            Color.hsl(dest.hue2, 0.65f, 0.6f),
                            Color.hsl(dest.hue2, 0.5f, 0.75f)
                        )
                    )
                )

                // Sun
                val sunY = size.height * 0.3f + sin(wave * 0.3f).toFloat() * 8f
                drawCircle(Color(0xFFFFD54F), 45f, Offset(size.width * 0.75f, sunY))
                drawCircle(Color(0xFFFFE082).copy(alpha = 0.2f), 70f, Offset(size.width * 0.75f, sunY))

                // Clouds
                for (i in 0..4) {
                    val cx = ((i * size.width / 4) + sin(wave + i * 0.7f).toFloat() * 15f) % size.width
                    val cy = size.height * (0.1f + i * 0.06f)
                    drawCircle(Color.White.copy(alpha = 0.3f), 22f, Offset(cx, cy))
                    drawCircle(Color.White.copy(alpha = 0.25f), 16f, Offset(cx + 20, cy - 5))
                }

                // Mountains
                val mp = Path().apply {
                    moveTo(0f, size.height)
                    lineTo(0f, size.height * 0.4f)
                    lineTo(size.width * 0.2f, size.height * 0.25f)
                    lineTo(size.width * 0.35f, size.height * 0.45f)
                    lineTo(size.width * 0.5f, size.height * 0.2f)
                    lineTo(size.width * 0.7f, size.height * 0.4f)
                    lineTo(size.width * 0.85f, size.height * 0.3f)
                    lineTo(size.width, size.height * 0.42f)
                    lineTo(size.width, size.height)
                    close()
                }
                drawPath(mp, Color(0xFF1A1A2E).copy(alpha = 0.5f))

                // Water
                val waterP = Path().apply {
                    moveTo(0f, size.height)
                    for (x in 0..size.width.toInt() step 3) {
                        val wy = size.height * 0.65f + sin(wave + x * 0.02f).toFloat() * 6f
                        lineTo(x.toFloat(), wy)
                    }
                    lineTo(size.width, size.height)
                    close()
                }
                drawPath(waterP, Brush.verticalGradient(
                    listOf(Color(0xFF1565C0).copy(alpha = 0.5f), Color(0xFF0D47A1).copy(alpha = 0.7f))
                ))
            }

            // Back button
            IconButton(
                onClick = onClose,
                modifier = Modifier.padding(16.dp).align(Alignment.TopStart)
                    .size(40.dp).clip(CircleShape).background(Color.Black.copy(alpha = 0.3f))
            ) {
                Icon(Icons.Filled.ArrowBack, "Back", tint = Color.White)
            }
        }

        // Details
        Column(
            modifier = Modifier.fillMaxWidth()
                .offset(y = (-24).dp)
                .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                .background(TravelBg)
                .padding(24.dp)
                .weight(1f)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(dest.name, color = TravelDark, fontSize = 26.sp, fontWeight = FontWeight.Bold)
                    Text(dest.country, color = TravelGray, fontSize = 14.sp)
                }
                Text(dest.price, color = TravelOrange, fontSize = 24.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(Modifier.height(16.dp))

            // Stats row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                listOf(
                    Triple(Icons.Filled.Star, "${dest.rating}", "Rating"),
                    Triple(Icons.Filled.CalendarMonth, "${dest.days} days", "Duration"),
                    Triple(Icons.Filled.Flight, "Direct", "Flight"),
                ).forEach { (icon, value, label) ->
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(icon, null, tint = TravelOrange, modifier = Modifier.size(22.dp))
                        Text(value, color = TravelDark, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        Text(label, color = TravelGray, fontSize = 11.sp)
                    }
                }
            }

            Spacer(Modifier.height(20.dp))

            Text(
                "Experience the breathtaking beauty of ${dest.name}, ${dest.country}. " +
                "This ${dest.days}-day journey takes you through stunning landscapes, " +
                "rich culture, and unforgettable adventures.",
                color = TravelDark.copy(alpha = 0.7f),
                fontSize = 14.sp,
                lineHeight = 22.sp
            )

            Spacer(Modifier.weight(1f))

            Button(
                onClick = {},
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = TravelOrange)
            ) {
                Text("Book Now", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}
