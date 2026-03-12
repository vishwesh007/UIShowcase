package com.ui.animatedmenu.showcase

import androidx.compose.animation.animateColorAsState
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
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.abs
import kotlin.math.sin

data class NavItem(
    val icon: ImageVector,
    val label: String
)

@Composable
fun SmartNavbarScreen(onBack: () -> Unit) {
    val navItems = remember {
        listOf(
            NavItem(Icons.Filled.Home, "Home"),
            NavItem(Icons.Filled.Search, "Search"),
            // Center FAB placeholder (index 2)
            NavItem(Icons.Filled.Add, ""),
            NavItem(Icons.Filled.Notifications, "Alerts"),
            NavItem(Icons.Filled.Person, "Profile"),
        )
    }

    val selectedIndex = remember { mutableIntStateOf(0) }

    // Animated indicator position (smooth slide)
    val indicatorX by animateFloatAsState(
        targetValue = selectedIndex.intValue.toFloat(),
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = 400f
        ),
        label = "indicatorX"
    )

    // Selection bounce
    val selectionScale = remember { Animatable(1f) }
    LaunchedEffect(selectedIndex.intValue) {
        selectionScale.snapTo(0.8f)
        selectionScale.animateTo(
            1f,
            spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = 500f)
        )
    }

    // Content shimmer
    val infiniteTransition = rememberInfiniteTransition(label = "nav")
    val shimmerPhase by infiniteTransition.animateFloat(
        0f, 6.28f,
        infiniteRepeatable(tween(4000, easing = LinearEasing)),
        label = "shimmer"
    )

    // FAB rotation
    val fabRotation by infiniteTransition.animateFloat(
        0f, 360f,
        infiniteRepeatable(tween(8000, easing = LinearEasing)),
        label = "fabRot"
    )

    val purple = Color(0xFF7C4DFF)
    val deepPurple = Color(0xFF5C35CC)
    val bgColor = Color(0xFFF8F9FE)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(bgColor)
    ) {
        // ── Main content area ──
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
        ) {
            // Top bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(Icons.Filled.ArrowBack, "Back", tint = Color(0xFF333333))
                }
                Spacer(Modifier.weight(1f))
                Text(
                    "Smart Navbar",
                    color = Color(0xFF333333),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.weight(1f))
                Spacer(Modifier.size(48.dp))
            }

            Spacer(Modifier.height(24.dp))

            // Content based on selected tab
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                val contentPages = listOf(
                    "Home" to "Discover amazing content",
                    "Search" to "Find what you need",
                    "Create" to "Make something new",
                    "Alerts" to "Stay up to date",
                    "Profile" to "Your personal space"
                )
                val (title, subtitle) = contentPages[selectedIndex.intValue]

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    // Large icon for current page
                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .clip(CircleShape)
                            .background(purple.copy(alpha = 0.08f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            navItems[selectedIndex.intValue].icon, null,
                            tint = purple,
                            modifier = Modifier
                                .size(48.dp)
                                .graphicsLayer {
                                    scaleX = selectionScale.value
                                    scaleY = selectionScale.value
                                }
                        )
                    }

                    Spacer(Modifier.height(20.dp))

                    Text(
                        title,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1A1A2E)
                    )

                    Spacer(Modifier.height(8.dp))

                    Text(
                        subtitle,
                        fontSize = 15.sp,
                        color = Color(0xFF999999)
                    )

                    Spacer(Modifier.height(32.dp))

                    // Decorative cards
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        repeat(3) { i ->
                            val cardFloat = sin(shimmerPhase + i * 2f).toFloat() * 6f
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(100.dp)
                                    .graphicsLayer { translationY = cardFloat }
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(
                                        Brush.verticalGradient(
                                            listOf(
                                                purple.copy(alpha = 0.06f + i * 0.04f),
                                                purple.copy(alpha = 0.02f)
                                            )
                                        )
                                    )
                                    .padding(12.dp),
                                contentAlignment = Alignment.TopStart
                            ) {
                                Column {
                                    Box(
                                        modifier = Modifier
                                            .size(28.dp)
                                            .clip(CircleShape)
                                            .background(purple.copy(alpha = 0.15f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            "${i + 1}",
                                            color = purple,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                    Spacer(Modifier.height(8.dp))
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth(0.7f)
                                            .height(6.dp)
                                            .clip(CircleShape)
                                            .background(Color(0xFFE0E0E0))
                                    )
                                    Spacer(Modifier.height(4.dp))
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth(0.5f)
                                            .height(6.dp)
                                            .clip(CircleShape)
                                            .background(Color(0xFFEEEEEE))
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // ── Bottom Navigation Bar ──
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
        ) {
            // Navbar background with shadow
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .shadow(12.dp, RoundedCornerShape(28.dp), ambientColor = Color.Black.copy(alpha = 0.08f))
                    .clip(RoundedCornerShape(28.dp))
                    .background(Color.White)
                    .height(72.dp)
            ) {
                // Sliding indicator behind active item
                Canvas(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 8.dp, vertical = 8.dp)
                ) {
                    val itemWidth = size.width / 5f
                    // Skip drawing indicator for center FAB (index 2)
                    if (indicatorX.toInt() != 2) {
                        val cx = itemWidth * indicatorX + itemWidth / 2f
                        val indicatorWidth = 56f
                        val indicatorHeight = size.height - 8f

                        drawRoundRect(
                            color = purple.copy(alpha = 0.1f),
                            topLeft = Offset(cx - indicatorWidth / 2f, 4f),
                            size = Size(indicatorWidth, indicatorHeight),
                            cornerRadius = CornerRadius(20f, 20f)
                        )
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    navItems.forEachIndexed { index, item ->
                        if (index == 2) {
                            // Center FAB
                            Box(
                                modifier = Modifier
                                    .offset(y = (-16).dp)
                                    .size(56.dp)
                                    .shadow(8.dp, CircleShape, ambientColor = purple.copy(alpha = 0.3f))
                                    .clip(CircleShape)
                                    .background(
                                        Brush.linearGradient(
                                            listOf(purple, deepPurple)
                                        )
                                    )
                                    .clickable(
                                        indication = null,
                                        interactionSource = remember { MutableInteractionSource() }
                                    ) { selectedIndex.intValue = 2 },
                                contentAlignment = Alignment.Center
                            ) {
                                // Glow ring
                                Canvas(modifier = Modifier.fillMaxSize()) {
                                    drawCircle(
                                        color = Color.White.copy(alpha = 0.15f),
                                        radius = size.minDimension / 2f - 2f,
                                        style = Stroke(1.5f)
                                    )
                                }
                                Icon(
                                    Icons.Filled.Add, "Create",
                                    tint = Color.White,
                                    modifier = Modifier
                                        .size(28.dp)
                                        .graphicsLayer {
                                            rotationZ = if (selectedIndex.intValue == 2) 45f else 0f
                                        }
                                )
                            }
                        } else {
                            // Regular nav item
                            val isSelected = selectedIndex.intValue == index
                            val itemColor by animateColorAsState(
                                targetValue = if (isSelected) purple else Color(0xFFBBBBBB),
                                animationSpec = tween(300),
                                label = "itemColor$index"
                            )
                            val itemScale by animateFloatAsState(
                                targetValue = if (isSelected) 1.15f else 1f,
                                animationSpec = spring(
                                    dampingRatio = Spring.DampingRatioMediumBouncy,
                                    stiffness = 500f
                                ),
                                label = "scale$index"
                            )

                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable(
                                        indication = null,
                                        interactionSource = remember { MutableInteractionSource() }
                                    ) { selectedIndex.intValue = index },
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    item.icon, item.label,
                                    tint = itemColor,
                                    modifier = Modifier
                                        .size(24.dp)
                                        .graphicsLayer {
                                            scaleX = itemScale
                                            scaleY = itemScale
                                        }
                                )

                                // Label appears with animation for selected item
                                val labelAlpha by animateFloatAsState(
                                    targetValue = if (isSelected) 1f else 0f,
                                    animationSpec = tween(200),
                                    label = "labelAlpha$index"
                                )
                                if (labelAlpha > 0f) {
                                    Spacer(Modifier.height(2.dp))
                                    Text(
                                        item.label,
                                        color = purple.copy(alpha = labelAlpha),
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier.graphicsLayer {
                                            alpha = labelAlpha
                                            translationY = (1f - labelAlpha) * 8f
                                        }
                                    )
                                }

                                // Active dot indicator
                                val dotScale by animateFloatAsState(
                                    targetValue = if (isSelected) 1f else 0f,
                                    animationSpec = spring(
                                        dampingRatio = Spring.DampingRatioMediumBouncy,
                                        stiffness = 600f
                                    ),
                                    label = "dot$index"
                                )
                                Spacer(Modifier.height(3.dp))
                                Box(
                                    modifier = Modifier
                                        .size(4.dp)
                                        .graphicsLayer {
                                            scaleX = dotScale
                                            scaleY = dotScale
                                        }
                                        .clip(CircleShape)
                                        .background(purple)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
