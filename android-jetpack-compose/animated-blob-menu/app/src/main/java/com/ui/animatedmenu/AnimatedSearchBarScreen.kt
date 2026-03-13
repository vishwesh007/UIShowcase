package com.ui.animatedmenu

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.sin

private val SearchBg = Color(0xFF0A0815)
private val SearchCardBg = Color(0xFF161222)
private val SearchTeal = Color(0xFF65BF9C)
private val SearchTealLight = Color(0xFF7FCBAD)
private val SearchGlow = Color(0xFFB3F8DC)
private val SearchDarkGreen = Color(0xFF1B6F4E)
private val SearchTextPrimary = Color(0xFFE6EDF3)
private val SearchTextMuted = Color(0xFF8B949E)

@Composable
fun AnimatedSearchBarScreen(onBack: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SearchBg)
            .statusBarsPadding()
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.ArrowBack, "Back", tint = SearchTextPrimary)
            }
            Text(
                "Search Bar Animations",
                color = SearchTextPrimary,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Spacer(Modifier.height(4.dp))
            SearchCardWrapper("Expanding Search") { ExpandingSearchBar() }
            SearchCardWrapper("Glowing Border") { GlowingBorderSearchBar() }
            SearchCardWrapper("Neon Pulse") { NeonPulseSearchBar() }
            SearchCardWrapper("Gradient Underline") { GradientUnderlineSearchBar() }
            SearchCardWrapper("Floating Label") { FloatingLabelSearchBar() }
            SearchCardWrapper("Morphing Icon") { MorphingIconSearchBar() }
            Spacer(Modifier.height(16.dp))
        }
    }
}

@Composable
private fun SearchCardWrapper(title: String, content: @Composable () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(SearchCardBg)
            .padding(20.dp)
    ) {
        Text(
            title,
            color = SearchTeal,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        content()
    }
}

// 1. Expanding Search Bar — icon expands to full-width input
@Composable
private fun ExpandingSearchBar() {
    var expanded by remember { mutableStateOf(false) }
    var text by remember { mutableStateOf("") }
    val width by animateFloatAsState(
        targetValue = if (expanded) 1f else 0f,
        animationSpec = spring(dampingRatio = 0.7f, stiffness = 300f)
    )

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.End
    ) {
        if (width > 0.01f) {
            BasicTextField(
                value = text,
                onValueChange = { text = it },
                textStyle = TextStyle(color = SearchTextPrimary, fontSize = 16.sp),
                modifier = Modifier
                    .weight(width.coerceAtLeast(0.01f))
                    .height(48.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(SearchBg)
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                decorationBox = { inner ->
                    Box(contentAlignment = Alignment.CenterStart) {
                        if (text.isEmpty()) {
                            Text("Search...", color = SearchTextMuted, fontSize = 16.sp)
                        }
                        inner()
                    }
                }
            )
            Spacer(Modifier.width(8.dp))
        }

        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(if (expanded) SearchDarkGreen else SearchTeal)
                .clickable { expanded = !expanded; if (!expanded) text = "" },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                if (expanded) Icons.Default.Close else Icons.Default.Search,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(22.dp)
            )
        }
    }
}

