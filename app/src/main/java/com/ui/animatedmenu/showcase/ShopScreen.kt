package com.ui.animatedmenu.showcase

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.PI
import kotlin.math.sin

private val ShopRed = Color(0xFFE53935)
private val ShopDark = Color(0xFF1A1A2E)
private val ShopGray = Color(0xFF9E9E9E)
private val ShopBg = Color(0xFFF5F5F5)

data class ShopShoe(
    val name: String,
    val category: String,
    val price: Int,
    val rating: Float,
    val colors: Int,
    val shoeColor: Color
)

private val shopShoes = listOf(
    ShopShoe("German's Shoes", "Men's Shoes", 199, 3.0f, 1, Color(0xFF37474F)),
    ShopShoe("Athletic Shoes", "Men's Shoes", 99, 4.0f, 1, Color(0xFF1565C0)),
    ShopShoe("Classic Runner", "Women's Shoes", 149, 4.5f, 3, Color(0xFFE91E63)),
    ShopShoe("Sport Elite", "Women's Shoes", 179, 3.5f, 2, Color(0xFF4CAF50)),
    ShopShoe("Urban Walker", "Men's Shoes", 129, 4.0f, 2, Color(0xFFFF6F00)),
    ShopShoe("Retro Kick", "Unisex", 89, 4.5f, 4, Color(0xFF7C4DFF)),
)

private val brands = listOf("Nike", "Adidas", "Zara", "Puma", "LV")

@Composable
fun ShopScreen(onBack: () -> Unit) {
    var currentPage by remember { mutableIntStateOf(0) }
    var selectedShoe by remember { mutableIntStateOf(0) }

    Box(modifier = Modifier.fillMaxSize().background(ShopBg)) {
        when (currentPage) {
            0 -> ShopHomePage(onBack, onShoeClick = { selectedShoe = it; currentPage = 1 })
            1 -> ShopDetailPage(shopShoes[selectedShoe], onBack = { currentPage = 0 })
        }
    }
}

