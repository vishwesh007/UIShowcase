package com.ui.animatedmenu.showcase

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.draw.shadow
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

// ── Colors ──────────────────────────────────────────────────────────────────────

private val PurplePrimary = Color(0xFF7C4DFF)
private val PurpleDark = Color(0xFF5E35B1)
private val PurpleLight = Color(0xFFB388FF)
private val DarkNav = Color(0xFF1A1A2E)
private val DarkNavSelected = Color(0xFF2D2B55)
private val ContentBg = Color(0xFFF5F5FA)
private val CardBg = Color.White
private val TextPrimary = Color(0xFF2D2D2D)
private val TextSecondary = Color(0xFF888888)
private val AccentTeal = Color(0xFF26C6DA)

// ── Data ────────────────────────────────────────────────────────────────────────

private data class SidebarMenuItem(
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val label: String
)

private val menuItems = listOf(
    SidebarMenuItem(Icons.Filled.Analytics, "Analytics"),
    SidebarMenuItem(Icons.Filled.PlayCircle, "Videos"),
    SidebarMenuItem(Icons.Filled.EmojiEvents, "Rankings"),
    SidebarMenuItem(Icons.Filled.MusicNote, "Audios"),
    SidebarMenuItem(Icons.Filled.Comment, "Comments"),
    SidebarMenuItem(Icons.Filled.Tune, "Customization"),
)

// ── Main Screen ─────────────────────────────────────────────────────────────────

@Composable
fun SidebarDashboardScreen(onBack: () -> Unit = {}) {
    var isSidebarExpanded by remember { mutableStateOf(false) }
    var selectedMenuItem by remember { mutableIntStateOf(0) }

    val sidebarWidth by animateDpAsState(
        targetValue = if (isSidebarExpanded) 200.dp else 0.dp,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioLowBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "sidebarWidth"
    )

    Row(modifier = Modifier.fillMaxSize()) {
        // Icon-only narrow bar (always visible)
        IconNavBar(
            selectedIndex = selectedMenuItem,
            onItemSelected = { selectedMenuItem = it },
            onToggleSidebar = { isSidebarExpanded = !isSidebarExpanded },
            isExpanded = isSidebarExpanded
        )

        // Expandable sidebar panel
        if (sidebarWidth > 1.dp) {
            ExpandedSidebarPanel(
                selectedIndex = selectedMenuItem,
                onItemSelected = {
                    selectedMenuItem = it
                    isSidebarExpanded = false
                },
                width = sidebarWidth
            )
        }

        // Main content
        DashboardContent(
            onBack = onBack,
            modifier = Modifier.weight(1f)
        )
    }
}

// ── Icon Nav Bar (narrow, always visible) ───────────────────────────────────────

@Composable
private fun IconNavBar(
    selectedIndex: Int,
    onItemSelected: (Int) -> Unit,
    onToggleSidebar: () -> Unit,
    isExpanded: Boolean
) {
    Column(
        modifier = Modifier
            .fillMaxHeight()
            .width(56.dp)
            .background(DarkNav)
            .padding(vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Hamburger / close toggle
        IconButton(onClick = onToggleSidebar) {
            Icon(
                if (isExpanded) Icons.Filled.Close else Icons.Filled.Menu,
                contentDescription = "Toggle sidebar",
                tint = Color.White,
                modifier = Modifier.size(22.dp)
            )
        }

        Spacer(Modifier.height(24.dp))

        // Menu icons
        menuItems.forEachIndexed { index, item ->
            val isSelected = index == selectedIndex
            val bgColor by animateColorAsState(
                if (isSelected) PurplePrimary else Color.Transparent,
                tween(200), label = "iconBg_$index"
            )
            Box(
                modifier = Modifier
                    .padding(vertical = 4.dp)
                    .size(40.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(bgColor)
                    .clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() }
                    ) { onItemSelected(index) },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    item.icon, null,
                    tint = if (isSelected) Color.White else Color.White.copy(alpha = 0.5f),
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        Spacer(Modifier.weight(1f))

        // Bottom icons
        IconButton(onClick = {}) {
            Icon(Icons.Filled.Settings, null, tint = Color.White.copy(alpha = 0.5f), modifier = Modifier.size(20.dp))
        }
        IconButton(onClick = {}) {
            Icon(Icons.Filled.Logout, null, tint = Color.White.copy(alpha = 0.5f), modifier = Modifier.size(20.dp))
        }
    }
}

// ── Expanded Sidebar Panel ──────────────────────────────────────────────────────

@Composable
private fun ExpandedSidebarPanel(
    selectedIndex: Int,
    onItemSelected: (Int) -> Unit,
    width: androidx.compose.ui.unit.Dp
) {
    Column(
        modifier = Modifier
            .fillMaxHeight()
            .width(width)
            .background(
                Brush.verticalGradient(
                    listOf(PurplePrimary, PurpleDark)
                )
            )
            .padding(vertical = 24.dp, horizontal = 16.dp)
    ) {
        // User avatar
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .shadow(8.dp, CircleShape)
                    .clip(CircleShape)
                    .background(PurpleLight),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Filled.Person,
                    null,
                    tint = Color.White,
                    modifier = Modifier.size(36.dp)
                )
            }
        }

        Spacer(Modifier.height(8.dp))

        // User name
        Text(
            "Anna Kutsuki",
            color = Color.White,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.fillMaxWidth(),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
        Text(
            "developer",
            color = Color.White.copy(alpha = 0.7f),
            fontSize = 11.sp,
            modifier = Modifier.fillMaxWidth(),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )

        Spacer(Modifier.height(24.dp))

        // Menu items with labels
        menuItems.forEachIndexed { index, item ->
            val isSelected = index == selectedIndex
            val bgColor by animateColorAsState(
                if (isSelected) Color.White.copy(alpha = 0.2f) else Color.Transparent,
                tween(200), label = "menuBg_$index"
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(bgColor)
                    .clickable { onItemSelected(index) }
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    item.icon, null,
                    tint = Color.White,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(Modifier.width(12.dp))
                Text(
                    item.label,
                    color = Color.White,
                    fontSize = 13.sp,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                )
            }
        }

        Spacer(Modifier.weight(1f))

        // Bottom settings
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Filled.Settings, null, tint = Color.White.copy(alpha = 0.7f), modifier = Modifier.size(16.dp))
            Spacer(Modifier.width(12.dp))
            Text("Settings", color = Color.White.copy(alpha = 0.7f), fontSize = 12.sp)
        }
    }
}

