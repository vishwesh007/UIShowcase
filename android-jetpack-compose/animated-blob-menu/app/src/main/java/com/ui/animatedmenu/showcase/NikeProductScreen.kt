package com.ui.animatedmenu.showcase

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
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
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

private val NikeBg = Color(0xFFF5F5F5)
private val NikeDark = Color(0xFF111111)
private val NikeOrange = Color(0xFFFA5B30)
private val NikeRed = Color(0xFFE53935)
private val NikeBlue = Color(0xFF1565C0)
private val NikeGreen = Color(0xFF2E7D32)
private val NikePurple = Color(0xFF7C4DFF)
private val NikeTeal = Color(0xFF00838F)

data class ShoeProduct(
    val name: String,
    val tagline: String,
    val price: String,
    val accentColor: Color,
    val swooshRotation: Float = -15f,
    val sizes: List<String> = listOf("7", "8", "9", "10", "11"),
)

private val shoes = listOf(
    ShoeProduct("Air Max 90", "Just Do It", "$149.99", NikeOrange, -12f),
    ShoeProduct("Air Jordan 1", "Wings of Greatness", "$179.99", NikeRed, -18f),
    ShoeProduct("Dunk Low", "Street Heritage", "$119.99", NikeBlue, -10f),
    ShoeProduct("Air Force 1", "The Icon", "$109.99", NikeGreen, -20f),
    ShoeProduct("React Infinity", "Smooth Ride", "$159.99", NikePurple, -15f),
)

@Composable
fun NikeProductScreen(onBack: () -> Unit) {
    var selectedShoeIdx by remember { mutableIntStateOf(0) }
    val selectedShoe = shoes[selectedShoeIdx]

    // Entry animations
    val headerEntry = remember { Animatable(0f) }
    val cardEntry = remember { Animatable(0f) }
    val detailEntry = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        headerEntry.animateTo(1f, tween(500, easing = FastOutSlowInEasing))
    }
    LaunchedEffect(Unit) {
        delay(200)
        cardEntry.animateTo(1f, spring(dampingRatio = 0.6f, stiffness = 200f))
    }
    LaunchedEffect(Unit) {
        delay(500)
        detailEntry.animateTo(1f, tween(400, easing = FastOutSlowInEasing))
    }

    // Color transition
    val accentAnim = animateColorAsState(
        selectedShoe.accentColor,
        animationSpec = tween(500), label = "accent"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(NikeBg)
            .statusBarsPadding()
    ) {
        // Top bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 12.dp)
                .graphicsLayer { alpha = headerEntry.value; translationY = -20f * (1f - headerEntry.value) },
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.Filled.ArrowBack, "Back", tint = NikeDark)
            }
            Spacer(Modifier.weight(1f))
            Text("NIKE", fontSize = 22.sp, fontWeight = FontWeight.Black,
                color = NikeDark, letterSpacing = 4.sp)
            Spacer(Modifier.weight(1f))
            IconButton(onClick = {}) {
                Icon(Icons.Filled.ShoppingBag, "Cart", tint = NikeDark)
            }
        }

        // Main product card
        NikeProductCard(
            shoe = selectedShoe,
            accentColor = accentAnim.value,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .graphicsLayer {
                    scaleX = cardEntry.value
                    scaleY = cardEntry.value
                    alpha = cardEntry.value
                }
        )

        Spacer(Modifier.height(20.dp))

        // Color selector row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .graphicsLayer { alpha = detailEntry.value; translationY = 20f * (1f - detailEntry.value) },
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("COLOR", fontSize = 11.sp, fontWeight = FontWeight.Bold,
                color = NikeDark.copy(alpha = 0.4f), letterSpacing = 2.sp)
            Spacer(Modifier.width(16.dp))
            shoes.forEachIndexed { idx, shoe ->
                val isSelected = idx == selectedShoeIdx
                val dotScale by animateFloatAsState(
                    if (isSelected) 1.3f else 1f,
                    spring(dampingRatio = 0.5f), label = "dot$idx"
                )
                Box(modifier = Modifier
                    .padding(horizontal = 6.dp)
                    .graphicsLayer { scaleX = dotScale; scaleY = dotScale }
                    .size(if (isSelected) 28.dp else 22.dp)
                    .clip(CircleShape)
                    .background(shoe.accentColor)
                    .then(if (isSelected) Modifier.shadow(4.dp, CircleShape) else Modifier)
                    .clickable { selectedShoeIdx = idx },
                    contentAlignment = Alignment.Center) {
                    if (isSelected) {
                        Icon(Icons.Filled.Check, null, tint = Color.White,
                            modifier = Modifier.size(14.dp))
                    }
                }
            }
        }

        Spacer(Modifier.height(20.dp))

        // Size selector
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .graphicsLayer { alpha = detailEntry.value; translationY = 30f * (1f - detailEntry.value) }
        ) {
            Text("SELECT SIZE", fontSize = 11.sp, fontWeight = FontWeight.Bold,
                color = NikeDark.copy(alpha = 0.4f), letterSpacing = 2.sp)
            Spacer(Modifier.height(10.dp))
            var selectedSize by remember { mutableIntStateOf(2) }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                selectedShoe.sizes.forEachIndexed { idx, size ->
                    val isSel = idx == selectedSize
                    val sizeScale by animateFloatAsState(
                        if (isSel) 1.05f else 1f,
                        spring(dampingRatio = 0.5f), label = "sz$idx"
                    )
                    Box(modifier = Modifier
                        .weight(1f)
                        .graphicsLayer { scaleX = sizeScale; scaleY = sizeScale }
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (isSel) accentAnim.value else Color.White)
                        .clickable { selectedSize = idx }
                        .padding(vertical = 14.dp),
                        contentAlignment = Alignment.Center) {
                        Text("US $size",
                            color = if (isSel) Color.White else NikeDark.copy(alpha = 0.7f),
                            fontSize = 13.sp, fontWeight = if (isSel) FontWeight.Bold else FontWeight.Medium)
                    }
                }
            }
        }

        Spacer(Modifier.weight(1f))

        // Bottom CTA
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 20.dp)
                .navigationBarsPadding()
                .graphicsLayer { alpha = detailEntry.value; translationY = 40f * (1f - detailEntry.value) },
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("PRICE", fontSize = 10.sp, fontWeight = FontWeight.Bold,
                    color = NikeDark.copy(alpha = 0.4f), letterSpacing = 1.sp)
                Text(selectedShoe.price, fontSize = 24.sp, fontWeight = FontWeight.Black, color = NikeDark)
            }
            Spacer(Modifier.weight(1f))
            val buttonScale = remember { Animatable(1f) }
            Button(
                onClick = {},
                modifier = Modifier
                    .height(52.dp)
                    .graphicsLayer { scaleX = buttonScale.value; scaleY = buttonScale.value }
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onPress = {
                                buttonScale.animateTo(0.92f, spring(dampingRatio = 0.5f, stiffness = 800f))
                                tryAwaitRelease()
                                buttonScale.animateTo(1f, spring(dampingRatio = 0.4f, stiffness = 400f))
                            }
                        )
                    },
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = NikeDark)
            ) {
                Icon(Icons.Filled.ShoppingCart, null, tint = Color.White, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text("ADD TO BAG", fontSize = 13.sp, fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp)
            }
        }
    }
}