@Composable
private fun ShopHomePage(onBack: () -> Unit, onShoeClick: (Int) -> Unit) {
    var selectedBrand by remember { mutableIntStateOf(-1) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 20.dp, end = 20.dp, top = 48.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Avatar
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFE0E0E0)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Filled.Person, "avatar", tint = ShopGray, modifier = Modifier.size(24.dp))
            }
            Spacer(Modifier.width(12.dp))
            Column {
                Text("Hello there,", fontSize = 12.sp, color = ShopGray)
                Text("Mounir", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = ShopDark)
            }
            Spacer(Modifier.weight(1f))
            IconButton(onClick = onBack) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(ShopRed.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Filled.Notifications, "notif", tint = ShopRed,
                        modifier = Modifier.size(20.dp))
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        // Search bar
        Card(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(1.dp)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Filled.Search, "search", tint = ShopGray, modifier = Modifier.size(20.dp))
                Spacer(Modifier.width(8.dp))
                Text("Search Shoes", color = ShopGray, fontSize = 14.sp,
                    modifier = Modifier.weight(1f))
                Icon(Icons.Filled.Tune, "filter", tint = ShopDark, modifier = Modifier.size(20.dp))
            }
        }

        Spacer(Modifier.height(16.dp))

        // 40% OFF Banner
        Card(
            modifier = Modifier.fillMaxWidth().height(130.dp).padding(horizontal = 20.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF3E0))
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    // Background shapes
                    drawCircle(Color(0xFFFFE0B2), size.minDimension * 0.4f,
                        Offset(size.width * 0.8f, size.height * 0.3f))
                    drawCircle(Color(0xFFFFCC80), size.minDimension * 0.25f,
                        Offset(size.width * 0.15f, size.height * 0.6f))
                }
                Row(
                    modifier = Modifier.fillMaxSize().padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.Bottom) {
                            Text("40", fontSize = 40.sp, fontWeight = FontWeight.Black,
                                color = ShopRed)
                            Text("% OFF", fontSize = 18.sp, fontWeight = FontWeight.Bold,
                                color = ShopRed, modifier = Modifier.padding(bottom = 6.dp))
                        }
                        Spacer(Modifier.height(4.dp))
                        Text("Limited time offer", fontSize = 12.sp, color = ShopGray)
                    }
                    Spacer(Modifier.weight(1f))
                    // Shoe illustration
                    Canvas(modifier = Modifier.size(80.dp)) {
                        drawShoeSide(Color(0xFFBDBDBD), size.width, size.height)
                    }
                }
                // Shop now button
                Button(
                    onClick = {},
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(12.dp)
                        .height(28.dp),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = ShopRed)
                ) {
                    Text("Shop now", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(Modifier.height(20.dp))

        // Popular brands
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Popular brands", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = ShopDark)
            Text("See All", fontSize = 12.sp, color = ShopRed, fontWeight = FontWeight.SemiBold)
        }
        Spacer(Modifier.height(12.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            brands.forEachIndexed { index, brand ->
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.clickable { selectedBrand = index }
                ) {
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .clip(CircleShape)
                            .background(Color.White)
                            .border(
                                if (index == selectedBrand) 2.dp else 1.dp,
                                if (index == selectedBrand) ShopRed else Color(0xFFE0E0E0),
                                CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Canvas(modifier = Modifier.size(28.dp)) {
                            drawBrandLogo(brand, size.width, size.height)
                        }
                    }
                    Spacer(Modifier.height(4.dp))
                    Text(brand, fontSize = 10.sp, color = ShopGray)
                }
            }
        }

        Spacer(Modifier.height(20.dp))

        // Men's Shoes section
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Men's Shoes", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = ShopDark)
            Text("See All", fontSize = 12.sp, color = ShopRed, fontWeight = FontWeight.SemiBold)
        }
        Spacer(Modifier.height(12.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            shopShoes.filter { it.category == "Men's Shoes" }.forEachIndexed { index, shoe ->
                ShoeCard(shoe, onClick = {
                    onShoeClick(shopShoes.indexOf(shoe))
                })
            }
        }

        Spacer(Modifier.height(20.dp))

        // Women's Shoes section
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Women's Shoes", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = ShopDark)
            Text("See All", fontSize = 12.sp, color = ShopRed, fontWeight = FontWeight.SemiBold)
        }
        Spacer(Modifier.height(12.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            shopShoes.filter { it.category == "Women's Shoes" }.forEachIndexed { index, shoe ->
                ShoeCard(shoe, onClick = {
                    onShoeClick(shopShoes.indexOf(shoe))
                })
            }
        }

        Spacer(Modifier.height(80.dp))
    }

    // Bottom navigation
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.BottomCenter) {
        Card(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 16.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = {}) {
                    Icon(Icons.Filled.BookmarkBorder, "save", tint = ShopGray)
                }
                IconButton(onClick = {}) {
                    Icon(Icons.Filled.ShoppingCart, "cart", tint = ShopGray)
                }
                // Center home button
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(ShopRed),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Filled.Home, "home", tint = Color.White, modifier = Modifier.size(24.dp))
                }
                IconButton(onClick = {}) {
                    Icon(Icons.Filled.FavoriteBorder, "fav", tint = ShopGray)
                }
                IconButton(onClick = {}) {
                    Icon(Icons.Filled.PersonOutline, "profile", tint = ShopGray)
                }
            }
        }
    }
}

@Composable
private fun ShoeCard(shoe: ShopShoe, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .width(160.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(3.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            // Image area
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFF5F5F5)),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.size(80.dp, 50.dp)) {
                    drawShoeSide(shoe.shoeColor, size.width, size.height)
                }
                // Heart icon
                Icon(Icons.Filled.FavoriteBorder, "fav",
                    tint = ShopGray.copy(alpha = 0.5f),
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(4.dp)
                        .size(18.dp))
            }
            Spacer(Modifier.height(8.dp))
            Text(shoe.name, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = ShopDark,
                maxLines = 1)
            // Stars
            Row {
                repeat(5) { i ->
                    Icon(
                        if (i < shoe.rating.toInt()) Icons.Filled.Star else Icons.Filled.StarBorder,
                        "star",
                        tint = if (i < shoe.rating.toInt()) Color(0xFFFFB300) else Color(0xFFE0E0E0),
                        modifier = Modifier.size(12.dp)
                    )
                }
            }
            Text("${shoe.colors} color${if (shoe.colors > 1) "s" else ""}", fontSize = 10.sp,
                color = ShopGray)
            Spacer(Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("$${shoe.price}", fontSize = 16.sp, fontWeight = FontWeight.Bold,
                    color = ShopDark)
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(ShopRed),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Filled.ShoppingCart, "cart", tint = Color.White,
                        modifier = Modifier.size(14.dp))
                }
            }
        }
    }
}

