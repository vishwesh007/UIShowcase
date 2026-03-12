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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin

data class ShoeSlide(
    val name: String,
    val tagline: String,
    val price: String,
    val bgGradientStart: Color,
    val bgGradientEnd: Color,
    val accentColor: Color,
    val shoeColor: Color
)

@Composable
fun ShoeSliderScreen(onBack: () -> Unit) {
    val shoes = remember {
        listOf(
            ShoeSlide(
                "NIKE SOLAR FLARE", "BRING THE HEAT", "$799",
                Color(0xFFFF6B35), Color(0xFFFF4500),
                Color(0xFFFFD700), Color(0xFFFF8C42)
            ),
            ShoeSlide(
                "NIKE AIR RUSH", "BRING THE HEAT", "$699",
                Color(0xFFE53935), Color(0xFFB71C1C),
                Color(0xFFFF8A80), Color(0xFFEF5350)
            ),
            ShoeSlide(
                "NIKE OCEAN WAVE", "BRING THE HEAT", "$849",
                Color(0xFF1565C0), Color(0xFF0D47A1),
                Color(0xFF64B5F6), Color(0xFF42A5F5)
            ),
            ShoeSlide(
                "NIKE VOLT STRIKE", "BRING THE HEAT", "$749",
                Color(0xFF2E7D32), Color(0xFF1B5E20),
                Color(0xFF69F0AE), Color(0xFF66BB6A)
            ),
            ShoeSlide(
                "NIKE SHADOW X", "BRING THE HEAT", "$899",
                Color(0xFF4A148C), Color(0xFF311B92),
                Color(0xFFCE93D8), Color(0xFFAB47BC)
            ),
        )
    }

    val currentIndex = remember { mutableIntStateOf(0) }
    val slideOffset = remember { Animatable(0f) }
    val scope = rememberCoroutineScope()

    // Entry animation
    val entryAnim = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        entryAnim.animateTo(1f, tween(800, easing = FastOutSlowInEasing))
    }

    // Floating shoe animation
    val infiniteTransition = rememberInfiniteTransition(label = "shoe")
    val floatY by infiniteTransition.animateFloat(
        -12f, 12f,
        infiniteRepeatable(tween(3000, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "floatY"
    )
    val rotateZ by infiniteTransition.animateFloat(
        -3f, 3f,
        infiniteRepeatable(tween(4000, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "rotZ"
    )

    // Auto-slide timer
    var autoSlideEnabled by remember { mutableStateOf(true) }
    LaunchedEffect(autoSlideEnabled, currentIndex.intValue) {
        if (autoSlideEnabled) {
            kotlinx.coroutines.delay(4000)
            val next = (currentIndex.intValue + 1) % shoes.size
            slideOffset.animateTo(-1f, tween(500, easing = FastOutSlowInEasing))
            currentIndex.intValue = next
            slideOffset.snapTo(1f)
            slideOffset.animateTo(0f, tween(400, easing = FastOutSlowInEasing))
        }
    }

    val shoe = shoes[currentIndex.intValue]

    val bgColor by animateColorAsState(
        targetValue = shoe.bgGradientStart,
        animationSpec = tween(500),
        label = "bg"
    )
    val bgColor2 by animateColorAsState(
        targetValue = shoe.bgGradientEnd,
        animationSpec = tween(500),
        label = "bg2"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(listOf(bgColor, bgColor2))
            )
            .pointerInput(Unit) {
                detectHorizontalDragGestures(
                    onDragStart = { autoSlideEnabled = false },
                    onDragEnd = {
                        scope.launch {
                            if (slideOffset.value > 0.15f) {
                                // Swipe right → previous
                                val prev = if (currentIndex.intValue > 0) currentIndex.intValue - 1 else shoes.size - 1
                                slideOffset.animateTo(1f, tween(300))
                                currentIndex.intValue = prev
                                slideOffset.snapTo(-1f)
                                slideOffset.animateTo(0f, tween(300))
                            } else if (slideOffset.value < -0.15f) {
                                // Swipe left → next
                                val next = (currentIndex.intValue + 1) % shoes.size
                                slideOffset.animateTo(-1f, tween(300))
                                currentIndex.intValue = next
                                slideOffset.snapTo(1f)
                                slideOffset.animateTo(0f, tween(300))
                            } else {
                                slideOffset.animateTo(0f, spring())
                            }
                            autoSlideEnabled = true
                        }
                    }
                ) { _, dragAmount ->
                    scope.launch {
                        slideOffset.snapTo(
                            (slideOffset.value + dragAmount / 600f).coerceIn(-1f, 1f)
                        )
                    }
                }
            }
    ) {
        // Background decorative circles
        Canvas(modifier = Modifier.fillMaxSize()) {
            // Large circle behind shoe
            drawCircle(
                color = Color.White.copy(alpha = 0.08f),
                radius = size.width * 0.45f,
                center = Offset(size.width * 0.5f, size.height * 0.42f)
            )
            drawCircle(
                color = Color.White.copy(alpha = 0.04f),
                radius = size.width * 0.55f,
                center = Offset(size.width * 0.5f, size.height * 0.42f)
            )

            // Diagonal speed lines
            for (i in 0..8) {
                val y = size.height * 0.2f + i * 35f
                val startX = size.width * 0.6f + i * 20f
                drawLine(
                    Color.White.copy(alpha = 0.06f),
                    start = Offset(startX, y),
                    end = Offset(startX + 80f, y - 20f),
                    strokeWidth = 2f
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .graphicsLayer {
                    alpha = entryAnim.value
                }
        ) {
            // Top bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(Icons.Filled.ArrowBack, "Back", tint = Color.White)
                }
                Spacer(Modifier.weight(1f))
                Text(
                    "New & Featured",
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
                Spacer(Modifier.weight(1f))
                IconButton(onClick = {}) {
                    Icon(Icons.Filled.ShoppingCart, "Cart", tint = Color.White)
                }
            }

            Spacer(Modifier.height(16.dp))

            // Tagline
            Text(
                shoe.tagline,
                color = Color.White,
                fontSize = 42.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = (-1).sp,
                lineHeight = 44.sp,
                modifier = Modifier
                    .padding(horizontal = 24.dp)
                    .graphicsLayer {
                        translationX = slideOffset.value * -200f
                        alpha = 1f - abs(slideOffset.value) * 0.5f
                    }
            )

            Spacer(Modifier.height(8.dp))

            // Subtitle
            Text(
                shoe.name,
                color = Color.White.copy(alpha = 0.7f),
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                letterSpacing = 3.sp,
                modifier = Modifier
                    .padding(horizontal = 24.dp)
                    .graphicsLayer {
                        translationX = slideOffset.value * -150f
                        alpha = 1f - abs(slideOffset.value) * 0.7f
                    }
            )

            // Shoe illustration area
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .graphicsLayer {
                        translationX = slideOffset.value * -300f
                        translationY = floatY
                        rotationZ = rotateZ + slideOffset.value * 15f
                        alpha = 1f - abs(slideOffset.value) * 0.3f
                    },
                contentAlignment = Alignment.Center
            ) {
                // Draw shoe silhouette
                Canvas(modifier = Modifier.size(280.dp)) {
                    val cx = size.width / 2f
                    val cy = size.height / 2f

                    // Shoe body (outsole)
                    val solePath = Path().apply {
                        moveTo(cx - 120f, cy + 30f)
                        cubicTo(cx - 140f, cy + 50f, cx - 100f, cy + 70f, cx - 40f, cy + 65f)
                        lineTo(cx + 100f, cy + 55f)
                        cubicTo(cx + 140f, cy + 50f, cx + 150f, cy + 35f, cx + 130f, cy + 25f)
                        lineTo(cx - 100f, cy + 20f)
                        close()
                    }
                    drawPath(solePath, color = Color.White.copy(alpha = 0.95f))

                    // Shoe upper (main body)
                    val upperPath = Path().apply {
                        moveTo(cx - 110f, cy + 20f)
                        cubicTo(cx - 120f, cy - 10f, cx - 80f, cy - 60f, cx - 30f, cy - 70f)
                        cubicTo(cx + 20f, cy - 80f, cx + 80f, cy - 50f, cx + 120f, cy - 20f)
                        cubicTo(cx + 140f, cy - 5f, cx + 135f, cy + 20f, cx + 125f, cy + 25f)
                        lineTo(cx - 100f, cy + 20f)
                        close()
                    }
                    drawPath(upperPath, color = shoe.shoeColor)

                    // Shoe accent overlay
                    val accentPath = Path().apply {
                        moveTo(cx - 60f, cy + 15f)
                        cubicTo(cx - 40f, cy - 20f, cx + 10f, cy - 50f, cx + 60f, cy - 40f)
                        cubicTo(cx + 100f, cy - 30f, cx + 120f, cy - 10f, cx + 110f, cy + 10f)
                        lineTo(cx - 50f, cy + 15f)
                        close()
                    }
                    drawPath(accentPath, color = shoe.accentColor.copy(alpha = 0.4f))

                    // Nike-like swoosh
                    val swooshPath = Path().apply {
                        moveTo(cx - 80f, cy + 5f)
                        cubicTo(cx - 30f, cy - 25f, cx + 40f, cy - 45f, cx + 110f, cy - 30f)
                        cubicTo(cx + 60f, cy - 15f, cx - 10f, cy + 5f, cx - 60f, cy + 15f)
                        close()
                    }
                    drawPath(swooshPath, color = Color.White.copy(alpha = 0.3f))

                    // Lace area dots
                    for (i in 0..4) {
                        val dx = cx - 40f + i * 20f
                        val dy = cy - 50f + i * 5f
                        drawCircle(Color.White.copy(alpha = 0.5f), radius = 3f, center = Offset(dx, dy))
                    }

                    // Shadow
                    drawOval(
                        color = Color.Black.copy(alpha = 0.15f),
                        topLeft = Offset(cx - 100f, cy + 70f),
                        size = androidx.compose.ui.geometry.Size(200f, 20f)
                    )
                }
            }

            // Price and action area
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .graphicsLayer {
                        translationY = (1f - entryAnim.value) * 60f
                    },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        "Price",
                        color = Color.White.copy(alpha = 0.5f),
                        fontSize = 12.sp,
                        letterSpacing = 1.sp
                    )
                    Text(
                        shoe.price,
                        color = Color.White,
                        fontSize = 36.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = (-1).sp,
                        modifier = Modifier.graphicsLayer {
                            translationX = slideOffset.value * -100f
                        }
                    )
                }

                Spacer(Modifier.weight(1f))

                // Buy Now button
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color.White)
                        .clickable { }
                        .padding(horizontal = 28.dp, vertical = 14.dp)
                ) {
                    Text(
                        "BUY NOW",
                        color = shoe.bgGradientStart,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.sp
                    )
                }
            }

            Spacer(Modifier.height(24.dp))

            // Navigation dots + arrows
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .navigationBarsPadding()
                    .padding(bottom = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                // Left arrow
                IconButton(
                    onClick = {
                        scope.launch {
                            autoSlideEnabled = false
                            val prev = if (currentIndex.intValue > 0) currentIndex.intValue - 1 else shoes.size - 1
                            slideOffset.animateTo(1f, tween(300))
                            currentIndex.intValue = prev
                            slideOffset.snapTo(-1f)
                            slideOffset.animateTo(0f, tween(300))
                            autoSlideEnabled = true
                        }
                    }
                ) {
                    Icon(
                        Icons.Filled.KeyboardArrowLeft, "Previous",
                        tint = Color.White.copy(alpha = 0.7f),
                        modifier = Modifier.size(28.dp)
                    )
                }

                Spacer(Modifier.width(16.dp))

                // Dots
                shoes.forEachIndexed { i, _ ->
                    val isActive = i == currentIndex.intValue
                    val dotWidth by animateFloatAsState(
                        targetValue = if (isActive) 24f else 8f,
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioMediumBouncy,
                            stiffness = 500f
                        ),
                        label = "dotW$i"
                    )
                    Box(
                        modifier = Modifier
                            .padding(horizontal = 3.dp)
                            .width(dotWidth.dp)
                            .height(8.dp)
                            .clip(CircleShape)
                            .background(
                                if (isActive) Color.White
                                else Color.White.copy(alpha = 0.3f)
                            )
                            .clickable(
                                indication = null,
                                interactionSource = remember { MutableInteractionSource() }
                            ) {
                                scope.launch {
                                    autoSlideEnabled = false
                                    val direction = if (i > currentIndex.intValue) -1f else 1f
                                    slideOffset.animateTo(direction, tween(300))
                                    currentIndex.intValue = i
                                    slideOffset.snapTo(-direction)
                                    slideOffset.animateTo(0f, tween(300))
                                    autoSlideEnabled = true
                                }
                            }
                    )
                }

                Spacer(Modifier.width(16.dp))

                // Right arrow
                IconButton(
                    onClick = {
                        scope.launch {
                            autoSlideEnabled = false
                            val next = (currentIndex.intValue + 1) % shoes.size
                            slideOffset.animateTo(-1f, tween(300))
                            currentIndex.intValue = next
                            slideOffset.snapTo(1f)
                            slideOffset.animateTo(0f, tween(300))
                            autoSlideEnabled = true
                        }
                    }
                ) {
                    Icon(
                        Icons.Filled.KeyboardArrowRight, "Next",
                        tint = Color.White.copy(alpha = 0.7f),
                        modifier = Modifier.size(28.dp)
                    )
                }
            }
        }
    }
}
