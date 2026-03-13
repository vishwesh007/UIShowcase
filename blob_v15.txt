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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.abs
import kotlin.math.cos
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
    var isFireExpanded by remember { mutableStateOf(false) }

    val screenTitles = listOf("Home", "Fire", "Settings")

    // Warm glow overlay when Fire is active
    val fireGlowAlpha by animateFloatAsState(
        targetValue = if (selectedIndex == 1) 0.06f else 0f,
        animationSpec = tween(600),
        label = "fireGlow"
    )

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
                onItemSelected = { index ->
                    if (index == 1) {
                        // Fire button: toggle expansion
                        selectedIndex = 1
                        isFireExpanded = !isFireExpanded
                    } else {
                        isFireExpanded = false
                        selectedIndex = index
                    }
                },
                isFireExpanded = isFireExpanded,
                modifier = Modifier
                    .padding(horizontal = 24.dp, vertical = 24.dp)
            )
        }

        // Warm glow overlay when Fire is selected
        if (fireGlowAlpha > 0.001f) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                drawRect(
                    Brush.verticalGradient(
                        listOf(Color.Transparent, Color(0xFFFF6D00).copy(alpha = fireGlowAlpha))
                    )
                )
            }
        }

        // Scrim overlay when Fire is expanded
        val scrimAlpha by animateFloatAsState(
            targetValue = if (isFireExpanded) 0.3f else 0f,
            animationSpec = tween(300),
            label = "scrim"
        )
        if (scrimAlpha > 0.01f) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = scrimAlpha))
                    .clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() }
                    ) { isFireExpanded = false }
            )
        }

        // Fire Expandable Options (replaces FAB)
        FireExpandableOptions(
            isExpanded = isFireExpanded,
            onDismiss = { isFireExpanded = false },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 120.dp)
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

// ── Fire Expandable Options (replaces FAB) ──────────────────────────────────────

private data class FireOption(
    val icon: ImageVector,
    val label: String,
    val color: Color
)

private val fireOptions = listOf(
    FireOption(Icons.Filled.CameraAlt, "Camera", Color(0xFFFF5252)),
    FireOption(Icons.Filled.Image, "Gallery", Color(0xFF448AFF)),
    FireOption(Icons.Filled.Edit, "Note", Color(0xFF00E676)),
    FireOption(Icons.Filled.Share, "Share", Color(0xFFAB47BC)),
)

