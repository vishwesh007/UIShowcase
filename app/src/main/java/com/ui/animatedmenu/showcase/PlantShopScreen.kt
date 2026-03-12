package com.ui.animatedmenu.showcase

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.draw.scale
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

// Design colors — deep green menu, white home
private val PlantGreen = Color(0xFF2E7D32)
private val PlantGreenDark = Color(0xFF1B5E20)
private val PlantGreenLight = Color(0xFF4CAF50)
private val PlantBg = Color(0xFFF8F9FA)
private val PlantDark = Color(0xFF1A1A2E)
private val PlantGray = Color(0xFF9E9E9E)
private val PlantAccent = Color(0xFFFF5722)

data class PlantProduct(
    val name: String,
    val subtitle: String,
    val price: String,
    val tag: String = "",
    // 0=pot, 1=tall plant, 2=succulent, 3=vase
    val type: Int = 0
)

private val products = listOf(
    PlantProduct("Fiddle Leaf Fig", "Indoor Plant", "$45", "NEW", 1),
    PlantProduct("Ceramic Vase", "Home Decor", "$32", "SALE", 3),
    PlantProduct("Snake Plant", "Low Light", "$28", "", 1),
    PlantProduct("Terra Pot Set", "Set of 3", "$55", "NEW", 0),
    PlantProduct("Mini Succulent", "Desktop Plant", "$15", "", 2),
    PlantProduct("Modern Planter", "Matte Black", "$40", "", 0),
)

data class DrawerMenuItem(
    val label: String,
    val iconType: Int // 0=grid, 1=wallet, 2=gift, 3=tag, 4=book, 5=heart, 6=logout
)

private val topMenuItems = listOf(
    DrawerMenuItem("Categories", 0),
    DrawerMenuItem("Wallet", 1),
    DrawerMenuItem("Gift Store", 2),
)

private val bottomMenuItems = listOf(
    DrawerMenuItem("Loyalty Program", 3),
    DrawerMenuItem("Blog & Articles", 4),
    DrawerMenuItem("Help & Support", 5),
    DrawerMenuItem("Logout", 6),
)

@Composable
fun PlantShopScreen(onBack: () -> Unit) {
    var menuOpen by remember { mutableStateOf(false) }
    var selectedNavIndex by remember { mutableIntStateOf(0) }

    // Animate the drawer slide and home page offset
    val transition = updateTransition(targetState = menuOpen, label = "menu")
    val drawerOffset by transition.animateFloat(
        label = "drawer",
        transitionSpec = { tween(400, easing = FastOutSlowInEasing) }
    ) { open -> if (open) 0f else -1f }

    val contentOffset by transition.animateFloat(
        label = "content",
        transitionSpec = { tween(400, easing = FastOutSlowInEasing) }
    ) { open -> if (open) 0.65f else 0f }

    val contentScale by transition.animateFloat(
        label = "scale",
        transitionSpec = { tween(400, easing = FastOutSlowInEasing) }
    ) { open -> if (open) 0.82f else 1f }

    val contentRadius by transition.animateFloat(
        label = "radius",
        transitionSpec = { tween(400, easing = FastOutSlowInEasing) }
    ) { open -> if (open) 32f else 0f }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(PlantGreenDark, PlantGreen)
                )
            )
    ) {
        // Drawer content (behind home page)
        DrawerContent(
            onClose = { menuOpen = false },
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(0.6f)
                .graphicsLayer {
                    translationX = drawerOffset * 200f
                    alpha = 1f + drawerOffset // 0 when closed, 1 when open
                }
        )

        // Home content (slides right when menu opens)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    translationX = contentOffset * size.width
                    scaleX = contentScale
                    scaleY = contentScale
                    shape = RoundedCornerShape(contentRadius.dp)
                    clip = true
                    shadowElevation = if (menuOpen) 16f else 0f
                }
                .clip(RoundedCornerShape(contentRadius.dp))
                .background(PlantBg)
        ) {
            HomeContent(
                onMenuClick = { menuOpen = !menuOpen },
                onBack = onBack,
                selectedNavIndex = selectedNavIndex,
                onNavSelect = { selectedNavIndex = it }
            )
        }
    }
}

