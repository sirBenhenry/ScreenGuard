package com.sirbenhenry.screenguard.ui.overlay

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.setContent
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sirbenhenry.screenguard.data.AppDatabase
import com.sirbenhenry.screenguard.data.entity.CooldownSession
import com.sirbenhenry.screenguard.data.entity.GoodApp
import com.sirbenhenry.screenguard.ui.theme.ScreenGuardTheme
import kotlinx.coroutines.*

class CooldownActivity : ComponentActivity() {

    companion object {
        const val EXTRA_PACKAGE = "pkg"
        const val EXTRA_APP_NAME = "appName"
        const val EXTRA_COOLDOWN_SECS = "cooldownSecs"
        const val EXTRA_OPEN_NUMBER = "openNumber"
        const val EXTRA_USED_MIN = "usedMin"
        const val EXTRA_LIMIT_MIN = "limitMin"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val pkg = intent.getStringExtra(EXTRA_PACKAGE) ?: run { finish(); return }
        val appName = intent.getStringExtra(EXTRA_APP_NAME) ?: pkg
        val cooldownSecs = intent.getIntExtra(EXTRA_COOLDOWN_SECS, 60)
        val openNumber = intent.getIntExtra(EXTRA_OPEN_NUMBER, 1)
        val usedMin = intent.getIntExtra(EXTRA_USED_MIN, 0)
        val limitMin = intent.getIntExtra(EXTRA_LIMIT_MIN, 30)

        // Block back button — cooldown cannot be skipped
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() { /* swallow */ }
        })

        setContent {
            ScreenGuardTheme {
                CooldownScreen(
                    appName = appName,
                    cooldownSecs = cooldownSecs,
                    openNumber = openNumber,
                    usedMin = usedMin,
                    limitMin = limitMin,
                    onFinished = {
                        CoroutineScope(Dispatchers.IO).launch {
                            AppDatabase.get(this@CooldownActivity).cooldownSessionDao()
                                .insert(CooldownSession(packageName = pkg, completedFully = true))
                        }
                        finish()
                    },
                    onOpenGoodApp = { goodPkg ->
                        CoroutineScope(Dispatchers.IO).launch {
                            AppDatabase.get(this@CooldownActivity).cooldownSessionDao()
                                .insert(CooldownSession(packageName = pkg, completedFully = false))
                        }
                        packageManager.getLaunchIntentForPackage(goodPkg)?.let { startActivity(it) }
                        finish()
                    },
                    getGoodApps = { AppDatabase.get(this).goodAppDao().getAll() }
                )
            }
        }
    }
}

// Variable cooldown messages — randomised to prevent adaptation (craving machine principle)
private val BREATH_CYCLES = listOf("in..." to "out...", "in..." to "out...", "in..." to "out...")
private val QUOTES = listOf(
    "The urge passes in 90 seconds if you don't feed it.",
    "You're choosing presence over pixels.",
    "Boredom is a portal, not a problem.",
    "Every pause is a vote for the life you want.",
    "You opened this ${"%d"}x today. Is this what you meant to do?",
    "The algorithm is designed to trap you. You just escaped for a minute.",
    "Check in with your body. Not your feed.",
    "What were you doing before you picked up your phone?",
    "This moment doesn't need to be filled.",
    "Real life has no infinite scroll."
)

