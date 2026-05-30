package com.sirbenhenry.screenguard.ui.screens

import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sirbenhenry.screenguard.util.UsageStatsUtil

data class OnboardingPage(
    val emoji: String,
    val title: String,
    val body: String,
    val color: Color
)

@Composable
fun OnboardingScreen(onComplete: () -> Unit) {
    val context = LocalContext.current
    var page by remember { mutableIntStateOf(0) }

    val pages = listOf(
        OnboardingPage(
            "🛑", "Take back control",
            "You open Instagram on autopilot. You scroll for seconds — then 40 minutes have passed.\n\nScreenGuard makes opening social media intentional, not automatic.",
            Color(0xFF4A9EFF)
        ),
        OnboardingPage(
            "⏳", "The cooldown screen",
            "Every time you open a monitored app, you wait 30–90 seconds first.\n\nYou can't skip it. This one feature alone will cut impulsive scrolling by more than half.",
            Color(0xFF44BB88)
        ),
        OnboardingPage(
            "📊", "Usage Stats access",
            "ScreenGuard needs to know how long you spend in each app to enforce your daily limits.",
            Color(0xFFFFCC00)
        ),
        OnboardingPage(
            "🪟", "Draw Over Apps access",
            "The cooldown screen needs to appear on top of Instagram or YouTube when you open them.",
            Color(0xFFFF8800)
        ),
        OnboardingPage(
            "♿", "Accessibility Service",
            "ScreenGuard uses Android's Accessibility API to detect when you switch to a monitored app.\n\nThis is the core feature — the app doesn't work without it.",
            Color(0xFFFF6B6B)
        ),
        OnboardingPage(
            "🔥", "Build your streak",
            "Stay under your limits every day and build a streak. Diamond milestones at 7, 14, 30, 60, 90, 180, and 365 days.\n\nThe heat map shows every day at a glance — make it all green.",
            Color(0xFFFFD700)
        )
    )

    val hasUsageStats = UsageStatsUtil.hasPermission(context)
    val hasOverlay = Settings.canDrawOverlays(context)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(Color(0xFF070B14), Color(0xFF0C1824))))
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Progress dots
            Spacer(Modifier.height(32.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                pages.forEachIndexed { i, _ ->
                    Box(
                        Modifier
                            .size(if (i == page) 20.dp else 6.dp, 6.dp)
                            .clip(CircleShape)
                            .background(
                                if (i == page) pages[page].color
                                else Color(0xFF2A3A4A)
                            )
                    )
                }
            }

            Spacer(Modifier.weight(0.5f))

            AnimatedContent(
                targetState = page,
                transitionSpec = {
                    (slideInHorizontally { it } + fadeIn()) togetherWith
                    (slideOutHorizontally { -it } + fadeOut())
                },
                label = "page"
            ) { p ->
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(pages[p].emoji, fontSize = 72.sp)
                    Spacer(Modifier.height(24.dp))
                    Text(
                        pages[p].title,
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold,
                        color = pages[p].color,
                        textAlign = TextAlign.Center
                    )
                    Spacer(Modifier.height(16.dp))
                    Text(
                        pages[p].body,
                        fontSize = 15.sp,
                        color = Color(0xFF8899AA),
                        textAlign = TextAlign.Center,
                        lineHeight = 22.sp
                    )
                }
            }

            Spacer(Modifier.weight(1f))

            // Action button for permission pages
            when (page) {
                2 -> { // Usage stats
                    val granted = UsageStatsUtil.hasPermission(context)
                    PermissionButton(
                        label = if (granted) "✓ Granted" else "Grant Usage Stats Access",
                        granted = granted,
                        color = pages[page].color
                    ) {
                        context.startActivity(Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS))
                    }
                    Spacer(Modifier.height(12.dp))
                }
                3 -> { // Overlay
                    val granted = Settings.canDrawOverlays(context)
                    PermissionButton(
                        label = if (granted) "✓ Granted" else "Grant Draw Over Apps",
                        granted = granted,
                        color = pages[page].color
                    ) {
                        context.startActivity(
                            Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                                Uri.parse("package:${context.packageName}"))
                        )
                    }
                    Spacer(Modifier.height(12.dp))
                }
                4 -> { // Accessibility
                    PermissionButton(
                        label = "Enable Accessibility Service",
                        granted = false,
                        color = pages[page].color
                    ) {
                        context.startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
                    }
                    Spacer(Modifier.height(12.dp))
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (page > 0) {
                    TextButton(onClick = { page-- }) {
                        Text("Back", color = Color(0xFF445566))
                    }
                } else {
                    Spacer(Modifier.width(72.dp))
                }

                Button(
                    onClick = {
                        if (page < pages.size - 1) page++
                        else onComplete()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = pages[page].color),
                    modifier = Modifier.height(50.dp)
                ) {
                    Text(
                        if (page == pages.size - 1) "Let's go 🚀" else "Next",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                }
            }

            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
private fun PermissionButton(label: String, granted: Boolean, color: Color, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth().height(52.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (granted) Color(0xFF1A3A2A) else color.copy(alpha = 0.15f),
            contentColor = if (granted) Color(0xFF44BB88) else color
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Text(label, fontWeight = FontWeight.SemiBold)
    }
}
