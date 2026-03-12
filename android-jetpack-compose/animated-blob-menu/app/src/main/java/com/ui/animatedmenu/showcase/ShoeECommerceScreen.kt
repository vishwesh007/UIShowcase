package com.ui.animatedmenu.showcase

import androidx.compose.animation.core.*
import androidx.compose.foundation.*
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import kotlin.math.*

private data class ECommerceShoe(
    val name: String,
    val subtitle: String,
    val price: Double,
    val bgColor: Color,
    val shoeBodyColor: Color,
    val swooshColor: Color,
    val soleColor: Color,
    val sizes: List<Double>,
    val colorOptions: List<Color>,
    val rating: Float
)

private val eCommerceShoes = listOf(
    ECommerceShoe(
        "Nike Shoes Sneakers", "Men's shoes", 189.99,
        Color(0xFF1565C0), Color(0xFF1B5E20), Color(0xFFFFD600),
        Color(0xFFE0E0E0), listOf(39.5, 40.5, 41.5, 42.5),
        listOf(Color(0xFF1565C0), Color(0xFFFF6D00), Color(0xFF388E3C), Color(0xFF212121)),
        4.8f
    ),
    ECommerceShoe(
        "Nike Kyrie 1 Letterman", "Men's shoes", 160.99,
        Color(0xFFE65100), Color(0xFF1565C0), Color(0xFFFF6D00),
        Color(0xFFF5F5F5), listOf(39.5, 40.5, 41.5, 42.5),
        listOf(Color(0xFFE65100), Color(0xFF1565C0), Color(0xFFD32F2F), Color(0xFF7B1FA2)),
        4.7f
    ),
    ECommerceShoe(
        "Nike Air Max 90", "Men's shoes", 149.99,
        Color(0xFF4CAF50), Color(0xFFFFEB3B), Color(0xFF2196F3),
        Color(0xFFF5F5F5), listOf(39.5, 40.0, 41.5, 42.5, 43.0),
        listOf(Color(0xFF4CAF50), Color(0xFFFF9800), Color(0xFF9C27B0), Color(0xFF607D8B)),
        4.9f
    ),
    ECommerceShoe(
        "Nike React Infinity", "Women's shoes", 170.99,
        Color(0xFF7B1FA2), Color(0xFFE91E63), Color(0xFF00BCD4),
        Color(0xFFEEEEEE), listOf(36.0, 37.5, 38.0, 39.0, 40.0),
        listOf(Color(0xFF7B1FA2), Color(0xFFE91E63), Color(0xFF00BCD4), Color(0xFF455A64)),
        4.6f
    ),
)

@Composable
fun ShoeECommerceScreen(onBack: () -> Unit) {
    var currentPage by remember { mutableIntStateOf(0) } // 0=home, 1=detail
    var selectedShoe by remember { mutableIntStateOf(0) }
    var selectedCategory by remember { mutableIntStateOf(0) }

    val inf = rememberInfiniteTransition(label = "shoe")
    val floatY by inf.animateFloat(-6f, 6f,
        infiniteRepeatable(tween(2500, easing = FastOutSlowInEasing), RepeatMode.Reverse), label = "floatY")
    val shimmer by inf.animateFloat(0f, 6.28f,
        infiniteRepeatable(tween(4000, easing = LinearEasing)), label = "shimmer")

    val headerAlpha = remember { Animatable(0f) }
    val contentAlpha = remember { Animatable(0f) }
    val contentScale = remember { Animatable(0.85f) }

    LaunchedEffect(currentPage) {
        headerAlpha.snapTo(0f)
        contentAlpha.snapTo(0f)
        contentScale.snapTo(0.85f)
        launch { headerAlpha.animateTo(1f, tween(500)) }
        launch { contentAlpha.animateTo(1f, tween(600, 150)) }
        launch { contentScale.animateTo(1f, spring(Spring.DampingRatioMediumBouncy)) }
    }

    Box(modifier = Modifier.fillMaxSize().background(
        if (currentPage == 0) Color(0xFFF3E5F5) else eCommerceShoes[selectedShoe].bgColor
    )) {
        if (currentPage == 0) {
            ShoeHomePage(
                headerAlpha = headerAlpha.value,
                contentAlpha = contentAlpha.value,
                contentScale = contentScale.value,
                floatY = floatY,
                shimmer = shimmer,
                selectedCategory = selectedCategory,
                onCategorySelected = { selectedCategory = it },
                onShoeSelected = { idx ->
                    selectedShoe = idx
                    currentPage = 1
                },
                onBack = onBack
            )
        } else {
            ShoeDetailPage(
                shoe = eCommerceShoes[selectedShoe],
                headerAlpha = headerAlpha.value,
                contentAlpha = contentAlpha.value,
                contentScale = contentScale.value,
                floatY = floatY,
                onBack = { currentPage = 0 }
            )
        }
    }
}