// 2. Glowing Border Search Bar — animated glow border
@Composable
private fun GlowingBorderSearchBar() {
    var text by remember { mutableStateOf("") }
    var focused by remember { mutableStateOf(false) }
    val glowAlpha by animateFloatAsState(
        targetValue = if (focused) 1f else 0.3f,
        animationSpec = tween(500)
    )
    val infiniteTransition = rememberInfiniteTransition()
    val glowPulse by infiniteTransition.animateFloat(
        initialValue = 0.6f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp)
            .drawBehind {
                val effectiveAlpha = (glowAlpha * glowPulse).coerceIn(0f, 1f)
                // Outer glow
                drawRoundRect(
                    color = SearchGlow.copy(alpha = effectiveAlpha * 0.3f),
                    cornerRadius = CornerRadius(28.dp.toPx()),
                    size = Size(size.width + 8.dp.toPx(), size.height + 8.dp.toPx()),
                    topLeft = Offset(-4.dp.toPx(), -4.dp.toPx())
                )
                // Inner glow border
                drawRoundRect(
                    color = SearchTeal.copy(alpha = effectiveAlpha),
                    cornerRadius = CornerRadius(26.dp.toPx()),
                    style = Stroke(width = 2.dp.toPx())
                )
            }
            .clip(RoundedCornerShape(26.dp))
            .background(SearchBg)
            .padding(horizontal = 16.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Search, null, tint = SearchTeal, modifier = Modifier.size(20.dp))
            Spacer(Modifier.width(12.dp))
            BasicTextField(
                value = text,
                onValueChange = { text = it; focused = text.isNotEmpty() },
                textStyle = TextStyle(color = SearchTextPrimary, fontSize = 16.sp),
                modifier = Modifier.weight(1f),
                decorationBox = { inner ->
                    Box {
                        if (text.isEmpty()) Text("Type to glow...", color = SearchTextMuted, fontSize = 16.sp)
                        inner()
                    }
                }
            )
        }
    }
}

// 3. Neon Pulse Search Bar — rotating neon border
@Composable
private fun NeonPulseSearchBar() {
    var text by remember { mutableStateOf("") }
    val infiniteTransition = rememberInfiniteTransition()
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing)
        )
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp)
            .drawBehind {
                rotate(rotation) {
                    drawRoundRect(
                        brush = Brush.sweepGradient(
                            colors = listOf(
                                SearchTeal,
                                SearchGlow,
                                SearchDarkGreen,
                                Color.Transparent,
                                Color.Transparent,
                                SearchTeal
                            )
                        ),
                        cornerRadius = CornerRadius(26.dp.toPx()),
                        style = Stroke(width = 2.5.dp.toPx())
                    )
                }
            }
            .padding(3.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(SearchBg)
            .padding(horizontal = 16.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Search, null, tint = SearchTeal, modifier = Modifier.size(20.dp))
            Spacer(Modifier.width(12.dp))
            BasicTextField(
                value = text,
                onValueChange = { text = it },
                textStyle = TextStyle(color = SearchTextPrimary, fontSize = 16.sp),
                modifier = Modifier.weight(1f),
                decorationBox = { inner ->
                    Box {
                        if (text.isEmpty()) Text("Neon search...", color = SearchTextMuted, fontSize = 16.sp)
                        inner()
                    }
                }
            )
        }
    }
}

// 4. Gradient Underline Search Bar
@Composable
private fun GradientUnderlineSearchBar() {
    var text by remember { mutableStateOf("") }
    val infiniteTransition = rememberInfiniteTransition()
    val shift by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing)
        )
    )

    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.Search, null, tint = SearchTealLight, modifier = Modifier.size(20.dp))
            Spacer(Modifier.width(12.dp))
            BasicTextField(
                value = text,
                onValueChange = { text = it },
                textStyle = TextStyle(color = SearchTextPrimary, fontSize = 16.sp),
                modifier = Modifier.weight(1f),
                decorationBox = { inner ->
                    Box {
                        if (text.isEmpty()) Text("Gradient line...", color = SearchTextMuted, fontSize = 16.sp)
                        inner()
                    }
                }
            )
        }
        // Animated gradient underline
        Canvas(modifier = Modifier.fillMaxWidth().height(3.dp)) {
            val w = size.width
            val colors = listOf(SearchDarkGreen, SearchTeal, SearchGlow, SearchTeal, SearchDarkGreen)
            val offsetPx = shift * w
            drawLine(
                brush = Brush.horizontalGradient(
                    colors = colors,
                    startX = -w + offsetPx,
                    endX = offsetPx
                ),
                start = Offset(0f, size.height / 2),
                end = Offset(w, size.height / 2),
                strokeWidth = size.height,
                cap = StrokeCap.Round
            )
        }
    }
}

