package com.ui.animatedmenu.showcase

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.sin

private data class MovieItem(
    val title: String,
    val year: String,
    val duration: String,
    val color1: Color,
    val color2: Color,
    val rating: Float = 4.5f
)

private data class BrandTab(
    val name: String,
    val color: Color
)

private val brands = listOf(
    BrandTab("Disney", Color(0xFF1A73E8)),
    BrandTab("Nat Geo", Color(0xFFFFD600)),
    BrandTab("Star Wars", Color(0xFFFFFFFF)),
    BrandTab("Marvel", Color(0xFFE53935)),
    BrandTab("Pixar", Color(0xFF00BFA5))
)

private val continueWatching = listOf(
    MovieItem("The Magic Flute", "2023", "2:14:46", Color(0xFF6A1B9A), Color(0xFF283593)),
    MovieItem("Guardians Vol.3", "2023", "2:30:00", Color(0xFFE65100), Color(0xFF880E4F)),
    MovieItem("Thor Thunder", "2022", "1:58:32", Color(0xFF1565C0), Color(0xFF4527A0)),
    MovieItem("Black Panther", "2022", "2:41:00", Color(0xFF1B5E20), Color(0xFF311B92)),
    MovieItem("Loki Season 2", "2023", "0:52:00", Color(0xFF00695C), Color(0xFF1A237E))
)

private val trending = listOf(
    MovieItem("Ant-Man", "2023", "2:05:00", Color(0xFF880E4F), Color(0xFF311B92), 4.7f),
    MovieItem("Elemental", "2023", "1:41:00", Color(0xFFFF6F00), Color(0xFFE65100), 4.3f),
    MovieItem("Wish", "2023", "1:35:00", Color(0xFF4527A0), Color(0xFF0D47A1), 4.1f),
    MovieItem("Ahsoka", "2023", "0:48:00", Color(0xFF1B5E20), Color(0xFF263238), 4.8f),
    MovieItem("Marvels", "2023", "1:45:00", Color(0xFFB71C1C), Color(0xFF1A237E), 4.0f)
)

