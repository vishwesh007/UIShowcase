package com.ui.animatedmenu

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
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
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Animated Floating Action Button with expanding sub-options
 * Aesthetic design: dark theme, amber accents, spring animations
 * Opens up with multiple options in a fan/arc layout
 */

private val FabMainColor = Color(0xFFFFC107)
private val FabDarkColor = Color(0xFF1A1A1A)
private val FabOptionColors = listOf(
    Color(0xFFFF5252),  // Red
    Color(0xFF448AFF),  // Blue
    Color(0xFF00E676),  // Green
    Color(0xFFAB47BC),  // Purple
)

data class FabOption(
    val icon: ImageVector,
    val label: String,
    val color: Color
)

val fabOptions = listOf(
    FabOption(Icons.Filled.CameraAlt, "Camera", FabOptionColors[0]),
    FabOption(Icons.Filled.Image, "Gallery", FabOptionColors[1]),
    FabOption(Icons.Filled.Edit, "Note", FabOptionColors[2]),
    FabOption(Icons.Filled.Share, "Share", FabOptionColors[3])
)

@Composable
fun AnimatedExpandableFab(
    modifier: Modifier = Modifier,
    onOptionSelected: (Int) -> Unit = {}
) {
    var isExpanded by remember { mutableStateOf(false) }

    // Main FAB rotation animation
    val fabRotation by animateFloatAsState(
        targetValue = if (isExpanded) 45f else 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "fabRotation"
    )

    // Main FAB color transition
    val fabColor by animateColorAsState(
        targetValue = if (isExpanded) FabDarkColor else FabMainColor,
        animationSpec = tween(300),
        label = "fabColor"
    )

    val fabIconColor by animateColorAsState(
        targetValue = if (isExpanded) FabMainColor else FabDarkColor,
        animationSpec = tween(300),
        label = "fabIconColor"
    )

    Box(
        modifier = modifier,
        contentAlignment = Alignment.BottomEnd
    ) {
        Column(
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Sub-option items (expanded)
            fabOptions.forEachIndexed { index, option ->
                val delay = if (isExpanded) index * 60 else (fabOptions.size - index) * 30

                val optionScale by animateFloatAsState(
                    targetValue = if (isExpanded) 1f else 0f,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessLow
                    ),
                    label = "optionScale_$index"
                )

                val optionAlpha by animateFloatAsState(
                    targetValue = if (isExpanded) 1f else 0f,
                    animationSpec = tween(200, delayMillis = delay),
                    label = "optionAlpha_$index"
                )

                val optionOffsetY by animateFloatAsState(
                    targetValue = if (isExpanded) 0f else 40f,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessLow
                    ),
                    label = "optionOffset_$index"
                )

                if (optionAlpha > 0.01f) {
                    FabOptionItem(
                        option = option,
                        scale = optionScale,
                        alpha = optionAlpha,
                        offsetY = optionOffsetY,
                        onClick = {
                            onOptionSelected(index)
                            isExpanded = false
                        }
                    )
                }
            }

            // Main FAB button
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .shadow(8.dp, CircleShape)
                    .clip(CircleShape)
                    .background(fabColor)
                    .clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() }
                    ) { isExpanded = !isExpanded },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = if (isExpanded) "Close" else "Add",
                    tint = fabIconColor,
                    modifier = Modifier
                        .size(28.dp)
                        .rotate(fabRotation)
                )
            }
        }
    }
}

@Composable
private fun FabOptionItem(
    option: FabOption,
    scale: Float,
    alpha: Float,
    offsetY: Float,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .alpha(alpha)
            .scale(scale)
            .offset(y = offsetY.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.End
    ) {
        // Label chip
        Box(
            modifier = Modifier
                .shadow(4.dp, RoundedCornerShape(8.dp))
                .clip(RoundedCornerShape(8.dp))
                .background(FabDarkColor)
                .padding(horizontal = 12.dp, vertical = 6.dp)
        ) {
            Text(
                text = option.label,
                color = Color.White,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        // Icon circle
        Box(
            modifier = Modifier
                .size(46.dp)
                .shadow(6.dp, CircleShape)
                .clip(CircleShape)
                .background(option.color)
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }
                ) { onClick() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = option.icon,
                contentDescription = option.label,
                tint = Color.White,
                modifier = Modifier.size(22.dp)
            )
        }
    }
}
