package com.ui.animatedmenu.showcase

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
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin

// Floating food element descriptor
private data class FloatingFood(
    val emoji: String,
    val baseX: Float,       // 0..1 fraction of width
    val baseY: Float,       // 0..1 fraction of height
    val size: Dp,
    val parallaxSpeed: Float, // How much it moves relative to scroll (0.2 = slow, 1.5 = fast)
    val floatAmplitude: Float,
    val floatFrequency: Float,
    val rotationSpeed: Float,
    val color: Color
)

private val floatingFoods = listOf(
    // Grapefruit (top-left, slow parallax)
    FloatingFood("🍊", 0.05f, 0.18f, 80.dp, 0.3f, 12f, 0.7f, 0.2f, Color(0xFFFF6B35)),
    // Berries cluster (upper-left)
    FloatingFood("🍒", 0.15f, 0.08f, 50.dp, 0.5f, 8f, 1.1f, 0.5f, Color(0xFFE53935)),
    // Honey bottle (right side, medium parallax)
    FloatingFood("🍯", 0.82f, 0.35f, 60.dp, 0.7f, 10f, 0.9f, -0.3f, Color(0xFFFFB300)),
    // Mint leaf (center-left)
    FloatingFood("🍃", 0.08f, 0.55f, 45.dp, 1.0f, 15f, 1.3f, 1.2f, Color(0xFF4CAF50)),
    // Strawberry (bottom-right, fast parallax)
    FloatingFood("🍓", 0.85f, 0.65f, 55.dp, 1.2f, 9f, 0.8f, -0.6f, Color(0xFFE91E63)),
    // Blueberries (bottom-left)
    FloatingFood("🫐", 0.12f, 0.72f, 40.dp, 0.8f, 7f, 1.5f, 0.4f, Color(0xFF3F51B5)),
    // Cherry (top-right)
    FloatingFood("🍒", 0.78f, 0.12f, 42.dp, 0.4f, 11f, 0.6f, -0.8f, Color(0xFFD32F2F)),
    // Lemon slice (center-right, medium)
    FloatingFood("🍋", 0.75f, 0.52f, 48.dp, 0.6f, 13f, 1.0f, 0.7f, Color(0xFFFDD835)),
    // Cookie (bottom center)
    FloatingFood("🍪", 0.45f, 0.82f, 38.dp, 1.3f, 6f, 1.2f, -0.4f, Color(0xFF8D6E63)),
    // Kiwi (left-center)
    FloatingFood("🥝", 0.02f, 0.40f, 52.dp, 0.45f, 10f, 0.85f, 0.6f, Color(0xFF689F38))
)