@Composable
private fun ShoeHomePage(
    headerAlpha: Float,
    contentAlpha: Float,
    contentScale: Float,
    floatY: Float,
    shimmer: Float,
    selectedCategory: Int,
    onCategorySelected: (Int) -> Unit,
    onShoeSelected: (Int) -> Unit,
    onBack: () -> Unit
) {
    val categories = listOf("All", "Running", "Lifestyle", "Sport")

    Column(
        modifier = Modifier.fillMaxSize().statusBarsPadding().verticalScroll(rememberScrollState())
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp).alpha(headerAlpha),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.Filled.ArrowBack, "Back", tint = Color(0xFF212121))
            }
            Spacer(Modifier.weight(1f))
            IconButton(onClick = { }) {
                Icon(Icons.Filled.Notifications, "Notifications", tint = Color(0xFF212121))
            }
        }

        // Title
        Text(
            "Experience Fashion with\nOur Shoe Lineup",
            modifier = Modifier.padding(horizontal = 24.dp).alpha(headerAlpha),
            color = Color(0xFF212121), fontSize = 24.sp, fontWeight = FontWeight.ExtraBold,
            lineHeight = 30.sp
        )

        Spacer(Modifier.height(16.dp))

        // Search bar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .alpha(headerAlpha)
                .clip(RoundedCornerShape(16.dp))
                .background(Color.White)
                .padding(14.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Filled.Search, "Search", tint = Color(0xFF9E9E9E), modifier = Modifier.size(20.dp))
                Spacer(Modifier.width(10.dp))
                Text("Search", color = Color(0xFF9E9E9E), fontSize = 14.sp)
            }
        }

        Spacer(Modifier.height(20.dp))

        // "New Collection" label
        Text(
            "New Collection",
            modifier = Modifier.padding(horizontal = 24.dp).alpha(contentAlpha),
            color = Color(0xFF212121), fontSize = 20.sp, fontWeight = FontWeight.Bold
        )

        Spacer(Modifier.height(12.dp))

        // Category tabs
        LazyRow(
            contentPadding = PaddingValues(horizontal = 24.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.alpha(contentAlpha)
        ) {
            itemsIndexed(categories) { idx, cat ->
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(if (idx == selectedCategory) Color(0xFF212121) else Color.White)
                        .clickable { onCategorySelected(idx) }
                        .padding(horizontal = 20.dp, vertical = 10.dp)
                ) {
                    Text(
                        cat,
                        color = if (idx == selectedCategory) Color.White else Color(0xFF616161),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }

        Spacer(Modifier.height(20.dp))

        // Shoe cards grid (2 columns manual)
        Column(
            modifier = Modifier.padding(horizontal = 24.dp).alpha(contentAlpha).scale(contentScale),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            for (row in 0 until (eCommerceShoes.size + 1) / 2) {
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    for (col in 0..1) {
                        val idx = row * 2 + col
                        if (idx < eCommerceShoes.size) {
                            ShoeGridCard(
                                shoe = eCommerceShoes[idx],
                                floatY = floatY,
                                modifier = Modifier.weight(1f),
                                onClick = { onShoeSelected(idx) }
                            )
                        } else {
                            Spacer(Modifier.weight(1f))
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(24.dp))
    }
}

@Composable
private fun ShoeGridCard(
    shoe: ECommerceShoe,
    floatY: Float,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .aspectRatio(0.75f)
            .clip(RoundedCornerShape(20.dp))
            .background(Color.White)
            .clickable(onClick = onClick)
            .padding(12.dp)
    ) {
        Column {
            // Heart icon
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.TopEnd) {
                Icon(Icons.Filled.FavoriteBorder, "Like", tint = Color(0xFFBDBDBD), modifier = Modifier.size(20.dp))
            }

            // Shoe drawing
            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(90.dp)
                    .graphicsLayer { translationY = floatY }
            ) {
                drawShoe(shoe.shoeBodyColor, shoe.swooshColor, shoe.soleColor, size)
            }

            Spacer(Modifier.height(8.dp))
            Text(shoe.name, color = Color(0xFF212121), fontSize = 13.sp,
                fontWeight = FontWeight.Bold, maxLines = 1)
            Text("$${String.format("%.2f", shoe.price)}", color = Color(0xFF616161),
                fontSize = 12.sp)
        }
    }
}

@Composable
private fun ShoeDetailPage(
    shoe: ECommerceShoe,
    headerAlpha: Float,
    contentAlpha: Float,
    contentScale: Float,
    floatY: Float,
    onBack: () -> Unit
) {
    var selectedColor by remember { mutableIntStateOf(0) }
    var selectedSize by remember { mutableIntStateOf(1) }

    Column(
        modifier = Modifier.fillMaxSize().statusBarsPadding().verticalScroll(rememberScrollState())
    ) {
        // Top bar
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp).alpha(headerAlpha),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Box(
                    modifier = Modifier.size(36.dp).clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Filled.ArrowBack, "Back", tint = Color.White, modifier = Modifier.size(20.dp))
                }
            }
            Spacer(Modifier.weight(1f))
            Box(
                modifier = Modifier.size(36.dp).clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Filled.Favorite, "Like", tint = Color(0xFFFF5252), modifier = Modifier.size(20.dp))
            }
        }

        // Shoe name
        Text(
            shoe.name,
            modifier = Modifier.padding(horizontal = 24.dp).alpha(headerAlpha),
            color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.ExtraBold
        )
        Text(
            shoe.subtitle,
            modifier = Modifier.padding(horizontal = 24.dp).alpha(headerAlpha),
            color = Color.White.copy(alpha = 0.7f), fontSize = 14.sp
        )

        // Big shoe
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(240.dp)
                .alpha(contentAlpha)
                .scale(contentScale),
            contentAlignment = Alignment.Center
        ) {
            // Shadow
            Canvas(modifier = Modifier.fillMaxSize()) {
                drawOval(
                    Color.Black.copy(alpha = 0.15f),
                    topLeft = Offset(size.width * 0.2f, size.height * 0.82f),
                    size = Size(size.width * 0.6f, size.height * 0.06f)
                )
            }
            Canvas(
                modifier = Modifier
                    .size(200.dp)
                    .rotate(-15f)
                    .graphicsLayer { translationY = floatY * 2f }
            ) {
                drawShoe(shoe.shoeBodyColor, shoe.swooshColor, shoe.soleColor, size, isLarge = true)
            }
        }

        // Price section
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp).alpha(contentAlpha),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("PRICE", color = Color.White.copy(alpha = 0.6f), fontSize = 12.sp,
                    fontWeight = FontWeight.Medium, letterSpacing = 1.sp)
                Spacer(Modifier.height(4.dp))
                Text("$${String.format("%.2f", shoe.price)}", color = Color.White,
                    fontSize = 28.sp, fontWeight = FontWeight.ExtraBold)
            }
            Spacer(Modifier.weight(1f))
            Column(horizontalAlignment = Alignment.End) {
                Text("COLORS", color = Color.White.copy(alpha = 0.6f), fontSize = 12.sp,
                    fontWeight = FontWeight.Medium, letterSpacing = 1.sp)
                Spacer(Modifier.height(4.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    shoe.colorOptions.forEachIndexed { idx, color ->
                        Box(
                            modifier = Modifier
                                .size(if (idx == selectedColor) 28.dp else 24.dp)
                                .clip(CircleShape)
                                .border(
                                    width = if (idx == selectedColor) 2.dp else 0.dp,
                                    color = Color.White,
                                    shape = CircleShape
                                )
                                .background(color)
                                .clickable { selectedColor = idx }
                        )
                    }
                }
            }
        }

        Spacer(Modifier.height(24.dp))

        // Size section
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp).alpha(contentAlpha),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            shoe.sizes.forEachIndexed { idx, size ->
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            if (idx == selectedSize) Color.White
                            else Color.White.copy(alpha = 0.15f)
                        )
                        .clickable { selectedSize = idx },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        size.toString(),
                        color = if (idx == selectedSize) shoe.bgColor else Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(Modifier.height(24.dp))

        // Rating row
        Row(
            modifier = Modifier.padding(horizontal = 24.dp).alpha(contentAlpha),
            verticalAlignment = Alignment.CenterVertically
        ) {
            repeat(5) { idx ->
                Icon(
                    if (idx < shoe.rating.toInt()) Icons.Filled.Star else Icons.Filled.StarBorder,
                    "Star",
                    tint = Color(0xFFFFD600),
                    modifier = Modifier.size(18.dp)
                )
            }
            Text(" ${shoe.rating}", color = Color.White.copy(alpha = 0.8f), fontSize = 14.sp,
                modifier = Modifier.padding(start = 6.dp))
        }

        Spacer(Modifier.height(24.dp))

        // Add to cart button
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .height(52.dp)
                .alpha(contentAlpha)
                .clip(RoundedCornerShape(16.dp))
                .background(Color.White)
                .clickable { },
            contentAlignment = Alignment.Center
        ) {
            Text("Add to cart", color = shoe.bgColor, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(Modifier.height(32.dp))
    }
}

