package com.ui.animatedmenu.showcase

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private data class Product(val name: String, val price: String, val oldPrice: String = "",
                           val liked: Boolean = false, val rating: Float = 4.5f,
                           val color1: Color, val color2: Color)

private val products = listOf(
    Product("Hyper Frame", "$99", "$129", true, 4.8f, Color(0xFFE8F5E9), Color(0xFFC8E6C9)),
    Product("Retro Shade", "$82", "$110", false, 4.6f, Color(0xFFFFF3E0), Color(0xFFFFE0B2)),
    Product("Neo Lens", "$120", "", false, 4.9f, Color(0xFFE3F2FD), Color(0xFFBBDEFB)),
    Product("Classic Pro", "$65", "$85", true, 4.3f, Color(0xFFFCE4EC), Color(0xFFF8BBD0)),
    Product("Urban Edge", "$145", "", false, 4.7f, Color(0xFFE0F2F1), Color(0xFFB2DFDB)),
    Product("Pilot Vista", "$78", "$98", false, 4.5f, Color(0xFFF3E5F5), Color(0xFFE1BEE7)),
)

@Composable
fun ECommerceScreen(onBack: () -> Unit = {}) {
    val Bg = Color(0xFFF8FAF8)
    val Accent = Color(0xFF1B5E20)
    val AccentLight = Color(0xFF4CAF50)
    val TextDark = Color(0xFF1A1A1A)
    val TextMuted = Color(0xFF9E9E9E)

    Column(modifier = Modifier.fillMaxSize().background(Bg).statusBarsPadding()) {
        // Top bar
        Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) {
                Icon(Icons.Filled.ArrowBack, "Back", tint = TextDark)
            }
            Spacer(Modifier.weight(1f))
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("DISCOVER", color = TextDark, fontSize = 11.sp,
                    fontWeight = FontWeight.Medium, letterSpacing = 2.sp)
            }
            Spacer(Modifier.weight(1f))
            Box(contentAlignment = Alignment.Center) {
                IconButton(onClick = {}) {
                    Icon(Icons.Filled.ShoppingBag, null, tint = TextDark)
                }
                Box(modifier = Modifier.align(Alignment.TopEnd).offset(x = (-4).dp, y = 4.dp)
                    .size(16.dp).clip(CircleShape).background(AccentLight),
                    contentAlignment = Alignment.Center) {
                    Text("3", color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        // Header
        Column(modifier = Modifier.padding(horizontal = 20.dp)) {
            Text("New\nArrivals", fontSize = 32.sp, fontWeight = FontWeight.Black,
                color = TextDark, lineHeight = 36.sp)
            Spacer(Modifier.height(8.dp))
            Text("16 Products available", color = TextMuted, fontSize = 13.sp)
        }

        Spacer(Modifier.height(12.dp))

        // Search bar
        Box(modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White)
            .padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Filled.Search, "Search", tint = TextMuted, modifier = Modifier.size(20.dp))
                Spacer(Modifier.width(10.dp))
                Text("Search products...", color = TextMuted, fontSize = 14.sp)
                Spacer(Modifier.weight(1f))
                Box(modifier = Modifier.size(32.dp).clip(RoundedCornerShape(10.dp))
                    .background(Accent), contentAlignment = Alignment.Center) {
                    Icon(Icons.Filled.Tune, null, tint = Color.White, modifier = Modifier.size(16.dp))
                }
            }
        }

        Spacer(Modifier.height(12.dp))

        // Category chips
        Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf("All" to true, "Trending" to false, "New" to false, "Sale" to false).forEach { (label, sel) ->
                Box(modifier = Modifier.clip(RoundedCornerShape(20.dp))
                    .background(if (sel) Accent else Color.White)
                    .padding(horizontal = 16.dp, vertical = 8.dp)) {
                    Text(label, color = if (sel) Color.White else TextMuted,
                        fontSize = 12.sp, fontWeight = FontWeight.Medium)
                }
            }
        }

        Spacer(Modifier.height(8.dp))

        // Product grid
        LazyVerticalGrid(columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            modifier = Modifier.fillMaxSize()) {
            itemsIndexed(products) { index, product ->
                val itemEntry = remember { Animatable(0f) }
                LaunchedEffect(Unit) {
                    kotlinx.coroutines.delay(index * 120L)
                    itemEntry.animateTo(1f, tween(400, easing = FastOutSlowInEasing))
                }
                Box(modifier = Modifier.graphicsLayer {
                    alpha = itemEntry.value
                    translationY = (1f - itemEntry.value) * 50f
                }) {
                    ProductCard2(product, Accent, AccentLight, TextDark)
                }
            }
        }
    }
}

@Composable
private fun ProductCard2(product: Product, accent: Color, accentLight: Color, textDark: Color) {
    var liked by remember { mutableStateOf(product.liked) }
    val heartScale by animateFloatAsState(
        if (liked) 1.2f else 1f,
        spring(Spring.DampingRatioMediumBouncy, 600f), label = "hs")

    Column(modifier = Modifier.clip(RoundedCornerShape(20.dp)).background(Color.White)) {
        // Product image area
        Box(modifier = Modifier.fillMaxWidth().height(150.dp)
            .background(Brush.verticalGradient(listOf(product.color1, product.color2))),
            contentAlignment = Alignment.Center) {
            Icon(Icons.Filled.RemoveRedEye, null,
                tint = accent.copy(alpha = 0.2f), modifier = Modifier.size(52.dp))
            // Heart
            Box(modifier = Modifier.align(Alignment.TopEnd).padding(8.dp)
                .size(30.dp).clip(CircleShape).background(Color.White.copy(alpha = 0.9f))
                .scale(heartScale).clickable { liked = !liked },
                contentAlignment = Alignment.Center) {
                Icon(if (liked) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                    "Like", tint = if (liked) Color(0xFFE91E63) else Color(0xFFBDBDBD),
                    modifier = Modifier.size(14.dp))
            }
        }
        Column(modifier = Modifier.padding(12.dp)) {
            Text(product.name, fontWeight = FontWeight.SemiBold, fontSize = 14.sp,
                color = textDark, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Spacer(Modifier.height(2.dp))
            // Rating
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Filled.Star, null, tint = Color(0xFFFFB300), modifier = Modifier.size(12.dp))
                Spacer(Modifier.width(3.dp))
                Text("${product.rating}", color = Color(0xFF757575), fontSize = 11.sp)
            }
            Spacer(Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(product.price, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = accent)
                if (product.oldPrice.isNotEmpty()) {
                    Spacer(Modifier.width(6.dp))
                    Text(product.oldPrice, color = Color(0xFFBDBDBD), fontSize = 12.sp,
                        fontWeight = FontWeight.Normal)
                }
            }
        }
    }
}
