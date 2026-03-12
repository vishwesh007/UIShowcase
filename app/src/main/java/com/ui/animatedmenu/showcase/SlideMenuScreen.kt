package com.ui.animatedmenu.showcase

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

data class SlideMenuItem(
    val icon: ImageVector,
    val label: String,
    val badge: String? = null
)

@Composable
fun SlideMenuScreen(onBack: () -> Unit) {
    val menuOpen = remember { mutableStateOf(false) }

    // Animated values
    val menuProgress by animateFloatAsState(
        targetValue = if (menuOpen.value) 1f else 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioLowBouncy,
            stiffness = 400f
        ),
        label = "menu"
    )

    val menuItems = remember {
        listOf(
            SlideMenuItem(Icons.Filled.Apps, "Categories"),
            SlideMenuItem(Icons.Filled.AccountBalanceWallet, "Wallet", "3"),
            SlideMenuItem(Icons.Filled.CardGiftcard, "Gift Ideas"),
            SlideMenuItem(Icons.Filled.Subscriptions, "Subscription"),
            SlideMenuItem(Icons.Filled.Favorite, "Favorites", "12"),
            SlideMenuItem(Icons.Filled.Settings, "Settings"),
        )
    }

    val selectedItem = remember { mutableIntStateOf(0) }

    // Staggered menu item entrance
    val itemAnimatables = remember { List(menuItems.size) { Animatable(0f) } }

    LaunchedEffect(menuOpen.value) {
        if (menuOpen.value) {
            for (i in itemAnimatables.indices) {
                launch {
                    kotlinx.coroutines.delay(i * 60L + 150L)
                    itemAnimatables[i].animateTo(
                        1f,
                        spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = 500f)
                    )
                }
            }
        } else {
            for (anim in itemAnimatables) {
                launch {
                    anim.snapTo(0f)
                }
            }
        }
    }

    val accentGreen = Color(0xFF2E7D5F)
    val deepGreen = Color(0xFF1B5E40)
    val menuBg = Color(0xFF22A06B)
    val orange = Color(0xFFFF8C42)

    Box(modifier = Modifier.fillMaxSize()) {
        // ── Menu layer (behind content) ──
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(menuBg, deepGreen)
                    )
                )
        ) {
            // Decorative orange triangle / arrow
            Canvas(modifier = Modifier.fillMaxSize()) {
                val path = Path().apply {
                    moveTo(size.width * 0.6f, 0f)
                    lineTo(size.width * 1.1f, size.height * 0.35f)
                    lineTo(size.width * 0.85f, 0f)
                    close()
                }
                drawPath(path, color = orange.copy(alpha = 0.25f))

                // Second smaller triangle
                val path2 = Path().apply {
                    moveTo(size.width * 0.7f, size.height * 0.05f)
                    lineTo(size.width * 1.05f, size.height * 0.4f)
                    lineTo(size.width * 0.9f, size.height * 0.05f)
                    close()
                }
                drawPath(path2, color = orange.copy(alpha = 0.15f))
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
                    .padding(start = 24.dp, top = 16.dp, end = 24.dp)
            ) {
                // Back button
                IconButton(onClick = onBack) {
                    Icon(Icons.Filled.ArrowBack, "Back", tint = Color.White.copy(alpha = 0.8f))
                }

                Spacer(Modifier.height(24.dp))

                // User avatar
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Filled.Person, null,
                        tint = Color.White,
                        modifier = Modifier.size(36.dp)
                    )
                }

                Spacer(Modifier.height(12.dp))

                Text(
                    "John Doe",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    "john.doe@email.com",
                    color = Color.White.copy(alpha = 0.6f),
                    fontSize = 13.sp
                )

                Spacer(Modifier.height(36.dp))

                // Menu items
                menuItems.forEachIndexed { index, item ->
                    val itemEntry = itemAnimatables[index].value

                    Row(
                        modifier = Modifier
                            .fillMaxWidth(0.65f)
                            .graphicsLayer {
                                alpha = itemEntry
                                translationX = (1f - itemEntry) * -60f
                            }
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                if (selectedItem.intValue == index)
                                    Color.White.copy(alpha = 0.15f)
                                else Color.Transparent
                            )
                            .clickable { selectedItem.intValue = index }
                            .padding(vertical = 14.dp, horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            item.icon, null,
                            tint = Color.White,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(Modifier.width(16.dp))
                        Text(
                            item.label,
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = if (selectedItem.intValue == index) FontWeight.Bold else FontWeight.Normal
                        )
                        if (item.badge != null) {
                            Spacer(Modifier.width(8.dp))
                            Box(
                                modifier = Modifier
                                    .size(22.dp)
                                    .clip(CircleShape)
                                    .background(orange),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    item.badge,
                                    color = Color.White,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    Spacer(Modifier.height(4.dp))
                }

                Spacer(Modifier.weight(1f))

                // Logout button
                Row(
                    modifier = Modifier
                        .graphicsLayer {
                            alpha = if (menuProgress > 0.5f) (menuProgress - 0.5f) * 2f else 0f
                        }
                        .clickable { }
                        .padding(vertical = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Filled.ExitToApp, null,
                        tint = Color.White.copy(alpha = 0.6f),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(Modifier.width(12.dp))
                    Text(
                        "Logout",
                        color = Color.White.copy(alpha = 0.6f),
                        fontSize = 14.sp
                    )
                }

                Spacer(Modifier.height(32.dp))
            }
        }

        // ── Main content layer (slides & scales) ──
        val contentTranslationX = menuProgress * 260f
        val contentScale = 1f - menuProgress * 0.15f
        val contentCorner = (menuProgress * 24f).roundToInt()

        Box(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    translationX = contentTranslationX.dp.toPx()
                    scaleX = contentScale
                    scaleY = contentScale
                    shape = RoundedCornerShape(contentCorner.dp)
                    clip = true
                    shadowElevation = menuProgress * 20f
                }
                .background(Color.White)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Top bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { menuOpen.value = !menuOpen.value }) {
                        Icon(
                            if (menuOpen.value) Icons.Filled.Close else Icons.Filled.Menu,
                            "Menu",
                            tint = Color(0xFF333333)
                        )
                    }
                    Spacer(Modifier.weight(1f))
                    Text(
                        "Discover",
                        color = Color(0xFF333333),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.weight(1f))
                    IconButton(onClick = {}) {
                        Icon(
                            Icons.Filled.Search, "Search",
                            tint = Color(0xFF333333)
                        )
                    }
                }

                // Category tabs
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    val tabs = listOf("Popular", "Recent", "Top Rated")
                    val activeTab = remember { mutableIntStateOf(0) }
                    tabs.forEachIndexed { i, tab ->
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.clickable(
                                indication = null,
                                interactionSource = remember { MutableInteractionSource() }
                            ) { activeTab.intValue = i }
                        ) {
                            Text(
                                tab,
                                color = if (activeTab.intValue == i) accentGreen else Color(0xFFBBBBBB),
                                fontSize = 15.sp,
                                fontWeight = if (activeTab.intValue == i) FontWeight.Bold else FontWeight.Normal
                            )
                            Spacer(Modifier.height(6.dp))
                            if (activeTab.intValue == i) {
                                Box(
                                    modifier = Modifier
                                        .width(24.dp)
                                        .height(3.dp)
                                        .clip(CircleShape)
                                        .background(accentGreen)
                                )
                            } else {
                                Spacer(Modifier.height(3.dp))
                            }
                        }
                    }
                }

                Spacer(Modifier.height(20.dp))

                // Content cards (sample products grid)
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        ProductCardMini(
                            name = "Wireless Buds",
                            price = "$89",
                            bgColor = Color(0xFFF5F5F5),
                            accentColor = accentGreen,
                            modifier = Modifier.weight(1f)
                        )
                        ProductCardMini(
                            name = "Smart Watch",
                            price = "$249",
                            bgColor = Color(0xFFFFF3E0),
                            accentColor = orange,
                            modifier = Modifier.weight(1f)
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        ProductCardMini(
                            name = "Desk Lamp",
                            price = "$45",
                            bgColor = Color(0xFFE8F5E9),
                            accentColor = accentGreen,
                            modifier = Modifier.weight(1f)
                        )
                        ProductCardMini(
                            name = "Speaker",
                            price = "$129",
                            bgColor = Color(0xFFEDE7F6),
                            accentColor = Color(0xFF7C4DFF),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                Spacer(Modifier.weight(1f))

                // Bottom navigation placeholder
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White)
                        .padding(vertical = 12.dp, horizontal = 32.dp)
                        .navigationBarsPadding(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    val navIcons = listOf(
                        Icons.Filled.Home,
                        Icons.Filled.ShoppingCart,
                        Icons.Filled.Favorite,
                        Icons.Filled.Person
                    )
                    navIcons.forEachIndexed { i, icon ->
                        Icon(
                            icon, null,
                            tint = if (i == 0) accentGreen else Color(0xFFCCCCCC),
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }
        }

        // Touch interceptor when menu is open – tap to close
        if (menuOpen.value) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        translationX = contentTranslationX.dp.toPx()
                    }
                    .clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() }
                    ) {
                        menuOpen.value = false
                    }
            )
        }
    }
}

@Composable
private fun ProductCardMini(
    name: String,
    price: String,
    bgColor: Color,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(bgColor)
            .padding(16.dp)
    ) {
        // Product icon placeholder
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(accentColor.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Filled.ShoppingBag, null,
                tint = accentColor,
                modifier = Modifier.size(24.dp)
            )
        }

        Spacer(Modifier.height(12.dp))

        Text(
            name,
            color = Color(0xFF333333),
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(Modifier.height(4.dp))

        Text(
            price,
            color = accentColor,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
    }
}
