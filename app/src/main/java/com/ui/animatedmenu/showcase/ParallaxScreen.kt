package com.ui.animatedmenu.showcase

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.sin
import kotlin.math.cos

data class ParallaxSection(
    val title: String,
    val subtitle: String,
    val description: String,
    val accentColor: Color,
    val bgGradientStart: Color,
    val bgGradientEnd: Color,
    val badge: String? = null
)

private val sections = listOf(
    ParallaxSection(
        title = "Taste\nHeritage",
        subtitle = "Artisan Collection",
        description = "Discover artisan wines curated from vineyards around the world. Every bottle tells a story that is smooth, bold and timeless.",
        accentColor = Color(0xFFE91E63),
        bgGradientStart = Color(0xFFFFF8F0),
        bgGradientEnd = Color(0xFFFFECE0),
        badge = "25% OFF"
    ),
    ParallaxSection(
        title = "Let the\nMoments\nPour",
        subtitle = "Premium Selection",
        description = "Cabernet Sauvignon, Merlot, Pinot Noir — handpicked from the finest estates for unforgettable evenings.",
        accentColor = Color(0xFFD81B60),
        bgGradientStart = Color(0xFF1A1A2E),
        bgGradientEnd = Color(0xFF16213E),
        badge = "NEW"
    ),
    ParallaxSection(
        title = "Quick\nDelivery",
        subtitle = "Express Shipping",
        description = "From our cellar to your door in 24 hours. Temperature-controlled packaging ensures perfect condition.",
        accentColor = Color(0xFFFF6F61),
        bgGradientStart = Color(0xFFF8F0E3),
        bgGradientEnd = Color(0xFFF0E6D3),
        badge = "FREE"
    ),
    ParallaxSection(
        title = "Exclusive\nReserve",
        subtitle = "Limited Editions",
        description = "Rare vintages from legendary vineyards. Each bottle is numbered and comes with a certificate of authenticity.",
        accentColor = Color(0xFF9C27B0),
        bgGradientStart = Color(0xFF1A0A2E),
        bgGradientEnd = Color(0xFF2D1B4E),
        badge = "LIMITED"
    ),
    ParallaxSection(
        title = "Wine\nClub",
        subtitle = "Join Today",
        description = "Monthly curated selections, exclusive tastings, and members-only discounts. Start your journey.",
        accentColor = Color(0xFFFF8A65),
        bgGradientStart = Color(0xFFFAF3E0),
        bgGradientEnd = Color(0xFFF5E6CC)
    )
)

