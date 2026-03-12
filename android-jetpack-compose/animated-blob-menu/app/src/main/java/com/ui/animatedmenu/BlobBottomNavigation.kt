package com.ui.animatedmenu

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.abs

/**
 * Animated Blob Bottom Navigation Bar
 *
 * Recreates the "Menu Animation in Figma" design by Shmelt Studios:
 * - Dark rounded bottom bar
 * - 3 icons: Home, Flame, Settings
 * - Organic blob/liquid shape rises from bar when item is selected
 * - Selected icon highlighted in amber/gold, unselected in white
 * - Smooth spring animation between selections
 */

// ── Data ────────────────────────────────────────────────────────────────────────

data class NavItem(
    val icon: ImageVector,
    val label: String
)

val navItems = listOf(
    NavItem(Icons.Filled.Home, "Home"),
    NavItem(Icons.Filled.LocalFireDepartment, "Fire"),
    NavItem(Icons.Filled.Settings, "Settings")
)

// ── Colors ──────────────────────────────────────────────────────────────────────

private val BarBackground = Color(0xFF1A1A1A)
private val SelectedIconColor = Color(0xFFFFC107) // Amber/gold
private val UnselectedIconColor = Color(0xFFB0B0B0)
private val BlobColor = Color(0xFF1A1A1A)
private val ScreenBackground = Color(0xFFCDBBA7) // Warm beige from the video

// ── Composables ─────────────────────────────────────────────────────────────────

@Composable
fun AnimatedBlobMenuScreen(onShowcase: () -> Unit = {}) {
    var selectedIndex by remember { mutableIntStateOf(0) }
    var isSidebarOpen by remember { mutableStateOf(false) }
    var sidebarSelectedIndex by remember { mutableIntStateOf(0) }

    val screenTitles = listOf("Home", "Fire", "Settings")

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(ScreenBackground)
    ) {
        // Main content column
        Column(modifier = Modifier.fillMaxSize()) {
            // Top App Bar with wave animation
            AnimatedAppBar(
                title = screenTitles[selectedIndex],
                onMenuClick = { isSidebarOpen = true },
                onSearchClick = onShowcase
            )

            // Content area
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = screenTitles[selectedIndex],
                        style = MaterialTheme.typography.headlineLarge,
                        color = Color(0xFF2D2D2D)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Tap 🔍 for UI Showcase",
                        color = Color(0xFF666666),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            // Bottom navigation bar with blob animation
            BlobBottomBar(
                items = navItems,
                selectedIndex = selectedIndex,
                onItemSelected = { selectedIndex = it },
                modifier = Modifier
                    .padding(horizontal = 24.dp, vertical = 24.dp)
            )
        }

        // Animated Expandable FAB
        AnimatedExpandableFab(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 24.dp, bottom = 110.dp)
        )

        // Animated Sidebar (drawn on top)
        AnimatedSidebar(
            isOpen = isSidebarOpen,
            selectedIndex = sidebarSelectedIndex,
            onItemSelected = { index ->
                sidebarSelectedIndex = index
                isSidebarOpen = false
            },
            onClose = { isSidebarOpen = false }
        )
    }
}