@Composable
fun DessertParallaxScreen(onBack: () -> Unit) {
    val scope = rememberCoroutineScope()

    // Scroll offset for parallax
    val scrollOffset = remember { Animatable(0f) }
    var totalDrag by remember { mutableFloatStateOf(0f) }

    // Entrance
    var entered by remember { mutableStateOf(false) }
    val entranceAlpha by animateFloatAsState(
        targetValue = if (entered) 1f else 0f,
        animationSpec = tween(800), label = "ea"
    )
    val entranceSlide by animateFloatAsState(
        targetValue = if (entered) 0f else 80f,
        animationSpec = spring(dampingRatio = 0.7f, stiffness = 300f), label = "es"
    )
    LaunchedEffect(Unit) { entered = true }

    // Infinite animations for floating elements
    val inf = rememberInfiniteTransition(label = "dessert")
    val time by inf.animateFloat(0f, (2 * PI).toFloat(),
        infiniteRepeatable(tween(4000, easing = LinearEasing)), label = "time")
    val slowTime by inf.animateFloat(0f, (2 * PI).toFloat(),
        infiniteRepeatable(tween(7000, easing = LinearEasing)), label = "slow")
    val pulseScale by inf.animateFloat(0.97f, 1.03f,
        infiniteRepeatable(tween(2000), RepeatMode.Reverse), label = "pulse")

    // Staggered food entrance
    val foodAlphas = remember { floatingFoods.map { Animatable(0f) } }
    val foodScales = remember { floatingFoods.map { Animatable(0.3f) } }
    LaunchedEffect(Unit) {
        floatingFoods.forEachIndexed { idx, _ ->
            launch {
                kotlinx.coroutines.delay(200L + idx * 120L)
                launch { foodAlphas[idx].animateTo(1f, tween(500, easing = FastOutSlowInEasing)) }
                foodScales[idx].animateTo(1f, spring(dampingRatio = 0.5f, stiffness = 200f))
            }
        }
    }

    val skyBlue = Color(0xFF5BC0EB)
    val skyBlueLight = Color(0xFF8DD6F0)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(skyBlue, skyBlueLight, Color(0xFFE3F6FD))))
            .graphicsLayer {
                alpha = entranceAlpha
                translationY = entranceSlide
            }
            .pointerInput(Unit) {
                detectVerticalDragGestures(
                    onDragEnd = {
                        scope.launch {
                            scrollOffset.animateTo(0f, spring(dampingRatio = 0.6f, stiffness = 200f))
                        }
                        totalDrag = 0f
                    }
                ) { _, dragAmount ->
                    totalDrag += dragAmount
                    scope.launch {
                        scrollOffset.snapTo(totalDrag.coerceIn(-500f, 500f))
                    }
                }
            }
    ) {
        // === BG Layer: Subtle cloud shapes ===
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height
            // Soft cloud circles
            for (i in 0..5) {
                val cx = (i * 193 + 100) % w.toInt()
                val cy = (i * 97 + 50) % (h * 0.4f).toInt()
                val r = 60f + i * 15f
                drawCircle(
                    Color.White.copy(alpha = 0.08f),
                    radius = r + sin(slowTime + i).toFloat() * 5f,
                    center = Offset(
                        cx.toFloat() + sin(slowTime + i * 0.5f).toFloat() * 10f,
                        cy.toFloat() + scrollOffset.value * 0.1f + cos(slowTime + i).toFloat() * 6f
                    )
                )
            }
        }

        // === Floating food elements (each with its own parallax speed) ===
        floatingFoods.forEachIndexed { idx, food ->
            val parallaxOffset = scrollOffset.value * food.parallaxSpeed
            val floatY = sin(time * food.floatFrequency + idx * 0.8f).toFloat() * food.floatAmplitude
            val floatX = cos(time * food.floatFrequency * 0.7f + idx).toFloat() * food.floatAmplitude * 0.5f
            val rotation = time * food.rotationSpeed * 20f

            // Draw food icon as colored circle with symbol
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .wrapContentSize(Alignment.TopStart)
            ) {
                Canvas(
                    modifier = Modifier
                        .offset(x = 0.dp, y = 0.dp)
                        .fillMaxSize()
                        .graphicsLayer {
                            alpha = foodAlphas[idx].value
                            scaleX = foodScales[idx].value
                            scaleY = foodScales[idx].value
                        }
                ) {
                    val centerX = size.width * food.baseX + floatX
                    val centerY = size.height * food.baseY + parallaxOffset + floatY
                    val radius = food.size.toPx() / 2f

                    // Glow behind food
                    drawCircle(
                        color = food.color.copy(alpha = 0.15f),
                        radius = radius * 1.5f,
                        center = Offset(centerX, centerY)
                    )

                    // Main food circle
                    drawCircle(
                        color = food.color.copy(alpha = 0.85f),
                        radius = radius,
                        center = Offset(centerX, centerY)
                    )

                    // Inner lighter highlight
                    drawCircle(
                        color = Color.White.copy(alpha = 0.25f),
                        radius = radius * 0.6f,
                        center = Offset(centerX - radius * 0.15f, centerY - radius * 0.15f)
                    )

                    // Tiny specular highlight
                    drawCircle(
                        color = Color.White.copy(alpha = 0.4f),
                        radius = radius * 0.2f,
                        center = Offset(centerX - radius * 0.25f, centerY - radius * 0.25f)
                    )

                    // Ring outline
                    drawCircle(
                        color = food.color.copy(alpha = 0.4f),
                        radius = radius + 3.dp.toPx(),
                        center = Offset(centerX, centerY),
                        style = Stroke(width = 1.5f.dp.toPx())
                    )
                }
            }
        }

        // === Central Hero: Dessert plate ===
        Canvas(
            modifier = Modifier
                .align(Alignment.Center)
                .offset(x = 20.dp, y = 20.dp)
                .size(220.dp)
                .graphicsLayer {
                    translationY = scrollOffset.value * 0.5f + sin(time * 0.4f).toFloat() * 4f
                    scaleX = pulseScale
                    scaleY = pulseScale
                }
        ) {
            drawDessertPlate(this, time)
        }

        // === Foreground text content ===
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .graphicsLayer {
                    translationY = scrollOffset.value * 0.9f
                }
        ) {
            // Top bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(Icons.Filled.ArrowBack, "Back", tint = Color.White)
                }
                Text(
                    "Angel Delight",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                )
                IconButton(onClick = {}) {
                    Icon(Icons.Filled.Person, "Profile", tint = Color.White)
                }
            }

            // Navigation
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                listOf("Home", "About", "Contact", "Blog").forEach { label ->
                    Text(
                        label,
                        color = Color.White.copy(alpha = 0.85f),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color.White.copy(alpha = 0.2f))
                        .padding(horizontal = 14.dp, vertical = 4.dp)
                ) {
                    Text("Sign up", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(Modifier.height(40.dp))

            // Hero text
            Column(
                modifier = Modifier
                    .padding(horizontal = 28.dp)
                    .graphicsLayer {
                        translationY = -scrollOffset.value * 0.15f // Counter-parallax
                    }
            ) {
                Text(
                    "Delicious",
                    color = Color.White,
                    fontSize = 52.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = (-1.5).sp,
                    fontStyle = FontStyle.Italic
                )
                Text(
                    "Dessert",
                    color = Color(0xFFFFEB3B),
                    fontSize = 52.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = (-1.5).sp,
                    fontStyle = FontStyle.Italic
                )

                Spacer(Modifier.height(16.dp))

                Text(
                    "Stay organized, focused, and achieve\nmore with our powerful productivity",
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 14.sp,
                    lineHeight = 22.sp
                )
            }

            Spacer(Modifier.weight(1f))

            // Bottom area
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 28.dp, vertical = 20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Order button
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(28.dp))
                        .background(Color(0xFFFF6B35))
                        .clickable { }
                        .padding(horizontal = 28.dp, vertical = 14.dp)
                ) {
                    Text("Order Now", color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                }

                // Scroll indicator
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    val bounceY by inf.animateFloat(0f, 1f,
                        infiniteRepeatable(tween(1200), RepeatMode.Reverse), label = "by")
                    Icon(
                        Icons.Filled.KeyboardArrowDown,
                        "Scroll",
                        tint = Color.White.copy(alpha = 0.6f),
                        modifier = Modifier
                            .size(24.dp)
                            .graphicsLayer { translationY = bounceY * 10f }
                    )
                    Text(
                        "Swipe to explore",
                        color = Color.White.copy(alpha = 0.5f),
                        fontSize = 10.sp
                    )
                }
            }

            Spacer(Modifier.height(16.dp))
        }

        // === Decorative swoosh arrow (red, like in video) ===
        Canvas(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .offset(x = (-10).dp, y = 100.dp)
                .size(60.dp, 120.dp)
                .graphicsLayer {
                    translationY = scrollOffset.value * 0.4f
                    alpha = 0.6f
                }
        ) {
            val path = Path().apply {
                moveTo(size.width * 0.8f, 0f)
                cubicTo(
                    size.width * 0.9f, size.height * 0.3f,
                    size.width * 0.2f, size.height * 0.5f,
                    size.width * 0.5f, size.height * 0.85f
                )
            }
            drawPath(path, Color(0xFFE53935), style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round))

            // Arrow head
            val arrowPath = Path().apply {
                moveTo(size.width * 0.35f, size.height * 0.78f)
                lineTo(size.width * 0.5f, size.height * 0.85f)
                lineTo(size.width * 0.55f, size.height * 0.7f)
            }
            drawPath(arrowPath, Color(0xFFE53935), style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round))
        }
    }
}