@Composable
fun ParallaxScreen(onBack: () -> Unit) {
    var currentSection by remember { mutableIntStateOf(0) }
    val scope = rememberCoroutineScope()

    // Parallax layer offsets
    val bgOffsetY = remember { Animatable(0f) }
    val midOffsetY = remember { Animatable(0f) }
    val fgOffsetY = remember { Animatable(0f) }

    // Section transition
    val sectionProgress = remember { Animatable(0f) }
    var isTransitioning by remember { mutableStateOf(false) }

    // Entrance animation
    var entered by remember { mutableStateOf(false) }
    val entranceAlpha by animateFloatAsState(
        targetValue = if (entered) 1f else 0f,
        animationSpec = tween(800, easing = FastOutSlowInEasing), label = "entrance"
    )
    val entranceScale by animateFloatAsState(
        targetValue = if (entered) 1f else 0.92f,
        animationSpec = spring(dampingRatio = 0.7f, stiffness = 300f), label = "escale"
    )

    LaunchedEffect(Unit) { entered = true }

    val section = sections[currentSection]
    val isDark = section.bgGradientStart.luminance() < 0.5f

    val animBgColor by animateColorAsState(
        targetValue = section.bgGradientStart, animationSpec = tween(600), label = "bg1"
    )
    val animBgColor2 by animateColorAsState(
        targetValue = section.bgGradientEnd, animationSpec = tween(600), label = "bg2"
    )
    val animAccent by animateColorAsState(
        targetValue = section.accentColor, animationSpec = tween(500), label = "accent"
    )
    val textColor = if (isDark) Color.White else Color(0xFF1A1A1A)
    val subtextColor = if (isDark) Color.White.copy(alpha = 0.6f) else Color(0xFF666666)

    // Infinite parallax float
    val inf = rememberInfiniteTransition(label = "parallax")
    val floatPhase by inf.animateFloat(0f, 6.28f,
        infiniteRepeatable(tween(6000, easing = LinearEasing)), label = "float")
    val slowPhase by inf.animateFloat(0f, 6.28f,
        infiniteRepeatable(tween(10000, easing = LinearEasing)), label = "slow")

    // Drag to switch sections
    Box(
        modifier = Modifier
            .fillMaxSize()
            .graphicsLayer {
                alpha = entranceAlpha
                scaleX = entranceScale
                scaleY = entranceScale
            }
            .background(Brush.verticalGradient(listOf(animBgColor, animBgColor2)))
            .pointerInput(Unit) {
                detectVerticalDragGestures(
                    onDragEnd = {
                        if (!isTransitioning) {
                            val dragVal = fgOffsetY.value
                            if (dragVal < -80f && currentSection < sections.size - 1) {
                                isTransitioning = true
                                scope.launch {
                                    // Parallax: layers move at different speeds
                                    launch { bgOffsetY.animateTo(-300f, tween(400)) }
                                    launch { midOffsetY.animateTo(-500f, tween(350)) }
                                    fgOffsetY.animateTo(-700f, tween(300))
                                    currentSection++
                                    bgOffsetY.snapTo(200f)
                                    midOffsetY.snapTo(350f)
                                    fgOffsetY.snapTo(500f)
                                    launch { bgOffsetY.animateTo(0f, spring(dampingRatio = 0.75f)) }
                                    launch { midOffsetY.animateTo(0f, spring(dampingRatio = 0.7f)) }
                                    fgOffsetY.animateTo(0f, spring(dampingRatio = 0.65f))
                                    isTransitioning = false
                                }
                            } else if (dragVal > 80f && currentSection > 0) {
                                isTransitioning = true
                                scope.launch {
                                    launch { bgOffsetY.animateTo(300f, tween(400)) }
                                    launch { midOffsetY.animateTo(500f, tween(350)) }
                                    fgOffsetY.animateTo(700f, tween(300))
                                    currentSection--
                                    bgOffsetY.snapTo(-200f)
                                    midOffsetY.snapTo(-350f)
                                    fgOffsetY.snapTo(-500f)
                                    launch { bgOffsetY.animateTo(0f, spring(dampingRatio = 0.75f)) }
                                    launch { midOffsetY.animateTo(0f, spring(dampingRatio = 0.7f)) }
                                    fgOffsetY.animateTo(0f, spring(dampingRatio = 0.65f))
                                    isTransitioning = false
                                }
                            } else {
                                scope.launch {
                                    launch { bgOffsetY.animateTo(0f, spring()) }
                                    launch { midOffsetY.animateTo(0f, spring()) }
                                    fgOffsetY.animateTo(0f, spring())
                                }
                            }
                        }
                    }
                ) { _, dragAmount ->
                    if (!isTransitioning) {
                        scope.launch {
                            // Parallax: BG moves slowest, FG moves fastest
                            bgOffsetY.snapTo(bgOffsetY.value + dragAmount * 0.3f)
                            midOffsetY.snapTo(midOffsetY.value + dragAmount * 0.6f)
                            fgOffsetY.snapTo(fgOffsetY.value + dragAmount * 1.0f)
                        }
                    }
                }
            }
    ) {
        // === LAYER 1: Background decorative elements (slowest parallax) ===
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer { translationY = bgOffsetY.value + sin(slowPhase).toFloat() * 8f }
        ) {
            val w = size.width
            val h = size.height

            // Large decorative circles
            drawCircle(
                color = animAccent.copy(alpha = 0.08f),
                radius = w * 0.45f,
                center = Offset(w * 0.85f, h * 0.15f + sin(slowPhase).toFloat() * 20f)
            )
            drawCircle(
                color = animAccent.copy(alpha = 0.05f),
                radius = w * 0.35f,
                center = Offset(w * 0.1f, h * 0.75f + cos(slowPhase).toFloat() * 15f)
            )
            // Small floating dots
            for (i in 0..8) {
                val cx = (i * 127 + 50) % w.toInt()
                val cy = (i * 173 + 100) % h.toInt()
                val dotAlpha = 0.04f + sin(slowPhase + i * 0.9f).toFloat() * 0.03f
                drawCircle(
                    color = animAccent.copy(alpha = dotAlpha),
                    radius = (4 + i % 3 * 3).toFloat().dp.toPx(),
                    center = Offset(cx.toFloat(), cy.toFloat() + sin(slowPhase + i).toFloat() * 12f)
                )
            }
            // Diagonal lines
            for (i in 0..3) {
                val startY = h * (0.1f + i * 0.25f)
                drawLine(
                    color = animAccent.copy(alpha = 0.04f),
                    start = Offset(0f, startY),
                    end = Offset(w, startY - w * 0.15f),
                    strokeWidth = 1.dp.toPx()
                )
            }
        }

        // === LAYER 2: Mid-ground product area (medium parallax) ===
        Box(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer { translationY = midOffsetY.value + sin(floatPhase + 1f).toFloat() * 5f }
        ) {
            // Wine bottle placeholder (decorative)
            Canvas(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .offset(x = 20.dp, y = (-30).dp)
                    .size(140.dp, 320.dp)
            ) {
                drawWineBottle(this, animAccent, floatPhase)
            }

            // Decorative pink circle behind bottle
            Canvas(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .offset(x = 10.dp, y = (-20).dp)
                    .size(200.dp)
            ) {
                drawCircle(
                    color = animAccent.copy(alpha = 0.12f),
                    radius = size.width / 2f
                )
                drawCircle(
                    color = animAccent.copy(alpha = 0.06f),
                    radius = size.width / 2f + 15.dp.toPx(),
                    style = Stroke(width = 1.dp.toPx())
                )
            }
        }

        // === LAYER 3: Foreground content (fastest parallax) ===
        Column(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer { translationY = fgOffsetY.value + sin(floatPhase).toFloat() * 3f }
                .statusBarsPadding()
                .padding(horizontal = 24.dp)
        ) {
            Spacer(Modifier.height(16.dp))

            // Top bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(Icons.Filled.ArrowBack, "Back", tint = textColor)
                }
                Text(
                    "Vintale",
                    color = textColor,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                IconButton(onClick = {}) {
                    Icon(Icons.Filled.ShoppingCart, "Cart", tint = textColor)
                }
            }

            // Navigation tags
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                listOf("Collections", "Our Story", "Journal", "Contact").forEach { label ->
                    Text(
                        label,
                        color = subtextColor,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        letterSpacing = 0.5.sp
                    )
                }
            }

            Spacer(Modifier.height(32.dp))

            // Badge
            section.badge?.let { badge ->
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(animAccent)
                        .padding(horizontal = 16.dp, vertical = 6.dp)
                ) {
                    Text(
                        badge,
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                }
                Spacer(Modifier.height(16.dp))
            }

            // Main title with parallax offset
            Text(
                section.title,
                color = textColor,
                fontSize = 48.sp,
                fontWeight = FontWeight.Black,
                lineHeight = 52.sp,
                letterSpacing = (-1.5).sp,
                modifier = Modifier.graphicsLayer {
                    translationY = -fgOffsetY.value * 0.15f // Counter-parallax for depth
                }
            )

            Spacer(Modifier.height(8.dp))

            // Subtitle
            Text(
                section.subtitle,
                color = animAccent,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 2.sp
            )

            Spacer(Modifier.height(16.dp))

            // Description
            Text(
                section.description,
                color = subtextColor,
                fontSize = 14.sp,
                lineHeight = 22.sp,
                modifier = Modifier.fillMaxWidth(0.7f)
            )

            Spacer(Modifier.weight(1f))

            // Bottom section indicator + CTA
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 32.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Section dots
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    sections.forEachIndexed { index, _ ->
                        val isActive = index == currentSection
                        val dotWidth by animateDpAsState(
                            targetValue = if (isActive) 24.dp else 8.dp,
                            animationSpec = spring(dampingRatio = 0.7f), label = "dot$index"
                        )
                        Box(
                            modifier = Modifier
                                .height(8.dp)
                                .width(dotWidth)
                                .clip(RoundedCornerShape(4.dp))
                                .background(
                                    if (isActive) animAccent
                                    else (if (isDark) Color.White.copy(alpha = 0.2f) else Color.Black.copy(alpha = 0.15f))
                                )
                        )
                    }
                }

                // CTA button
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(28.dp))
                        .background(animAccent)
                        .clickable { }
                        .padding(horizontal = 24.dp, vertical = 12.dp)
                ) {
                    Text(
                        "Explore",
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Scroll hint
            val hintAlpha by animateFloatAsState(
                targetValue = if (currentSection < sections.size - 1) 1f else 0f,
                animationSpec = tween(300), label = "hint"
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
                    .alpha(hintAlpha),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                val bounce by inf.animateFloat(0f, 1f,
                    infiniteRepeatable(tween(1200), RepeatMode.Reverse), label = "bounce")
                Icon(
                    Icons.Filled.KeyboardArrowDown,
                    "Scroll",
                    tint = subtextColor.copy(alpha = 0.5f),
                    modifier = Modifier
                        .size(20.dp)
                        .graphicsLayer { translationY = bounce * 8f }
                )
                Spacer(Modifier.width(4.dp))
                Text(
                    "Swipe to explore",
                    color = subtextColor.copy(alpha = 0.4f),
                    fontSize = 11.sp
                )
            }
        }

        // === Floating nav dots on right side ===
        Column(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            sections.forEachIndexed { index, _ ->
                val isActive = index == currentSection
                val scale by animateFloatAsState(
                    targetValue = if (isActive) 1.4f else 1f,
                    animationSpec = spring(dampingRatio = 0.6f), label = "ns$index"
                )
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .scale(scale)
                        .clip(CircleShape)
                        .background(
                            if (isActive) animAccent
                            else (if (isDark) Color.White.copy(alpha = 0.25f) else Color.Black.copy(alpha = 0.2f))
                        )
                        .clickable {
                            if (!isTransitioning && index != currentSection) {
                                isTransitioning = true
                                scope.launch {
                                    val dir = if (index > currentSection) -1 else 1
                                    launch { bgOffsetY.animateTo(dir * 300f, tween(400)) }
                                    launch { midOffsetY.animateTo(dir * 500f, tween(350)) }
                                    fgOffsetY.animateTo(dir * 700f, tween(300))
                                    currentSection = index
                                    bgOffsetY.snapTo(-dir * 200f)
                                    midOffsetY.snapTo(-dir * 350f)
                                    fgOffsetY.snapTo(-dir * 500f)
                                    launch { bgOffsetY.animateTo(0f, spring(dampingRatio = 0.75f)) }
                                    launch { midOffsetY.animateTo(0f, spring(dampingRatio = 0.7f)) }
                                    fgOffsetY.animateTo(0f, spring(dampingRatio = 0.65f))
                                    isTransitioning = false
                                }
                            }
                        }
                )
            }
        }

        // Section counter overlay
        Text(
            "${currentSection + 1}/${sections.size}",
            color = subtextColor.copy(alpha = 0.3f),
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(start = 24.dp, bottom = 72.dp)
        )
    }
}

