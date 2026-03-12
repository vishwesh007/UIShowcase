package com.ui.animatedmenu.showcase

import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
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
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.*

private data class PizzaItem(
    val name: String,
    val price: Double,
    val description: String,
    val toppings: List<String>,
    val bgColor: Color,
    val accentColor: Color,
    val rating: Float
)

private val pizzaMenu = listOf(
    PizzaItem("Margarita", 6.70, "Classic tomato, mozzarella & basil", listOf("Tomato", "Mozzarella", "Basil"), Color(0xFF2962FF), Color(0xFFFF6D00), 4.8f),
    PizzaItem("Pepperoni", 8.50, "Loaded pepperoni with extra cheese", listOf("Pepperoni", "Cheese", "Oregano"), Color(0xFFFF3D00), Color(0xFFFFAB00), 4.9f),
    PizzaItem("BBQ Chicken", 9.90, "Smoky BBQ sauce with grilled chicken", listOf("Chicken", "BBQ Sauce", "Onion"), Color(0xFF6200EA), Color(0xFFFF6D00), 4.7f),
    PizzaItem("Veggie Supreme", 7.80, "Fresh garden vegetables & herbs", listOf("Bell Pepper", "Mushroom", "Olive"), Color(0xFF00C853), Color(0xFFFFD600), 4.6f),
    PizzaItem("Hawaiian", 8.20, "Ham & pineapple tropical delight", listOf("Ham", "Pineapple", "Cheese"), Color(0xFFFF6F00), Color(0xFF00E5FF), 4.5f),
)

private data class ToppingIcon(val name: String, val color: Color, val x: Float, val y: Float)

@Composable
fun PizzaAppScreen(onBack: () -> Unit) {
    var currentPage by remember { mutableIntStateOf(0) } // 0=home, 1=detail
    var selectedPizza by remember { mutableIntStateOf(0) }

    val inf = rememberInfiniteTransition(label = "pizza")
    val pizzaSpin by inf.animateFloat(0f, 360f,
        infiniteRepeatable(tween(12000, easing = LinearEasing)), label = "spin")
    val floatY by inf.animateFloat(-8f, 8f,
        infiniteRepeatable(tween(3000, easing = FastOutSlowInEasing), RepeatMode.Reverse), label = "float")
    val pulseScale by inf.animateFloat(1f, 1.05f,
        infiniteRepeatable(tween(2000, easing = FastOutSlowInEasing), RepeatMode.Reverse), label = "pulse")

    // Entrance animations
    val scope = rememberCoroutineScope()
    val headerAlpha = remember { Animatable(0f) }
    val contentScale = remember { Animatable(0.8f) }
    val contentAlpha = remember { Animatable(0f) }

    LaunchedEffect(currentPage) {
        headerAlpha.snapTo(0f)
        contentScale.snapTo(0.8f)
        contentAlpha.snapTo(0f)
        launch { headerAlpha.animateTo(1f, tween(600)) }
        launch { contentScale.animateTo(1f, spring(Spring.DampingRatioMediumBouncy)) }
        launch { contentAlpha.animateTo(1f, tween(500, 200)) }
    }

    val pizza = pizzaMenu[selectedPizza]

    Box(modifier = Modifier.fillMaxSize().background(
        if (currentPage == 0) Color(0xFF2962FF) else pizza.bgColor
    )) {
        // Background pattern circles
        Canvas(modifier = Modifier.fillMaxSize()) {
            for (i in 0..15) {
                val cx = (i * 127 + 40) % size.width.toInt()
                val cy = (i * 89 + 60) % size.height.toInt()
                drawCircle(
                    Color.White.copy(alpha = 0.04f),
                    radius = 30f + (i % 4) * 20f,
                    center = Offset(cx.toFloat(), cy.toFloat() + floatY * (i % 3))
                )
            }
        }

        if (currentPage == 0) {
            PizzaHomePage(
                headerAlpha = headerAlpha.value,
                contentAlpha = contentAlpha.value,
                contentScale = contentScale.value,
                pizzaSpin = pizzaSpin,
                floatY = floatY,
                pulseScale = pulseScale,
                onPizzaSelected = { idx ->
                    selectedPizza = idx
                    currentPage = 1
                },
                onBack = onBack
            )
        } else {
            PizzaDetailPage(
                pizza = pizza,
                headerAlpha = headerAlpha.value,
                contentAlpha = contentAlpha.value,
                contentScale = contentScale.value,
                pizzaSpin = pizzaSpin,
                floatY = floatY,
                onBack = { currentPage = 0 }
            )
        }
    }
}