@Composable
private fun ShopDetailPage(shoe: ShopShoe, onBack: () -> Unit) {
    var selectedSize by remember { mutableIntStateOf(2) }
    val sizes = listOf("38", "39", "40", "41", "42", "43")

    val inf = rememberInfiniteTransition(label = "detail")
    val floatY by inf.animateFloat(0f, 10f,
        infiniteRepeatable(tween(3000, easing = FastOutSlowInEasing), RepeatMode.Reverse), label = "f")

    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())
    ) {
        // Product image area
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
                .background(shoe.shoeColor.copy(alpha = 0.08f)),
            contentAlignment = Alignment.Center
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier.align(Alignment.TopStart).padding(start = 16.dp, top = 48.dp)
            ) {
                Icon(Icons.Filled.ArrowBack, "back", tint = ShopDark)
            }
            IconButton(
                onClick = {},
                modifier = Modifier.align(Alignment.TopEnd).padding(end = 16.dp, top = 48.dp)
            ) {
                Icon(Icons.Filled.FavoriteBorder, "fav", tint = ShopDark)
            }

            Canvas(modifier = Modifier
                .size(200.dp, 120.dp)
                .offset(y = (-floatY).dp)
            ) {
                drawShoeSide(shoe.shoeColor, size.width, size.height)
            }
        }

        Column(modifier = Modifier.padding(24.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(shoe.name, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = ShopDark)
                Text("$${shoe.price}", fontSize = 24.sp, fontWeight = FontWeight.Bold,
                    color = ShopRed)
            }
            Text(shoe.category, fontSize = 14.sp, color = ShopGray)

            Spacer(Modifier.height(4.dp))
            // Stars
            Row {
                repeat(5) { i ->
                    Icon(
                        if (i < shoe.rating.toInt()) Icons.Filled.Star else Icons.Filled.StarBorder,
                        "star",
                        tint = if (i < shoe.rating.toInt()) Color(0xFFFFB300) else Color(0xFFE0E0E0),
                        modifier = Modifier.size(16.dp)
                    )
                }
                Spacer(Modifier.width(4.dp))
                Text("(${shoe.rating})", fontSize = 12.sp, color = ShopGray)
            }

            Spacer(Modifier.height(20.dp))

            Text("Select Size", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = ShopDark)
            Spacer(Modifier.height(12.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                sizes.forEachIndexed { index, size ->
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                if (index == selectedSize) ShopRed else Color.White
                            )
                            .border(
                                1.dp,
                                if (index == selectedSize) ShopRed else Color(0xFFE0E0E0),
                                RoundedCornerShape(12.dp)
                            )
                            .clickable { selectedSize = index },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(size, fontSize = 13.sp, fontWeight = FontWeight.SemiBold,
                            color = if (index == selectedSize) Color.White else ShopDark)
                    }
                }
            }

            Spacer(Modifier.height(20.dp))

            Text("Description", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = ShopDark)
            Spacer(Modifier.height(8.dp))
            Text(
                "Premium quality footwear designed for comfort and style. Features breathable mesh upper, cushioned insole, and durable rubber outsole for all-day wear.",
                fontSize = 13.sp, color = ShopGray, lineHeight = 20.sp
            )

            Spacer(Modifier.height(24.dp))

            // Add to cart and buy buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = {},
                    modifier = Modifier.weight(1f).height(48.dp),
                    shape = RoundedCornerShape(14.dp),
                    border = ButtonDefaults.outlinedButtonBorder
                ) {
                    Icon(Icons.Filled.ShoppingCart, "cart", modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("Add to Cart")
                }
                Button(
                    onClick = {},
                    modifier = Modifier.weight(1f).height(48.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = ShopRed)
                ) {
                    Text("Buy Now", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

private fun DrawScope.drawShoeSide(color: Color, w: Float, h: Float) {
    val darker = color.copy(
        red = (color.red * 0.7f).coerceIn(0f, 1f),
        green = (color.green * 0.7f).coerceIn(0f, 1f),
        blue = (color.blue * 0.7f).coerceIn(0f, 1f)
    )

    // Sole
    val solePath = Path().apply {
        moveTo(w * 0.05f, h * 0.75f)
        cubicTo(w * 0.0f, h * 0.85f, w * 0.1f, h * 0.95f, w * 0.2f, h * 0.9f)
        lineTo(w * 0.9f, h * 0.9f)
        cubicTo(w * 1f, h * 0.9f, w * 1f, h * 0.75f, w * 0.95f, h * 0.7f)
        lineTo(w * 0.05f, h * 0.75f)
        close()
    }
    drawPath(solePath, darker)

    // Midsole
    drawRoundRect(Color.White, Offset(w * 0.08f, h * 0.65f), Size(w * 0.87f, h * 0.12f),
        CornerRadius(6f))

    // Shoe body
    val bodyPath = Path().apply {
        moveTo(w * 0.05f, h * 0.68f)
        cubicTo(w * 0.0f, h * 0.5f, w * 0.05f, h * 0.25f, w * 0.2f, h * 0.2f)
        lineTo(w * 0.45f, h * 0.15f)
        cubicTo(w * 0.5f, h * 0.1f, w * 0.55f, h * 0.1f, w * 0.6f, h * 0.15f)
        lineTo(w * 0.6f, h * 0.35f)
        cubicTo(w * 0.65f, h * 0.3f, w * 0.85f, h * 0.35f, w * 0.95f, h * 0.5f)
        lineTo(w * 0.95f, h * 0.68f)
        close()
    }
    drawPath(bodyPath, color)

    // Swoosh/stripe
    val swooshPath = Path().apply {
        moveTo(w * 0.15f, h * 0.55f)
        cubicTo(w * 0.3f, h * 0.35f, w * 0.65f, h * 0.38f, w * 0.85f, h * 0.5f)
        lineTo(w * 0.8f, h * 0.55f)
        cubicTo(w * 0.6f, h * 0.45f, w * 0.35f, h * 0.42f, w * 0.2f, h * 0.58f)
        close()
    }
    drawPath(swooshPath, Color.White.copy(alpha = 0.3f))

    // Lace holes
    for (i in 0..2) {
        val x = w * (0.35f + i * 0.07f)
        val y = h * (0.25f + i * 0.05f)
        drawCircle(Color.White.copy(alpha = 0.4f), 3f, Offset(x, y))
    }
}

private fun DrawScope.drawBrandLogo(brand: String, w: Float, h: Float) {
    when (brand) {
        "Nike" -> {
            // Swoosh
            val path = Path().apply {
                moveTo(w * 0.1f, h * 0.6f)
                cubicTo(w * 0.2f, h * 0.3f, w * 0.6f, h * 0.25f, w * 0.9f, h * 0.2f)
                lineTo(w * 0.85f, h * 0.3f)
                cubicTo(w * 0.55f, h * 0.35f, w * 0.25f, h * 0.5f, w * 0.2f, h * 0.65f)
                close()
            }
            drawPath(path, Color.Black)
        }
        "Adidas" -> {
            // Three stripes
            for (i in 0..2) {
                val x = w * (0.2f + i * 0.2f)
                drawLine(Color.Black, Offset(x, h * 0.7f), Offset(x + w * 0.08f, h * 0.2f), 3f)
            }
        }
        "Zara" -> {
            // Z shape
            drawLine(Color.Black, Offset(w * 0.2f, h * 0.25f), Offset(w * 0.8f, h * 0.25f), 3f)
            drawLine(Color.Black, Offset(w * 0.8f, h * 0.25f), Offset(w * 0.2f, h * 0.75f), 3f)
            drawLine(Color.Black, Offset(w * 0.2f, h * 0.75f), Offset(w * 0.8f, h * 0.75f), 3f)
        }
        "Puma" -> {
            // Leaping cat simplified
            val path = Path().apply {
                moveTo(w * 0.2f, h * 0.7f)
                cubicTo(w * 0.3f, h * 0.4f, w * 0.5f, h * 0.2f, w * 0.8f, h * 0.3f)
                lineTo(w * 0.7f, h * 0.35f)
                cubicTo(w * 0.5f, h * 0.3f, w * 0.35f, h * 0.5f, w * 0.3f, h * 0.7f)
                close()
            }
            drawPath(path, Color.Black)
        }
        "LV" -> {
            // L and V letters
            drawLine(Color.Black, Offset(w * 0.15f, h * 0.2f), Offset(w * 0.15f, h * 0.7f), 3f)
            drawLine(Color.Black, Offset(w * 0.15f, h * 0.7f), Offset(w * 0.4f, h * 0.7f), 3f)
            drawLine(Color.Black, Offset(w * 0.5f, h * 0.2f), Offset(w * 0.65f, h * 0.7f), 3f)
            drawLine(Color.Black, Offset(w * 0.65f, h * 0.7f), Offset(w * 0.85f, h * 0.2f), 3f)
        }
    }
}
