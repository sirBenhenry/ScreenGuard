package com.sirbenhenry.screenguard.ui.overlay

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.setContent
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sirbenhenry.screenguard.MainActivity
import com.sirbenhenry.screenguard.ui.theme.ScreenGuardTheme

class LimitReachedActivity : ComponentActivity() {

    companion object {
        const val EXTRA_PACKAGE = "pkg"
        const val EXTRA_APP_NAME = "appName"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val appName = intent.getStringExtra(EXTRA_APP_NAME) ?: "this app"

        // Can't back out of this screen
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                goHome()
            }
        })

        setContent {
            ScreenGuardTheme {
                LimitReachedScreen(
                    appName = appName,
                    onGoHome = { goHome() },
                    onOpenApp = { openMainApp() }
                )
            }
        }

        // Auto-go home after 5s if user does nothing
        window.decorView.postDelayed({ goHome() }, 5000)
    }

    private fun goHome() {
        startActivity(Intent(Intent.ACTION_MAIN).apply {
            addCategory(Intent.CATEGORY_HOME)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        })
        finish()
    }

    private fun openMainApp() {
        startActivity(Intent(this, MainActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        })
        finish()
    }
}

@Composable
private fun LimitReachedScreen(
    appName: String,
    onGoHome: () -> Unit,
    onOpenApp: () -> Unit
) {
    var countdown by remember { mutableIntStateOf(5) }

    LaunchedEffect(Unit) {
        for (i in 4 downTo 0) {
            kotlinx.coroutines.delay(1000)
            countdown = i
        }
    }

    val pulse by rememberInfiniteTransition(label = "pulse").animateFloat(
        initialValue = 0.95f, targetValue = 1.05f,
        animationSpec = infiniteRepeatable(tween(800), RepeatMode.Reverse),
        label = "pulse"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(Color(0xFF1A0A0A), Color(0xFF2A0808)))),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(32.dp)
        ) {
            Text("🛑", fontSize = 64.sp)
            Spacer(Modifier.height(24.dp))

            Text(
                "Daily limit reached",
                color = Color(0xFFFF6666),
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(8.dp))
            Text(
                "$appName is blocked for today.",
                color = Color(0xFFAA8888),
                fontSize = 16.sp,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(8.dp))
            Text(
                "Your limit resets at midnight.",
                color = Color(0xFF886666),
                fontSize = 14.sp
            )

            Spacer(Modifier.height(48.dp))

            Text(
                "Going home in $countdown...",
                color = Color(0xFF664444),
                fontSize = 13.sp
            )

            Spacer(Modifier.height(24.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedButton(
                    onClick = onGoHome,
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFAA6666))
                ) {
                    Text("Home")
                }
                Button(
                    onClick = onOpenApp,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3A1A1A))
                ) {
                    Text("View stats")
                }
            }

            Spacer(Modifier.height(32.dp))
            Text(
                "\"You can want it, or you can have it.\nYou can't scroll and live fully at the same time.\"",
                color = Color(0xFF553333),
                fontSize = 12.sp,
                textAlign = TextAlign.Center,
                lineHeight = 18.sp,
                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
            )
        }
    }
}
