package com.ui.animatedmenu.showcase

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

private val FurniturePrimary = Color(0xFF3949AB)
private val FurnitureAccent = Color(0xFF5C6BC0)
private val FurnitureDark = Color(0xFF1A237E)
private val FurnitureWhite = Color(0xFFF8F9FF)

data class FurnitureItem(
    val name: String,
    val subtitle: String,
    val price: String,
    val bodyColor: Color,
    val colors: List<Color>
)

private val furnitureItems = listOf(
    FurnitureItem("BOSNAS POSSE", "Footstool with storage", "124$", Color(0xFF5C6BC0),
        listOf(Color(0xFFFDD835), Color(0xFF66BB6A), Color(0xFF42A5F5), Color(0xFF3949AB))),
    FurnitureItem("EKERO CHAIR", "Armchair with cushion", "189$", Color(0xFFE57373),
        listOf(Color(0xFFE57373), Color(0xFF90A4AE), Color(0xFF4DB6AC), Color(0xFFFFB74D))),
    FurnitureItem("STRANDMON", "Wing chair",  "259$", Color(0xFF4DB6AC),
        listOf(Color(0xFF4DB6AC), Color(0xFFBA68C8), Color(0xFF7986CB), Color(0xFFFF8A65))),
    FurnitureItem("POANG BENCH", "Rocking chair",  "149$", Color(0xFFFFB74D),
        listOf(Color(0xFFFFB74D), Color(0xFF81C784), Color(0xFFE57373), Color(0xFF64B5F6))),
)

private val categories = listOf(
    "Bathroom" to Icons.Filled.Bathtub,
    "Sofa" to Icons.Filled.Chair,
    "Icebox" to Icons.Filled.Kitchen,
    "Bedroom" to Icons.Filled.Bed,
)

@Composable
fun FurnitureScreen(onBack: () -> Unit) {
    var currentPage by remember { mutableIntStateOf(0) }
    var selectedItem by remember { mutableIntStateOf(0) }

    Box(modifier = Modifier.fillMaxSize().background(FurnitureWhite)) {
        when (currentPage) {
            0 -> FurnitureHomePage(onBack, onItemClick = { selectedItem = it; currentPage = 1 })
            1 -> FurnitureDetailPage(furnitureItems[selectedItem], onBack = { currentPage = 0 })
        }
    }
}

@Composable
private fun FurnitureHomePage(onBack: () -> Unit, onItemClick: (Int) -> Unit) {
    var selectedCategory by remember { mutableIntStateOf(1) } // Sofa selected by default

    Column(modifier = Modifier.fillMaxSize()) {
        // Blue gradient header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp)
                .background(
                    Brush.verticalGradient(
                        listOf(FurniturePrimary, FurnitureAccent)
                    ),
                    RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp)
                )
        ) {
            Column(modifier = Modifier.fillMaxSize().padding(top = 48.dp, start = 24.dp, end = 24.dp)) {
                // Top bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, "back", tint = Color.White)
                    }
                    Text("Furniture", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    IconButton(onClick = {}) {
                        Icon(Icons.Filled.Search, "search", tint = Color.White)
                    }
                }

                Spacer(Modifier.height(20.dp))

                // Category icons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    categories.forEachIndexed { index, (label, icon) ->
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.clickable { selectedCategory = index }
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(52.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (index == selectedCategory)
                                            Color.White
                                        else
                                            Color.White.copy(alpha = 0.2f)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(icon, label,
                                    tint = if (index == selectedCategory) FurniturePrimary
                                    else Color.White,
                                    modifier = Modifier.size(24.dp))
                            }
                            Spacer(Modifier.height(6.dp))
                            Text(label, fontSize = 11.sp,
                                color = if (index == selectedCategory) Color.White
                                else Color.White.copy(alpha = 0.7f),
                                fontWeight = if (index == selectedCategory) FontWeight.Bold
                                else FontWeight.Normal)
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(24.dp))

        // Popular section
        Text("Popular", fontSize = 20.sp, fontWeight = FontWeight.Bold,
            color = FurnitureDark, modifier = Modifier.padding(horizontal = 24.dp))
        Spacer(Modifier.height(16.dp))

        // Furniture grid
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            furnitureItems.forEachIndexed { index, item ->
                FurnitureCard(item, onClick = { onItemClick(index) })
            }
        }

        Spacer(Modifier.height(24.dp))

        // Recent section
        Text("Recently Viewed", fontSize = 18.sp, fontWeight = FontWeight.SemiBold,
            color = FurnitureDark, modifier = Modifier.padding(horizontal = 24.dp))
        Spacer(Modifier.height(12.dp))

        furnitureItems.take(2).forEach { item ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 4.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(item.bodyColor.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Canvas(modifier = Modifier.size(32.dp)) {
                            drawFurnitureIcon(item.bodyColor)
                        }
                    }
                    Spacer(Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(item.name, fontSize = 14.sp, fontWeight = FontWeight.SemiBold,
                            color = FurnitureDark)
                        Text(item.subtitle, fontSize = 12.sp, color = FurnitureDark.copy(alpha = 0.5f))
                    }
                    Text(item.price, fontSize = 16.sp, fontWeight = FontWeight.Bold,
                        color = FurniturePrimary)
                }
            }
        }
    }
}