@Composable
fun BlobBottomBar(
    items: List<NavItem>,
    selectedIndex: Int,
    onItemSelected: (Int) -> Unit,
    modifier: Modifier = Modifier,
    barHeight: Dp = 64.dp,
    blobHeight: Dp = 28.dp
) {
    val density = LocalDensity.current

    // Animate the blob position horizontally
    val animatedSelectedIndex by animateFloatAsState(
        targetValue = selectedIndex.toFloat(),
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "blobPosition"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(barHeight + blobHeight)
    ) {
        // The blob canvas drawn above the bar
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(barHeight + blobHeight)
        ) {
            val barTop = blobHeight.toPx()
            val barWidthPx = size.width
            val barHeightPx = barHeight.toPx()
            val blobHeightPx = blobHeight.toPx()
            val itemWidth = barWidthPx / items.size
            val blobCenterX = itemWidth * animatedSelectedIndex + itemWidth / 2f
            val blobWidthPx = itemWidth * 0.85f

            // Draw the bar background with rounded corners
            drawBarWithBlob(
                barTop = barTop,
                barWidth = barWidthPx,
                barHeight = barHeightPx,
                blobCenterX = blobCenterX,
                blobWidth = blobWidthPx,
                blobHeight = blobHeightPx,
                cornerRadius = barHeightPx / 2f
            )
        }

        // Icon row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(barHeight)
                .align(Alignment.BottomCenter),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            items.forEachIndexed { index, item ->
                val isSelected = index == selectedIndex

                // Animate icon vertical offset when selected (rises into the blob)
                val iconOffsetY by animateFloatAsState(
                    targetValue = if (isSelected) -blobHeight.value * 0.45f else 0f,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessLow
                    ),
                    label = "iconOffset_$index"
                )

                val iconColor by animateColorAsState(
                    targetValue = if (isSelected) SelectedIconColor else UnselectedIconColor,
                    animationSpec = tween(300),
                    label = "iconColor_$index"
                )

                val iconSize by animateDpAsState(
                    targetValue = if (isSelected) 30.dp else 24.dp,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessLow
                    ),
                    label = "iconSize_$index"
                )

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .offset(y = iconOffsetY.dp)
                        .clickable(
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() }
                        ) { onItemSelected(index) },
                    contentAlignment = Alignment.Center
                ) {
                    // Circle behind selected icon
                    if (isSelected) {
                        val circleAlpha by animateFloatAsState(
                            targetValue = 1f,
                            animationSpec = tween(200),
                            label = "circleAlpha"
                        )
                        Canvas(
                            modifier = Modifier.size(44.dp)
                        ) {
                            drawCircle(
                                color = Color(0xFF2D2D2D),
                                radius = size.minDimension / 2f,
                                alpha = circleAlpha
                            )
                        }
                    }

                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.label,
                        tint = iconColor,
                        modifier = Modifier.size(iconSize)
                    )
                }
            }
        }
    }
}

// ── Canvas Drawing ──────────────────────────────────────────────────────────────

private fun DrawScope.drawBarWithBlob(
    barTop: Float,
    barWidth: Float,
    barHeight: Float,
    blobCenterX: Float,
    blobWidth: Float,
    blobHeight: Float,
    cornerRadius: Float
) {
    val path = Path()
    val halfBlob = blobWidth / 2f
    val blobLeft = blobCenterX - halfBlob
    val blobRight = blobCenterX + halfBlob

    // Start from top-left corner of the bar
    path.moveTo(cornerRadius, barTop)

    // Top edge: draw up to the blob, then the blob curve, then continue
    if (blobLeft > cornerRadius) {
        path.lineTo(blobLeft, barTop)
    }

    // Blob curve: organic/liquid shape that rises above the bar
    // Using cubic bezier curves for smooth organic look
    path.cubicTo(
        blobLeft + halfBlob * 0.15f, barTop,           // control 1
        blobCenterX - halfBlob * 0.45f, barTop - blobHeight, // control 2
        blobCenterX, barTop - blobHeight               // end point (top of blob)
    )
    path.cubicTo(
        blobCenterX + halfBlob * 0.45f, barTop - blobHeight, // control 1
        blobRight - halfBlob * 0.15f, barTop,           // control 2
        blobRight, barTop                               // end point (back to bar)
    )

    // Continue top edge to right side
    if (blobRight < barWidth - cornerRadius) {
        path.lineTo(barWidth - cornerRadius, barTop)
    }

    // Top-right corner
    path.cubicTo(
        barWidth, barTop,
        barWidth, barTop,
        barWidth, barTop + cornerRadius
    )

    // Right edge
    path.lineTo(barWidth, barTop + barHeight - cornerRadius)

    // Bottom-right corner
    path.cubicTo(
        barWidth, barTop + barHeight,
        barWidth, barTop + barHeight,
        barWidth - cornerRadius, barTop + barHeight
    )

    // Bottom edge
    path.lineTo(cornerRadius, barTop + barHeight)

    // Bottom-left corner
    path.cubicTo(
        0f, barTop + barHeight,
        0f, barTop + barHeight,
        0f, barTop + barHeight - cornerRadius
    )

    // Left edge
    path.lineTo(0f, barTop + cornerRadius)

    // Top-left corner
    path.cubicTo(
        0f, barTop,
        0f, barTop,
        cornerRadius, barTop
    )

    path.close()

    drawPath(
        path = path,
        color = BlobColor
    )
}