// ── Dashboard Content ───────────────────────────────────────────────────────────

@Composable
private fun DashboardContent(onBack: () -> Unit, modifier: Modifier = Modifier) {
    val scrollState = rememberScrollState()

    // Animated chart values
    val inf = rememberInfiniteTransition(label = "charts")
    val chartPhase by inf.animateFloat(
        0f, 6.28f,
        infiniteRepeatable(tween(6000, easing = LinearEasing)),
        label = "chartPhase"
    )

    Column(
        modifier = modifier
            .fillMaxHeight()
            .background(ContentBg)
            .verticalScroll(scrollState)
            .padding(20.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack) {
                    Icon(Icons.Filled.ArrowBack, "Back", tint = TextPrimary)
                }
                Spacer(Modifier.width(4.dp))
                Text(
                    "Dashboard",
                    color = TextPrimary,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(PurplePrimary.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Filled.Person, null,
                    tint = PurplePrimary,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        Spacer(Modifier.height(20.dp))

        // Stats row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            StatCard("Views 24h", "2,000", Icons.Filled.Visibility, Modifier.weight(1f))
            StatCard("Subscribers", "458", Icons.Filled.People, Modifier.weight(1f))
        }

        Spacer(Modifier.height(16.dp))

        // Line Chart Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = CardBg),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Activity", color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                    Text("This week", color = TextSecondary, fontSize = 11.sp)
                }
                Spacer(Modifier.height(12.dp))
                LineChart(phase = chartPhase)
            }
        }

        Spacer(Modifier.height(16.dp))

        // Donut Chart + Progress Arc row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Donut chart
            Card(
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = CardBg),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Sales", color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                    Spacer(Modifier.height(8.dp))
                    DonutChart()
                    Spacer(Modifier.height(8.dp))
                    LegendRow("Series 1", PurplePrimary)
                    LegendRow("Series 2", PurpleLight)
                    LegendRow("Series 3", AccentTeal)
                }
            }

            // Progress Arc
            Card(
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = CardBg),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Surface area", color = TextSecondary, fontSize = 11.sp)
                    Spacer(Modifier.height(12.dp))
                    ProgressArc(percentage = 40)
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        // Recent Activity list
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = CardBg),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Recent Activity", color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.height(12.dp))
                ActivityRow("New video uploaded", "2 hours ago", Icons.Filled.PlayCircle)
                ActivityRow("Comment received", "5 hours ago", Icons.Filled.Comment)
                ActivityRow("Ranking updated", "1 day ago", Icons.Filled.EmojiEvents)
            }
        }

        Spacer(Modifier.height(80.dp))
    }
}

// ── Stat Card ───────────────────────────────────────────────────────────────────