// 5. Floating Label Search Bar — label animates up when focused
@Composable
private fun FloatingLabelSearchBar() {
    var text by remember { mutableStateOf("") }
    var focused by remember { mutableStateOf(false) }
    val labelOffset by animateFloatAsState(
        targetValue = if (focused || text.isNotEmpty()) -24f else 0f,
        animationSpec = spring(dampingRatio = 0.8f)
    )
    val labelScale by animateFloatAsState(
        targetValue = if (focused || text.isNotEmpty()) 0.8f else 1f,
        animationSpec = spring(dampingRatio = 0.8f)
    )
    val borderColor by animateColorAsState(
        targetValue = if (focused) SearchTeal else SearchTextMuted.copy(alpha = 0.3f),
        animationSpec = tween(300)
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .border(width = 1.5.dp, brush = SolidColor(borderColor), shape = RoundedCornerShape(12.dp))
            .clip(RoundedCornerShape(12.dp))
            .background(SearchBg)
            .padding(horizontal = 16.dp)
            .clickable { focused = true },
        contentAlignment = Alignment.CenterStart
    ) {
        // Floating label
        Text(
            "Search query",
            color = if (focused) SearchTeal else SearchTextMuted,
            fontSize = 16.sp,
            modifier = Modifier
                .offset(y = labelOffset.dp)
                .scale(labelScale)
        )
        // Input
        BasicTextField(
            value = text,
            onValueChange = { text = it; focused = true },
            textStyle = TextStyle(color = SearchTextPrimary, fontSize = 16.sp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp)
        )
    }
}

// 6. Morphing Icon Search Bar — icon morphs with wave bg
@Composable
private fun MorphingIconSearchBar() {
    var expanded by remember { mutableStateOf(false) }
    var text by remember { mutableStateOf("") }
    val infiniteTransition = rememberInfiniteTransition()
    val wave by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 6.2832f, // 2*PI
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing)
        )
    )
    val expandProgress by animateFloatAsState(
        targetValue = if (expanded) 1f else 0f,
        animationSpec = spring(dampingRatio = 0.65f, stiffness = 200f)
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp)
            .drawBehind {
                // Wave background particles
                val particleCount = 8
                for (i in 0 until particleCount) {
                    val px = size.width * (i.toFloat() / particleCount)
                    val py = size.height / 2 + sin(wave + i * 0.8f).toFloat() * 8.dp.toPx()
                    val alpha = (0.15f + expandProgress * 0.25f).coerceIn(0f, 1f)
                    drawCircle(
                        color = SearchTeal.copy(alpha = alpha),
                        radius = (3.dp.toPx() + expandProgress * 2.dp.toPx()),
                        center = Offset(px, py)
                    )
                }
            }
            .clip(RoundedCornerShape(26.dp))
            .background(SearchBg.copy(alpha = 0.9f))
            .border(
                width = 1.dp,
                color = SearchTeal.copy(alpha = 0.2f + expandProgress * 0.4f),
                shape = RoundedCornerShape(26.dp)
            )
            .padding(horizontal = 12.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(SearchTeal.copy(alpha = 0.15f + expandProgress * 0.15f))
                    .clickable { expanded = !expanded; if (!expanded) text = "" },
                contentAlignment = Alignment.Center
            ) {
                val iconScale by animateFloatAsState(
                    targetValue = if (expanded) 0.85f else 1.1f,
                    animationSpec = spring(dampingRatio = 0.5f)
                )
                Icon(
                    if (expanded) Icons.Default.Close else Icons.Default.Search,
                    null,
                    tint = SearchTeal,
                    modifier = Modifier.size(18.dp).scale(iconScale)
                )
            }

            AnimatedVisibility(
                visible = expanded,
                enter = expandHorizontally() + fadeIn(),
                exit = shrinkHorizontally() + fadeOut()
            ) {
                BasicTextField(
                    value = text,
                    onValueChange = { text = it },
                    textStyle = TextStyle(color = SearchTextPrimary, fontSize = 16.sp),
                    modifier = Modifier
                        .padding(start = 12.dp)
                        .weight(1f),
                    decorationBox = { inner ->
                        Box {
                            if (text.isEmpty()) Text("Discover...", color = SearchTextMuted, fontSize = 16.sp)
                            inner()
                        }
                    }
                )
            }

            if (!expanded) {
                Text(
                    "Tap to search",
                    color = SearchTextMuted,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(start = 12.dp)
                )
            }
        }
    }
}