@Composable
private fun PizzaHomePage(
    headerAlpha: Float,
    contentAlpha: Float,
    contentScale: Float,
    pizzaSpin: Float,
    floatY: Float,
    pulseScale: Float,
    onPizzaSelected: (Int) -> Unit,
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize().statusBarsPadding()
    ) {
        // Top bar
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp).alpha(headerAlpha),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.Filled.ArrowBack, "Back", tint = Color.White)
            }
            Spacer(Modifier.weight(1f))
            Text("FOOD APP", color = Color.White, fontSize = 20.sp,
                fontWeight = FontWeight.ExtraBold, letterSpacing = 2.sp)
            Spacer(Modifier.weight(1f))
            IconButton(onClick = { }) {
                Icon(Icons.Filled.ShoppingCart, "Cart", tint = Color.White)
            }
        }

        // Category tabs
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp)
                .alpha(headerAlpha),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            listOf("All", "Pizza", "Burgers", "Drinks").forEachIndexed { idx, cat ->
                val isSelected = idx == 1
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(if (isSelected) Color.White else Color.White.copy(alpha = 0.15f))
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text(
                        cat,
                        color = if (isSelected) Color(0xFF2962FF) else Color.White,
                        fontSize = 13.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                    )
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        // Hero section
        Text(
            "Choose Your\nFavorite Pizza",
            modifier = Modifier.padding(horizontal = 24.dp).alpha(contentAlpha),
            color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.ExtraBold,
            lineHeight = 34.sp
        )

        Spacer(Modifier.height(8.dp))

        Text(
            "Fresh from the oven, delivered hot",
            modifier = Modifier.padding(horizontal = 24.dp).alpha(contentAlpha),
            color = Color.White.copy(alpha = 0.7f), fontSize = 14.sp
        )

        Spacer(Modifier.height(20.dp))

        // Pizza cards row
        LazyRow(
            contentPadding = PaddingValues(horizontal = 24.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.alpha(contentAlpha).scale(contentScale)
        ) {
            itemsIndexed(pizzaMenu) { idx, pizza ->
                PizzaCard(
                    pizza = pizza,
                    spin = pizzaSpin,
                    floatY = floatY,
                    onClick = { onPizzaSelected(idx) }
                )
            }
        }

        Spacer(Modifier.height(24.dp))

        // Popular section
        Text(
            "Popular Now",
            modifier = Modifier.padding(horizontal = 24.dp).alpha(contentAlpha),
            color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold
        )

        Spacer(Modifier.height(12.dp))

        // Popular items list
        Column(
            modifier = Modifier.padding(horizontal = 24.dp).alpha(contentAlpha),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            pizzaMenu.take(3).forEach { pizza ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color.White.copy(alpha = 0.12f))
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Mini pizza icon
                    Canvas(modifier = Modifier.size(44.dp)) {
                        drawCircle(pizza.accentColor.copy(alpha = 0.3f), radius = size.width / 2)
                        drawCircle(Color(0xFFFFC107), radius = size.width / 2.8f)
                        // Toppings
                        for (i in 0..5) {
                            val angle = i * 60f * (PI / 180f)
                            val r = size.width / 5f
                            drawCircle(
                                Color(0xFFD32F2F),
                                radius = 3.dp.toPx(),
                                center = center + Offset(
                                    cos(angle).toFloat() * r,
                                    sin(angle).toFloat() * r
                                )
                            )
                        }
                    }
                    Spacer(Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(pizza.name, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                        Text(pizza.description, color = Color.White.copy(alpha = 0.6f), fontSize = 11.sp, maxLines = 1)
                    }
                    Text("$${String.format("%.2f", pizza.price)}", color = Color(0xFFFFD600),
                        fontSize = 15.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun PizzaCard(
    pizza: PizzaItem,
    spin: Float,
    floatY: Float,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .width(180.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(Color.White.copy(alpha = 0.15f))
            .clickable(onClick = onClick)
            .padding(16.dp),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            // Pizza drawing
            Canvas(
                modifier = Modifier
                    .size(120.dp)
                    .graphicsLayer { translationY = floatY }
            ) {
                // Shadow
                drawOval(
                    Color.Black.copy(alpha = 0.15f),
                    topLeft = Offset(size.width * 0.15f, size.height * 0.85f),
                    size = Size(size.width * 0.7f, size.height * 0.1f)
                )
                // Pizza base
                drawCircle(Color(0xFFFFC107), radius = size.width / 2.2f)
                // Crust ring
                drawCircle(
                    Color(0xFFE8A317),
                    radius = size.width / 2.2f,
                    style = Stroke(width = 6.dp.toPx())
                )
                // Sauce
                drawCircle(Color(0xFFE53935).copy(alpha = 0.6f), radius = size.width / 2.8f)
                // Cheese blobs
                for (i in 0..7) {
                    val angle = (i * 45f + spin * 0.1f) * (PI / 180f)
                    val r = size.width / 4f
                    drawCircle(
                        Color(0xFFFFF176).copy(alpha = 0.7f),
                        radius = 8.dp.toPx(),
                        center = center + Offset(
                            cos(angle).toFloat() * r,
                            sin(angle).toFloat() * r
                        )
                    )
                }
                // Toppings
                for (i in 0..5) {
                    val angle = (i * 60f + 30f) * (PI / 180f)
                    val r = size.width / 3.5f
                    drawCircle(
                        pizza.accentColor,
                        radius = 5.dp.toPx(),
                        center = center + Offset(
                            cos(angle).toFloat() * r,
                            sin(angle).toFloat() * r
                        )
                    )
                }
                // Basil leaf
                val leafPath = Path().apply {
                    moveTo(center.x - 4.dp.toPx(), center.y)
                    quadraticBezierTo(center.x, center.y - 12.dp.toPx(),
                        center.x + 4.dp.toPx(), center.y)
                    quadraticBezierTo(center.x, center.y + 4.dp.toPx(),
                        center.x - 4.dp.toPx(), center.y)
                }
                drawPath(leafPath, Color(0xFF4CAF50))
            }

            Spacer(Modifier.height(12.dp))
            Text(pizza.name, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            Text(pizza.description, color = Color.White.copy(alpha = 0.6f),
                fontSize = 11.sp, maxLines = 1)
            Spacer(Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("$${String.format("%.2f", pizza.price)}", color = Color(0xFFFFD600),
                    fontSize = 18.sp, fontWeight = FontWeight.ExtraBold)
                Spacer(Modifier.weight(1f))
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFFF6D00)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Filled.Add, "Add", tint = Color.White, modifier = Modifier.size(18.dp))
                }
            }
            // Rating
            Row(
                modifier = Modifier.padding(top = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Filled.Star, "Rating", tint = Color(0xFFFFD600), modifier = Modifier.size(14.dp))
                Text(" ${pizza.rating}", color = Color.White.copy(alpha = 0.8f), fontSize = 12.sp)
            }
        }
    }
}

@Composable
private fun PizzaDetailPage(
    pizza: PizzaItem,
    headerAlpha: Float,
    contentAlpha: Float,
    contentScale: Float,
    pizzaSpin: Float,
    floatY: Float,
    onBack: () -> Unit
) {
    var quantity by remember { mutableIntStateOf(1) }
    var selectedSize by remember { mutableIntStateOf(1) } // 0=S, 1=M, 2=L
    val sizes = listOf("S" to 0.0, "M" to 2.0, "L" to 4.0)

    Column(
        modifier = Modifier.fillMaxSize().statusBarsPadding().verticalScroll(rememberScrollState())
    ) {
        // Top bar
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp).alpha(headerAlpha),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.Filled.ArrowBack, "Back", tint = Color.White)
            }
            Spacer(Modifier.weight(1f))
            Text("Details", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.weight(1f))
            IconButton(onClick = { }) {
                Icon(Icons.Filled.Favorite, "Favorite", tint = Color(0xFFFF5252))
            }
        }

        // Big pizza
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(260.dp)
                .alpha(contentAlpha)
                .scale(contentScale),
            contentAlignment = Alignment.Center
        ) {
            Canvas(
                modifier = Modifier
                    .size(220.dp)
                    .rotate(pizzaSpin * 0.3f)
                    .graphicsLayer { translationY = floatY * 1.5f }
            ) {
                // Shadow
                drawOval(
                    Color.Black.copy(alpha = 0.2f),
                    topLeft = Offset(size.width * 0.1f, size.height * 0.88f),
                    size = Size(size.width * 0.8f, size.height * 0.08f)
                )
                // Base
                drawCircle(Color(0xFFFFC107), radius = size.width / 2.1f)
                // Crust
                drawCircle(Color(0xFFD4930A), radius = size.width / 2.1f, style = Stroke(10.dp.toPx()))
                // Sauce
                drawCircle(Color(0xFFE53935).copy(alpha = 0.5f), radius = size.width / 2.6f)
                // Cheese
                for (i in 0..12) {
                    val angle = i * 30f * (PI / 180f)
                    val r = size.width / 3.5f + (i % 3) * 8f
                    drawCircle(
                        Color(0xFFFFF176).copy(alpha = 0.65f),
                        radius = (6 + i % 3 * 3).dp.toPx(),
                        center = center + Offset(cos(angle).toFloat() * r, sin(angle).toFloat() * r)
                    )
                }
                // Toppings
                for (i in 0..8) {
                    val angle = (i * 40f + 15f) * (PI / 180f)
                    val r = size.width / 3f
                    drawCircle(
                        pizza.accentColor,
                        radius = (4 + i % 2 * 2).dp.toPx(),
                        center = center + Offset(cos(angle).toFloat() * r, sin(angle).toFloat() * r)
                    )
                }
                // Basil leaves
                for (leafIdx in 0..2) {
                    val la = (leafIdx * 120f + 45f) * (PI / 180f).toFloat()
                    val lr = size.width / 4f
                    val lc = center + Offset(cos(la) * lr, sin(la) * lr)
                    val lp = Path().apply {
                        moveTo(lc.x - 6.dp.toPx(), lc.y)
                        quadraticBezierTo(lc.x, lc.y - 14.dp.toPx(), lc.x + 6.dp.toPx(), lc.y)
                        quadraticBezierTo(lc.x, lc.y + 6.dp.toPx(), lc.x - 6.dp.toPx(), lc.y)
                    }
                    drawPath(lp, Color(0xFF388E3C))
                }
                // Center highlight
                drawCircle(Color.White.copy(alpha = 0.08f), radius = size.width / 6f)
            }
        }

        // Name & price
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp).alpha(contentAlpha),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(pizza.name, color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.ExtraBold)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.Star, "Rating", tint = Color(0xFFFFD600), modifier = Modifier.size(16.dp))
                    Text(" ${pizza.rating}", color = Color.White.copy(alpha = 0.8f), fontSize = 14.sp)
                    Text("  •  30 min", color = Color.White.copy(alpha = 0.6f), fontSize = 13.sp)
                }
            }
            Text(
                "$${String.format("%.2f", pizza.price + sizes[selectedSize].second)}",
                color = Color(0xFFFFD600), fontSize = 28.sp, fontWeight = FontWeight.ExtraBold
            )
        }

        Spacer(Modifier.height(16.dp))

        // Description
        Text(
            pizza.description,
            modifier = Modifier.padding(horizontal = 24.dp).alpha(contentAlpha),
            color = Color.White.copy(alpha = 0.7f), fontSize = 14.sp, lineHeight = 20.sp
        )

        Spacer(Modifier.height(20.dp))

        // Size selector
        Text(
            "Size",
            modifier = Modifier.padding(horizontal = 24.dp).alpha(contentAlpha),
            color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.height(10.dp))
        Row(
            modifier = Modifier.padding(horizontal = 24.dp).alpha(contentAlpha),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            sizes.forEachIndexed { idx, (label, _) ->
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(if (idx == selectedSize) Color(0xFFFF6D00) else Color.White.copy(alpha = 0.15f))
                        .clickable { selectedSize = idx },
                    contentAlignment = Alignment.Center
                ) {
                    Text(label, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            }
        }

        Spacer(Modifier.height(20.dp))

        // Toppings
        Text(
            "Toppings",
            modifier = Modifier.padding(horizontal = 24.dp).alpha(contentAlpha),
            color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.height(10.dp))
        Row(
            modifier = Modifier.padding(horizontal = 24.dp).alpha(contentAlpha),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            pizza.toppings.forEach { topping ->
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color.White.copy(alpha = 0.12f))
                        .padding(horizontal = 14.dp, vertical = 8.dp)
                ) {
                    Text(topping, color = Color.White, fontSize = 12.sp)
                }
            }
        }

        Spacer(Modifier.height(24.dp))

        // Quantity & Add to Cart
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp).alpha(contentAlpha),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Quantity
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color.White.copy(alpha = 0.15f))
                    .padding(horizontal = 4.dp, vertical = 2.dp)
            ) {
                IconButton(onClick = { if (quantity > 1) quantity-- }, modifier = Modifier.size(36.dp)) {
                    Icon(Icons.Filled.Remove, "Minus", tint = Color.White, modifier = Modifier.size(18.dp))
                }
                Text("$quantity", color = Color.White, fontSize = 18.sp,
                    fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 8.dp))
                IconButton(onClick = { if (quantity < 10) quantity++ }, modifier = Modifier.size(36.dp)) {
                    Icon(Icons.Filled.Add, "Plus", tint = Color.White, modifier = Modifier.size(18.dp))
                }
            }

            Spacer(Modifier.width(16.dp))

            // Add to cart
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(Color(0xFFFF6D00))
                    .clickable { },
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.ShoppingCart, "Cart", tint = Color.White, modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Add to Cart", color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(Modifier.height(32.dp))
    }
}
