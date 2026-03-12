package com.ui.animatedmenu.showcase

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import kotlin.math.*

private val CryptoBg = Color(0xFF0B0E11)
private val CryptoCard = Color(0xFF1A1E25)
private val CryptoGreen = Color(0xFF00C853)
private val CryptoRed = Color(0xFFFF1744)
private val CryptoGold = Color(0xFFFFB300)
private val CryptoBlue = Color(0xFF2979FF)
private val CryptoText = Color(0xFFE8EAED)
private val CryptoGray = Color(0xFF757575)

data class CryptoCoin(
    val name: String,
    val symbol: String,
    val price: String,
    val change: String,
    val isUp: Boolean,
    val chartData: List<Float>,
    val color: Color,
    val marketCap: String
)

private val coins = listOf(
    CryptoCoin("Bitcoin", "BTC", "$67,234.50", "+2.34%", true,
        listOf(0.3f, 0.35f, 0.4f, 0.38f, 0.5f, 0.55f, 0.48f, 0.6f, 0.65f, 0.7f),
        Color(0xFFF7931A), "$1.3T"),
    CryptoCoin("Ethereum", "ETH", "$3,456.78", "+1.89%", true,
        listOf(0.4f, 0.38f, 0.45f, 0.5f, 0.48f, 0.55f, 0.6f, 0.58f, 0.63f, 0.65f),
        Color(0xFF627EEA), "$415B"),
    CryptoCoin("Solana", "SOL", "$178.92", "-0.56%", false,
        listOf(0.6f, 0.55f, 0.58f, 0.5f, 0.48f, 0.52f, 0.45f, 0.47f, 0.42f, 0.44f),
        Color(0xFF14F195), "$79B"),
    CryptoCoin("Cardano", "ADA", "$0.62", "+4.12%", true,
        listOf(0.2f, 0.25f, 0.3f, 0.28f, 0.35f, 0.4f, 0.38f, 0.45f, 0.5f, 0.55f),
        Color(0xFF0033AD), "$22B"),
    CryptoCoin("Polkadot", "DOT", "$8.45", "-1.23%", false,
        listOf(0.5f, 0.48f, 0.45f, 0.47f, 0.42f, 0.44f, 0.4f, 0.38f, 0.41f, 0.39f),
        Color(0xFFE6007A), "$11B"),
    CryptoCoin("Avalanche", "AVAX", "$42.67", "+3.78%", true,
        listOf(0.3f, 0.32f, 0.38f, 0.4f, 0.45f, 0.43f, 0.5f, 0.55f, 0.58f, 0.6f),
        Color(0xFFE84142), "$16B"),
)

