package com.ui.animatedmenu.showcase

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.sin

data class ImageSlide(
    val category: String,
    val description: String,
    val bgGradient: List<Color>,
    val accentColor: Color,
    val navItems: List<String>
)

@Composable
fun ImageSliderScreen(onBack: () -> Unit) {
    val slides = remember {
        listOf(
            ImageSlide(
                "Landscapes",
                "Explore breathtaking vistas and verdant forests captured in their most pristine moments. Each photograph tells a story of nature's grandeur.",
                listOf(Color(0xFF1B5E20), Color(0xFF2E7D32), Color(0xFF388E3C)),
                Color(0xFF66BB6A),
                listOf("Discover", "Stories", "Contests", "Login")
            ),
            ImageSlide(
                "Portraits",
                "Intimate close-ups revealing the raw beauty and emotion of human expression. Bold colors and dramatic lighting define this collection.",
                listOf(Color(0xFFB71C1C), Color(0xFFC62828), Color(0xFFD32F2F)),
                Color(0xFFEF5350),
                listOf("Discover", "Stories", "Contests", "Login")
            ),
            ImageSlide(
                "Architecture",
                "Modern structures and timeless monuments captured through geometric precision. Lines, curves, and light paint urban symphonies.",
                listOf(Color(0xFF1A237E), Color(0xFF283593), Color(0xFF303F9F)),
                Color(0xFF7986CB),
                listOf("Discover", "Stories", "Contests", "Login")
            ),
            ImageSlide(
                "Street",
                "Candid moments from bustling cities worldwide. Life unfolds in vivid color on every corner, every alley, every intersection.",
                listOf(Color(0xFF4A148C), Color(0xFF6A1B9A), Color(0xFF7B1FA2)),
                Color(0xFFCE93D8),
                listOf("Discover", "Stories", "Contests", "Login")
            ),
            ImageSlide(
                "Wildlife",
                "Majestic creatures in their natural habitats. Patience and precision meet in photographs celebrating Earth's diverse inhabitants.",
                listOf(Color(0xFFE65100), Color(0xFFEF6C00), Color(0xFFF57C00)),
                Color(0xFFFFCC02),
                listOf("Discover", "Stories", "Contests", "Login")
            ),
        )
    }

    val currentIndex = remember { mutableIntStateOf(0) }
    val slideOffset = remember { Animatable(0f) }
    val scope = rememberCoroutineScope()

    // Entry animation
    val entryAnim = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        entryAnim.animateTo(1f, tween(700, easing = FastOutSlowInEasing))
    }

    // Content reveal on slide change
    val contentReveal = remember { Animatable(1f) }
    LaunchedEffect(currentIndex.intValue) {
        contentReveal.snapTo(0f)
        contentReveal.animateTo(1f, tween(500, easing = FastOutSlowInEasing))
    }

    // Floating particles
    val infiniteTransition = rememberInfiniteTransition(label = "particles")
    val particlePhase by infiniteTransition.animateFloat(
        0f, 6.28f,
        infiniteRepeatable(tween(8000, easing = LinearEasing)),
        label = "pp"
    )

    val slide = slides[currentIndex.intValue]

    val bgColor1 by animateColorAsState(
        slide.bgGradient[0], tween(600), label = "c1"
    )
    val bgColor2 by animateColorAsState(
        slide.bgGradient[1], tween(600), label = "c2"
    )
    val bgColor3 by animateColorAsState(
        slide.bgGradient[2], tween(600), label = "c3"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(bgColor1, bgColor2, bgColor3)))
            .pointerInput(Unit) {
                detectHorizontalDragGestures(
                    onDragEnd = {
                        scope.launch {
                            if (slideOffset.value > 0.15f && currentIndex.intValue > 0) {
                                slideOffset.animateTo(1f, tween(300))
                                currentIndex.intValue -= 1
                                slideOffset.snapTo(-1f)
                                slideOffset.animateTo(0f, tween(350))
                            } else if (slideOffset.value < -0.15f && currentIndex.intValue < slides.size - 1) {
                                slideOffset.animateTo(-1f, tween(300))
                                currentIndex.intValue += 1
                                slideOffset.snapTo(1f)
                                slideOffset.animateTo(0f, tween(350))
                            } else {
                                slideOffset.animateTo(0f, spring())
                            }
                        }
                    }
                ) { _, dragAmount ->
                    scope.launch {
                        slideOffset.snapTo(
                            (slideOffset.value + dragAmount / 500f).coerceIn(-1f, 1f)
                        )
                    }
                }
            }
    ) {
        // Floating light particles
        Canvas(modifier = Modifier.fillMaxSize()) {
            for (i in 0..15) {
                val px = ((i * 137 + 50) % size.width.toInt()).toFloat()
                val baseY = ((i * 97 + 100) % size.height.toInt()).toFloat()
                val py = baseY + sin(particlePhase + i * 0.9f).toFloat() * 20f
                val alpha = 0.04f + (sin(particlePhase + i * 1.3f).toFloat() + 1f) * 0.04f
                val r = (4f + i % 4 * 3f)
                drawCircle(
                    Color.White.copy(alpha = alpha),
                    radius = r,
                    center = Offset(px, py)
                )
            }
        }

        // Decorative image placeholder blocks (representing photo thumbnails)
        Canvas(modifier = Modifier.fillMaxSize()) {
            // Main image block (large)
            val mainX = size.width * 0.08f + slideOffset.value * -40f
            val mainY = size.height * 0.22f
            val mainW = size.width * 0.84f
            val mainH = size.height * 0.32f

            drawRoundRect(
                color = Color.White.copy(alpha = 0.1f),
                topLeft = Offset(mainX, mainY),
                size = Size(mainW, mainH),
                cornerRadius = CornerRadius(24f, 24f)
            )

            // Inner image simulation (gradient overlay)
            drawRoundRect(
                brush = Brush.verticalGradient(
                    listOf(Color.White.copy(alpha = 0.03f), Color.White.copy(alpha = 0.15f)),
                    startY = mainY,
                    endY = mainY + mainH
                ),
                topLeft = Offset(mainX, mainY),
                size = Size(mainW, mainH),
                cornerRadius = CornerRadius(24f, 24f)
            )

            // Grid pattern inside (photo mosaic feel)
            val gridSpacing = 45f
            for (gx in 0..((mainW / gridSpacing).toInt())) {
                drawLine(
                    Color.White.copy(alpha = 0.04f),
                    start = Offset(mainX + gx * gridSpacing, mainY),
                    end = Offset(mainX + gx * gridSpacing, mainY + mainH),
                    strokeWidth = 0.5f
                )
            }
            for (gy in 0..((mainH / gridSpacing).toInt())) {
                drawLine(
                    Color.White.copy(alpha = 0.04f),
                    start = Offset(mainX, mainY + gy * gridSpacing),
                    end = Offset(mainX + mainW, mainY + gy * gridSpacing),
                    strokeWidth = 0.5f
                )
            }

            // Small thumbnail blocks on right side
            for (i in 0..2) {
                val thumbX = size.width * 0.72f
                val thumbY = mainY + 20f + i * 90f
                val thumbAlpha = 0.08f + (if (i == 0) 0.06f else 0f)
                drawRoundRect(
                    Color.White.copy(alpha = thumbAlpha),
                    topLeft = Offset(thumbX, thumbY),
                    size = Size(80f, 70f),
                    cornerRadius = CornerRadius(12f, 12f)
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
        ) {
            // Top bar with brand + nav
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
                    .graphicsLayer {
                        alpha = entryAnim.value
                        translationY = (1f - entryAnim.value) * -30f
                    },
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(Icons.Filled.ArrowBack, "Back", tint = Color.White)
                }

                Text(
                    "@SnapStory",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(start = 4.dp)
                )

                Spacer(Modifier.weight(1f))

                // Nav items
                slide.navItems.take(3).forEach { nav ->
                    Text(
                        nav,
                        color = Color.White.copy(alpha = 0.6f),
                        fontSize = 12.sp,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )
                }
            }

            // Main image area spacer
            Spacer(Modifier.weight(0.45f))

            // Category title
            Text(
                slide.category,
                color = Color.White,
                fontSize = 48.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = (-2).sp,
                modifier = Modifier
                    .padding(horizontal = 24.dp)
                    .graphicsLayer {
                        alpha = contentReveal.value
                        translationX = (1f - contentReveal.value) * 80f + slideOffset.value * -120f
                    }
            )

            Spacer(Modifier.height(12.dp))

            // Description
            Text(
                slide.description,
                color = Color.White.copy(alpha = 0.65f),
                fontSize = 14.sp,
                lineHeight = 22.sp,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .padding(horizontal = 24.dp)
                    .fillMaxWidth(0.85f)
                    .graphicsLayer {
                        alpha = contentReveal.value
                        translationX = (1f - contentReveal.value) * 50f + slideOffset.value * -80f
                    }
            )

            Spacer(Modifier.height(24.dp))

            // Explore Now button
            Box(
                modifier = Modifier
                    .padding(horizontal = 24.dp)
                    .graphicsLayer {
                        alpha = contentReveal.value
                        translationY = (1f - contentReveal.value) * 30f
                    }
                    .clip(RoundedCornerShape(12.dp))
                    .background(slide.accentColor)
                    .clickable { }
                    .padding(horizontal = 24.dp, vertical = 12.dp)
            ) {
                Text(
                    "Explore Now",
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                )
            }

            Spacer(Modifier.weight(0.15f))

            // Bottom: slide counter + dots + arrows
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .navigationBarsPadding()
                    .padding(bottom = 20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Slide number
                Text(
                    "${currentIndex.intValue + 1}",
                    color = Color.White,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Black
                )
                Text(
                    " / ${slides.size}",
                    color = Color.White.copy(alpha = 0.4f),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )

                Spacer(Modifier.weight(1f))

                // Progress bar
                Box(
                    modifier = Modifier
                        .width(100.dp)
                        .height(3.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.2f))
                ) {
                    val progressWidth by animateFloatAsState(
                        targetValue = (currentIndex.intValue + 1f) / slides.size,
                        animationSpec = tween(400),
                        label = "prog"
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(progressWidth)
                            .fillMaxHeight()
                            .clip(CircleShape)
                            .background(Color.White)
                    )
                }

                Spacer(Modifier.weight(1f))

                // Arrow buttons
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.15f))
                            .clickable {
                                if (currentIndex.intValue > 0) {
                                    scope.launch {
                                        slideOffset.animateTo(1f, tween(300))
                                        currentIndex.intValue -= 1
                                        slideOffset.snapTo(-1f)
                                        slideOffset.animateTo(0f, tween(350))
                                    }
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Filled.KeyboardArrowLeft, "Previous",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.15f))
                            .clickable {
                                if (currentIndex.intValue < slides.size - 1) {
                                    scope.launch {
                                        slideOffset.animateTo(-1f, tween(300))
                                        currentIndex.intValue += 1
                                        slideOffset.snapTo(1f)
                                        slideOffset.animateTo(0f, tween(350))
                                    }
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Filled.KeyboardArrowRight, "Next",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}
