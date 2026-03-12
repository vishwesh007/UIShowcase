package com.ui.animatedmenu.showcase

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.sin

private data class ChatMsg(val text: String, val isMine: Boolean, val time: String, val isVoice: Boolean = false)

private val chatMessages = listOf(
    ChatMsg("Hey! How's the project going?", false, "10:30 AM"),
    ChatMsg("Pretty good! Just finished the UI designs", true, "10:31 AM"),
    ChatMsg("That's awesome! Can you share some screenshots?", false, "10:32 AM"),
    ChatMsg("Sure! Let me grab them from Figma", true, "10:33 AM"),
    ChatMsg("", true, "10:34 AM", isVoice = true),
    ChatMsg("Wow, looks amazing! Love the color palette", false, "10:35 AM"),
    ChatMsg("Thanks! I used inspiration from Dribbble", true, "10:36 AM"),
)

@Composable
fun ChatScreen(onBack: () -> Unit = {}) {
    val Bg = Color(0xFFF2F3F7)
    val AccentPurple = Color(0xFF6C63FF)
    val AccentGradient1 = Color(0xFF7C4DFF)
    val AccentGradient2 = Color(0xFF536DFE)
    val BubbleTheirs = Color.White
    val ContactGreen = Color(0xFF00C853)
    val TextDark = Color(0xFF1A1A1A)
    val TextMuted = Color(0xFF9E9E9E)

    val inf = rememberInfiniteTransition(label = "chat")
    val dotPhase by inf.animateFloat(0f, 6.28f,
        infiniteRepeatable(tween(1500, easing = LinearEasing)), label = "dp")
    val voiceWave by inf.animateFloat(0f, 6.28f,
        infiniteRepeatable(tween(2000, easing = LinearEasing)), label = "vw")
    val onlinePulse by inf.animateFloat(0.5f, 1f,
        infiniteRepeatable(tween(2000, easing = FastOutSlowInEasing), RepeatMode.Reverse), label = "op")

    Column(modifier = Modifier.fillMaxSize().background(Bg).statusBarsPadding()) {
        // Top bar with gradient
        Box(modifier = Modifier.fillMaxWidth()
            .background(Brush.horizontalGradient(listOf(AccentGradient1, AccentGradient2)))
            .padding(horizontal = 8.dp, vertical = 8.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack) { Icon(Icons.Filled.ArrowBack, "Back", tint = Color.White) }
                Spacer(Modifier.width(4.dp))
                Box(contentAlignment = Alignment.Center) {
                    Box(modifier = Modifier.size(44.dp).clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center) {
                        Text("A", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    }
                    // Pulsing online indicator
                    Box(modifier = Modifier.align(Alignment.BottomEnd).offset(x = 1.dp, y = 1.dp)
                        .size(12.dp).clip(CircleShape)
                        .background(AccentGradient1).padding(2.dp)) {
                        Box(modifier = Modifier.fillMaxSize().clip(CircleShape)
                            .graphicsLayer { alpha = onlinePulse }
                            .background(ContactGreen))
                    }
                }
                Spacer(Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text("Alex Chen", fontWeight = FontWeight.SemiBold, fontSize = 16.sp, color = Color.White)
                    Text("Online", color = Color.White.copy(alpha = 0.7f), fontSize = 12.sp)
                }
                IconButton(onClick = {}) { Icon(Icons.Filled.Call, null, tint = Color.White.copy(alpha = 0.8f)) }
                IconButton(onClick = {}) { Icon(Icons.Filled.Videocam, null, tint = Color.White.copy(alpha = 0.8f)) }
            }
        }

        // Messages with staggered entrance
        LazyColumn(modifier = Modifier.weight(1f).padding(horizontal = 16.dp),
            contentPadding = PaddingValues(vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)) {

            // Date separator
            item {
                val dateEntry = remember { Animatable(0f) }
                LaunchedEffect(Unit) { dateEntry.animateTo(1f, tween(400)) }
                Box(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
                    .graphicsLayer { alpha = dateEntry.value; scaleX = 0.9f + dateEntry.value * 0.1f; scaleY = scaleX },
                    contentAlignment = Alignment.Center) {
                    Text("Today", color = TextMuted, fontSize = 11.sp,
                        modifier = Modifier.clip(RoundedCornerShape(12.dp))
                            .background(Color.White).padding(horizontal = 16.dp, vertical = 4.dp))
                }
            }

            itemsIndexed(chatMessages) { index, msg ->
                val msgEntry = remember { Animatable(0f) }
                LaunchedEffect(Unit) {
                    kotlinx.coroutines.delay(index * 150L + 200L)
                    msgEntry.animateTo(1f, spring(dampingRatio = 0.7f, stiffness = 300f))
                }
                val slideDir = if (msg.isMine) 1f else -1f

                Column(modifier = Modifier.fillMaxWidth()
                    .graphicsLayer {
                        alpha = msgEntry.value
                        translationX = (1f - msgEntry.value) * slideDir * 80f
                    },
                    horizontalAlignment = if (msg.isMine) Alignment.End else Alignment.Start) {

                    if (!msg.isMine) {
                        Row(verticalAlignment = Alignment.Bottom) {
                            Box(modifier = Modifier.size(28.dp).clip(CircleShape)
                                .background(ContactGreen.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center) {
                                Text("A", color = ContactGreen, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                            Spacer(Modifier.width(6.dp))
                            Box(modifier = Modifier.widthIn(max = 260.dp)
                                .clip(RoundedCornerShape(18.dp, 18.dp, 18.dp, 4.dp))
                                .background(BubbleTheirs).padding(12.dp)) {
                                Text(msg.text, color = TextDark, fontSize = 14.sp, lineHeight = 20.sp)
                            }
                        }
                    } else if (msg.isVoice) {
                        // Voice message with animated waveform
                        Box(modifier = Modifier.widthIn(max = 260.dp)
                            .clip(RoundedCornerShape(18.dp, 18.dp, 4.dp, 18.dp))
                            .background(Brush.linearGradient(listOf(AccentGradient1, AccentGradient2)))
                            .padding(12.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Filled.PlayArrow, null, tint = Color.White, modifier = Modifier.size(20.dp))
                                Spacer(Modifier.width(8.dp))
                                Canvas(modifier = Modifier.weight(1f).height(24.dp)) {
                                    val barCount = 28
                                    val barW = size.width / barCount * 0.55f
                                    val sp = size.width / barCount
                                    for (i in 0 until barCount) {
                                        val amp = (sin(voiceWave + i * 0.6f) * 0.4f + 0.5f).toFloat()
                                        val h = amp * size.height * 0.85f
                                        drawRoundRect(Color.White.copy(alpha = 0.7f + amp * 0.3f),
                                            topLeft = Offset(i * sp, (size.height - h) / 2),
                                            size = Size(barW, h),
                                            cornerRadius = CornerRadius(2f))
                                    }
                                }
                                Spacer(Modifier.width(8.dp))
                                Text("0:12", color = Color.White.copy(alpha = 0.7f), fontSize = 11.sp)
                            }
                        }
                    } else {
                        Box(modifier = Modifier.widthIn(max = 260.dp)
                            .clip(RoundedCornerShape(18.dp, 18.dp, 4.dp, 18.dp))
                            .background(Brush.linearGradient(listOf(AccentGradient1, AccentGradient2)))
                            .padding(12.dp)) {
                            Text(msg.text, color = Color.White, fontSize = 14.sp, lineHeight = 20.sp)
                        }
                    }

                    Row(modifier = Modifier.padding(top = 2.dp, start = if (!msg.isMine) 36.dp else 0.dp,
                        end = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                        Text(msg.time, color = TextMuted, fontSize = 10.sp)
                        if (msg.isMine) {
                            Spacer(Modifier.width(3.dp))
                            Icon(Icons.Filled.DoneAll, null, tint = AccentPurple, modifier = Modifier.size(12.dp))
                        }
                    }
                }
            }

            // Typing indicator with bouncing dots
            item {
                val typingEntry = remember { Animatable(0f) }
                LaunchedEffect(Unit) {
                    kotlinx.coroutines.delay(chatMessages.size * 150L + 600L)
                    typingEntry.animateTo(1f, spring(dampingRatio = 0.6f, stiffness = 200f))
                }
                Row(modifier = Modifier.padding(top = 4.dp)
                    .graphicsLayer { alpha = typingEntry.value; translationX = (1f - typingEntry.value) * -60f },
                    verticalAlignment = Alignment.Bottom) {
                    Box(modifier = Modifier.size(28.dp).clip(CircleShape)
                        .background(ContactGreen.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center) {
                        Text("A", color = ContactGreen, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                    Spacer(Modifier.width(6.dp))
                    Box(modifier = Modifier.clip(RoundedCornerShape(18.dp, 18.dp, 18.dp, 4.dp))
                        .background(Color.White).padding(horizontal = 16.dp, vertical = 10.dp)) {
                        Row(horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                            for (i in 0..2) {
                                // Bouncing dots with sin wave offset
                                val bounce = sin(dotPhase + i * 1.2f).toFloat().coerceAtLeast(0f)
                                Box(modifier = Modifier.size(7.dp)
                                    .graphicsLayer { translationY = -bounce * 8f }
                                    .clip(CircleShape)
                                    .background(AccentPurple.copy(alpha = 0.4f + bounce * 0.6f)))
                            }
                        }
                    }
                }
            }
        }

        // Input bar with subtle shadow
        Box(modifier = Modifier.fillMaxWidth().background(Color.White).padding(8.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = {}) { Icon(Icons.Filled.AttachFile, null, tint = TextMuted) }
                Box(modifier = Modifier.weight(1f).clip(RoundedCornerShape(24.dp))
                    .background(Bg).padding(horizontal = 16.dp, vertical = 10.dp)) {
                    Text("Type a message...", color = TextMuted, fontSize = 14.sp)
                }
                Spacer(Modifier.width(4.dp))
                IconButton(onClick = {}) { Icon(Icons.Filled.Mic, null, tint = AccentPurple) }
                Box(modifier = Modifier.size(44.dp).clip(CircleShape)
                    .background(Brush.linearGradient(listOf(AccentGradient1, AccentGradient2))),
                    contentAlignment = Alignment.Center) {
                    Icon(Icons.Filled.Send, null, tint = Color.White, modifier = Modifier.size(18.dp))
                }
            }
        }
    }
}
