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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import kotlin.math.*

private data class FoodItem(
    val name: String,
    val price: Double,
    val description: String,
    val rating: Float,
    val category: String,
    val color1: Color,
    val color2: Color,
    val addOns: List<String>
)

private val foodItems = listOf(
    FoodItem("Beef Burger", 20.0, "Big juicy beef burger with cheese, lettuce, tomato, onions and special sauce!",
        4.8f, "Burger", Color(0xFFE8A317), Color(0xFF8B4513), listOf("Extra Cheese", "Bacon", "Jalapeño", "Pickles")),
    FoodItem("Cheese Pizza", 18.0, "Freshly baked pizza with mozzarella, tomato sauce and fresh basil",
        4.7f, "Pizza", Color(0xFFFF6F00), Color(0xFFD32F2F), listOf("Mushrooms", "Olives", "Peppers", "Onions")),
    FoodItem("Noodles", 15.0, "Stir-fried noodles with vegetables, soy sauce and sesame oil",
        4.5f, "All", Color(0xFFFFC107), Color(0xFF795548), listOf("Egg", "Tofu", "Shrimp", "Chili")),
    FoodItem("Caesar Salad", 12.0, "Fresh romaine lettuce with parmesan, croutons and caesar dressing",
        4.6f, "All", Color(0xFF4CAF50), Color(0xFF2E7D32), listOf("Chicken", "Avocado", "Bacon", "Egg")),
    FoodItem("Chicken Wings", 16.0, "Crispy fried chicken wings with BBQ or buffalo sauce",
        4.9f, "All", Color(0xFFFF5722), Color(0xFFBF360C), listOf("Ranch Dip", "Blue Cheese", "Celery", "Extra Sauce")),
)

private data class CartItem(val food: FoodItem, var qty: Int)

@Composable
fun FoodDeliveryScreen(onBack: () -> Unit) {
    var currentPage by remember { mutableIntStateOf(0) } // 0=splash, 1=home, 2=detail, 3=cart
    var selectedFood by remember { mutableIntStateOf(0) }
    val cartItems = remember { mutableStateListOf<CartItem>() }

    val inf = rememberInfiniteTransition(label = "food")
    val floatY by inf.animateFloat(-6f, 6f,
        infiniteRepeatable(tween(2500, easing = FastOutSlowInEasing), RepeatMode.Reverse), label = "float")
    val shimmer by inf.animateFloat(0f, 6.28f,
        infiniteRepeatable(tween(3000, easing = LinearEasing)), label = "shimmer")

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

    when (currentPage) {
        0 -> FoodSplashPage(
            headerAlpha = headerAlpha.value,
            contentAlpha = contentAlpha.value,
            floatY = floatY,
            onGetStarted = { currentPage = 1 },
            onBack = onBack
        )
        1 -> FoodHomePage(
            headerAlpha = headerAlpha.value,
            contentAlpha = contentAlpha.value,
            contentScale = contentScale.value,
            floatY = floatY,
            shimmer = shimmer,
            cartCount = cartItems.sumOf { it.qty },
            onFoodSelected = { idx -> selectedFood = idx; currentPage = 2 },
            onCartClicked = { currentPage = 3 },
            onBack = { currentPage = 0 }
        )
        2 -> FoodDetailPage(
            food = foodItems[selectedFood],
            headerAlpha = headerAlpha.value,
            contentAlpha = contentAlpha.value,
            contentScale = contentScale.value,
            floatY = floatY,
            onAddToCart = { qty ->
                val existing = cartItems.find { it.food.name == foodItems[selectedFood].name }
                if (existing != null) existing.qty += qty
                else cartItems.add(CartItem(foodItems[selectedFood], qty))
                currentPage = 1
            },
            onBack = { currentPage = 1 }
        )
        3 -> FoodCartPage(
            cartItems = cartItems,
            headerAlpha = headerAlpha.value,
            contentAlpha = contentAlpha.value,
            onRemove = { idx -> cartItems.removeAt(idx) },
            onBack = { currentPage = 1 }
        )
    }
}