@Composable
fun TvAppScreen(onBack: () -> Unit) {
    val scope = rememberCoroutineScope()
    val scrollState = rememberScrollState()

    // Entrance animation
    var entered by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { entered = true }

    val entranceAlpha by animateFloatAsState(
        if (entered) 1f else 0f, tween(600), label = "ea"
    )

    // Active sidebar item
    var activeSidebar by remember { mutableIntStateOf(0) }

    // Active brand
    var activeBrand by remember { mutableIntStateOf(0) }

    // Hero animation
    val inf = rememberInfiniteTransition(label = "tv")
    val heroGlow by inf.animateFloat(0f, 1f,
        infiniteRepeatable(tween(3000), RepeatMode.Reverse), label = "glow")
    val shimmer by inf.animateFloat(0f, 6.28f,
        infiniteRepeatable(tween(5000, easing = LinearEasing)), label = "sh")

    // Staggered content entrance
    val rowAlphas = remember { List(5) { Animatable(0f) } }
    val rowSlides = remember { List(5) { Animatable(60f) } }
    LaunchedEffect(Unit) {
        rowAlphas.forEachIndexed { idx, anim ->
            launch {
                delay(300L + idx * 150L)
                launch { anim.animateTo(1f, tween(500)) }
                rowSlides[idx].animateTo(0f, spring(dampingRatio = 0.7f))
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0A0A0F))
            .graphicsLayer { alpha = entranceAlpha }
    ) {
        Row(modifier = Modifier.fillMaxSize()) {
            // === LEFT SIDEBAR ===
            Column(
                modifier = Modifier
                    .width(56.dp)
                    .fillMaxHeight()
                    .background(Color(0xFF12121A))
                    .statusBarsPadding()
                    .padding(vertical = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {
                IconButton(onClick = onBack) {
                    Icon(Icons.Filled.ArrowBack, "Back", tint = Color.White.copy(alpha = 0.7f), modifier = Modifier.size(20.dp))
                }
                Spacer(Modifier.height(24.dp))

                val sidebarIcons = listOf(
                    Icons.Filled.Star to "Favorites",
                    Icons.Filled.Search to "Search",
                    Icons.Filled.Downloading to "Downloads",
                    Icons.Filled.Settings to "Settings"
                )

                sidebarIcons.forEachIndexed { idx, (icon, desc) ->
                    val isActive = idx == activeSidebar
                    val iconAlpha by animateFloatAsState(
                        if (isActive) 1f else 0.4f, tween(200), label = "si$idx"
                    )
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isActive) Color(0xFF7C4DFF).copy(alpha = 0.15f) else Color.Transparent)
                            .clickable { activeSidebar = idx },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(icon, desc, tint = Color.White.copy(alpha = iconAlpha), modifier = Modifier.size(20.dp))
                    }
                    if (isActive) {
                        Box(
                            Modifier
                                .padding(top = 4.dp)
                                .width(20.dp)
                                .height(2.dp)
                                .clip(RoundedCornerShape(1.dp))
                                .background(Color(0xFF7C4DFF))
                        )
                    }
                    Spacer(Modifier.height(16.dp))
                }
            }

            // === MAIN CONTENT ===
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .verticalScroll(scrollState)
                    .statusBarsPadding()
            ) {
                // Hero banner
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(260.dp)
                        .padding(12.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .graphicsLayer {
                            alpha = rowAlphas[0].value
                            translationY = rowSlides[0].value
                        }
                ) {
                    // Hero gradient background
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        // Blue/purple dramatic gradient (Kang vibes)
                        drawRect(
                            Brush.horizontalGradient(
                                listOf(Color(0xFF1A0A3E), Color(0xFF2D1B69), Color(0xFF4A2C8A))
                            )
                        )
                        // Dramatic lighting
                        drawCircle(
                            Brush.radialGradient(
                                listOf(Color(0xFF7C4DFF).copy(alpha = 0.3f + heroGlow * 0.15f), Color.Transparent),
                                center = Offset(size.width * 0.7f, size.height * 0.3f),
                                radius = size.width * 0.5f
                            ),
                            center = Offset(size.width * 0.7f, size.height * 0.3f),
                            radius = size.width * 0.5f
                        )
                        // Silhouette figure
                        drawHeroFigure(this, shimmer)
                        // Bottom gradient fade
                        drawRect(
                            Brush.verticalGradient(
                                listOf(Color.Transparent, Color(0xFF0A0A0F).copy(alpha = 0.8f)),
                                startY = size.height * 0.6f
                            )
                        )
                    }

                    // Hero text overlay
                    Column(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(20.dp)
                    ) {
                        Text(
                            "Quantumania",
                            color = Color.White,
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = (-0.5).sp
                        )
                        Spacer(Modifier.height(4.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("1 Season", color = Color.White.copy(alpha = 0.6f), fontSize = 12.sp)
                            DotSeparator()
                            Text("6 Episodes", color = Color.White.copy(alpha = 0.6f), fontSize = 12.sp)
                            DotSeparator()
                            Text("Superhero", color = Color.White.copy(alpha = 0.6f), fontSize = 12.sp)
                        }
                        Spacer(Modifier.height(12.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(Color(0xFFE53935))
                                .clickable { }
                                .padding(horizontal = 20.dp, vertical = 8.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Filled.PlayArrow, "Play", tint = Color.White, modifier = Modifier.size(16.dp))
                                Spacer(Modifier.width(6.dp))
                                Text("Watch Trailer", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                Spacer(Modifier.height(8.dp))

                // Brand tabs
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .graphicsLayer {
                            alpha = rowAlphas[1].value
                            translationY = rowSlides[1].value
                        }
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    brands.forEachIndexed { idx, brand ->
                        val isActive = idx == activeBrand
                        val bgAlpha by animateFloatAsState(
                            if (isActive) 0.2f else 0.08f, tween(200), label = "ba$idx"
                        )
                        val borderAlpha by animateFloatAsState(
                            if (isActive) 0.6f else 0f, tween(200), label = "brd$idx"
                        )

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(brand.color.copy(alpha = bgAlpha))
                                .then(
                                    if (isActive) Modifier.background(Color.Transparent) else Modifier
                                )
                                .clickable { activeBrand = idx }
                                .padding(horizontal = 18.dp, vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                brand.name,
                                color = if (isActive) brand.color else Color.White.copy(alpha = 0.5f),
                                fontSize = 12.sp,
                                fontWeight = if (isActive) FontWeight.Bold else FontWeight.Medium,
                                letterSpacing = 0.5.sp
                            )
                        }
                    }
                }

                Spacer(Modifier.height(20.dp))

                // Continue Watching section
                SectionHeader(
                    title = "Continue Watching",
                    modifier = Modifier.graphicsLayer {
                        alpha = rowAlphas[2].value
                        translationY = rowSlides[2].value
                    }
                )
                Spacer(Modifier.height(10.dp))
                MovieRow(
                    movies = continueWatching,
                    showProgress = true,
                    modifier = Modifier.graphicsLayer {
                        alpha = rowAlphas[2].value
                        translationX = rowSlides[2].value * 2f
                    }
                )

                Spacer(Modifier.height(24.dp))

                // Trending Now section
                SectionHeader(
                    title = "Trending Now",
                    modifier = Modifier.graphicsLayer {
                        alpha = rowAlphas[3].value
                        translationY = rowSlides[3].value
                    }
                )
                Spacer(Modifier.height(10.dp))
                MovieRow(
                    movies = trending,
                    showProgress = false,
                    modifier = Modifier.graphicsLayer {
                        alpha = rowAlphas[3].value
                        translationX = rowSlides[3].value * 2f
                    }
                )

                Spacer(Modifier.height(24.dp))

                // Top Picks section
                SectionHeader(
                    title = "Top Picks For You",
                    modifier = Modifier.graphicsLayer {
                        alpha = rowAlphas[4].value
                        translationY = rowSlides[4].value
                    }
                )
                Spacer(Modifier.height(10.dp))
                MovieRow(
                    movies = continueWatching.reversed(),
                    showProgress = false,
                    modifier = Modifier.graphicsLayer {
                        alpha = rowAlphas[4].value
                        translationX = rowSlides[4].value * 2f
                    }
                )

                Spacer(Modifier.height(32.dp))
            }
        }
    }
}

@Composable
private fun DotSeparator() {
    Text(
        " · ",
        color = Color.White.copy(alpha = 0.4f),
        fontSize = 12.sp
    )
}

@Composable
private fun SectionHeader(title: String, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            title,
            color = Color.White,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            "See All",
            color = Color(0xFF7C4DFF),
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
private fun MovieRow(movies: List<MovieItem>, showProgress: Boolean, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        movies.forEach { movie ->
            MovieCard(movie = movie, showProgress = showProgress)
        }
    }
}

@Composable
private fun MovieCard(movie: MovieItem, showProgress: Boolean) {
    var hovered by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        if (hovered) 1.05f else 1f, spring(dampingRatio = 0.7f), label = "ms"
    )

    Column(
        modifier = Modifier
            .width(140.dp)
            .scale(scale)
            .clickable { hovered = !hovered }
    ) {
        // Movie poster
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(90.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(
                    Brush.linearGradient(
                        listOf(movie.color1, movie.color2),
                        start = Offset.Zero,
                        end = Offset(300f, 300f)
                    )
                )
        ) {
            // Film decoration
            Canvas(modifier = Modifier.fillMaxSize()) {
                // Decorative shapes to simulate movie poster
                drawCircle(
                    Color.White.copy(alpha = 0.06f),
                    radius = size.width * 0.4f,
                    center = Offset(size.width * 0.7f, size.height * 0.3f)
                )
                drawCircle(
                    Color.White.copy(alpha = 0.04f),
                    radius = size.width * 0.3f,
                    center = Offset(size.width * 0.2f, size.height * 0.7f)
                )
                // Small star accents
                for (i in 0..2) {
                    drawCircle(
                        Color.White.copy(alpha = 0.1f),
                        radius = 3.dp.toPx(),
                        center = Offset(
                            size.width * (0.3f + i * 0.2f),
                            size.height * 0.5f
                        )
                    )
                }
            }

            // Play icon overlay
            Box(
                modifier = Modifier
                    .align(Alignment.Center)
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(Color.Black.copy(alpha = 0.4f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Filled.PlayArrow, "Play",
                    tint = Color.White.copy(alpha = 0.9f),
                    modifier = Modifier.size(18.dp)
                )
            }

            // Duration badge
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(6.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(Color.Black.copy(alpha = 0.6f))
                    .padding(horizontal = 5.dp, vertical = 2.dp)
            ) {
                Text(movie.duration, color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Medium)
            }
        }

        // Progress bar
        if (showProgress) {
            Spacer(Modifier.height(4.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(3.dp)
                    .clip(RoundedCornerShape(1.5.dp))
                    .background(Color.White.copy(alpha = 0.1f))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(fraction = (0.3f + movie.rating / 10f).coerceAtMost(0.9f))
                        .clip(RoundedCornerShape(1.5.dp))
                        .background(Color(0xFFE53935))
                )
            }
        }

        Spacer(Modifier.height(6.dp))
        Text(
            movie.title,
            color = Color.White.copy(alpha = 0.85f),
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Text(
            movie.year,
            color = Color.White.copy(alpha = 0.4f),
            fontSize = 10.sp
        )
    }
}

private fun drawHeroFigure(scope: DrawScope, phase: Float) {
    with(scope) {
        val cx = size.width * 0.65f
        val cy = size.height * 0.35f

        // Helmet/head shape (Kang-inspired angular)
        val helmetPath = Path().apply {
            moveTo(cx - 30, cy + 10)
            lineTo(cx - 35, cy - 25)
            lineTo(cx - 15, cy - 45)
            lineTo(cx + 15, cy - 45)
            lineTo(cx + 35, cy - 25)
            lineTo(cx + 30, cy + 10)
            close()
        }
        drawPath(helmetPath, Color(0xFF4A2C8A).copy(alpha = 0.6f))

        // Visor/eye slit
        drawRoundRect(
            color = Color(0xFF7C4DFF).copy(alpha = 0.7f + sin(phase).toFloat() * 0.15f),
            topLeft = Offset(cx - 22, cy - 18),
            size = Size(44f, 8f),
            cornerRadius = CornerRadius(4f)
        )

        // Shoulders/body
        val bodyPath = Path().apply {
            moveTo(cx - 30, cy + 10)
            lineTo(cx - 60, cy + 50)
            lineTo(cx - 50, cy + size.height * 0.5f)
            lineTo(cx + 50, cy + size.height * 0.5f)
            lineTo(cx + 60, cy + 50)
            lineTo(cx + 30, cy + 10)
            close()
        }
        drawPath(bodyPath, Color(0xFF2D1B69).copy(alpha = 0.5f))

        // Energy glow lines on suit
        for (i in 0..3) {
            val lineY = cy + 20 + i * 18
            val glowAlpha = 0.15f + sin(phase + i * 0.5f).toFloat() * 0.1f
            drawLine(
                Color(0xFF7C4DFF).copy(alpha = glowAlpha),
                start = Offset(cx - 25 + i * 5, lineY),
                end = Offset(cx + 25 - i * 5, lineY),
                strokeWidth = 1.5f
            )
        }

        // Particle sparkles around figure
        for (i in 0..6) {
            val px = cx + sin(phase * 0.8f + i * 1.2f).toFloat() * 80f
            val py = cy + kotlin.math.cos(phase * 0.6f + i * 0.9f).toFloat() * 60f
            drawCircle(
                Color(0xFFBB86FC).copy(alpha = 0.3f + sin(phase + i).toFloat() * 0.2f),
                radius = (2 + i % 3).toFloat(),
                center = Offset(px, py)
            )
        }
    }
}
