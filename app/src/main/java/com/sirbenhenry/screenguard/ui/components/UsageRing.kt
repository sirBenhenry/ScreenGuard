package com.sirbenhenry.screenguard.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun UsageRing(
    usedMinutes: Int,
    limitMinutes: Int,
    label: String,
    modifier: Modifier = Modifier,
    size: Dp = 120.dp,
    strokeWidth: Dp = 10.dp
) {
    val fraction = if (limitMinutes > 0) (usedMinutes.toFloat() / limitMinutes).coerceIn(0f, 1f) else 0f
    val isOver = usedMinutes >= limitMinutes

    val color = when {
        fraction >= 1f -> Color(0xFFFF4444)
        fraction >= 0.9f -> Color(0xFFFF8800)
        fraction >= 0.75f -> Color(0xFFFFCC00)
        else -> Color(0xFF44BB88)
    }

    val animFraction by animateFloatAsState(
        targetValue = fraction,
        animationSpec = tween(800, easing = FastOutSlowInEasing),
        label = "ring"
    )

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.size(size)) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val stroke = Stroke(width = strokeWidth.toPx(), cap = StrokeCap.Round)
                val inset = strokeWidth.toPx() / 2
                val arcSize = Size(this.size.width - inset * 2, this.size.height - inset * 2)
                val topLeft = Offset(inset, inset)

                // Track
                drawArc(
                    color = Color(0xFF1A2233),
                    startAngle = -90f,
                    sweepAngle = 360f,
                    useCenter = false,
                    topLeft = topLeft,
                    size = arcSize,
                    style = stroke
                )
                // Progress
                if (animFraction > 0f) {
                    drawArc(
                        color = color,
                        startAngle = -90f,
                        sweepAngle = 360f * animFraction,
                        useCenter = false,
                        topLeft = topLeft,
                        size = arcSize,
                        style = stroke
                    )
                }
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    "${usedMinutes}m",
                    fontSize = if (size >= 120.dp) 20.sp else 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isOver) Color(0xFFFF4444) else MaterialTheme.colorScheme.onSurface
                )
                Text(
                    "/ ${limitMinutes}m",
                    fontSize = if (size >= 120.dp) 11.sp else 9.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        Spacer(Modifier.height(4.dp))
        Text(
            label,
            fontSize = 11.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1
        )
    }
}