@Composable
private fun FoodSplashPage(
    headerAlpha: Float,
    contentAlpha: Float,
    floatY: Float,
    onGetStarted: () -> Unit,
    onBack: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize().background(
            Brush.verticalGradient(listOf(Color(0xFF7B1FA2), Color(0xFF5C1689)))
        )
    ) {
        Column(
            modifier = Modifier.fillMaxSize().statusBarsPadding().padding(24.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Top bar
            Row(
                modifier = Modifier.fillMaxWidth().alpha(headerAlpha),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(Icons.Filled.ArrowBack, "Back", tint = Color.White)
                }
            }

            // Center content
            Column(
                modifier = Modifier.alpha(contentAlpha),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text("Food\nOrdering\nApp Design",
                    color = Color.White, fontSize = 36.sp, fontWeight = FontWeight.ExtraBold,
                    lineHeight = 42.sp)

                // Food plate illustration
                Canvas(
                    modifier = Modifier
                        .size(200.dp)
                        .align(Alignment.CenterHorizontally)
                        .graphicsLayer { translationY = floatY * 2f }
                ) {
                    // Plate
                    drawCircle(Color.White.copy(alpha = 0.15f), radius = size.width / 2.2f)
                    drawCircle(Color.White.copy(alpha = 0.1f), radius = size.width / 2.5f)
                    // Burger
                    val bw = size.width * 0.5f
                    val bx = center.x - bw / 2
                    val by = center.y - bw * 0.3f
                    // Bottom bun
                    drawRoundRect(Color(0xFFE8A317), Offset(bx, by + bw * 0.35f),
                        Size(bw, bw * 0.18f), CornerRadius(8f, 8f))
                    // Patty
                    drawRoundRect(Color(0xFF5D4037), Offset(bx + bw * 0.05f, by + bw * 0.25f),
                        Size(bw * 0.9f, bw * 0.12f), CornerRadius(4f, 4f))
                    // Lettuce
                    drawRoundRect(Color(0xFF4CAF50), Offset(bx - bw * 0.02f, by + bw * 0.2f),
                        Size(bw * 1.04f, bw * 0.08f), CornerRadius(4f, 4f))
                    // Cheese
                    drawRoundRect(Color(0xFFFFC107), Offset(bx + bw * 0.02f, by + bw * 0.15f),
                        Size(bw * 0.96f, bw * 0.08f), CornerRadius(2f, 2f))
                    // Top bun
                    drawArc(Color(0xFFD4930A), 180f, 180f,
                        useCenter = true,
                        topLeft = Offset(bx, by - bw * 0.05f),
                        size = Size(bw, bw * 0.4f))
                    // Sesame seeds
                    for (s in 0..4) {
                        val sx = bx + bw * (0.2f + s * 0.15f)
                        val sy = by + bw * 0.03f
                        drawOval(Color(0xFFFFF9C4), Offset(sx, sy), Size(4.dp.toPx(), 2.5.dp.toPx()))
                    }
                }

                Spacer(Modifier.height(8.dp))

                Text("Enjoy\nYour Food",
                    color = Color.White.copy(alpha = 0.9f), fontSize = 22.sp,
                    fontWeight = FontWeight.Medium, lineHeight = 28.sp)
            }

            // Bottom
            Column(modifier = Modifier.alpha(contentAlpha)) {
                // Popular section
                Text("Popular", color = Color.White.copy(alpha = 0.7f), fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    foodItems.take(3).forEach { food ->
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color.White.copy(alpha = 0.12f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Canvas(modifier = Modifier.size(40.dp)) {
                                drawCircle(food.color1.copy(alpha = 0.6f), radius = size.width / 2.2f)
                                drawCircle(food.color2, radius = size.width / 3.5f)
                            }
                        }
                    }
                }

                Spacer(Modifier.height(16.dp))

                // Get Started button
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color.White)
                        .clickable(onClick = onGetStarted),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Get Started", color = Color(0xFF7B1FA2), fontSize = 16.sp,
                        fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun FoodHomePage(
    headerAlpha: Float,
    contentAlpha: Float,
    contentScale: Float,
    floatY: Float,
    shimmer: Float,
    cartCount: Int,
    onFoodSelected: (Int) -> Unit,
    onCartClicked: () -> Unit,
    onBack: () -> Unit
) {
    var selectedCategory by remember { mutableIntStateOf(0) }
    val categories = listOf("All", "Burger", "Pizza")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F6FF))
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
    ) {
        // Top bar
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp).alpha(headerAlpha),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.Filled.ArrowBack, "Back", tint = Color(0xFF212121))
            }
            Spacer(Modifier.weight(1f))
            Box {
                IconButton(onClick = onCartClicked) {
                    Icon(Icons.Filled.ShoppingCart, "Cart", tint = Color(0xFF7B1FA2))
                }
                if (cartCount > 0) {
                    Box(
                        modifier = Modifier.size(18.dp).clip(CircleShape)
                            .background(Color(0xFFFF5252))
                            .align(Alignment.TopEnd),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("$cartCount", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Search bar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .alpha(headerAlpha)
                .clip(RoundedCornerShape(12.dp))
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

        // Category icons
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp).alpha(contentAlpha),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            categories.forEachIndexed { idx, cat ->
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.clickable { selectedCategory = idx }
                ) {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(
                                if (idx == selectedCategory) Color(0xFF7B1FA2)
                                else Color.White
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Canvas(modifier = Modifier.size(32.dp)) {
                            when (idx) {
                                0 -> { // All - grid dots
                                    for (r in 0..1) for (c in 0..1) {
                                        drawCircle(
                                            if (selectedCategory == 0) Color.White else Color(0xFF7B1FA2),
                                            radius = 4.dp.toPx(),
                                            center = Offset(
                                                size.width * (0.3f + c * 0.4f),
                                                size.height * (0.3f + r * 0.4f)
                                            )
                                        )
                                    }
                                }
                                1 -> { // Burger
                                    val iconColor = if (selectedCategory == 1) Color.White else Color(0xFFE8A317)
                                    drawRoundRect(iconColor, Offset(size.width * 0.15f, size.height * 0.55f),
                                        Size(size.width * 0.7f, size.height * 0.12f), CornerRadius(4f))
                                    drawRoundRect(iconColor.copy(alpha = 0.8f), Offset(size.width * 0.2f, size.height * 0.42f),
                                        Size(size.width * 0.6f, size.height * 0.1f), CornerRadius(2f))
                                    drawArc(iconColor, 180f, 180f, true,
                                        Offset(size.width * 0.15f, size.height * 0.15f),
                                        Size(size.width * 0.7f, size.height * 0.5f))
                                }
                                2 -> { // Pizza
                                    val iconColor = if (selectedCategory == 2) Color.White else Color(0xFFFF6F00)
                                    drawCircle(iconColor, radius = size.width / 2.5f)
                                    drawCircle(iconColor.copy(alpha = 0.3f), radius = size.width / 2.5f,
                                        style = Stroke(2.dp.toPx()))
                                    for (i in 0..2) {
                                        drawCircle(
                                            if (selectedCategory == 2) Color(0xFF7B1FA2) else Color(0xFFD32F2F),
                                            radius = 2.5.dp.toPx(),
                                            center = center + Offset(
                                                cos(i * 2.1f) * size.width * 0.2f,
                                                sin(i * 2.1f) * size.height * 0.2f
                                            )
                                        )
                                    }
                                }
                            }
                        }
                    }
                    Spacer(Modifier.height(6.dp))
                    Text(cat, color = if (idx == selectedCategory) Color(0xFF7B1FA2) else Color(0xFF616161),
                        fontSize = 12.sp, fontWeight = FontWeight.Medium)
                }
            }
        }

        Spacer(Modifier.height(20.dp))

        // Promotions banner
        Text("Promotions", modifier = Modifier.padding(horizontal = 20.dp).alpha(contentAlpha),
            color = Color(0xFF212121), fontSize = 18.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(10.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .height(80.dp)
                .alpha(contentAlpha)
                .clip(RoundedCornerShape(16.dp))
                .background(Brush.horizontalGradient(listOf(Color(0xFF7B1FA2), Color(0xFF9C27B0))))
                .padding(16.dp)
        ) {
            Column {
                Text("Today's Offer", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                Text("Free box on all orders above \$30", color = Color.White.copy(alpha = 0.8f), fontSize = 12.sp)
            }
        }

        Spacer(Modifier.height(20.dp))

        // Popular section
        Text("Popular", modifier = Modifier.padding(horizontal = 20.dp).alpha(contentAlpha),
            color = Color(0xFF212121), fontSize = 18.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(10.dp))

        // Food cards
        val filtered = if (selectedCategory == 0) foodItems
            else foodItems.filter { it.category == categories[selectedCategory] }

        LazyRow(
            contentPadding = PaddingValues(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            modifier = Modifier.alpha(contentAlpha).scale(contentScale)
        ) {
            itemsIndexed(filtered) { idx, food ->
                val originalIdx = foodItems.indexOf(food)
                FoodCard(food = food, floatY = floatY, onClick = { onFoodSelected(originalIdx) })
            }
        }

        Spacer(Modifier.height(24.dp))
    }
}

@Composable
private fun FoodCard(food: FoodItem, floatY: Float, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .width(150.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(Color.White)
            .clickable(onClick = onClick)
            .padding(12.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Canvas(
                modifier = Modifier
                    .size(80.dp)
                    .graphicsLayer { translationY = floatY }
            ) {
                drawCircle(food.color1.copy(alpha = 0.2f), radius = size.width / 2.2f)
                drawCircle(food.color1, radius = size.width / 2.8f)
                drawCircle(food.color2.copy(alpha = 0.7f), radius = size.width / 4f)
                // Highlight
                drawCircle(Color.White.copy(alpha = 0.25f), radius = size.width / 6f,
                    center = center + Offset(-size.width * 0.1f, -size.height * 0.1f))
            }
            Spacer(Modifier.height(8.dp))
            Text(food.name, color = Color(0xFF212121), fontSize = 14.sp,
                fontWeight = FontWeight.Bold, maxLines = 1)
            Spacer(Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Filled.Star, "Rating", tint = Color(0xFFFFD600), modifier = Modifier.size(14.dp))
                Text(" ${food.rating}", color = Color(0xFF616161), fontSize = 12.sp)
            }
            Spacer(Modifier.height(6.dp))
            Text("$${food.price.toInt()}", color = Color(0xFF7B1FA2), fontSize = 16.sp,
                fontWeight = FontWeight.ExtraBold)
        }
    }
}

@Composable
private fun FoodDetailPage(
    food: FoodItem,
    headerAlpha: Float,
    contentAlpha: Float,
    contentScale: Float,
    floatY: Float,
    onAddToCart: (Int) -> Unit,
    onBack: () -> Unit
) {
    var quantity by remember { mutableIntStateOf(1) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
    ) {
        // Hero image area
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp)
                .alpha(contentAlpha)
                .scale(contentScale)
                .clip(RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp))
                .background(Brush.verticalGradient(listOf(food.color1.copy(alpha = 0.3f), food.color1.copy(alpha = 0.1f)))),
            contentAlignment = Alignment.Center
        ) {
            // Back button overlay
            IconButton(
                onClick = onBack,
                modifier = Modifier.align(Alignment.TopStart).padding(8.dp).alpha(headerAlpha)
            ) {
                Icon(Icons.Filled.ArrowBack, "Back", tint = Color(0xFF212121))
            }

            Canvas(
                modifier = Modifier
                    .size(160.dp)
                    .graphicsLayer { translationY = floatY * 2f }
            ) {
                // Large food illustration
                drawCircle(food.color1.copy(alpha = 0.3f), radius = size.width / 2f)
                drawCircle(food.color1, radius = size.width / 2.4f)
                drawCircle(food.color2.copy(alpha = 0.6f), radius = size.width / 3.5f)
                drawCircle(Color.White.copy(alpha = 0.15f), radius = size.width / 5f,
                    center = center + Offset(-size.width * 0.12f, -size.height * 0.12f))
                // Detail dots
                for (i in 0..5) {
                    val angle = i * 60f * (PI / 180f)
                    drawCircle(
                        food.color2.copy(alpha = 0.5f),
                        radius = 3.dp.toPx(),
                        center = center + Offset(
                            cos(angle).toFloat() * size.width * 0.32f,
                            sin(angle).toFloat() * size.height * 0.32f
                        )
                    )
                }
            }
        }

        // Rating + Price row
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 12.dp).alpha(contentAlpha),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFF7B1FA2))
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.Star, "Star", tint = Color(0xFFFFD600), modifier = Modifier.size(14.dp))
                    Text(" ${food.rating}", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }
            }
            Spacer(Modifier.weight(1f))
            Text("$${food.price.toInt()}", color = Color(0xFF7B1FA2), fontSize = 26.sp,
                fontWeight = FontWeight.ExtraBold)
        }

        // Title + quantity
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp).alpha(contentAlpha),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(food.name, color = Color(0xFF212121), fontSize = 22.sp,
                fontWeight = FontWeight.ExtraBold, modifier = Modifier.weight(1f))
            // Quantity
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFF3E5F5))
                    .padding(horizontal = 4.dp, vertical = 2.dp)
            ) {
                IconButton(onClick = { if (quantity > 1) quantity-- }, modifier = Modifier.size(28.dp)) {
                    Icon(Icons.Filled.Remove, "Minus", tint = Color(0xFF7B1FA2), modifier = Modifier.size(16.dp))
                }
                Text("$quantity", color = Color(0xFF7B1FA2), fontSize = 16.sp,
                    fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 6.dp))
                IconButton(onClick = { if (quantity < 10) quantity++ }, modifier = Modifier.size(28.dp)) {
                    Icon(Icons.Filled.Add, "Plus", tint = Color(0xFF7B1FA2), modifier = Modifier.size(16.dp))
                }
            }
        }

        Spacer(Modifier.height(12.dp))

        // Description
        Text(
            food.description,
            modifier = Modifier.padding(horizontal = 24.dp).alpha(contentAlpha),
            color = Color(0xFF616161), fontSize = 14.sp, lineHeight = 20.sp
        )

        Spacer(Modifier.height(20.dp))

        // Add Ons
        Text("Add Ons", modifier = Modifier.padding(horizontal = 24.dp).alpha(contentAlpha),
            color = Color(0xFF212121), fontSize = 16.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(10.dp))

        LazyRow(
            contentPadding = PaddingValues(horizontal = 24.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.alpha(contentAlpha)
        ) {
            itemsIndexed(food.addOns) { idx, addon ->
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFFF3E5F5)),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Canvas(modifier = Modifier.size(28.dp)) {
                            drawCircle(Color(0xFF7B1FA2).copy(alpha = 0.3f), radius = size.width / 2.2f)
                            drawCircle(Color(0xFF7B1FA2).copy(alpha = 0.6f), radius = size.width / 3.5f)
                        }
                        Text(addon, color = Color(0xFF424242), fontSize = 9.sp, maxLines = 1)
                    }
                }
                // Plus badge
                Box(
                    modifier = Modifier
                        .size(20.dp)
                        .offset(x = (-6).dp, y = (-6).dp)
                        .clip(CircleShape)
                        .background(Color(0xFF4CAF50)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Filled.Add, "Add", tint = Color.White, modifier = Modifier.size(12.dp))
                }
            }
        }

        Spacer(Modifier.weight(1f))

        // Checkout button
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
                .height(52.dp)
                .alpha(contentAlpha)
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0xFF7B1FA2))
                .clickable { onAddToCart(quantity) },
            contentAlignment = Alignment.Center
        ) {
            Text("Add to Cart  •  $${food.price.toInt() * quantity}",
                color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun FoodCartPage(
    cartItems: List<CartItem>,
    headerAlpha: Float,
    contentAlpha: Float,
    onRemove: (Int) -> Unit,
    onBack: () -> Unit
) {
    val total = cartItems.sumOf { it.food.price * it.qty }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F6FF))
            .statusBarsPadding()
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
            Text("My Cart", color = Color(0xFF212121), fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.weight(1f))
            Spacer(Modifier.size(48.dp))
        }

        if (cartItems.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxWidth().weight(1f).alpha(contentAlpha),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Filled.ShoppingCart, "Empty", tint = Color(0xFFBDBDBD),
                        modifier = Modifier.size(64.dp))
                    Spacer(Modifier.height(12.dp))
                    Text("Your cart is empty", color = Color(0xFF9E9E9E), fontSize = 16.sp)
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 8.dp)
                    .alpha(contentAlpha),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                cartItems.forEachIndexed { idx, item ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color.White)
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(item.food.color1.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Canvas(modifier = Modifier.size(28.dp)) {
                                drawCircle(item.food.color1, radius = size.width / 2.4f)
                                drawCircle(item.food.color2, radius = size.width / 4f)
                            }
                        }
                        Spacer(Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(item.food.name, color = Color(0xFF212121), fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold)
                            Text("$${item.food.price.toInt()}", color = Color(0xFF7B1FA2),
                                fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }
                        // Quantity
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("×${item.qty}", color = Color(0xFF616161), fontSize = 14.sp)
                        }
                        Spacer(Modifier.width(12.dp))
                        // Remove
                        IconButton(onClick = { onRemove(idx) }, modifier = Modifier.size(32.dp)) {
                            Icon(Icons.Filled.Close, "Remove", tint = Color(0xFFFF5252),
                                modifier = Modifier.size(18.dp))
                        }
                    }
                }
            }

            // Total + Checkout
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .alpha(contentAlpha)
                    .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                    .background(Color.White)
                    .padding(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Total:", color = Color(0xFF616161), fontSize = 16.sp)
                    Spacer(Modifier.weight(1f))
                    Text("$${total.toInt()}", color = Color(0xFF7B1FA2), fontSize = 24.sp,
                        fontWeight = FontWeight.ExtraBold)
                }
                Spacer(Modifier.height(14.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFF7B1FA2))
                        .clickable { },
                    contentAlignment = Alignment.Center
                ) {
                    Text("Checkout", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
