package com.ui.animatedmenu.showcase

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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

// Colors matching the design — soft neumorphic light theme with purple accents
private val MusicBg = Color(0xFFE8EDF5)
private val MusicSurface = Color(0xFFF0F4FA)
private val MusicPurple = Color(0xFF7C4DFF)
private val MusicPurpleDark = Color(0xFF5E35B1)
private val MusicPlaylistBg = Color(0xFF7C4DFF)
private val MusicGray = Color(0xFF9E9E9E)
private val MusicDark = Color(0xFF212121)

data class MusicTrack(
    val title: String,
    val artist: String,
    val album: String,
    val duration: String,
    // hue offset for album art generation
    val artHue: Float
)

private val tracks = listOf(
    MusicTrack("Forever Young (Dance Mix)", "Alphaville", "Club Playlist", "4:23", 270f),
    MusicTrack("Thought Contagion", "Simulation Theory", "Muse Collection", "3:42", 320f),
    MusicTrack("Blinding Lights", "The Weeknd", "After Hours", "3:20", 35f),
    MusicTrack("Midnight City", "M83", "Hurry Up", "4:03", 200f),
    MusicTrack("Take On Me", "A-ha", "Hunting High", "3:48", 160f),
    MusicTrack("Blue Monday", "New Order", "Power Corruption", "4:30", 220f),
)