private fun drawDessertPlate(scope: DrawScope, phase: Float) {
    with(scope) {
        val cx = size.width / 2f
        val cy = size.height / 2f
        val plateR = size.width * 0.42f

        // Plate shadow
        drawCircle(
            Color.Black.copy(alpha = 0.1f),
            radius = plateR + 8.dp.toPx(),
            center = Offset(cx + 4, cy + 6)
        )

        // Plate base (dark)
        drawCircle(
            Brush.radialGradient(
                listOf(Color(0xFF3E3E3E), Color(0xFF2A2A2A)),
                center = Offset(cx, cy),
                radius = plateR
            ),
            radius = plateR,
            center = Offset(cx, cy)
        )

        // Plate rim highlight
        drawCircle(
            Color.White.copy(alpha = 0.08f),
            radius = plateR,
            center = Offset(cx, cy),
            style = Stroke(width = 2.dp.toPx())
        )

        // Inner plate
        drawCircle(
            Brush.radialGradient(
                listOf(Color(0xFF4A4A4A), Color(0xFF353535)),
                center = Offset(cx - 5, cy - 5),
                radius = plateR * 0.8f
            ),
            radius = plateR * 0.75f,
            center = Offset(cx, cy)
        )

        // Tart/pie base (golden brown)
        val tartR = plateR * 0.55f
        drawCircle(
            Brush.radialGradient(
                listOf(Color(0xFFE8C97A), Color(0xFFD4A843)),
                center = Offset(cx, cy),
                radius = tartR
            ),
            radius = tartR,
            center = Offset(cx, cy)
        )

        // Tart crust edge
        drawCircle(
            Color(0xFFC29332),
            radius = tartR,
            center = Offset(cx, cy),
            style = Stroke(width = 3.dp.toPx())
        )

        // Cream/custard filling
        drawCircle(
            Brush.radialGradient(
                listOf(Color(0xFFFFF8E1), Color(0xFFFFECB3)),
                center = Offset(cx, cy),
                radius = tartR * 0.85f
            ),
            radius = tartR * 0.8f,
            center = Offset(cx, cy)
        )

        // Berry decorations on top
        val berryPositions = listOf(
            Offset(cx - 20, cy - 15) to Color(0xFFE53935),  // Strawberry
            Offset(cx + 15, cy - 20) to Color(0xFFE53935),  // Strawberry
            Offset(cx, cy + 10) to Color(0xFFD32F2F),       // Cherry
            Offset(cx - 25, cy + 5) to Color(0xFF3F51B5),   // Blueberry
            Offset(cx + 25, cy) to Color(0xFF3F51B5),       // Blueberry
            Offset(cx + 5, cy - 5) to Color(0xFF3F51B5),    // Blueberry
            Offset(cx - 10, cy + 20) to Color(0xFFE53935),  // Strawberry
        )

        berryPositions.forEach { (pos, color) ->
            val bobble = sin(phase + pos.x * 0.01f).toFloat() * 1.5f
            drawCircle(
                color = color,
                radius = 8.dp.toPx(),
                center = Offset(pos.x, pos.y + bobble)
            )
            // Berry highlight
            drawCircle(
                color = Color.White.copy(alpha = 0.3f),
                radius = 3.dp.toPx(),
                center = Offset(pos.x - 2, pos.y - 3 + bobble)
            )
        }

        // Mint leaf
        val leafPath = Path().apply {
            moveTo(cx + 8, cy - 25)
            quadraticBezierTo(cx + 20, cy - 40, cx + 12, cy - 50)
            quadraticBezierTo(cx + 2, cy - 38, cx + 8, cy - 25)
        }
        drawPath(leafPath, Color(0xFF4CAF50))

        // Plate specular highlight
        drawCircle(
            Brush.radialGradient(
                listOf(Color.White.copy(alpha = 0.12f), Color.Transparent),
                center = Offset(cx - plateR * 0.3f, cy - plateR * 0.3f),
                radius = plateR * 0.5f
            ),
            radius = plateR * 0.5f,
            center = Offset(cx - plateR * 0.3f, cy - plateR * 0.3f)
        )
    }
}
