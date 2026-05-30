package com.sirbenhenry.screenguard.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sirbenhenry.screenguard.ui.components.HeatMap
import com.sirbenhenry.screenguard.ui.components.StreakCard
import com.sirbenhenry.screenguard.ui.components.UsageRing
import com.sirbenhenry.screenguard.viewmodel.HomeUiState
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun HomeScreen(
    state: HomeUiState,
    streakRecords: List<com.sirbenhenry.screenguard.data.entity.StreakRecord>
) {
    if (state.isLoading) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    val scrollState = rememberScrollState()
    val today = SimpleDateFormat("EEEE, MMMM d", Locale.getDefault()).format(Date())

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = 20.dp)
    ) {
        Spacer(Modifier.height(24.dp))

        // Date + greeting
        Text(today, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 13.sp)
        Spacer(Modifier.height(2.dp))
        Text(
            greeting(state.todayScore),
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(Modifier.height(20.dp))

        // TODAY'S SCORE — the one obsessable metric
        TodayScoreCard(state.todayScore)

        Spacer(Modifier.height(20.dp))

        // App usage rings
        if (state.monitoredApps.isNotEmpty()) {
            Text(
                "TODAY'S USAGE",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 11.sp,
                letterSpacing = 1.5.sp
            )
            Spacer(Modifier.height(12.dp))
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(end = 16.dp)
            ) {
                items(state.monitoredApps) { app ->
                    val used = state.todayUsageMap[app.packageName] ?: 0
                    UsageRing(
                        usedMinutes = used,
                        limitMinutes = app.dailyLimitMinutes,
                        label = app.appName,
                        size = 100.dp
                    )
                }
            }
        } else {
            NoAppsCard()
        }

        Spacer(Modifier.height(20.dp))

        // Streak card
        StreakCard(
            currentStreak = state.currentStreak,
            longestStreak = state.longestStreak,
            totalSavedMinutes = state.totalSavedMinutes,
            availableFreezes = state.availableFreezes,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(20.dp))

        // Quick stats row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            MiniStatCard(
                "Cooldowns\ncompleted",
                "${state.totalCooldowns}",
                Color(0xFF4A9EFF),
                modifier = Modifier.weight(1f)
            )
            MiniStatCard(
                "Life minutes\nreclaimed",
                "${state.totalSavedMinutes}m",
                Color(0xFF44BB88),
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(Modifier.height(8.dp))

        // "What you could do instead" — infinite game mechanic
        if (state.totalSavedMinutes > 10) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.5f)
            ) {
                val books = state.totalSavedMinutes / 250
                val walks = state.totalSavedMinutes / 30
                val songs = state.totalSavedMinutes / 4
                Text(
                    "With ${state.totalSavedMinutes}min saved: ~$books books read · ~$walks walks · ~$songs songs listened",
                    modifier = Modifier.padding(12.dp),
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            }
        }

        Spacer(Modifier.height(20.dp))

        // GitHub-style heat map
        Text(
            "ACTIVITY HEAT MAP",
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 11.sp,
            letterSpacing = 1.5.sp
        )
        Spacer(Modifier.height(4.dp))
        Text(
            "Green = under limit. Red = over limit.",
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 11.sp
        )
        Spacer(Modifier.height(10.dp))
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier.fillMaxWidth()
        ) {
            HeatMap(
                records = streakRecords,
                modifier = Modifier.padding(12.dp)
            )
        }

        Spacer(Modifier.height(32.dp))
    }
}

@Composable
private fun TodayScoreCard(score: Int) {
    val color = when {
        score >= 90 -> Color(0xFF44BB88)
        score >= 70 -> Color(0xFF4A9EFF)
        score >= 50 -> Color(0xFFFFCC00)
        score >= 25 -> Color(0xFFFF8800)
        else -> Color(0xFFFF4444)
    }

    val animScore by animateIntAsState(score, tween(1200, easing = FastOutSlowInEasing), label = "score")

    Surface(
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surface,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Ring
            Box(contentAlignment = Alignment.Center, modifier = Modifier.size(80.dp)) {
                androidx.compose.foundation.Canvas(Modifier.fillMaxSize()) {
                    val sw = Stroke(width = 8.dp.toPx(), cap = StrokeCap.Round)
                    drawArc(Color(0xFF1A2233), -90f, 360f, false, style = sw)
                    if (score > 0) {
                        drawArc(color, -90f, 3.6f * animScore, false, style = sw)
                    }
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("$animScore", fontSize = 24.sp, fontWeight = FontWeight.ExtraBold, color = color)
                    Text("/100", fontSize = 9.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }

            Spacer(Modifier.width(20.dp))

            Column {
                Text("Today's Score", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(Modifier.height(4.dp))
                Text(
                    scoreLabel(score),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = color
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    scoreSub(score),
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 17.sp
                )
            }
        }
    }
}

@Composable
private fun MiniStatCard(label: String, value: String, color: Color, modifier: Modifier = Modifier) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.surface,
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(value, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = color)
            Spacer(Modifier.height(4.dp))
            Text(label, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, lineHeight = 15.sp)
        }
    }
}

@Composable
private fun NoAppsCard() {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("📱", fontSize = 32.sp)
            Spacer(Modifier.height(8.dp))
            Text(
                "No apps monitored yet",
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Medium
            )
            Spacer(Modifier.height(4.dp))
            Text(
                "Go to Apps tab to add Instagram, YouTube, or any app you want to limit.",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 13.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}

private fun greeting(score: Int) = when {
    score >= 90 -> "You're crushing it 🔥"
    score >= 70 -> "Solid day so far"
    score >= 50 -> "Halfway there"
    score >= 25 -> "Rough day — you can turn it around"
    else -> "Focus up. You've got this."
}

private fun scoreLabel(score: Int) = when {
    score >= 90 -> "Excellent"
    score >= 70 -> "Good"
    score >= 50 -> "Okay"
    score >= 25 -> "Struggling"
    else -> "Over limits"
}

private fun scoreSub(score: Int) = when {
    score >= 90 -> "All apps well within limits"
    score >= 70 -> "Mostly on track today"
    score >= 50 -> "Some apps near their limit"
    score >= 25 -> "Multiple apps over limit"
    else -> "Daily limits exceeded"
}