@Composable
private fun FurnitureCard(item: FurnitureItem, onClick: () -> Unit) {
    val inf = rememberInfiniteTransition(label = "card")
    val float by inf.animateFloat(0f, 8f,
        infiniteRepeatable(tween(3000, easing = FastOutSlowInEasing), RepeatMode.Reverse), label = "float")

    Card(
        modifier = Modifier
            .width(160.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 3D Furniture illustration
            Canvas(modifier = Modifier.size(100.dp).offset(y = (-float).dp)) {
                drawFurniture3D(item.bodyColor, size.width, size.height)
            }

            Spacer(Modifier.height(8.dp))

            Text(item.name, fontSize = 13.sp, fontWeight = FontWeight.Bold,
                color = FurnitureDark, maxLines = 1)
            Text(item.subtitle, fontSize = 11.sp, color = FurnitureDark.copy(alpha = 0.5f),
                maxLines = 1)
            Spacer(Modifier.height(8.dp))
            Text(item.price, fontSize = 18.sp, fontWeight = FontWeight.Bold,
                color = FurniturePrimary)
        }
    }
}

@Composable
private fun FurnitureDetailPage(item: FurnitureItem, onBack: () -> Unit) {
    var selectedColor by remember { mutableIntStateOf(2) }
    var quantity by remember { mutableIntStateOf(1) }

    val inf = rememberInfiniteTransition(label = "detail")
    val rotate by inf.animateFloat(0f, 360f,
        infiniteRepeatable(tween(20000, easing = LinearEasing)), label = "rotate")
    val floatY by inf.animateFloat(0f, 12f,
        infiniteRepeatable(tween(3000, easing = FastOutSlowInEasing), RepeatMode.Reverse), label = "floatY")

    Column(modifier = Modifier.fillMaxSize()) {
        // Top product display
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(350.dp)
                .background(item.bodyColor.copy(alpha = 0.08f)),
            contentAlignment = Alignment.Center
        ) {
            // Back button
            IconButton(
                onClick = onBack,
                modifier = Modifier.align(Alignment.TopStart).padding(start = 16.dp, top = 48.dp)
            ) {
                Icon(Icons.Filled.ArrowBack, "back", tint = FurnitureDark)
            }

            // Favorite button
            IconButton(
                onClick = {},
                modifier = Modifier.align(Alignment.TopEnd).padding(end = 16.dp, top = 48.dp)
            ) {
                Icon(Icons.Filled.FavoriteBorder, "fav", tint = FurnitureDark)
            }

            // Big 3D furniture
            Canvas(modifier = Modifier
                .size(200.dp)
                .offset(y = (-floatY).dp)
            ) {
                drawFurniture3D(
                    item.colors[selectedColor],
                    size.width,
                    size.height
                )
                // Shadow underneath
                drawOval(
                    color = Color.Black.copy(alpha = 0.08f),
                    topLeft = Offset(size.width * 0.2f, size.height * 0.92f),
                    size = Size(size.width * 0.6f, size.height * 0.06f)
                )
            }
        }

        // Product info
        Column(modifier = Modifier.fillMaxWidth().padding(24.dp)) {
            Text(item.name, fontSize = 24.sp, fontWeight = FontWeight.Bold, color = FurnitureDark)
            Text(item.subtitle, fontSize = 14.sp, color = FurnitureDark.copy(alpha = 0.5f))

            Spacer(Modifier.height(16.dp))

            Text(item.price, fontSize = 32.sp, fontWeight = FontWeight.Bold, color = FurniturePrimary)

            Spacer(Modifier.height(20.dp))

            // Color options
            Row(verticalAlignment = Alignment.CenterVertically) {
                item.colors.forEachIndexed { index, color ->
                    Box(
                        modifier = Modifier
                            .size(if (index == selectedColor) 36.dp else 28.dp)
                            .padding(4.dp)
                            .clip(CircleShape)
                            .background(color)
                            .then(
                                if (index == selectedColor)
                                    Modifier.border(2.dp, FurnitureDark, CircleShape)
                                else Modifier
                            )
                            .clickable { selectedColor = index }
                    )
                    Spacer(Modifier.width(8.dp))
                }
            }

            Spacer(Modifier.height(24.dp))

            // Bottom actions
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Add to cart
                TextButton(onClick = {}) {
                    Icon(Icons.Filled.ShoppingCart, "cart", tint = FurniturePrimary,
                        modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("Add to Cart", color = FurniturePrimary, fontWeight = FontWeight.SemiBold)
                }

                // Buy Now
                Button(
                    onClick = {},
                    shape = RoundedCornerShape(24.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = FurniturePrimary),
                    modifier = Modifier.height(48.dp)
                ) {
                    Text("Buy Now", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            }

            Spacer(Modifier.height(16.dp))

            // Color wheel FAB
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterEnd) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .shadow(4.dp, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Canvas(modifier = Modifier.size(48.dp)) {
                        val segments = 6
                        val segColors = listOf(
                            Color(0xFFFF5252), Color(0xFFFF9800), Color(0xFFFFEB3B),
                            Color(0xFF4CAF50), Color(0xFF2196F3), Color(0xFF9C27B0)
                        )
                        segColors.forEachIndexed { i, c ->
                            drawArc(
                                color = c,
                                startAngle = (i * 360f / segments),
                                sweepAngle = 360f / segments,
                                useCenter = true
                            )
                        }
                        drawCircle(Color.White, size.minDimension * 0.2f)
                    }
                }
            }
        }
    }
}

