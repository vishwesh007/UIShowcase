package com.ui.animatedmenu

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.abs
import kotlin.math.sin

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

            // Content area with tool buttons
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                ToolButtonGrid(onShowcase = onShowcase)
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

// ── Tool Buttons Grid ───────────────────────────────────────────────────────────

data class ToolItem(
    val icon: ImageVector,
    val label: String,
    val description: String,
    val color: Color
)

private val toolItems = listOf(
    ToolItem(Icons.Filled.FolderOpen, "Select APK", "Pick an APK file from storage", Color(0xFFFFC107)),
    ToolItem(Icons.Filled.PhoneAndroid, "Extract App", "Extract APK from installed apps", Color(0xFF4CAF50)),
    ToolItem(Icons.Filled.Build, "Patch", "Apply patches to selected APK", Color(0xFF2196F3)),
    ToolItem(Icons.Filled.ContentCopy, "Backup", "Create a full backup of APK", Color(0xFF9C27B0)),
    ToolItem(Icons.Filled.Key, "Signer", "Sign APK with custom keystore", Color(0xFFFF5722)),
    ToolItem(Icons.Filled.AutoFixHigh, "Toolkit", "Additional tools & utilities", Color(0xFF00BCD4)),
)

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun ToolButtonGrid(onShowcase: () -> Unit) {
    var tooltipText by remember { mutableStateOf<String?>(null) }

    val inf = rememberInfiniteTransition(label = "toolGrid")
    val phase by inf.animateFloat(
        0f, 6.28f,
        infiniteRepeatable(tween(4000, easing = LinearEasing)),
        label = "toolPhase"
    )

    Column(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 28.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            "Tools",
            color = Color(0xFF2D2D2D),
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
        )

        Spacer(Modifier.height(4.dp))

        // 3x2 grid
        for (row in 0..1) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                for (col in 0..2) {
                    val idx = row * 3 + col
                    val tool = toolItems[idx]
                    val enterAnim = remember { Animatable(0f) }
                    LaunchedEffect(Unit) {
                        kotlinx.coroutines.delay(idx * 100L)
                        enterAnim.animateTo(1f, tween(400, easing = FastOutSlowInEasing))
                    }

                    val float = sin(phase + idx * 0.8f).toFloat() * 2f

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .scale(enterAnim.value)
                            .offset(y = float.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(RoundedCornerShape(18.dp))
                                .background(
                                    Brush.verticalGradient(
                                        listOf(
                                            tool.color.copy(alpha = 0.15f),
                                            tool.color.copy(alpha = 0.08f)
                                        )
                                    )
                                )
                                .combinedClickable(
                                    onClick = { /* tool action placeholder */ },
                                    onLongClick = { tooltipText = "${tool.label}: ${tool.description}" }
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            // Subtle gradient ring
                            Canvas(modifier = Modifier.fillMaxSize()) {
                                drawCircle(
                                    tool.color.copy(alpha = 0.1f),
                                    radius = size.width / 2.5f
                                )
                            }
                            Icon(
                                tool.icon, null,
                                tint = tool.color,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                        Spacer(Modifier.height(6.dp))
                        Text(
                            tool.label,
                            color = Color(0xFF4A4A4A),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            textAlign = TextAlign.Center,
                            maxLines = 1
                        )
                    }
                }
            }
        }

        // Tooltip display
        if (tooltipText != null) {
            Spacer(Modifier.height(4.dp))
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF2D2D2D))
                    .clickable { tooltipText = null }
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text(tooltipText!!, color = Color(0xFFF5F5F5), fontSize = 12.sp)
            }
        }

        Spacer(Modifier.height(8.dp))

        // Showcase link
        Text(
            text = "Tap 🔍 for UI Showcase",
            color = Color(0xFF888888),
            fontSize = 12.sp,
            modifier = Modifier.clickable { onShowcase() }
        )
    }
}

// ── Bottom Bar ──────────────────────────────────────────────────────────────────

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

    // Start from bottom of top-left corner on the left edge
    path.moveTo(0f, barTop + cornerRadius)

    // Top-left corner → top edge → blob
    if (blobLeft >= cornerRadius) {
        // Normal: corner, flat top, then blob
        path.cubicTo(0f, barTop, 0f, barTop, cornerRadius, barTop)
        path.lineTo(blobLeft, barTop)
    } else {
        // Blob overlaps left corner — curve from left edge directly into blob area
        path.cubicTo(0f, barTop, 0f, barTop, blobLeft.coerceAtLeast(0f), barTop)
    }

    // Blob curve: organic/liquid shape that rises above the bar
    path.cubicTo(
        blobLeft + halfBlob * 0.15f, barTop,
        blobCenterX - halfBlob * 0.45f, barTop - blobHeight,
        blobCenterX, barTop - blobHeight
    )
    path.cubicTo(
        blobCenterX + halfBlob * 0.45f, barTop - blobHeight,
        blobRight - halfBlob * 0.15f, barTop,
        blobRight, barTop
    )

    // Top edge → top-right corner
    if (blobRight <= barWidth - cornerRadius) {
        // Normal: flat top, then corner
        path.lineTo(barWidth - cornerRadius, barTop)
        path.cubicTo(barWidth, barTop, barWidth, barTop, barWidth, barTop + cornerRadius)
    } else {
        // Blob overlaps right corner — curve from blob area into right edge
        path.cubicTo(barWidth, barTop, barWidth, barTop, barWidth, barTop + cornerRadius)
    }

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

    // Left edge back to start
    path.close()

    drawPath(
        path = path,
        color = BlobColor
    )
}
