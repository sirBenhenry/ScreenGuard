package com.sirbenhenry.screenguard.ui.overlay

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sirbenhenry.screenguard.ui.theme.ScreenGuardTheme
import java.util.Calendar

class FocusBlockActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val appName = intent.getStringExtra("appName") ?: "this app"

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() { goHome() }
        })

        setContent {
            ScreenGuardTheme {
                FocusBlockScreen(appName = appName, onGoHome = { goHome() })
            }
        }
        window.decorView.postDelayed({ goHome() }, 4000)
    }

    private fun goHome() {
        startActivity(Intent(Intent.ACTION_MAIN).apply {
            addCategory(Intent.CATEGORY_HOME)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        })
        finish()
    }
}

@Composable
private fun FocusBlockScreen(appName: String, onGoHome: () -> Unit) {
    val cal = Calendar.getInstance()
    val hour = cal.get(Calendar.HOUR_OF_DAY)
    val endHint = when {
        hour < 9 -> "until 9:00"
        hour in 12..13 -> "until 14:00"
        else -> "until focus time ends"
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(Color(0xFF0A0A18), Color(0xFF141428)))),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(40.dp)
        ) {
            Text("🎯", fontSize = 72.sp)
            Spacer(Modifier.height(24.dp))
            Text(
                "Focus Time",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF8888FF)
            )
            Spacer(Modifier.height(12.dp))
            Text(
                "$appName is blocked $endHint.\n\nYou set this up to protect your focus. It's working.",
                fontSize = 16.sp,
                color = Color(0xFF667788),
                textAlign = TextAlign.Center,
                lineHeight = 24.sp
            )
            Spacer(Modifier.height(40.dp))
            OutlinedButton(
                onClick = onGoHome,
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF8888FF))
            ) {
                Text("Got it")
            }
        }
    }
}