@Composable
private fun DrawerContent(onClose: () -> Unit, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .statusBarsPadding()
            .padding(start = 24.dp, top = 24.dp, bottom = 32.dp)
    ) {
        // Profile avatar + close button
        Row(
            modifier = Modifier.fillMaxWidth().padding(end = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Avatar circle with plant icon
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Filled.Person, null, tint = Color.White, modifier = Modifier.size(28.dp))
            }
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.15f))
                    .clickable { onClose() },
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Filled.Close, "Close", tint = Color.White, modifier = Modifier.size(18.dp))
            }
        }

        Spacer(Modifier.height(36.dp))

        // Top menu items
        topMenuItems.forEach { item ->
            MenuItemRow(item)
            Spacer(Modifier.height(20.dp))
        }

        // Divider
        Spacer(Modifier.height(12.dp))
        Box(
            modifier = Modifier
                .width(40.dp)
                .height(1.dp)
                .background(Color.White.copy(alpha = 0.3f))
        )
        Spacer(Modifier.height(24.dp))

        // Bottom menu items
        bottomMenuItems.forEach { item ->
            MenuItemRow(item)
            Spacer(Modifier.height(20.dp))
        }

        Spacer(Modifier.weight(1f))
    }
}

@Composable
private fun MenuItemRow(item: DrawerMenuItem) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.clickable { }
    ) {
        val icon = when (item.iconType) {
            0 -> Icons.Filled.GridView
            1 -> Icons.Filled.AccountBalanceWallet
            2 -> Icons.Filled.CardGiftcard
            3 -> Icons.Filled.Loyalty
            4 -> Icons.Filled.Article
            5 -> Icons.Filled.Favorite
            else -> Icons.Filled.Logout
        }
        Icon(icon, null, tint = Color.White, modifier = Modifier.size(22.dp))
        Spacer(Modifier.width(16.dp))
        Text(
            item.label,
            color = Color.White,
            fontSize = 15.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun HomeContent(
    onMenuClick: () -> Unit,
    onBack: () -> Unit,
    selectedNavIndex: Int,
    onNavSelect: (Int) -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .statusBarsPadding()
                .padding(bottom = 80.dp)
        ) {
            // Top bar
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onMenuClick) {
                    Icon(Icons.Filled.Menu, "Menu", tint = PlantDark, modifier = Modifier.size(24.dp))
                }
                IconButton(onClick = onBack) {
                    Icon(Icons.Filled.ArrowBack, "Back", tint = PlantDark, modifier = Modifier.size(24.dp))
                }
            }

            // Popular section
            Text(
                "Popular",
                color = PlantDark,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 24.dp)
            )
            Spacer(Modifier.height(16.dp))

            // Feature banner
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .height(160.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(
                        Brush.horizontalGradient(
                            listOf(Color(0xFFFFF3E0), Color(0xFFFFE0B2))
                        )
                    )
            ) {
                Row(
                    modifier = Modifier.fillMaxSize().padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(PlantAccent)
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text("NEW", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                        Spacer(Modifier.height(8.dp))
                        Text(
                            "Bring Nature\nInto Your\nHome",
                            color = PlantDark,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            lineHeight = 22.sp
                        )
                        Spacer(Modifier.height(4.dp))
                        Text("From $15", color = PlantGray, fontSize = 13.sp)
                    }

                    // Plant illustration
                    Canvas(modifier = Modifier.size(100.dp, 130.dp)) {
                        drawPlantPot(size.width, size.height)
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

            // Explore section
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Explore",
                    color = PlantDark,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    "See all",
                    color = PlantGreen,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.clickable { }
                )
            }
            Spacer(Modifier.height(16.dp))

            // Products horizontal gallery
            LazyRow(
                contentPadding = PaddingValues(horizontal = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(products) { product ->
                    ProductCard(product)
                }
            }

            Spacer(Modifier.height(24.dp))

            // Recently Viewed
            Text(
                "Recently Viewed",
                color = PlantDark,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 24.dp)
            )
            Spacer(Modifier.height(12.dp))

            // List of recent items
            products.take(3).forEach { product ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 6.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color.White)
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Mini plant canvas
                    Canvas(modifier = Modifier.size(48.dp)) {
                        val s = size.width
                        drawMiniPlant(s, product.type)
                    }
                    Spacer(Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(product.name, color = PlantDark, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                        Text(product.subtitle, color = PlantGray, fontSize = 12.sp)
                    }
                    Text(product.price, color = PlantGreen, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        // Bottom navigation
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .background(Color.White)
                .padding(horizontal = 24.dp, vertical = 12.dp)
                .navigationBarsPadding(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            val navIcons = listOf(
                Icons.Filled.Home,
                Icons.Filled.Search,
                Icons.Filled.ShoppingCart,
                Icons.Filled.FavoriteBorder,
                Icons.Filled.Person
            )
            navIcons.forEachIndexed { index, icon ->
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(
                            if (index == selectedNavIndex) PlantGreen else Color.Transparent
                        )
                        .clickable { onNavSelect(index) },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        icon, null,
                        tint = if (index == selectedNavIndex) Color.White else PlantGray,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun ProductCard(product: PlantProduct) {
    Box(
        modifier = Modifier
            .width(160.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(Color.White)
            .shadow(2.dp, RoundedCornerShape(20.dp))
    ) {
        Column {
            // Product image area
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
                    .background(Color(0xFFF5F5F5)),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.size(80.dp, 110.dp)) {
                    drawMiniPlant(size.width, product.type)
                }
                if (product.tag.isNotEmpty()) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(8.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(
                                if (product.tag == "NEW") PlantAccent else PlantGreen
                            )
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(product.tag, color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
            Column(modifier = Modifier.padding(12.dp)) {
                Text(product.name, color = PlantDark, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                Text(product.subtitle, color = PlantGray, fontSize = 11.sp)
                Spacer(Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(product.price, color = PlantGreen, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(PlantGreen),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Filled.Add, null, tint = Color.White, modifier = Modifier.size(18.dp))
                    }
                }
            }
        }
    }
}

// Draw a potted plant: pot + soil + leaves/stems
private fun DrawScope.drawPlantPot(w: Float, h: Float) {
    val potTop = h * 0.5f
    val potW = w * 0.5f
    val potH = h * 0.35f

    // Pot body — trapezoid
    val potPath = Path().apply {
        moveTo(w / 2 - potW / 2, potTop)
        lineTo(w / 2 + potW / 2, potTop)
        lineTo(w / 2 + potW * 0.4f, potTop + potH)
        lineTo(w / 2 - potW * 0.4f, potTop + potH)
        close()
    }
    drawPath(potPath, Color(0xFF5D4037))

    // Pot rim
    drawRoundRect(
        Color(0xFF6D4C41),
        topLeft = Offset(w / 2 - potW / 2 - 4f, potTop - 6f),
        size = Size(potW + 8f, 12f),
        cornerRadius = CornerRadius(4f)
    )

    // Soil
    drawOval(
        Color(0xFF3E2723),
        topLeft = Offset(w / 2 - potW / 2 + 4f, potTop - 2f),
        size = Size(potW - 8f, 12f)
    )

    // Main stem
    drawLine(
        PlantGreen,
        Offset(w / 2, potTop),
        Offset(w / 2, h * 0.15f),
        strokeWidth = 4f,
        cap = StrokeCap.Round
    )

    // Leaves
    val leafPairs = listOf(0.42f to true, 0.32f to false, 0.22f to true, 0.15f to false)
    leafPairs.forEach { (yFrac, goRight) ->
        val ly = h * yFrac
        val dir = if (goRight) 1f else -1f
        val leafPath = Path().apply {
            moveTo(w / 2, ly)
            quadraticBezierTo(
                w / 2 + dir * w * 0.3f, ly - h * 0.06f,
                w / 2 + dir * w * 0.35f, ly + h * 0.02f
            )
            quadraticBezierTo(
                w / 2 + dir * w * 0.2f, ly + h * 0.04f,
                w / 2, ly
            )
        }
        drawPath(leafPath, PlantGreenLight.copy(alpha = 0.85f))
        // Leaf vein
        drawLine(
            PlantGreenDark.copy(alpha = 0.3f),
            Offset(w / 2, ly),
            Offset(w / 2 + dir * w * 0.28f, ly),
            strokeWidth = 1f
        )
    }
}

// Draw mini plant for cards and list items
private fun DrawScope.drawMiniPlant(s: Float, type: Int) {
    when (type) {
        0 -> { // Pot
            drawRoundRect(
                Color(0xFF5D4037),
                topLeft = Offset(s * 0.25f, s * 0.55f),
                size = Size(s * 0.5f, s * 0.35f),
                cornerRadius = CornerRadius(4f)
            )
            drawOval(Color(0xFF3E2723), Offset(s * 0.28f, s * 0.52f), Size(s * 0.44f, s * 0.12f))
            // Small plant
            drawLine(PlantGreen, Offset(s / 2, s * 0.55f), Offset(s / 2, s * 0.25f), 3f, cap = StrokeCap.Round)
            drawCircle(PlantGreenLight, s * 0.12f, Offset(s / 2, s * 0.22f))
            drawCircle(PlantGreenLight, s * 0.08f, Offset(s * 0.38f, s * 0.32f))
            drawCircle(PlantGreenLight, s * 0.08f, Offset(s * 0.62f, s * 0.32f))
        }
        1 -> { // Tall plant
            drawRoundRect(
                Color(0xFF5D4037),
                topLeft = Offset(s * 0.3f, s * 0.65f),
                size = Size(s * 0.4f, s * 0.28f),
                cornerRadius = CornerRadius(3f)
            )
            drawLine(PlantGreen, Offset(s / 2, s * 0.65f), Offset(s / 2, s * 0.15f), 3f, cap = StrokeCap.Round)
            // Tall leaves
            for (i in 0..3) {
                val y = s * (0.2f + i * 0.1f)
                val dir = if (i % 2 == 0) 1f else -1f
                val path = Path().apply {
                    moveTo(s / 2, y)
                    quadraticBezierTo(s / 2 + dir * s * 0.25f, y - s * 0.06f, s / 2 + dir * s * 0.3f, y + s * 0.02f)
                    quadraticBezierTo(s / 2 + dir * s * 0.15f, y + s * 0.04f, s / 2, y)
                }
                drawPath(path, PlantGreenLight)
            }
        }
        2 -> { // Succulent
            drawRoundRect(
                Color(0xFF8D6E63),
                topLeft = Offset(s * 0.3f, s * 0.6f),
                size = Size(s * 0.4f, s * 0.25f),
                cornerRadius = CornerRadius(6f)
            )
            // Rosette petals
            for (i in 0..5) {
                val angle = (i * 60f) * (PI / 180f).toFloat()
                val px = s / 2 + cos(angle) * s * 0.15f
                val py = s * 0.45f + sin(angle) * s * 0.1f
                drawOval(
                    Color(0xFF81C784),
                    Offset(px - s * 0.08f, py - s * 0.05f),
                    Size(s * 0.16f, s * 0.12f)
                )
            }
            drawCircle(Color(0xFFA5D6A7), s * 0.06f, Offset(s / 2, s * 0.45f))
        }
        3 -> { // Vase
            val path = Path().apply {
                moveTo(s * 0.35f, s * 0.35f)
                quadraticBezierTo(s * 0.2f, s * 0.55f, s * 0.3f, s * 0.85f)
                lineTo(s * 0.7f, s * 0.85f)
                quadraticBezierTo(s * 0.8f, s * 0.55f, s * 0.65f, s * 0.35f)
                close()
            }
            drawPath(path, Color(0xFF37474F))
            // Dried flowers
            drawLine(Color(0xFF8D6E63), Offset(s * 0.45f, s * 0.35f), Offset(s * 0.35f, s * 0.1f), 2f, cap = StrokeCap.Round)
            drawLine(Color(0xFF8D6E63), Offset(s * 0.55f, s * 0.35f), Offset(s * 0.65f, s * 0.12f), 2f, cap = StrokeCap.Round)
            drawCircle(Color(0xFFE57373), s * 0.05f, Offset(s * 0.35f, s * 0.1f))
            drawCircle(Color(0xFFFFB74D), s * 0.05f, Offset(s * 0.65f, s * 0.12f))
            drawCircle(Color(0xFFBA68C8), s * 0.04f, Offset(s * 0.5f, s * 0.15f))
            drawLine(Color(0xFF8D6E63), Offset(s * 0.5f, s * 0.35f), Offset(s * 0.5f, s * 0.18f), 2f, cap = StrokeCap.Round)
        }
    }
}