@Composable
private fun NikeProductCard(
    shoe: ShoeProduct,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    var isPressed by remember { mutableStateOf(false) }
    val elevation by animateFloatAsState(
        if (isPressed) 2f else 16f,
        spring(dampingRatio = 0.6f), label = "elev"
    )
    val shoeScale by animateFloatAsState(
        if (isPressed) 0.92f else 1f,
        spring(dampingRatio = 0.5f, stiffness = 400f), label = "shSc"
    )
    val shoeRotation by animateFloatAsState(
        if (isPressed) 5f else shoe.swooshRotation,
        spring(dampingRatio = 0.4f, stiffness = 200f), label = "shRt"
    )
    val bgCircleScale by animateFloatAsState(
        if (isPressed) 1.3f else 1f,
        spring(dampingRatio = 0.6f, stiffness = 300f), label = "bgC"
    )

    // Continuous float animation for shoe
    val inf = rememberInfiniteTransition(label = "shoe")
    val floatY by inf.animateFloat(-6f, 6f,
        infiniteRepeatable(tween(3000, easing = FastOutSlowInEasing), RepeatMode.Reverse), label = "fl")
    val shadowScale by inf.animateFloat(0.9f, 1.1f,
        infiniteRepeatable(tween(3000, easing = FastOutSlowInEasing), RepeatMode.Reverse), label = "shd")

    Box(
        modifier = modifier
            .aspectRatio(1f)
            .shadow(elevation.dp, RoundedCornerShape(28.dp))
            .clip(RoundedCornerShape(28.dp))
            .background(Color.White)
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = {
                        isPressed = true
                        tryAwaitRelease()
                        isPressed = false
                    }
                )
            },
        contentAlignment = Alignment.Center
    ) {
        // Background accent circle
        Canvas(modifier = Modifier.fillMaxSize()) {
            val cx = size.width / 2
            val cy = size.height * 0.42f
            drawCircle(
                accentColor.copy(alpha = 0.12f),
                radius = size.width * 0.32f * bgCircleScale,
                center = Offset(cx, cy)
            )
            drawCircle(
                accentColor.copy(alpha = 0.06f),
                radius = size.width * 0.46f * bgCircleScale,
                center = Offset(cx, cy)
            )
        }

        // Nike Swoosh drawn with Canvas
        Canvas(modifier = Modifier
            .fillMaxWidth(0.65f)
            .aspectRatio(2f)
            .align(Alignment.Center)
            .graphicsLayer {
                scaleX = shoeScale
                scaleY = shoeScale
                rotationZ = shoeRotation
                translationY = floatY
            }
        ) {
            val w = size.width
            val h = size.height

            // Swoosh shadow
            val shadowPath = Path().apply {
                moveTo(w * 0.05f, h * 0.72f)
                cubicTo(w * 0.2f, h * 0.35f, w * 0.55f, h * 0.18f, w * 0.98f, h * 0.15f)
                cubicTo(w * 0.65f, h * 0.38f, w * 0.35f, h * 0.55f, w * 0.08f, h * 0.78f)
                close()
            }
            drawPath(shadowPath, accentColor.copy(alpha = 0.15f))

            // Main swoosh
            val swooshPath = Path().apply {
                moveTo(w * 0.03f, h * 0.68f)
                cubicTo(w * 0.18f, h * 0.28f, w * 0.52f, h * 0.12f, w * 0.97f, h * 0.1f)
                cubicTo(w * 0.63f, h * 0.32f, w * 0.33f, h * 0.5f, w * 0.06f, h * 0.74f)
                close()
            }
            drawPath(swooshPath, accentColor)

            // Highlight on swoosh
            val highlightPath = Path().apply {
                moveTo(w * 0.65f, h * 0.2f)
                cubicTo(w * 0.75f, h * 0.17f, w * 0.88f, h * 0.13f, w * 0.95f, h * 0.12f)
                cubicTo(w * 0.82f, h * 0.22f, w * 0.72f, h * 0.26f, w * 0.65f, h * 0.24f)
                close()
            }
            drawPath(highlightPath, Color.White.copy(alpha = 0.3f))

            // Shoe sole line
            drawLine(
                accentColor.copy(alpha = 0.5f),
                Offset(w * 0.1f, h * 0.82f),
                Offset(w * 0.7f, h * 0.82f),
                strokeWidth = 3f,
                cap = StrokeCap.Round
            )

            // Lace dots
            for (i in 0..3) {
                val lx = w * (0.3f + i * 0.1f)
                val ly = h * (0.25f - i * 0.02f)
                drawCircle(Color.White.copy(alpha = 0.6f), radius = 4f, center = Offset(lx, ly))
            }
        }

        // Ellipse shadow under the shoe
        Canvas(modifier = Modifier
            .fillMaxWidth(0.35f)
            .height(12.dp)
            .align(Alignment.Center)
            .graphicsLayer { translationY = 120f; scaleX = shadowScale }
        ) {
            drawOval(
                Color.Black.copy(alpha = 0.08f),
                topLeft = Offset.Zero,
                size = size
            )
        }

        // Product name overlay — top left
        Column(modifier = Modifier
            .align(Alignment.TopStart)
            .padding(24.dp)
        ) {
            Text("NIKE", fontSize = 10.sp, fontWeight = FontWeight.Bold,
                color = NikeDark.copy(alpha = 0.3f), letterSpacing = 3.sp)
            Spacer(Modifier.height(2.dp))
            Text(shoe.name, fontSize = 22.sp, fontWeight = FontWeight.Black,
                color = NikeDark, letterSpacing = (-0.5).sp)
            Text(shoe.tagline, fontSize = 12.sp, fontWeight = FontWeight.Medium,
                color = accentColor)
        }

        // Favorite button — top right
        var isFav by remember { mutableStateOf(false) }
        val favScale by animateFloatAsState(
            if (isFav) 1f else 0.9f,
            spring(dampingRatio = 0.3f, stiffness = 500f), label = "fav"
        )
        IconButton(
            onClick = { isFav = !isFav },
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(16.dp)
                .graphicsLayer { scaleX = favScale; scaleY = favScale }
                .size(40.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.9f))
        ) {
            Icon(
                if (isFav) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                null,
                tint = if (isFav) NikeRed else NikeDark.copy(alpha = 0.4f),
                modifier = Modifier.size(20.dp)
            )
        }

        // Rating — bottom left
        Row(modifier = Modifier
            .align(Alignment.BottomStart)
            .padding(24.dp),
            verticalAlignment = Alignment.CenterVertically) {
            repeat(5) { i ->
                Icon(
                    if (i < 4) Icons.Filled.Star else Icons.Filled.StarHalf,
                    null,
                    tint = NikeOrange,
                    modifier = Modifier.size(16.dp)
                )
            }
            Spacer(Modifier.width(6.dp))
            Text("4.5", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = NikeDark.copy(alpha = 0.6f))
            Text(" (2.4k)", fontSize = 11.sp, color = NikeDark.copy(alpha = 0.3f))
        }
    }
}