@Composable
private fun FireExpandableOptions(
    isExpanded: Boolean,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val inf = rememberInfiniteTransition(label = "fireParticles")
    val phase by inf.animateFloat(
        0f, 6.28f,
        infiniteRepeatable(tween(3000, easing = LinearEasing)),
        label = "firePhase"
    )

    Box(modifier = modifier, contentAlignment = Alignment.BottomCenter) {
        // Flame particle canvas behind the options
        if (isExpanded) {
            Canvas(
                modifier = Modifier
                    .size(300.dp)
                    .align(Alignment.Center)
            ) {
                // Rising ember particles in a ring
                for (i in 0..19) {
                    val angle = (i * 18f + phase * 40f) * (Math.PI / 180.0).toFloat()
                    val radius = 90f + sin(phase * 2f + i * 0.7f).toFloat() * 50f
                    val px = center.x + cos(angle) * radius
                    val py = center.y - sin(phase * 1.5f + i * 0.4f).toFloat() * 70f - 30f
                    val dotSize = (4f + sin(phase + i.toFloat()).toFloat() * 3f).coerceAtLeast(1.5f)
                    val alpha = (0.7f + sin(phase * 2f + i.toFloat()).toFloat() * 0.25f).coerceIn(0f, 1f)
                    drawCircle(
                        Color(0xFFFF6D00).copy(alpha = alpha),
                        dotSize,
                        Offset(px, py)
                    )
                }
                // Warm glow ring
                drawCircle(
                    Color(0xFFFFC107).copy(alpha = 0.12f),
                    radius = 110f + sin(phase).toFloat() * 20f,
                    center = center
                )
            }
        }

        // Options in a semi-circle arc above the fire position
        fireOptions.forEachIndexed { index, option ->
            val delay = if (isExpanded) index * 50 else (fireOptions.size - index) * 30

            val optionScale by animateFloatAsState(
                targetValue = if (isExpanded) 1f else 0f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow
                ),
                label = "fireOpt_$index"
            )

            val optionAlpha by animateFloatAsState(
                targetValue = if (isExpanded) 1f else 0f,
                animationSpec = tween(250, delayMillis = delay),
                label = "fireOptA_$index"
            )

            if (optionAlpha > 0.01f) {
                // Position in upward arc: 150° → 30° (left-to-right, curving up)
                val arcAngle = (155f - index * 43.3f) * (Math.PI / 180.0).toFloat()
                val arcRadius = 100f
                val offsetX = cos(arcAngle) * arcRadius * optionScale
                val offsetY = -sin(arcAngle) * arcRadius * optionScale

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .offset(x = offsetX.dp, y = (offsetY - 10f).dp)
                        .graphicsLayer {
                            alpha = optionAlpha.coerceIn(0f, 1f)
                            scaleX = optionScale.coerceIn(0f, 1.5f)
                            scaleY = optionScale.coerceIn(0f, 1.5f)
                        }
                ) {
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .shadow(10.dp, CircleShape)
                            .clip(CircleShape)
                            .background(option.color)
                            .clickable(
                                indication = null,
                                interactionSource = remember { MutableInteractionSource() }
                            ) { onDismiss() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            option.icon, null,
                            tint = Color.White,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Spacer(Modifier.height(4.dp))
                    Text(
                        option.label,
                        color = Color(0xFF2D2D2D),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
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
    var activeButton by remember { mutableIntStateOf(-1) } // -1=none, 0-5=active tool
    var isPatchExploding by remember { mutableStateOf(false) }
    var patchProgress by remember { mutableFloatStateOf(0f) }
    var patchStatusText by remember { mutableStateOf("") }

    // Screen fill animation for non-patch buttons
    val fillRadius by animateFloatAsState(
        targetValue = if (activeButton in listOf(0, 1, 3, 4)) 2000f else 0f,
        animationSpec = tween(1200, easing = FastOutSlowInEasing),
        label = "fillRadius",
        finishedListener = {
            if (activeButton in listOf(0, 1, 3, 4)) {
                // After fill completes, reset after a delay (handled by LaunchedEffect)
            }
        }
    )

    // Reset after fill animation or patch
    LaunchedEffect(activeButton) {
        if (activeButton in listOf(0, 1, 3, 4)) {
            kotlinx.coroutines.delay(2500)
            activeButton = -1
        }
    }

    // Patch explosion sequence
    LaunchedEffect(isPatchExploding) {
        if (isPatchExploding) {
            patchProgress = 0f
            patchStatusText = "Initializing patch engine..."
            kotlinx.coroutines.delay(800)
            patchStatusText = "Decompiling resources..."
            val steps = listOf(
                0.15f to "Analyzing smali classes...",
                0.30f to "Applying method patches...",
                0.45f to "Injecting hooks...",
                0.60f to "Rebuilding resources...",
                0.75f to "Optimizing DEX files...",
                0.90f to "Signing APK...",
                1.0f to "Patch complete!"
            )
            for ((target, msg) in steps) {
                patchStatusText = msg
                val current = patchProgress
                val animSteps = 20
                for (i in 1..animSteps) {
                    patchProgress = current + (target - current) * i / animSteps
                    kotlinx.coroutines.delay(25)
                }
                kotlinx.coroutines.delay(200)
            }
            kotlinx.coroutines.delay(1000)
            isPatchExploding = false
            activeButton = -1
            patchProgress = 0f
        }
    }

    // Explosion scale
    val explodeScale by animateFloatAsState(
        targetValue = if (isPatchExploding) 1f else 0f,
        animationSpec = if (isPatchExploding) spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ) else tween(300),
        label = "explode"
    )

    // Icons visibility
    val iconsAlpha by animateFloatAsState(
        targetValue = if (isPatchExploding || activeButton in listOf(0, 1, 3, 4)) 0f else 1f,
        animationSpec = tween(400),
        label = "iconsAlpha"
    )

    val inf = rememberInfiniteTransition(label = "toolGrid")
    val phase by inf.animateFloat(
        0f, 6.28f,
        infiniteRepeatable(tween(4000, easing = LinearEasing)),
        label = "toolPhase"
    )

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        // Screen fill overlay for non-patch buttons
        if (activeButton in listOf(0, 1, 3, 4)) {
            val fillColor = toolItems[activeButton].color
            Canvas(modifier = Modifier.fillMaxSize()) {
                drawCircle(
                    fillColor.copy(alpha = 0.85f),
                    radius = fillRadius,
                    center = center
                )
            }
            // Status text on fill
            if (fillRadius > 500f) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.align(Alignment.Center)
                ) {
                    Icon(
                        toolItems[activeButton].icon, null,
                        tint = Color.White,
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(Modifier.height(12.dp))
                    Text(
                        toolItems[activeButton].label,
                        color = Color.White,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        toolItems[activeButton].description,
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 14.sp
                    )
                }
            }
        }

        // Patch explosion view
        if (isPatchExploding) {
            Box(
                modifier = Modifier.fillMaxSize()
                    .background(Color(0xFF0D1117).copy(alpha = explodeScale.coerceIn(0f, 1f))),
                contentAlignment = Alignment.Center
            ) {
                // Explosion particles
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val patchColor = toolItems[2].color
                    for (i in 0..20) {
                        val angle = (i * 18f) * (Math.PI / 180f).toFloat()
                        val dist = explodeScale * 300f * (1f + sin(phase + i * 0.5f).toFloat() * 0.3f)
                        val px = center.x + kotlin.math.cos(angle) * dist
                        val py = center.y + kotlin.math.sin(angle) * dist
                        val dotSize = (4f + (i % 4) * 2f) * explodeScale
                        drawCircle(
                            patchColor.copy(alpha = (0.6f * explodeScale.coerceIn(0f, 1f))),
                            dotSize,
                            Offset(px, py)
                        )
                    }
                }

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(horizontal = 40.dp)
                ) {
                    // Patch icon
                    Icon(
                        Icons.Filled.Build, null,
                        tint = toolItems[2].color,
                        modifier = Modifier
                            .size(56.dp)
                            .scale(0.8f + explodeScale * 0.2f)
                    )
                    Spacer(Modifier.height(20.dp))

                    // Status text
                    Text(
                        patchStatusText,
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        textAlign = TextAlign.Center
                    )
                    Spacer(Modifier.height(24.dp))

                    // Progress bar
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(Color.White.copy(alpha = 0.1f))
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(patchProgress.coerceIn(0.01f, 1f))
                                .fillMaxHeight()
                                .clip(RoundedCornerShape(4.dp))
                                .background(
                                    Brush.horizontalGradient(
                                        listOf(toolItems[2].color, Color(0xFF64B5F6))
                                    )
                                )
                        )
                    }
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "${(patchProgress * 100).toInt()}%",
                        color = toolItems[2].color,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // Normal grid (fades out during animations)
        if (iconsAlpha > 0f) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 28.dp)
                    .graphicsLayer { alpha = iconsAlpha },
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
                                            onClick = {
                                                if (idx == 2) {
                                                    // Patch: explosion
                                                    activeButton = 2
                                                    isPatchExploding = true
                                                } else if (idx in listOf(0, 1, 3, 4)) {
                                                    // Fill animation
                                                    activeButton = idx
                                                }
                                            },
                                            onLongClick = {
                                                tooltipText = "${tool.label}: ${tool.description}"
                                            }
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Canvas(modifier = Modifier.fillMaxSize()) {
                                        drawCircle(tool.color.copy(alpha = 0.1f), radius = size.width / 2.5f)
                                    }
                                    Icon(tool.icon, null, tint = tool.color, modifier = Modifier.size(28.dp))
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
                Text(
                    text = "Tap \uD83D\uDD0D for UI Showcase",
                    color = Color(0xFF888888),
                    fontSize = 12.sp,
                    modifier = Modifier.clickable { onShowcase() }
                )
            }
        }
    }
}

