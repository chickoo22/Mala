package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun MalaBeadsWheel(
    currentBead: Int,
    totalBeads: Int,
    roundNumber: Int,
    modifier: Modifier = Modifier,
    onTap: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val tapScale = remember { Animatable(1f) }

    // Subtle breathing pulse on the center counter
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.85f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseAlpha"
    )

    val primaryColor = MaterialTheme.colorScheme.primary
    val goldColor = Color(0xFFFFC107)
    val guruBeadColor = Color(0xFFFFB300)
    val unchantedBeadColor = MaterialTheme.colorScheme.surfaceVariant
    val threadColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
    val chantedGold = Color(0xFFFF9800)

    val safeTotalBeads = if (totalBeads > 0) totalBeads else 108

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .size(310.dp)
            .clip(CircleShape)
            .testTag("mala_beads_wheel")
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(bounded = true, radius = 150.dp)
            ) {
                coroutineScope.launch {
                    tapScale.animateTo(0.96f, tween(50))
                    tapScale.animateTo(1f, tween(120))
                }
                onTap()
            }
    ) {
        Canvas(modifier = Modifier.fillMaxSize().padding(12.dp)) {
            val center = Offset(size.width / 2f, size.height / 2f)
            val radius = (size.minDimension / 2f) - 24.dp.toPx()

            // Outer subtle sacred halo glow
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(primaryColor.copy(alpha = 0.12f), Color.Transparent),
                    center = center,
                    radius = radius + 22.dp.toPx()
                ),
                radius = radius + 22.dp.toPx(),
                center = center
            )

            // Connecting mala string
            drawCircle(
                color = threadColor,
                radius = radius,
                center = center,
                style = Stroke(width = 2.dp.toPx())
            )

            // Progress Arc indicating fraction completed
            val progressFraction = (currentBead.toFloat() / safeTotalBeads.toFloat()).coerceIn(0f, 1f)
            if (progressFraction > 0f) {
                drawArc(
                    brush = Brush.sweepGradient(
                        colors = listOf(primaryColor, goldColor, primaryColor),
                        center = center
                    ),
                    startAngle = -90f,
                    sweepAngle = progressFraction * 360f,
                    useCenter = false,
                    style = Stroke(width = 4.dp.toPx(), cap = StrokeCap.Round)
                )
            }

            // We render a representative set of beads around the perimeter (27 beads displayed visually)
            // so they are clearly identifiable and tactile without overcrowding
            val visualBeadCount = 27
            val activeVisualBeadIndex = ((currentBead.toFloat() / safeTotalBeads) * visualBeadCount).toInt()

            for (i in 0 until visualBeadCount) {
                // Angle starting from top (-90 degrees) moving clockwise
                val angleDeg = -90f + (i.toFloat() / visualBeadCount) * 360f
                val angleRad = Math.toRadians(angleDeg.toDouble())
                val beadCenter = Offset(
                    x = center.x + (radius * cos(angleRad)).toFloat(),
                    y = center.y + (radius * sin(angleRad)).toFloat()
                )

                val isChanted = i < activeVisualBeadIndex
                val isCurrent = i == activeVisualBeadIndex

                val beadRadius = when {
                    isCurrent -> 7.dp.toPx()
                    isChanted -> 5.5.dp.toPx()
                    else -> 4.5.dp.toPx()
                }

                val beadColor = when {
                    isCurrent -> goldColor
                    isChanted -> chantedGold
                    else -> unchantedBeadColor
                }

                // Bead shadow / 3D depth
                drawCircle(
                    color = Color.Black.copy(alpha = 0.2f),
                    radius = beadRadius + 0.5.dp.toPx(),
                    center = beadCenter.copy(y = beadCenter.y + 1.dp.toPx())
                )

                // Bead body
                drawCircle(
                    color = beadColor,
                    radius = beadRadius,
                    center = beadCenter
                )

                // Bead inner highlight for tactile 3D look
                drawCircle(
                    color = Color.White.copy(alpha = if (isCurrent) 0.6f else 0.35f),
                    radius = beadRadius * 0.4f,
                    center = Offset(beadCenter.x - beadRadius * 0.25f, beadCenter.y - beadRadius * 0.25f)
                )
            }

            // Sumeru (Guru Bead) at the very top (-90 degrees)
            val guruCenter = Offset(center.x, center.y - radius)
            val guruRadius = 9.dp.toPx()

            // Guru Bead base shadow
            drawCircle(
                color = Color.Black.copy(alpha = 0.3f),
                radius = guruRadius + 1.dp.toPx(),
                center = guruCenter.copy(y = guruCenter.y + 1.5.dp.toPx())
            )

            // Guru Bead golden sphere
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(Color(0xFFFFE082), guruBeadColor, Color(0xFFD87D00)),
                    center = guruCenter.copy(x = guruCenter.x - 2.dp.toPx(), y = guruCenter.y - 2.dp.toPx()),
                    radius = guruRadius
                ),
                radius = guruRadius,
                center = guruCenter
            )

            // Guru Bead Sacred Tassel Hanging
            val tasselPath = Path().apply {
                moveTo(guruCenter.x - 3.dp.toPx(), guruCenter.y - guruRadius)
                lineTo(guruCenter.x + 3.dp.toPx(), guruCenter.y - guruRadius)
                lineTo(guruCenter.x + 6.dp.toPx(), guruCenter.y - guruRadius - 14.dp.toPx())
                lineTo(guruCenter.x - 6.dp.toPx(), guruCenter.y - guruRadius - 14.dp.toPx())
                close()
            }
            drawPath(tasselPath, color = Color(0xFFC62828))
        }

        // Center Content: Counters & Meditative Status
        Surface(
            shape = CircleShape,
            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f),
            shadowElevation = 6.dp,
            modifier = Modifier.size(175.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = androidx.compose.foundation.layout.Arrangement.Center,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp)
            ) {
                // Mala round indicator tag
                Surface(
                    color = primaryColor.copy(alpha = 0.14f),
                    shape = CircleShape,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "Mala #${roundNumber + 1}",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = primaryColor,
                            fontSize = 11.sp
                        ),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }

                Spacer(modifier = Modifier.height(2.dp))

                // Big Chanted Bead Counter
                Text(
                    text = "$currentBead",
                    style = MaterialTheme.typography.displayMedium.copy(
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = pulseAlpha),
                        fontSize = 48.sp
                    ),
                    modifier = Modifier.testTag("current_bead_text")
                )

                // Subtitle
                Text(
                    text = "of $safeTotalBeads beads",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "TAP TO COUNT",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = primaryColor,
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = 1.sp,
                        fontSize = 9.sp
                    )
                )
            }
        }
    }
}