private fun DrawScope.drawFurniture3D(color: Color, w: Float, h: Float) {
    val darker = color.copy(
        red = (color.red * 0.7f).coerceIn(0f, 1f),
        green = (color.green * 0.7f).coerceIn(0f, 1f),
        blue = (color.blue * 0.7f).coerceIn(0f, 1f)
    )
    val lighter = color.copy(
        red = (color.red * 1.2f).coerceIn(0f, 1f),
        green = (color.green * 1.2f).coerceIn(0f, 1f),
        blue = (color.blue * 1.2f).coerceIn(0f, 1f)
    )

    // Cube/ottoman front face
    val frontPath = Path().apply {
        moveTo(w * 0.15f, h * 0.35f)
        lineTo(w * 0.65f, h * 0.35f)
        lineTo(w * 0.65f, h * 0.85f)
        lineTo(w * 0.15f, h * 0.85f)
        close()
    }
    drawPath(frontPath, color)

    // Top face
    val topPath = Path().apply {
        moveTo(w * 0.15f, h * 0.35f)
        lineTo(w * 0.4f, h * 0.2f)
        lineTo(w * 0.9f, h * 0.2f)
        lineTo(w * 0.65f, h * 0.35f)
        close()
    }
    drawPath(topPath, lighter)

    // Right face
    val rightPath = Path().apply {
        moveTo(w * 0.65f, h * 0.35f)
        lineTo(w * 0.9f, h * 0.2f)
        lineTo(w * 0.9f, h * 0.7f)
        lineTo(w * 0.65f, h * 0.85f)
        close()
    }
    drawPath(rightPath, darker)

    // Legs
    val legWidth = w * 0.04f
    val legHeight = h * 0.1f
    // Front left leg
    drawRoundRect(darker, Offset(w * 0.18f, h * 0.85f), Size(legWidth, legHeight),
        CornerRadius(2f))
    // Front right leg
    drawRoundRect(darker, Offset(w * 0.58f, h * 0.85f), Size(legWidth, legHeight),
        CornerRadius(2f))
    // Back right leg (visible)
    drawRoundRect(color.copy(alpha = 0.6f), Offset(w * 0.82f, h * 0.7f), Size(legWidth, legHeight),
        CornerRadius(2f))

    // Front face detail line
    drawLine(Color.White.copy(alpha = 0.2f),
        Offset(w * 0.2f, h * 0.55f), Offset(w * 0.6f, h * 0.55f), 2f)
}

private fun DrawScope.drawFurnitureIcon(color: Color) {
    // Simple cube icon
    val w = size.width
    val h = size.height
    drawRoundRect(color, Offset(w * 0.1f, h * 0.3f), Size(w * 0.6f, h * 0.6f),
        CornerRadius(4f))
    val topPath = Path().apply {
        moveTo(w * 0.1f, h * 0.3f)
        lineTo(w * 0.35f, h * 0.1f)
        lineTo(w * 0.9f, h * 0.1f)
        lineTo(w * 0.7f, h * 0.3f)
        close()
    }
    drawPath(topPath, color.copy(alpha = 0.7f))
}
