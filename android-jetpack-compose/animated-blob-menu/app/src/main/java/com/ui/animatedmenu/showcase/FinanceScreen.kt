package com.ui.animatedmenu.showcase

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
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

// FinWise-inspired teal/green banking app colors
private val FinTeal = Color(0xFF00BFA5)
private val FinTealDark = Color(0xFF00897B)
private val FinBg = Color(0xFFF0F4F3)
private val FinDark = Color(0xFF1A2332)
private val FinGray = Color(0xFF9E9E9E)
private val FinCard = Color(0xFF1A2332)
private val FinWhite = Color(0xFFFFFFFF)
private val FinRed = Color(0xFFEF5350)
private val FinGreen = Color(0xFF66BB6A)

data class FinTransaction(
    val title: String,
    val subtitle: String,
    val amount: String,
    val isIncome: Boolean,
    val iconType: Int // 0=shopping, 1=food, 2=transport, 3=salary, 4=subscription
)

private val transactions = listOf(
    FinTransaction("Salary Deposit", "Monthly salary", "+$4,500.00", true, 3),
    FinTransaction("Amazon", "Online shopping", "-$89.99", false, 0),
    FinTransaction("Uber Eats", "Food & drinks", "-$24.50", false, 1),
    FinTransaction("Netflix", "Entertainment", "-$15.99", false, 4),
    FinTransaction("Gas Station", "Transport", "-$45.00", false, 2),
    FinTransaction("Freelance", "Design project", "+$1,200.00", true, 3),
    FinTransaction("Spotify", "Music premium", "-$9.99", false, 4),
    FinTransaction("Grocery", "Weekly food", "-$156.30", false, 1),
)

data class FinCategory(
    val name: String,
    val amount: String,
    val percent: Float,
    val color: Color
)

private val categories = listOf(
    FinCategory("Shopping", "$890", 0.32f, Color(0xFF7C4DFF)),
    FinCategory("Food", "$456", 0.18f, Color(0xFFFF7043)),
    FinCategory("Transport", "$234", 0.12f, Color(0xFF42A5F5)),
    FinCategory("Bills", "$320", 0.15f, Color(0xFFEF5350)),
    FinCategory("Entertainment", "$180", 0.10f, Color(0xFFAB47BC)),
    FinCategory("Others", "$220", 0.13f, Color(0xFF78909C)),
)