@Composable
fun CryptoScreen(onBack: () -> Unit) {
    var selectedCoin by remember { mutableStateOf(coins[0]) }

    val inf = rememberInfiniteTransition(label = "crypto")
    val tick by inf.animateFloat(
        0f, 6.28f,
        infiniteRepeatable(tween(4000, easing = LinearEasing)),
        label = "tick"
    )
    val glow by inf.animateFloat(
        0.7f, 1f,
        infiniteRepeatable(tween(2000, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "glow"
    )

    Box(modifier = Modifier.fillMaxSize().background(CryptoBg)) {
        LazyColumn(
            modifier = Modifier.fillMaxSize().statusBarsPadding(),
            contentPadding = PaddingValues(bottom = 80.dp)
        ) {
            // Header
            item {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, "Back", tint = CryptoText)
                    }
                    Text("CryptoVault", color = CryptoText, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    Row {
                        Box(
                            modifier = Modifier.size(36.dp).clip(CircleShape)
                                .background(CryptoCard),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Filled.Search, null, tint = CryptoGray, modifier = Modifier.size(18.dp))
                        }
                        Spacer(Modifier.width(8.dp))
                        Box(
                            modifier = Modifier.size(36.dp).clip(CircleShape)
                                .background(CryptoCard),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Filled.Notifications, null, tint = CryptoGray, modifier = Modifier.size(18.dp))
                        }
                    }
                }
            }

            // Portfolio card
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                        .clip(RoundedCornerShape(24.dp))
                        .background(
                            Brush.horizontalGradient(
                                listOf(Color(0xFF1A237E), Color(0xFF283593), Color(0xFF1565C0))
                            )
                        )
                        .padding(24.dp)
                ) {
                    Column {
                        Text("Portfolio Value", color = Color.White.copy(alpha = 0.7f), fontSize = 13.sp)
                        Spacer(Modifier.height(4.dp))
                        Row(verticalAlignment = Alignment.Bottom) {
                            Text("$24,567", color = Color.White, fontSize = 34.sp, fontWeight = FontWeight.Bold)
                            Text(".89", color = Color.White.copy(alpha = 0.6f), fontSize = 20.sp,
                                fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 3.dp))
                        }
                        Spacer(Modifier.height(8.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier.clip(RoundedCornerShape(6.dp))
                                    .background(CryptoGreen.copy(alpha = 0.2f))
                                    .padding(horizontal = 8.dp, vertical = 3.dp)
                            ) {
                                Text("+$1,234.56 (5.28%)", color = CryptoGreen, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                            Spacer(Modifier.width(8.dp))
                            Text("24h", color = Color.White.copy(alpha = 0.5f), fontSize = 11.sp)
                        }
                    }

                    // Decorative dots
                    Canvas(modifier = Modifier.matchParentSize()) {
                        for (i in 0..8) {
                            val x = size.width - 20 - (i * 15)
                            val y = 20f + sin(tick + i * 0.5f).toFloat() * 10f
                            drawCircle(Color.White.copy(alpha = 0.05f + (i % 3) * 0.02f), 6f, Offset(x, y))
                        }
                    }
                }

                Spacer(Modifier.height(20.dp))
            }

            // Large chart for selected coin
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(CryptoCard)
                        .padding(16.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                // Coin icon
                                Box(
                                    modifier = Modifier.size(36.dp).clip(CircleShape)
                                        .background(selectedCoin.color.copy(alpha = 0.15f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Canvas(modifier = Modifier.size(20.dp)) {
                                        drawCircle(selectedCoin.color, radius = size.width / 2)
                                        drawCircle(selectedCoin.color.copy(alpha = 0.5f), radius = size.width / 3)
                                    }
                                }
                                Spacer(Modifier.width(10.dp))
                                Column {
                                    Text(selectedCoin.name, color = CryptoText, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                                    Text(selectedCoin.symbol, color = CryptoGray, fontSize = 12.sp)
                                }
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text(selectedCoin.price, color = CryptoText, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                                Text(
                                    selectedCoin.change,
                                    color = if (selectedCoin.isUp) CryptoGreen else CryptoRed,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }

                        Spacer(Modifier.height(16.dp))

                        // Chart
                        Canvas(modifier = Modifier.fillMaxWidth().height(150.dp)) {
                            val data = selectedCoin.chartData
                            val stepX = size.width / (data.size - 1)
                            val lineColor = if (selectedCoin.isUp) CryptoGreen else CryptoRed

                            // Grid
                            for (i in 0..4) {
                                val y = size.height * i / 4
                                drawLine(Color.White.copy(alpha = 0.03f), Offset(0f, y), Offset(size.width, y))
                            }

                            // Candlestick-style bars at each data point
                            data.forEachIndexed { i, v ->
                                val x = i * stepX
                                val barH = v * size.height * 0.15f
                                val topY = size.height * (1 - v)
                                // Thin wick
                                drawLine(
                                    lineColor.copy(alpha = 0.3f),
                                    Offset(x, topY - barH * 0.5f),
                                    Offset(x, topY + barH * 0.5f),
                                    strokeWidth = 1f
                                )
                                // Body
                                drawRoundRect(
                                    lineColor.copy(alpha = 0.2f),
                                    Offset(x - 4f, topY - barH * 0.2f),
                                    Size(8f, barH * 0.4f),
                                    CornerRadius(2f)
                                )
                            }

                            // Area fill
                            val areaPath = Path().apply {
                                moveTo(0f, size.height)
                                data.forEachIndexed { i, v ->
                                    lineTo(i * stepX, size.height * (1 - v))
                                }
                                lineTo(size.width, size.height)
                                close()
                            }
                            drawPath(
                                areaPath,
                                Brush.verticalGradient(
                                    listOf(lineColor.copy(alpha = 0.2f), lineColor.copy(alpha = 0.01f))
                                )
                            )

                            // Line
                            for (i in 0 until data.lastIndex) {
                                drawLine(
                                    lineColor,
                                    Offset(i * stepX, size.height * (1 - data[i])),
                                    Offset((i + 1) * stepX, size.height * (1 - data[i + 1])),
                                    strokeWidth = 2.5f,
                                    cap = StrokeCap.Round
                                )
                            }

                            // Animated dot on last point
                            val lastX = (data.lastIndex) * stepX
                            val lastY = size.height * (1 - data.last())
                            drawCircle(lineColor.copy(alpha = glow * 0.3f), 12f, Offset(lastX, lastY))
                            drawCircle(lineColor, 4f, Offset(lastX, lastY))
                        }

                        Spacer(Modifier.height(12.dp))

                        // Time filter chips
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            listOf("1H", "1D", "1W", "1M", "1Y", "ALL").forEachIndexed { i, label ->
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (i == 2) selectedCoin.color.copy(alpha = 0.15f) else Color.Transparent)
                                        .padding(horizontal = 10.dp, vertical = 5.dp)
                                ) {
                                    Text(
                                        label,
                                        color = if (i == 2) selectedCoin.color else CryptoGray,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(Modifier.height(20.dp))
            }

            // Coin list header
            item {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Market", color = CryptoText, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    Text("See all", color = CryptoBlue, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                }
                Spacer(Modifier.height(12.dp))
            }

            // Coin list
            items(coins) { coin ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 3.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(CryptoCard)
                        .clickable { selectedCoin = coin }
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Coin icon
                    Box(
                        modifier = Modifier.size(42.dp).clip(CircleShape)
                            .background(coin.color.copy(alpha = 0.12f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Canvas(modifier = Modifier.size(22.dp)) {
                            drawCircle(coin.color, size.width / 2)
                            // Letter
                            drawCircle(coin.color.copy(alpha = 0.3f), size.width / 3)
                        }
                    }
                    Spacer(Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(coin.name, color = CryptoText, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(coin.symbol, color = CryptoGray, fontSize = 12.sp)
                            Spacer(Modifier.width(6.dp))
                            Text("MCap ${coin.marketCap}", color = CryptoGray, fontSize = 10.sp)
                        }
                    }

                    // Mini sparkline
                    Canvas(modifier = Modifier.width(50.dp).height(24.dp)) {
                        val d = coin.chartData
                        val sx = size.width / (d.size - 1)
                        val lineColor = if (coin.isUp) CryptoGreen else CryptoRed
                        for (i in 0 until d.lastIndex) {
                            drawLine(
                                lineColor,
                                Offset(i * sx, size.height * (1 - d[i])),
                                Offset((i + 1) * sx, size.height * (1 - d[i + 1])),
                                strokeWidth = 1.5f,
                                cap = StrokeCap.Round
                            )
                        }
                    }

                    Spacer(Modifier.width(10.dp))
                    Column(horizontalAlignment = Alignment.End) {
                        Text(coin.price, color = CryptoText, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        Text(
                            coin.change,
                            color = if (coin.isUp) CryptoGreen else CryptoRed,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }

        // Bottom nav
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
                .background(CryptoCard)
                .padding(horizontal = 24.dp, vertical = 12.dp)
                .navigationBarsPadding(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            listOf(
                Triple(Icons.Filled.Home, "Home", true),
                Triple(Icons.Filled.ShowChart, "Market", false),
                Triple(Icons.Filled.SwapHoriz, "Trade", false),
                Triple(Icons.Filled.AccountBalanceWallet, "Wallet", false),
            ).forEach { (icon, label, active) ->
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(icon, null, tint = if (active) CryptoBlue else CryptoGray, modifier = Modifier.size(24.dp))
                    Text(label, color = if (active) CryptoBlue else CryptoGray, fontSize = 11.sp)
                }
            }
        }
    }
}