// ── Bottom Bar ──────────────────────────────────────────────────────────────────

@Composable
fun BlobBottomBar(
    items: List<NavItem>,
    selectedIndex: Int,
    onItemSelected: (Int) -> Unit,
    isFireExpanded: Boolean = false,
    modifier: Modifier = Modifier,
    barHeight: Dp = 64.dp,
    blobHeight: Dp = 28.dp
) {
    // Animate the blob position horizontally
    val animatedSelectedIndex by animateFloatAsState(
        targetValue = selectedIndex.toFloat(),
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "blobPosition"
    )

    // Infinite transition for fire glow
    val fireInf = rememberInfiniteTransition(label = "fireGlow")
    val firePulse by fireInf.animateFloat(
        0f, 1f,
        infiniteRepeatable(tween(1200, easing = LinearEasing), RepeatMode.Reverse),
        label = "firePulse"
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

            // Fire glow on the bar when expanded
            if (isFireExpanded) {
                val fireCenterX = itemWidth * 1f + itemWidth / 2f // Fire is index 1
                val glowRadius = 60f + firePulse * 20f
                drawCircle(
                    Color(0xFFFF6D00).copy(alpha = (0.15f + firePulse * 0.1f).coerceIn(0f, 1f)),
                    radius = glowRadius,
                    center = Offset(fireCenterX, barTop + barHeightPx / 2f)
                )
            }
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
                    targetValue = if (isSelected && index == 1 && isFireExpanded)
                        Color(0xFFFF6D00) // Hot orange when fire expanded
                    else if (isSelected) SelectedIconColor
                    else UnselectedIconColor,
                    animationSpec = tween(300),
                    label = "iconColor_$index"
                )

                val iconSize by animateDpAsState(
                    targetValue = if (isSelected && index == 1 && isFireExpanded) 34.dp
                    else if (isSelected) 30.dp else 24.dp,
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