@Composable
fun FinanceScreen(onBack: () -> Unit) {
    var currentPage by remember { mutableIntStateOf(0) } // 0=home, 1=analytics

    val inf = rememberInfiniteTransition(label = "fin")
    val pulse by inf.animateFloat(
        0f, 1f,
        infiniteRepeatable(tween(2000, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "pulse"
    )

    Box(modifier = Modifier.fillMaxSize().background(FinBg)) {
        LazyColumn(
            modifier = Modifier.fillMaxSize().statusBarsPadding().padding(bottom = 72.dp),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {
            item {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, "Back", tint = FinDark)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("FinWise", color = FinTeal, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                        Text("Good morning!", color = FinGray, fontSize = 12.sp)
                    }
                    Box(
                        modifier = Modifier.size(40.dp).clip(CircleShape).background(FinTeal.copy(alpha = 0.1f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Filled.Notifications, null, tint = FinTeal, modifier = Modifier.size(20.dp))
                    }
                }
            }

            if (currentPage == 0) {
                // HOME PAGE
                item {
                    // Balance Card
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp)
                            .clip(RoundedCornerShape(24.dp))
                            .background(
                                Brush.horizontalGradient(
                                    listOf(FinCard, Color(0xFF263238))
                                )
                            )
                            .padding(24.dp)
                    ) {
                        Column {
                            Text("Total Balance", color = Color.White.copy(alpha = 0.7f), fontSize = 13.sp)
                            Spacer(Modifier.height(4.dp))
                            Text("$7,263.00", color = Color.White, fontSize = 32.sp, fontWeight = FontWeight.Bold)
                            Spacer(Modifier.height(16.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                // Income
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier.size(32.dp).clip(CircleShape)
                                            .background(FinGreen.copy(alpha = 0.2f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(Icons.Filled.TrendingUp, null, tint = FinGreen, modifier = Modifier.size(16.dp))
                                    }
                                    Spacer(Modifier.width(8.dp))
                                    Column {
                                        Text("Income", color = Color.White.copy(alpha = 0.6f), fontSize = 11.sp)
                                        Text("$5,700", color = FinGreen, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                                // Expense
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier.size(32.dp).clip(CircleShape)
                                            .background(FinRed.copy(alpha = 0.2f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(Icons.Filled.TrendingDown, null, tint = FinRed, modifier = Modifier.size(16.dp))
                                    }
                                    Spacer(Modifier.width(8.dp))
                                    Column {
                                        Text("Expense", color = Color.White.copy(alpha = 0.6f), fontSize = 11.sp)
                                        Text("$2,437", color = FinRed, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }

                        // Decorative circles
                        Canvas(modifier = Modifier.matchParentSize()) {
                            drawCircle(FinTeal.copy(alpha = 0.08f), radius = 80f, center = Offset(size.width - 30f, 30f))
                            drawCircle(FinTeal.copy(alpha = 0.05f), radius = 50f, center = Offset(size.width - 80f, 100f))
                        }
                    }
                }

                item { Spacer(Modifier.height(20.dp)) }

                // Quick actions
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        val actions = listOf(
                            Triple(Icons.Filled.Send, "Send", FinTeal),
                            Triple(Icons.Filled.CallReceived, "Receive", Color(0xFF7C4DFF)),
                            Triple(Icons.Filled.CreditCard, "Pay", Color(0xFFFF7043)),
                            Triple(Icons.Filled.MoreHoriz, "More", FinGray),
                        )
                        actions.forEach { (icon, label, color) ->
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Box(
                                    modifier = Modifier.size(52.dp).clip(CircleShape)
                                        .background(color.copy(alpha = 0.12f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(icon, null, tint = color, modifier = Modifier.size(24.dp))
                                }
                                Spacer(Modifier.height(6.dp))
                                Text(label, color = FinDark, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                            }
                        }
                    }
                }

                item { Spacer(Modifier.height(20.dp)) }

                // Mini chart preview
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp)
                            .clip(RoundedCornerShape(20.dp))
                            .background(FinWhite)
                            .clickable { currentPage = 1 }
                            .padding(16.dp)
                    ) {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Spending Overview", color = FinDark, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                                Text("See All →", color = FinTeal, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                            }
                            Spacer(Modifier.height(12.dp))
                            // Bar chart
                            Canvas(modifier = Modifier.fillMaxWidth().height(100.dp)) {
                                val barW = size.width / 14
                                val days = listOf(0.4f, 0.7f, 0.3f, 0.9f, 0.6f, 0.5f, 0.8f)
                                val labels = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
                                days.forEachIndexed { i, h ->
                                    val x = i * (size.width / 7) + barW / 2
                                    val barH = h * size.height * 0.8f
                                    drawRoundRect(
                                        if (i == 3) FinTeal else FinTeal.copy(alpha = 0.2f),
                                        topLeft = Offset(x, size.height - barH),
                                        size = Size(barW, barH),
                                        cornerRadius = CornerRadius(barW / 2)
                                    )
                                }
                            }
                        }
                    }
                }

                item { Spacer(Modifier.height(20.dp)) }

                // Transactions header
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Recent Transactions", color = FinDark, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        Text("View all", color = FinTeal, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                    }
                    Spacer(Modifier.height(12.dp))
                }

                // Transaction list
                items(transactions.take(5)) { tx ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 4.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(FinWhite)
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Category icon
                        val (iconColor, icon) = when (tx.iconType) {
                            0 -> Pair(Color(0xFF7C4DFF), Icons.Filled.ShoppingBag)
                            1 -> Pair(Color(0xFFFF7043), Icons.Filled.Fastfood)
                            2 -> Pair(Color(0xFF42A5F5), Icons.Filled.DirectionsCar)
                            3 -> Pair(FinGreen, Icons.Filled.AccountBalance)
                            else -> Pair(Color(0xFFAB47BC), Icons.Filled.Subscriptions)
                        }
                        Box(
                            modifier = Modifier.size(44.dp).clip(RoundedCornerShape(12.dp))
                                .background(iconColor.copy(alpha = 0.12f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(icon, null, tint = iconColor, modifier = Modifier.size(22.dp))
                        }
                        Spacer(Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(tx.title, color = FinDark, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
                            Text(tx.subtitle, color = FinGray, fontSize = 12.sp)
                        }
                        Text(
                            tx.amount,
                            color = if (tx.isIncome) FinGreen else FinRed,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            } else {
                // ANALYTICS PAGE
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Analytics", color = FinDark, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                        Text("This Month", color = FinTeal, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                    }
                    Spacer(Modifier.height(16.dp))
                }

                // Donut chart
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp)
                            .clip(RoundedCornerShape(24.dp))
                            .background(FinWhite)
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(modifier = Modifier.size(200.dp)) {
                            Canvas(modifier = Modifier.fillMaxSize()) {
                                var startAngle = -90f
                                categories.forEach { cat ->
                                    val sweep = cat.percent * 360f
                                    drawArc(
                                        cat.color,
                                        startAngle, sweep,
                                        useCenter = false,
                                        style = Stroke(width = 32f, cap = StrokeCap.Round),
                                        topLeft = Offset(16f, 16f),
                                        size = Size(size.width - 32f, size.height - 32f)
                                    )
                                    startAngle += sweep
                                }
                            }
                            Column(
                                modifier = Modifier.align(Alignment.Center),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text("$2,300", color = FinDark, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                                Text("Total Spent", color = FinGray, fontSize = 12.sp)
                            }
                        }
                    }
                    Spacer(Modifier.height(16.dp))
                }

                // Category breakdown
                item {
                    Text(
                        "Categories",
                        color = FinDark,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 20.dp)
                    )
                    Spacer(Modifier.height(12.dp))
                }

                items(categories) { cat ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 4.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(FinWhite)
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier.size(12.dp).clip(CircleShape).background(cat.color)
                        )
                        Spacer(Modifier.width(12.dp))
                        Text(cat.name, color = FinDark, fontSize = 14.sp, fontWeight = FontWeight.Medium,
                            modifier = Modifier.weight(1f))
                        Text(cat.amount, color = FinDark, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        Spacer(Modifier.width(8.dp))
                        Text("${(cat.percent * 100).toInt()}%", color = FinGray, fontSize = 12.sp)
                    }
                }

                // Weekly trend chart
                item {
                    Spacer(Modifier.height(16.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp)
                            .clip(RoundedCornerShape(20.dp))
                            .background(FinWhite)
                            .padding(16.dp)
                    ) {
                        Column {
                            Text("Weekly Trend", color = FinDark, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                            Spacer(Modifier.height(12.dp))
                            Canvas(modifier = Modifier.fillMaxWidth().height(120.dp)) {
                                val points = listOf(0.3f, 0.5f, 0.4f, 0.8f, 0.6f, 0.7f, 0.45f)
                                val stepX = size.width / (points.size - 1)

                                // Grid lines
                                for (i in 0..3) {
                                    val y = size.height * i / 3
                                    drawLine(FinGray.copy(alpha = 0.1f), Offset(0f, y), Offset(size.width, y))
                                }

                                // Area fill
                                val path = Path().apply {
                                    moveTo(0f, size.height)
                                    points.forEachIndexed { i, p ->
                                        lineTo(i * stepX, size.height * (1 - p))
                                    }
                                    lineTo(size.width, size.height)
                                    close()
                                }
                                drawPath(path, Brush.verticalGradient(
                                    listOf(FinTeal.copy(alpha = 0.3f), FinTeal.copy(alpha = 0.02f))
                                ))

                                // Line
                                for (i in 0 until points.lastIndex) {
                                    drawLine(
                                        FinTeal,
                                        Offset(i * stepX, size.height * (1 - points[i])),
                                        Offset((i + 1) * stepX, size.height * (1 - points[i + 1])),
                                        strokeWidth = 3f,
                                        cap = StrokeCap.Round
                                    )
                                }

                                // Dots
                                points.forEachIndexed { i, p ->
                                    drawCircle(FinTeal, 5f, Offset(i * stepX, size.height * (1 - p)))
                                    drawCircle(FinWhite, 3f, Offset(i * stepX, size.height * (1 - p)))
                                }
                            }
                        }
                    }
                }
            }
        }

        // Bottom nav
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                .background(FinWhite)
                .padding(horizontal = 24.dp, vertical = 12.dp)
                .navigationBarsPadding(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            val navItems = listOf(
                Triple(Icons.Filled.Home, "Home", 0),
                Triple(Icons.Filled.PieChart, "Analytics", 1),
                Triple(Icons.Filled.CreditCard, "Cards", 0),
                Triple(Icons.Filled.Person, "Profile", 0),
            )
            navItems.forEach { (icon, label, page) ->
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.clickable { currentPage = page }
                ) {
                    Icon(
                        icon, null,
                        tint = if ((page == currentPage) || (page == 0 && currentPage == 0))
                            FinTeal else FinGray,
                        modifier = Modifier.size(24.dp)
                    )
                    Text(
                        label,
                        color = if ((page == currentPage) || (page == 0 && currentPage == 0))
                            FinTeal else FinGray,
                        fontSize = 11.sp
                    )
                }
            }
        }
    }
}