@Composable
private fun StatCard(
    title: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardBg),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(PurplePrimary.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(icon, null, tint = PurplePrimary, modifier = Modifier.size(16.dp))
                }
                Spacer(Modifier.width(8.dp))
                Text(title, color = TextSecondary, fontSize = 11.sp)
            }
            Spacer(Modifier.height(8.dp))
            Text(
                value,
                color = TextPrimary,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

// ── Line Chart ──────────────────────────────────────────────────────────────────

@Composable
private fun LineChart(phase: Float) {
    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
    ) {
        val w = size.width
        val h = size.height
        val points = 7
        val stepX = w / (points - 1)

        // Data points (normalized 0-1)
        val data = listOf(0.3f, 0.5f, 0.4f, 0.7f, 0.6f, 0.85f, 0.75f)

        // Grid lines
        for (i in 0..3) {
            val y = h * i / 3f
            drawLine(
                Color(0xFFEEEEEE),
                Offset(0f, y), Offset(w, y),
                strokeWidth = 1f
            )
        }

        // Build path
        val path = Path()
        val fillPath = Path()
        data.forEachIndexed { i, value ->
            val x = i * stepX
            val y = h - value * h * 0.85f
            if (i == 0) {
                path.moveTo(x, y)
                fillPath.moveTo(x, y)
            } else {
                val prevX = (i - 1) * stepX
                val prevY = h - data[i - 1] * h * 0.85f
                val cx1 = prevX + stepX * 0.4f
                val cx2 = x - stepX * 0.4f
                path.cubicTo(cx1, prevY, cx2, y, x, y)
                fillPath.cubicTo(cx1, prevY, cx2, y, x, y)
            }
        }

        // Fill under curve
        fillPath.lineTo(w, h)
        fillPath.lineTo(0f, h)
        fillPath.close()
        drawPath(
            fillPath,
            Brush.verticalGradient(
                listOf(AccentTeal.copy(alpha = 0.15f), Color.Transparent)
            )
        )

        // Line
        drawPath(
            path,
            AccentTeal,
            style = Stroke(width = 2.5f, cap = StrokeCap.Round, join = StrokeJoin.Round)
        )

        // Dots
        data.forEachIndexed { i, value ->
            val x = i * stepX
            val y = h - value * h * 0.85f
            drawCircle(AccentTeal, 4f, Offset(x, y))
            drawCircle(Color.White, 2f, Offset(x, y))
        }
    }
}

// ── Donut Chart ─────────────────────────────────────────────────────────────────

@Composable
private fun DonutChart() {
    Canvas(
        modifier = Modifier.size(100.dp)
    ) {
        val strokeWidth = 24f
        val radius = (size.minDimension - strokeWidth) / 2f

        // Background ring
        drawCircle(
            Color(0xFFF0F0F0),
            radius = radius,
            style = Stroke(width = strokeWidth)
        )

        // Segments: 60%, 25%, 15%
        val segments = listOf(
            216f to PurplePrimary,  // 60%
            90f to PurpleLight,     // 25%
            54f to AccentTeal       // 15%
        )
        var startAngle = -90f
        segments.forEach { (sweep, color) ->
            drawArc(
                color = color,
                startAngle = startAngle,
                sweepAngle = sweep,
                useCenter = false,
                topLeft = Offset(strokeWidth / 2f, strokeWidth / 2f),
                size = Size(size.width - strokeWidth, size.height - strokeWidth),
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )
            startAngle += sweep
        }
    }
}

// ── Progress Arc ────────────────────────────────────────────────────────────────

@Composable
private fun ProgressArc(percentage: Int) {
    val animatedProgress by animateFloatAsState(
        targetValue = percentage / 100f,
        animationSpec = tween(1500, easing = FastOutSlowInEasing),
        label = "progress"
    )

    Box(contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.size(100.dp)) {
            val strokeWidth = 12f
            val arcSize = Size(size.width - strokeWidth, size.height - strokeWidth)
            val topLeft = Offset(strokeWidth / 2f, strokeWidth / 2f)

            // Background arc
            drawArc(
                Color(0xFFF0F0F0),
                startAngle = 135f,
                sweepAngle = 270f,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )

            // Progress arc
            drawArc(
                PurplePrimary,
                startAngle = 135f,
                sweepAngle = 270f * animatedProgress,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )
        }

        Text(
            "$percentage%",
            color = TextPrimary,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

// ── Legend Row ───────────────────────────────────────────────────────────────────

@Composable
private fun LegendRow(label: String, color: Color) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(vertical = 2.dp)
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(color)
        )
        Spacer(Modifier.width(6.dp))
        Text(label, color = TextSecondary, fontSize = 10.sp)
    }
}

// ── Activity Row ────────────────────────────────────────────────────────────────

@Composable
private fun ActivityRow(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(PurplePrimary.copy(alpha = 0.08f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, null, tint = PurplePrimary, modifier = Modifier.size(18.dp))
        }
        Spacer(Modifier.width(12.dp))
        Column {
            Text(title, color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Medium)
            Text(subtitle, color = TextSecondary, fontSize = 11.sp)
        }
    }
}