private fun DrawScope.drawShoe(
    bodyColor: Color,
    swooshColor: Color,
    soleColor: Color,
    canvasSize: Size,
    isLarge: Boolean = false
) {
    val w = canvasSize.width
    val h = canvasSize.height
    val strokeW = if (isLarge) 3.dp.toPx() else 2.dp.toPx()

    // Sole
    val solePath = Path().apply {
        moveTo(w * 0.1f, h * 0.75f)
        lineTo(w * 0.95f, h * 0.75f)
        quadraticBezierTo(w * 0.98f, h * 0.75f, w * 0.95f, h * 0.85f)
        lineTo(w * 0.08f, h * 0.85f)
        quadraticBezierTo(w * 0.05f, h * 0.85f, w * 0.08f, h * 0.75f)
        close()
    }
    drawPath(solePath, soleColor)

    // Midsole pattern
    drawRect(
        Color.Black.copy(alpha = 0.08f),
        topLeft = Offset(w * 0.12f, h * 0.78f),
        size = Size(w * 0.8f, h * 0.04f)
    )

    // Shoe body
    val bodyPath = Path().apply {
        moveTo(w * 0.08f, h * 0.75f)
        lineTo(w * 0.08f, h * 0.5f)
        quadraticBezierTo(w * 0.05f, h * 0.3f, w * 0.2f, h * 0.25f)
        // Collar
        quadraticBezierTo(w * 0.35f, h * 0.15f, w * 0.45f, h * 0.2f)
        // Tongue
        quadraticBezierTo(w * 0.48f, h * 0.08f, w * 0.55f, h * 0.15f)
        // Top opening
        lineTo(w * 0.6f, h * 0.22f)
        // Heel area
        quadraticBezierTo(w * 0.75f, h * 0.3f, w * 0.85f, h * 0.4f)
        // Toe box
        quadraticBezierTo(w * 0.98f, h * 0.55f, w * 0.95f, h * 0.75f)
        close()
    }
    drawPath(bodyPath, bodyColor)

    // Swoosh / Nike-like checkmark
    val swooshPath = Path().apply {
        moveTo(w * 0.15f, h * 0.6f)
        quadraticBezierTo(w * 0.35f, h * 0.45f, w * 0.55f, h * 0.5f)
        quadraticBezierTo(w * 0.7f, h * 0.53f, w * 0.85f, h * 0.38f)
        lineTo(w * 0.82f, h * 0.42f)
        quadraticBezierTo(w * 0.65f, h * 0.55f, w * 0.5f, h * 0.54f)
        quadraticBezierTo(w * 0.32f, h * 0.52f, w * 0.15f, h * 0.65f)
        close()
    }
    drawPath(swooshPath, swooshColor)

    // Lace holes
    for (i in 0..3) {
        val hx = w * (0.32f + i * 0.05f)
        val hy = h * (0.28f + i * 0.04f)
        drawCircle(Color.White, radius = if (isLarge) 3.dp.toPx() else 2.dp.toPx(),
            center = Offset(hx, hy))
    }

    // Outline
    drawPath(bodyPath, Color.Black.copy(alpha = 0.15f), style = Stroke(strokeW))

    // Highlight
    val highlightPath = Path().apply {
        moveTo(w * 0.2f, h * 0.35f)
        quadraticBezierTo(w * 0.35f, h * 0.25f, w * 0.5f, h * 0.28f)
    }
    drawPath(highlightPath, Color.White.copy(alpha = 0.3f), style = Stroke(strokeW * 1.5f))
}
