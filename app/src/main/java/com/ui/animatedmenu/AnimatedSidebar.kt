package com.ui.animatedmenu

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
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.sin

/**
 * Animated Sidebar / Drawer with smooth multi-phase cascading reveal
 * - Phase 1: Scrim fades in with ease
 * - Phase 2: Drawer slides in with gentle deceleration + subtle scale
 * - Phase 3: Profile section fades/scales in
 * - Phase 4: Menu items cascade in with staggered slide + fade
 * - Organic blob edge has a gentle breathing wave
 */

private val DrawerBackground = Color(0xFF1A1A1A)
private val DrawerAccent = Color(0xFFFFC107)
private val DrawerItemSelected = Color(0xFF2D2D2D)
private val DrawerItemHover = Color(0xFF252525)
private val DrawerTextColor = Color(0xFFF5F5F5)
private val DrawerSubtextColor = Color(0xFF888888)

data class DrawerItem(
    val icon: ImageVector,
    val label: String,
    val badge: Int = 0
)

val drawerItems = listOf(
    DrawerItem(Icons.Filled.Home, "Home"),
    DrawerItem(Icons.Filled.Person, "Profile"),
    DrawerItem(Icons.Filled.Favorite, "Favorites", badge = 3),
    DrawerItem(Icons.Filled.Notifications, "Notifications", badge = 12),
    DrawerItem(Icons.Filled.Settings, "Settings"),
    DrawerItem(Icons.Filled.Info, "About")
)

