package com.sirbenhenry.screenguard.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun StreakCard(
    currentStreak: Int,
    longestStreak: Int,
    totalSavedMinutes: Int,
    modifier: Modifier = Modifier,
    availableFreezes: Int = 0,
    onUseFreezeForYesterday: (() -> Unit)? = null
) {
    // Diamond milestones (from video: streaks unlock diamonds)
    val diamonds = listOf(7, 14, 30, 60, 90, 180, 365)
    val nextDiamond = diamonds.firstOrNull { it > currentStreak }
    val earnedDiamonds = diamonds.count { it <= currentStreak }

    val pulse by rememberInfiniteTransition(label = "streak").animateFloat(
        initialValue = 1f, targetValue = if (currentStreak > 0) 1.05f else 1f,
        animationSpec = infiniteRepeatable(tween(2000), RepeatMode.Reverse),
        label = "scale"
    )

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(
                Brush.linearGradient(
                    listOf(Color(0xFF0A1A2A), Color(0xFF0A2A1A))
                )
            )
            .padding(20.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Current Streak", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp)
                    Spacer(Modifier.height(2.dp))
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(
                            "$currentStreak",
                            fontSize = 48.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = when {
                                currentStreak >= 30 -> Color(0xFFFFD700)
                                currentStreak >= 7 -> Color(0xFF44BB88)
                                currentStreak > 0 -> Color(0xFF4A9EFF)
                                else -> Color(0xFF445566)
                            },
                            modifier = Modifier.scale(pulse)
                        )
                        Spacer(Modifier.width(4.dp))
                        Text(
                            "days",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 14.sp,
                            modifier = Modifier.padding(bottom = 10.dp)
                        )
                    }
                }

                // Diamond badges
                Column(horizontalAlignment = Alignment.End) {
                    Text("💎".repeat(earnedDiamonds.coerceAtMost(5)), fontSize = 18.sp)
                    if (earnedDiamonds > 5) Text("+${earnedDiamonds - 5} more", fontSize = 10.sp, color = Color(0xFFFFD700))
                    Spacer(Modifier.height(4.dp))
                    if (nextDiamond != null) {
                        Text(
                            "Next 💎 at day $nextDiamond",
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    } else {
                        Text("All diamonds earned! 🏆", fontSize = 10.sp, color = Color(0xFFFFD700))
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            // Progress bar to next diamond
            if (nextDiamond != null) {
                val prevDiamond = diamonds.lastOrNull { it <= currentStreak } ?: 0
                val progress = (currentStreak - prevDiamond).toFloat() / (nextDiamond - prevDiamond)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(4.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(Color(0xFF1A2233))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(progress)
                            .fillMaxHeight()
                            .clip(RoundedCornerShape(2.dp))
                            .background(
                                Brush.horizontalGradient(listOf(Color(0xFF44BB88), Color(0xFFFFD700)))
                            )
                    )
                }
                Spacer(Modifier.height(4.dp))
                Text(
                    "${nextDiamond - currentStreak} more days to next diamond",
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(Modifier.height(16.dp))

            // Stats row
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                StatItem("Best", "${longestStreak}d", Color(0xFFFFD700))
                StatItem("Saved", "${totalSavedMinutes / 60}h ${totalSavedMinutes % 60}m", Color(0xFF44BB88))
                if (availableFreezes > 0) {
                    StatItem("Freezes", "🧊×$availableFreezes", Color(0xFF88CCFF))
                }
            }

            // Use freeze button (only if streak broke yesterday and freeze available)
            if (availableFreezes > 0 && onUseFreezeForYesterday != null) {
                Spacer(Modifier.height(12.dp))
                OutlinedButton(
                    onClick = onUseFreezeForYesterday,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF88CCFF))
                ) {
                    Text("🧊 Use freeze to protect streak")
                }
            }
        }
    }
}

@Composable
private fun StatItem(label: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = color)
        Text(label, fontSize = 11.sp, color = Color(0xFF667788))
    }
}