@Composable
private fun CooldownScreen(
    appName: String,
    cooldownSecs: Int,
    openNumber: Int,
    usedMin: Int,
    limitMin: Int,
    onFinished: () -> Unit,
    onOpenGoodApp: (String) -> Unit,
    getGoodApps: suspend () -> List<GoodApp>
) {
    var secondsLeft by remember { mutableIntStateOf(cooldownSecs) }
    var goodApps by remember { mutableStateOf<List<GoodApp>>(emptyList()) }
    val pctUsed = if (limitMin > 0) (usedMin * 100 / limitMin).coerceIn(0, 100) else 0

    // Pick a random quote at start (variable reward principle — different each time)
    val quote by remember {
        val raw = QUOTES.random()
        mutableStateOf(raw.replace("%d", openNumber.toString()))
    }

    val breathPhase by remember { derivedStateOf {
        when ((cooldownSecs - secondsLeft) % 16) {
            in 0..3 -> "Breathe in"
            in 4..7 -> "Hold"
            in 8..11 -> "Breathe out"
            else -> "Hold"
        }
    }}

    LaunchedEffect(Unit) { goodApps = getGoodApps() }

    LaunchedEffect(Unit) {
        repeat(cooldownSecs) {
            delay(1000)
            secondsLeft--
            if (secondsLeft <= 0) { onFinished(); return@LaunchedEffect }
        }
    }

    val breathScale by rememberInfiniteTransition(label = "breath").animateFloat(
        initialValue = 0.90f, targetValue = 1.10f,
        animationSpec = infiniteRepeatable(tween(4000, easing = EaseInOutSine), RepeatMode.Reverse),
        label = "bs"
    )

    val ringColor = when {
        pctUsed >= 90 -> Color(0xFFFF4444)
        pctUsed >= 75 -> Color(0xFFFFAA00)
        else -> Color(0xFF44BB88)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(Color(0xFF070B14), Color(0xFF0C1824)))),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 28.dp)
        ) {
            Spacer(Modifier.height(40.dp))

            // Header
            Text(
                openNumber.let {
                    when (it) {
                        1 -> "Opening $appName"
                        2 -> "Back again? ($it opens today)"
                        3 -> "Third time already ($it opens)"
                        else -> "$it opens today. Still worth it?"
                    }
                },
                color = when {
                    openNumber >= 4 -> Color(0xFFFF8866)
                    openNumber >= 2 -> Color(0xFFFFCC88)
                    else -> Color(0xFF7799BB)
                },
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
                letterSpacing = 0.5.sp
            )

            Spacer(Modifier.height(12.dp))

            // Usage progress
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("$usedMin", color = ringColor, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                Text(" / $limitMin min used today", color = Color(0xFF556677), fontSize = 13.sp)
            }
            Spacer(Modifier.height(6.dp))
            LinearProgressIndicator(
                progress = { pctUsed / 100f },
                modifier = Modifier.fillMaxWidth(0.7f).height(5.dp).clip(RoundedCornerShape(3.dp)),
                color = ringColor,
                trackColor = Color(0xFF1A2233)
            )

            Spacer(Modifier.height(44.dp))

            // Breathing circle — the main focus
            Box(contentAlignment = Alignment.Center, modifier = Modifier.scale(breathScale)) {
                Box(
                    modifier = Modifier
                        .size(190.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                0f to Color(0xFF162840),
                                0.7f to Color(0xFF0D1E30),
                                1f to Color(0xFF060E18)
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            "$secondsLeft",
                            color = Color.White,
                            fontSize = 60.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Text(
                            breathPhase,
                            color = Color(0xFF6699BB),
                            fontSize = 12.sp,
                            letterSpacing = 1.sp
                        )
                    }
                }
            }

            Spacer(Modifier.height(28.dp))

            // Variable quote (craving machine: different content each time)
            Text(
                quote,
                color = Color(0xFF445566),
                fontSize = 13.sp,
                textAlign = TextAlign.Center,
                lineHeight = 20.sp,
                fontStyle = FontStyle.Italic,
                modifier = Modifier.fillMaxWidth(0.85f)
            )

            // Good apps section
            if (goodApps.isNotEmpty()) {
                Spacer(Modifier.height(36.dp))
                Text(
                    "BETTER ALTERNATIVES",
                    color = Color(0xFF334455),
                    fontSize = 10.sp,
                    letterSpacing = 2.sp
                )
                Spacer(Modifier.height(10.dp))
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp)
                ) {
                    items(goodApps) { app ->
                        GoodAppButton(app, onOpenGoodApp)
                    }
                }
            }

            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
private fun GoodAppButton(app: GoodApp, onClick: (String) -> Unit) {
    Surface(
        onClick = { onClick(app.packageName) },
        shape = RoundedCornerShape(14.dp),
        color = Color(0xFF0F1E2E),
        tonalElevation = 2.dp,
        modifier = Modifier.width(88.dp).height(72.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("▶", fontSize = 22.sp, color = Color(0xFF4A9EFF))
            Spacer(Modifier.height(3.dp))
            Text(
                app.appName,
                color = Color(0xFF889BAA),
                fontSize = 10.sp,
                maxLines = 2,
                textAlign = TextAlign.Center,
                lineHeight = 13.sp
            )
        }
    }
}