@Composable
fun AnimatedSidebar(
    isOpen: Boolean,
    selectedIndex: Int = 0,
    onItemSelected: (Int) -> Unit = {},
    onClose: () -> Unit = {}
) {
    val drawerWidth = 280.dp

    // Phase 1: Scrim — smooth ease-in-out
    val scrimAlpha by animateFloatAsState(
        targetValue = if (isOpen) 0.55f else 0f,
        animationSpec = tween(
            durationMillis = 400,
            easing = FastOutSlowInEasing
        ),
        label = "scrimAlpha"
    )

    // Phase 2: Drawer slide — smooth deceleration (no bounce)
    val slideProgress by animateFloatAsState(
        targetValue = if (isOpen) 1f else 0f,
        animationSpec = tween(
            durationMillis = 450,
            easing = FastOutSlowInEasing
        ),
        label = "drawerSlide"
    )

    // Phase 2b: Subtle scale — drawer scales from 0.92 → 1.0
    val drawerScale by animateFloatAsState(
        targetValue = if (isOpen) 1f else 0.92f,
        animationSpec = tween(
            durationMillis = 500,
            easing = FastOutSlowInEasing
        ),
        label = "drawerScale"
    )

    // Phase 3: Profile section — delayed fade + slide up
    val profileAlpha by animateFloatAsState(
        targetValue = if (isOpen) 1f else 0f,
        animationSpec = tween(
            durationMillis = 350,
            delayMillis = 200,
            easing = FastOutSlowInEasing
        ),
        label = "profileAlpha"
    )
    val profileOffsetY by animateFloatAsState(
        targetValue = if (isOpen) 0f else 20f,
        animationSpec = tween(
            durationMillis = 400,
            delayMillis = 200,
            easing = FastOutSlowInEasing
        ),
        label = "profileOffsetY"
    )

    // Breathing wave for organic edge
    val infiniteTransition = rememberInfiniteTransition(label = "edgeWave")
    val wavePhase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 6.2832f, // 2 * PI
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "wavePhase"
    )

    if (slideProgress > 0f || scrimAlpha > 0f) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Scrim overlay — smooth independent fade
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .alpha(scrimAlpha)
                    .background(Color.Black)
                    .clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() }
                    ) { onClose() }
            )

            // Drawer panel with slide + scale
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(drawerWidth)
                    .graphicsLayer {
                        translationX = -drawerWidth.toPx() * (1f - slideProgress)
                        scaleY = drawerScale
                        transformOrigin = androidx.compose.ui.graphics.TransformOrigin(0f, 0.5f)
                    }
            ) {
                // Animated organic blob edge
                Canvas(
                    modifier = Modifier.fillMaxSize()
                ) {
                    val w = size.width
                    val h = size.height
                    val waveAmp = 8f * slideProgress // Wave grows as drawer opens

                    val path = Path().apply {
                        moveTo(0f, 0f)
                        lineTo(w - 20f, 0f)

                        // Organic right edge with animated wave
                        val segments = 8
                        for (i in 0 until segments) {
                            val t0 = i.toFloat() / segments
                            val t1 = (i + 1).toFloat() / segments
                            val y0 = h * t0
                            val y1 = h * t1
                            val wobble = sin(wavePhase + t0 * 4f).toFloat() * waveAmp
                            val wobble2 = sin(wavePhase + t1 * 4f).toFloat() * waveAmp

                            cubicTo(
                                w - 5f + wobble, y0 + (y1 - y0) * 0.3f,
                                w + 10f + wobble2, y0 + (y1 - y0) * 0.7f,
                                w - 5f + wobble2, y1
                            )
                        }

                        lineTo(0f, h)
                        close()
                    }

                    drawPath(path = path, color = DrawerBackground)
                }

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 48.dp, start = 16.dp, end = 24.dp)
                ) {
                    // Profile section with delayed entrance
                    Column(
                        modifier = Modifier
                            .alpha(profileAlpha)
                            .graphicsLayer { translationY = profileOffsetY }
                    ) {
                        // User avatar area with scale-in
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .scale(profileAlpha) // scales with alpha
                                .clip(CircleShape)
                                .background(DrawerAccent.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Person,
                                contentDescription = null,
                                tint = DrawerAccent,
                                modifier = Modifier.size(36.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = "Animated Menu",
                            color = DrawerTextColor,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "UI Components Demo",
                            color = DrawerSubtextColor,
                            fontSize = 13.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    // Phase 4: Menu items with staggered cascade
                    drawerItems.forEachIndexed { index, item ->
                        val staggerDelay = 300 + index * 80 // starts after profile, 80ms gaps

                        val itemAlpha by animateFloatAsState(
                            targetValue = if (isOpen) 1f else 0f,
                            animationSpec = tween(
                                durationMillis = 350,
                                delayMillis = staggerDelay,
                                easing = FastOutSlowInEasing
                            ),
                            label = "itemAlpha_$index"
                        )

                        val itemSlideX by animateFloatAsState(
                            targetValue = if (isOpen) 0f else -30f,
                            animationSpec = tween(
                                durationMillis = 400,
                                delayMillis = staggerDelay,
                                easing = FastOutSlowInEasing
                            ),
                            label = "itemSlideX_$index"
                        )

                        val isSelected = index == selectedIndex

                        SidebarMenuItem(
                            item = item,
                            isSelected = isSelected,
                            alpha = itemAlpha,
                            slideX = itemSlideX,
                            onClick = { onItemSelected(index) }
                        )

                        if (index < drawerItems.lastIndex) {
                            Spacer(modifier = Modifier.height(4.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SidebarMenuItem(
    item: DrawerItem,
    isSelected: Boolean,
    alpha: Float,
    slideX: Float = 0f,
    onClick: () -> Unit
) {
    val bgColor by animateColorAsState(
        targetValue = if (isSelected) DrawerItemSelected else Color.Transparent,
        animationSpec = tween(200),
        label = "menuItemBg"
    )

    val iconColor by animateColorAsState(
        targetValue = if (isSelected) DrawerAccent else DrawerSubtextColor,
        animationSpec = tween(200),
        label = "menuItemIcon"
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .alpha(alpha)
            .graphicsLayer { translationX = slideX * 2.75f } // dp to px approx
            .clip(RoundedCornerShape(12.dp))
            .background(bgColor)
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) { onClick() }
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = item.icon,
            contentDescription = item.label,
            tint = iconColor,
            modifier = Modifier.size(22.dp)
        )

        Spacer(modifier = Modifier.width(16.dp))

        Text(
            text = item.label,
            color = if (isSelected) DrawerTextColor else DrawerSubtextColor,
            fontSize = 15.sp,
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
            modifier = Modifier.weight(1f)
        )

        if (item.badge > 0) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .background(DrawerAccent)
                    .padding(horizontal = 8.dp, vertical = 2.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "${item.badge}",
                    color = Color(0xFF1A1A1A),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
