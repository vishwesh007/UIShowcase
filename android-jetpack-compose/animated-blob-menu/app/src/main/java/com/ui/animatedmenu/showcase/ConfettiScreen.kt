package com.ui.animatedmenu.showcase

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Celebration
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

private val DarkBackground = Color(0xFF1E1E2E)
private val LightText = Color(0xFFEEEEEE)

data class ConfettiParticle(
    val x: Float,
    val y: Float,
    val vx: Float,
    val vy: Float,
    val rotation: Float,
    val rotationSpeed: Float,
    val color: Color,
    val size: Float,
    val shape: Int // 0: square, 1: circle, 2: rectangle
)

@Composable
fun ConfettiScreen(onBack: () -> Unit) {
    val coroutineScope = rememberCoroutineScope()
    var particles by remember { mutableStateOf<List<ConfettiParticle>>(emptyList()) }
    var trigger by remember { mutableIntStateOf(0) }
    
    val colors = listOf(
        Color(0xFFE53935), Color(0xFF43A047), Color(0xFF1E88E5),
        Color(0xFFFDD835), Color(0xFF8E24AA), Color(0xFFF4511E)
    )

    // Fire confetti logic
    fun fireConfetti(width: Float, height: Float, originX: Float, originY: Float) {
        val newParticles = List(80) {
            val angle = Random.nextDouble(PI * 1.1, PI * 1.9).toFloat()
            val speed = Random.nextFloat() * 40f + 10f
            ConfettiParticle(
                x = originX,
                y = originY,
                vx = cos(angle) * speed,
                vy = sin(angle) * speed,
                rotation = Random.nextFloat() * 360f,
                rotationSpeed = Random.nextFloat() * 20f - 10f,
                color = colors.random(),
                size = Random.nextFloat() * 15f + 10f,
                shape = Random.nextInt(3)
            )
        }
        particles = particles + newParticles
        trigger++
    }

    // Animation loop
    LaunchedEffect(trigger) {
        while (particles.isNotEmpty()) {
            delay(16)
            particles = particles.mapNotNull { p ->
                val nextVy = p.vy + 0.8f // Gravity
                val nextY = p.y + nextVy
                val nextX = p.x + p.vx * 0.98f // Air resistance
                if (nextY > 3000f) null // Remove when off screen
                else p.copy(
                    x = nextX,
                    y = nextY,
                    vy = nextVy,
                    vx = p.vx * 0.98f,
                    rotation = p.rotation + p.rotationSpeed
                )
            }
        }
    }

    Box(modifier = Modifier
        .fillMaxSize()
        .background(DarkBackground)) {
        
        // Content
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Top Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .statusBarsPadding(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(Icons.Filled.ArrowBack, "Back", tint = LightText)
                }
                Text("Confetti Effect", color = LightText, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.weight(1f))

            // Main Trigger Card
            BoxWithConstraints(
                modifier = Modifier
                    .padding(32.dp)
                    .fillMaxWidth()
            ) {
                val width = maxWidth.value
                val height = maxHeight.value
                
                var isPressed by remember { mutableStateOf(false) }
                val scale by animateFloatAsState(if (isPressed) 0.95f else 1f, spring(dampingRatio = 0.5f))
                
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .graphicsLayer {
                            scaleX = scale
                            scaleY = scale
                        }
                        .clickable { fireConfetti(width, height, width / 2, height / 2) },
                    shape = RoundedCornerShape(24.dp),
                    color = Color(0xFF2B2B40),
                    shadowElevation = 8.dp
                ) {
                    Column(
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            Icons.Filled.Celebration, 
                            contentDescription = null, 
                            tint = Color(0xFFFFCA28),
                            modifier = Modifier.size(64.dp)
                        )
                        Spacer(Modifier.height(16.dp))
                        Text(
                            "Tap to Celebrate!", 
                            color = Color.White, 
                            fontSize = 24.sp, 
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.weight(1f))
        }

        // Particle Canvas overlay
        Canvas(modifier = Modifier.fillMaxSize()) {
            particles.forEach { p ->
                rotate(degrees = p.rotation, pivot = Offset(p.x, p.y)) {
                    when (p.shape) {
                        0 -> drawRect(
                            color = p.color,
                            topLeft = Offset(p.x - p.size/2, p.y - p.size/2),
                            size = Size(p.size, p.size)
                        )
                        1 -> drawCircle(
                            color = p.color,
                            radius = p.size / 2,
                            center = Offset(p.x, p.y)
                        )
                        2 -> drawRect(
                            color = p.color,
                            topLeft = Offset(p.x - p.size/2, p.y - p.size/4),
                            size = Size(p.size, p.size/2)
                        )
                    }
                }
            }
        }
    }
}