@Composable
fun MusicAppScreen(onBack: () -> Unit) {
    var currentTrackIndex by remember { mutableIntStateOf(0) }
    var isPlaying by remember { mutableStateOf(true) }
    var showPlaylist by remember { mutableStateOf(false) }
    val currentTrack = tracks[currentTrackIndex]

    // Vinyl rotation animation
    val inf = rememberInfiniteTransition(label = "vinyl")
    val vinylRotation by inf.animateFloat(
        0f, 360f,
        infiniteRepeatable(tween(4000, easing = LinearEasing)),
        label = "rot"
    )

    // Shimmer for metallic rim
    val shimmer by inf.animateFloat(
        0f, (2 * PI).toFloat(),
        infiniteRepeatable(tween(3000, easing = LinearEasing)),
        label = "shimmer"
    )

    // Progress animation
    val progress by inf.animateFloat(
        0f, 1f,
        infiniteRepeatable(tween(30000, easing = LinearEasing)),
        label = "progress"
    )

    Box(modifier = Modifier.fillMaxSize().background(MusicBg)) {
        Column(modifier = Modifier.fillMaxSize().statusBarsPadding()) {
            // Header: search + menu
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(Icons.Filled.Search, "Search", tint = MusicGray, modifier = Modifier.size(24.dp))
                }
                IconButton(onClick = { showPlaylist = !showPlaylist }) {
                    Icon(Icons.Filled.Menu, "Menu", tint = MusicGray, modifier = Modifier.size(24.dp))
                }
            }

            if (!showPlaylist) {
                // Player view
                Column(
                    modifier = Modifier.fillMaxWidth().weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Song title + artist
                    Text(
                        currentTrack.title,
                        color = MusicDark,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 32.dp)
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        currentTrack.artist,
                        color = MusicGray,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium
                    )

                    Spacer(Modifier.height(32.dp))

                    // Vinyl disc with metallic rim and album art center
                    Box(
                        modifier = Modifier.size(260.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Canvas(modifier = Modifier.size(260.dp)) {
                            val cx = size.width / 2
                            val cy = size.height / 2
                            val outerR = size.width / 2
                            val innerR = outerR * 0.62f
                            val artR = outerR * 0.48f
                            val holeR = outerR * 0.08f

                            // Drop shadow
                            drawCircle(
                                Color.Black.copy(alpha = 0.08f),
                                radius = outerR + 8f,
                                center = Offset(cx, cy + 6f)
                            )

                            // Metallic rim — gradient sweep with shimmer
                            val rotation = if (isPlaying) vinylRotation else 0f
                            rotate(rotation, Offset(cx, cy)) {
                                // Outer metallic ring
                                val rimColors = listOf(
                                    Color(0xFFB0BEC5),
                                    Color(0xFFE0E0E0),
                                    Color(0xFF90CAF9),
                                    Color(0xFFCE93D8),
                                    Color(0xFFB0BEC5),
                                    Color(0xFF80DEEA),
                                    Color(0xFFE0E0E0),
                                    Color(0xFFB0BEC5),
                                )
                                drawCircle(
                                    Brush.sweepGradient(rimColors, Offset(cx, cy)),
                                    radius = outerR,
                                    center = Offset(cx, cy)
                                )

                                // Vinyl grooves between rim and art
                                for (i in 0..5) {
                                    val r = innerR + (outerR - innerR) * i / 6f
                                    drawCircle(
                                        Color.Black.copy(alpha = 0.04f),
                                        radius = r,
                                        center = Offset(cx, cy),
                                        style = Stroke(1f)
                                    )
                                }

                                // Album art circle — nebula/cosmic
                                drawAlbumArt(cx, cy, artR, currentTrack.artHue, shimmer)

                                // Center hole
                                drawCircle(
                                    Color(0xFF1A1A2E),
                                    radius = holeR,
                                    center = Offset(cx, cy)
                                )
                                drawCircle(
                                    Color.White.copy(alpha = 0.3f),
                                    radius = holeR * 0.5f,
                                    center = Offset(cx, cy)
                                )
                            }
                        }
                    }

                    Spacer(Modifier.height(24.dp))

                    // Album name
                    Text(
                        currentTrack.album,
                        color = MusicGray,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        letterSpacing = 1.sp
                    )

                    Spacer(Modifier.height(8.dp))

                    // Progress bar
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.7f)
                            .height(3.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(MusicGray.copy(alpha = 0.2f))
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(progress)
                                .fillMaxHeight()
                                .clip(RoundedCornerShape(2.dp))
                                .background(MusicPurple)
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(0.7f).padding(top = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(formatTime(progress, currentTrack.duration), color = MusicGray, fontSize = 11.sp)
                        Text(currentTrack.duration, color = MusicGray, fontSize = 11.sp)
                    }

                    Spacer(Modifier.height(24.dp))

                    // Player controls
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Shuffle
                        IconButton(onClick = {}) {
                            Icon(Icons.Filled.Shuffle, "Shuffle", tint = MusicGray, modifier = Modifier.size(22.dp))
                        }
                        Spacer(Modifier.width(12.dp))
                        // Previous
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(MusicSurface)
                                .shadow(2.dp, CircleShape)
                                .clickable {
                                    currentTrackIndex = if (currentTrackIndex > 0) currentTrackIndex - 1 else tracks.lastIndex
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Filled.SkipPrevious, "Previous", tint = MusicDark, modifier = Modifier.size(24.dp))
                        }
                        Spacer(Modifier.width(16.dp))
                        // Play/Pause — large
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CircleShape)
                                .background(MusicSurface)
                                .shadow(4.dp, CircleShape)
                                .clickable { isPlaying = !isPlaying },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                if (isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                                "Play/Pause",
                                tint = MusicDark,
                                modifier = Modifier.size(32.dp)
                            )
                        }
                        Spacer(Modifier.width(16.dp))
                        // Next
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(MusicSurface)
                                .shadow(2.dp, CircleShape)
                                .clickable {
                                    currentTrackIndex = (currentTrackIndex + 1) % tracks.size
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Filled.SkipNext, "Next", tint = MusicDark, modifier = Modifier.size(24.dp))
                        }
                        Spacer(Modifier.width(12.dp))
                        // Add / repeat
                        IconButton(onClick = {}) {
                            Icon(Icons.Filled.Add, "Add", tint = MusicGray, modifier = Modifier.size(22.dp))
                        }
                    }

                    Spacer(Modifier.height(16.dp))
                }

                // Bottom playlist peek section
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp))
                        .background(MusicPlaylistBg)
                        .padding(20.dp)
                ) {
                    // Drag handle
                    Box(
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .width(40.dp)
                            .height(4.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(Color.White.copy(alpha = 0.3f))
                    )
                    Spacer(Modifier.height(16.dp))

                    // Show next 2 tracks
                    val nextTracks = listOf(
                        tracks[(currentTrackIndex + 1) % tracks.size],
                        tracks[(currentTrackIndex + 2) % tracks.size]
                    )
                    nextTracks.forEachIndexed { i, track ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .clickable { currentTrackIndex = (currentTrackIndex + i + 1) % tracks.size }
                                .padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Mini play circle
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(Color.White.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Filled.PlayArrow, null, tint = Color.White, modifier = Modifier.size(18.dp))
                            }
                            Spacer(Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(track.title, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                                Text(track.artist, color = Color.White.copy(alpha = 0.6f), fontSize = 12.sp)
                            }
                            Text(track.duration, color = Color.White.copy(alpha = 0.6f), fontSize = 12.sp)
                        }
                        if (i == 0) {
                            Spacer(Modifier.height(4.dp))
                        }
                    }
                }
            } else {
                // Full playlist view
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 24.dp, vertical = 8.dp)
                ) {
                    item {
                        Text(
                            "Playlist",
                            color = MusicDark,
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )
                    }
                    itemsIndexed(tracks) { index, track ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .background(
                                    if (index == currentTrackIndex)
                                        MusicPurple.copy(alpha = 0.1f)
                                    else Color.Transparent
                                )
                                .clickable {
                                    currentTrackIndex = index
                                    showPlaylist = false
                                }
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Mini album art
                            Canvas(modifier = Modifier.size(48.dp)) {
                                drawCircle(Color(0xFFE0E0E0), radius = size.width / 2)
                                drawAlbumArt(
                                    size.width / 2, size.height / 2,
                                    size.width / 2 * 0.7f, track.artHue, 0f
                                )
                                drawCircle(Color(0xFF1A1A2E), radius = size.width / 2 * 0.12f)
                            }
                            Spacer(Modifier.width(16.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    track.title,
                                    color = if (index == currentTrackIndex) MusicPurple else MusicDark,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(track.artist, color = MusicGray, fontSize = 13.sp)
                            }
                            Text(track.duration, color = MusicGray, fontSize = 13.sp)
                            if (index == currentTrackIndex) {
                                Spacer(Modifier.width(8.dp))
                                Icon(Icons.Filled.GraphicEq, null, tint = MusicPurple, modifier = Modifier.size(20.dp))
                            }
                        }
                        if (index < tracks.lastIndex) {
                            Divider(
                                color = MusicGray.copy(alpha = 0.1f),
                                modifier = Modifier.padding(start = 76.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

// Draw generative cosmic/nebula album art
private fun DrawScope.drawAlbumArt(cx: Float, cy: Float, radius: Float, hueOffset: Float, phase: Float) {
    // Base dark circle
    drawCircle(Color(0xFF1A1A2E), radius = radius, center = Offset(cx, cy))

    // Nebula blobs — multiple layered circles with varying transparency
    val baseAngle = hueOffset * (PI / 180f).toFloat()
    for (i in 0..7) {
        val angle = baseAngle + i * (PI / 4f).toFloat() + phase * 0.3f
        val dist = radius * (0.2f + (i % 3) * 0.15f)
        val bx = cx + cos(angle) * dist
        val by = cy + sin(angle) * dist
        val blobR = radius * (0.3f + (i % 2) * 0.2f)

        val hue = (hueOffset + i * 40f) % 360f
        val color = hsvToColor(hue, 0.7f, 0.85f)
        drawCircle(
            color.copy(alpha = 0.25f),
            radius = blobR,
            center = Offset(bx, by)
        )
    }

    // Central bright glow
    drawCircle(
        Color.White.copy(alpha = 0.08f),
        radius = radius * 0.6f,
        center = Offset(cx, cy)
    )
    drawCircle(
        hsvToColor(hueOffset, 0.5f, 1f).copy(alpha = 0.15f),
        radius = radius * 0.4f,
        center = Offset(cx, cy)
    )
}

private fun hsvToColor(h: Float, s: Float, v: Float): Color {
    val hNorm = ((h % 360f) + 360f) % 360f
    val c = v * s
    val x = c * (1 - kotlin.math.abs((hNorm / 60f) % 2 - 1))
    val m = v - c
    val (r, g, b) = when {
        hNorm < 60 -> Triple(c, x, 0f)
        hNorm < 120 -> Triple(x, c, 0f)
        hNorm < 180 -> Triple(0f, c, x)
        hNorm < 240 -> Triple(0f, x, c)
        hNorm < 300 -> Triple(x, 0f, c)
        else -> Triple(c, 0f, x)
    }
    return Color(r + m, g + m, b + m)
}

private fun formatTime(progress: Float, totalDuration: String): String {
    val parts = totalDuration.split(":")
    if (parts.size != 2) return "0:00"
    val totalSeconds = parts[0].toIntOrNull()?.times(60)?.plus(parts[1].toIntOrNull() ?: 0) ?: 0
    val currentSeconds = (totalSeconds * progress).toInt()
    val mins = currentSeconds / 60
    val secs = currentSeconds % 60
    return "$mins:${secs.toString().padStart(2, '0')}"
}