private fun drawWineBottle(scope: DrawScope, accent: Color, phase: Float) {
    with(scope) {
        val w = size.width
        val h = size.height
        val bottleWidth = w * 0.35f
        val neckWidth = w * 0.15f

        // Shadow
        drawRoundRect(
            color = Color.Black.copy(alpha = 0.08f),
            topLeft = Offset(w / 2 - bottleWidth / 2 + 6, h * 0.35f + 6),
            size = Size(bottleWidth, h * 0.6f),
            cornerRadius = CornerRadius(8.dp.toPx())
        )

        // Bottle body
        drawRoundRect(
            color = Color(0xFF2D1B0E),
            topLeft = Offset(w / 2 - bottleWidth / 2, h * 0.35f),
            size = Size(bottleWidth, h * 0.6f),
            cornerRadius = CornerRadius(8.dp.toPx())
        )

        // Neck
        drawRoundRect(
            color = Color(0xFF2D1B0E),
            topLeft = Offset(w / 2 - neckWidth / 2, h * 0.08f),
            size = Size(neckWidth, h * 0.32f),
            cornerRadius = CornerRadius(4.dp.toPx())
        )

        // Cap
        drawRoundRect(
            color = accent.copy(alpha = 0.9f),
            topLeft = Offset(w / 2 - neckWidth * 0.7f, h * 0.04f),
            size = Size(neckWidth * 1.4f, h * 0.06f),
            cornerRadius = CornerRadius(3.dp.toPx())
        )

        // Label background
        drawRoundRect(
            color = Color(0xFFF5F0E8),
            topLeft = Offset(w / 2 - bottleWidth * 0.4f, h * 0.48f),
            size = Size(bottleWidth * 0.8f, h * 0.22f),
            cornerRadius = CornerRadius(4.dp.toPx())
        )

        // Label accent stripe
        drawRect(
            color = accent,
            topLeft = Offset(w / 2 - bottleWidth * 0.4f, h * 0.48f),
            size = Size(bottleWidth * 0.8f, h * 0.03f)
        )

        // Decorative lines on label
        for (i in 0..2) {
            val lineY = h * (0.54f + i * 0.05f)
            drawLine(
                color = Color(0xFFD0C8B8),
                start = Offset(w / 2 - bottleWidth * 0.3f, lineY),
                end = Offset(w / 2 + bottleWidth * 0.3f, lineY),
                strokeWidth = 1.dp.toPx()
            )
        }

        // Shine highlight
        drawRoundRect(
            brush = Brush.horizontalGradient(
                listOf(Color.White.copy(alpha = 0f), Color.White.copy(alpha = 0.12f), Color.White.copy(alpha = 0f))
            ),
            topLeft = Offset(w / 2 - bottleWidth * 0.15f, h * 0.35f),
            size = Size(bottleWidth * 0.3f, h * 0.6f),
            cornerRadius = CornerRadius(8.dp.toPx())
        )

        // Subtle bottle glow
        drawCircle(
            brush = Brush.radialGradient(
                listOf(accent.copy(alpha = 0.1f), Color.Transparent),
                center = Offset(w / 2, h * 0.55f),
                radius = w * 0.6f
            ),
            center = Offset(w / 2, h * 0.55f),
            radius = w * 0.6f
        )
    }
}

// Extension to check luminance for text color
private fun Color.luminance(): Float {
    val r = red * 0.299f
    val g = green * 0.587f
    val b = blue * 0.114f
    return r + g + b
}
