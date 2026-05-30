package com.sirbenhenry.screenguard.ui.screens

import android.accessibilityservice.AccessibilityServiceInfo
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.view.accessibility.AccessibilityManager
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sirbenhenry.screenguard.data.AppDatabase
import com.sirbenhenry.screenguard.data.entity.FocusHour
import com.sirbenhenry.screenguard.util.Prefs
import com.sirbenhenry.screenguard.util.UsageStatsUtil
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@Composable
fun SettingsScreen(onCheckUpdate: () -> Unit) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val scroll = rememberScrollState()
    var cooldownSecs by remember { mutableIntStateOf(60) }
    var breathingEnabled by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        Prefs.cooldownSecondsFlow(context).collect { cooldownSecs = it }
    }
    LaunchedEffect(Unit) {
        Prefs.enableBreathingFlow(context).collect { breathingEnabled = it }
    }

    val hasUsageStats = UsageStatsUtil.hasPermission(context)
    val hasOverlay = Settings.canDrawOverlays(context)
    val hasAccessibility = isAccessibilityEnabled(context)

    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(scroll)
            .padding(horizontal = 20.dp)
    ) {
        Spacer(Modifier.height(24.dp))
        Text("Settings", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)

        Spacer(Modifier.height(20.dp))

        // Permission status
        SectionHeader("REQUIRED PERMISSIONS")
        Spacer(Modifier.height(8.dp))
        PermissionRow("Usage Stats Access", hasUsageStats, Icons.Default.Timeline) {
            context.startActivity(Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS))
        }
        Spacer(Modifier.height(8.dp))
        PermissionRow("Draw Over Apps", hasOverlay, Icons.Default.Layers) {
            context.startActivity(
                Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:${context.packageName}"))
            )
        }
        Spacer(Modifier.height(8.dp))
        PermissionRow("Accessibility Service", hasAccessibility, Icons.Default.Accessibility) {
            context.startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
        }

        Spacer(Modifier.height(20.dp))

        // Settings
        SectionHeader("COOLDOWN SETTINGS")
        Spacer(Modifier.height(8.dp))
        Surface(shape = RoundedCornerShape(14.dp), color = MaterialTheme.colorScheme.surface, modifier = Modifier.fillMaxWidth()) {
            Column(Modifier.padding(16.dp)) {
                Text("Base cooldown: ${cooldownSecs}s", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text("(Escalates per open: ${cooldownSecs}s → ${(cooldownSecs*1.5).toInt()}s → ${cooldownSecs*2}s → ...)", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Slider(
                    value = cooldownSecs.toFloat(),
                    onValueChange = {
                        cooldownSecs = it.toInt()
                        scope.launch { Prefs.setCooldownSeconds(context, it.toInt()) }
                    },
                    valueRange = 10f..120f, steps = 22
                )
                Spacer(Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Breathing animation", Modifier.weight(1f), color = MaterialTheme.colorScheme.onSurface)
                    Switch(
                        checked = breathingEnabled,
                        onCheckedChange = {
                            breathingEnabled = it
                            scope.launch { Prefs.setEnableBreathing(context, it) }
                        }
                    )
                }
            }
        }

        Spacer(Modifier.height(20.dp))

        // Update
        SectionHeader("APP UPDATES")
        Spacer(Modifier.height(8.dp))
        Surface(
            shape = RoundedCornerShape(14.dp),
            color = MaterialTheme.colorScheme.surface,
            onClick = onCheckUpdate,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.SystemUpdate, "Update", tint = Color(0xFF4A9EFF))
                Spacer(Modifier.width(12.dp))
                Column(Modifier.weight(1f)) {
                    Text("Check for updates", fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onSurface)
                    Text("Downloads latest version from GitHub", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Icon(Icons.Default.ChevronRight, null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }

        Spacer(Modifier.height(20.dp))

        // Focus hours
        FocusHoursSection()

        Spacer(Modifier.height(20.dp))

        // About
        SectionHeader("ABOUT")
        Spacer(Modifier.height(8.dp))
        Surface(shape = RoundedCornerShape(14.dp), color = MaterialTheme.colorScheme.surface, modifier = Modifier.fillMaxWidth()) {
            Column(Modifier.padding(16.dp)) {
                Text("ScreenGuard", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                Text("Built to help you reclaim your time.", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(Modifier.height(4.dp))
                Text("github.com/SirBenHenry/ScreenGuard", fontSize = 11.sp, color = Color(0xFF4A9EFF))
            }
        }

        Spacer(Modifier.height(32.dp))
    }
}

@Composable
private fun SectionHeader(text: String) {
    Text(text, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, letterSpacing = 1.5.sp)
}

@Composable
private fun PermissionRow(label: String, granted: Boolean, icon: ImageVector, onFix: () -> Unit) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surface,
        onClick = if (!granted) onFix else ({})
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, null, tint = if (granted) Color(0xFF44BB88) else Color(0xFFFF6666))
            Spacer(Modifier.width(12.dp))
            Text(label, Modifier.weight(1f), color = MaterialTheme.colorScheme.onSurface)
            if (granted) {
                Icon(Icons.Default.CheckCircle, "Granted", tint = Color(0xFF44BB88))
            } else {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Fix", fontSize = 12.sp, color = Color(0xFFFF6666))
                    Spacer(Modifier.width(4.dp))
                    Icon(Icons.Default.ChevronRight, null, tint = Color(0xFFFF6666))
                }
            }
        }
    }
}

@Composable
private fun FocusHoursSection() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var focusHours by remember { mutableStateOf<List<FocusHour>>(emptyList()) }
    var showAdd by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        AppDatabase.get(context).focusHourDao().getAllFlow().collect { focusHours = it }
    }

    SectionHeader("FOCUS HOURS (FULL BLOCK)")
    Spacer(Modifier.height(4.dp))
    Text("During focus hours, monitored apps are completely blocked with no cooldown.", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
    Spacer(Modifier.height(8.dp))

    focusHours.forEach { fh ->
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                Text("🎯", fontSize = 18.sp)
                Spacer(Modifier.width(10.dp))
                Column(Modifier.weight(1f)) {
                    Text(fh.label, color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Medium)
                    Text(
                        "%02d:%02d – %02d:%02d  %s".format(fh.startHour, fh.startMinute, fh.endHour, fh.endMinute,
                            when {
                                fh.appliesWeekdays && fh.appliesWeekends -> "every day"
                                fh.appliesWeekdays -> "weekdays"
                                else -> "weekends"
                            }),
                        fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Switch(
                    checked = fh.isEnabled,
                    onCheckedChange = { scope.launch { AppDatabase.get(context).focusHourDao().update(fh.copy(isEnabled = it)) } }
                )
                IconButton(onClick = { scope.launch { AppDatabase.get(context).focusHourDao().delete(fh) } }) {
                    Icon(Icons.Default.Delete, null, tint = Color(0xFFFF6666))
                }
            }
        }
        Spacer(Modifier.height(6.dp))
    }

    OutlinedButton(
        onClick = { showAdd = true },
        modifier = Modifier.fillMaxWidth()
    ) {
        Icon(Icons.Default.Add, null)
        Spacer(Modifier.width(6.dp))
        Text("Add focus hour")
    }

    if (showAdd) {
        AddFocusHourDialog(
            onAdd = { fh ->
                scope.launch { AppDatabase.get(context).focusHourDao().insert(fh) }
                showAdd = false
            },
            onDismiss = { showAdd = false }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddFocusHourDialog(onAdd: (FocusHour) -> Unit, onDismiss: () -> Unit) {
    var label by remember { mutableStateOf("Focus time") }
    var startH by remember { mutableIntStateOf(9) }
    var startM by remember { mutableIntStateOf(0) }
    var endH by remember { mutableIntStateOf(12) }
    var endM by remember { mutableIntStateOf(0) }
    var weekdays by remember { mutableStateOf(true) }
    var weekends by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Focus Hour") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(value = label, onValueChange = { label = it }, label = { Text("Label") }, singleLine = true)

                Text("Start: %02d:%02d".format(startH, startM), fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Column(Modifier.weight(1f)) {
                        Text("Hour", fontSize = 11.sp)
                        Slider(value = startH.toFloat(), onValueChange = { startH = it.toInt() }, valueRange = 0f..23f, steps = 22)
                    }
                    Column(Modifier.weight(1f)) {
                        Text("Min", fontSize = 11.sp)
                        Slider(value = startM.toFloat(), onValueChange = { startM = (it / 5).toInt() * 5 }, valueRange = 0f..55f, steps = 10)
                    }
                }

                Text("End: %02d:%02d".format(endH, endM), fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Column(Modifier.weight(1f)) {
                        Text("Hour", fontSize = 11.sp)
                        Slider(value = endH.toFloat(), onValueChange = { endH = it.toInt() }, valueRange = 0f..23f, steps = 22)
                    }
                    Column(Modifier.weight(1f)) {
                        Text("Min", fontSize = 11.sp)
                        Slider(value = endM.toFloat(), onValueChange = { endM = (it / 5).toInt() * 5 }, valueRange = 0f..55f, steps = 10)
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = weekdays, onCheckedChange = { weekdays = it })
                    Text("Weekdays")
                    Spacer(Modifier.width(16.dp))
                    Checkbox(checked = weekends, onCheckedChange = { weekends = it })
                    Text("Weekends")
                }
            }
        },
        confirmButton = {
            Button(onClick = {
                onAdd(FocusHour(startHour = startH, startMinute = startM, endHour = endH, endMinute = endM,
                    label = label, appliesWeekdays = weekdays, appliesWeekends = weekends))
            }) { Text("Add") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}

private fun isAccessibilityEnabled(context: Context): Boolean {
    val am = context.getSystemService(Context.ACCESSIBILITY_SERVICE) as AccessibilityManager
    val services = am.getEnabledAccessibilityServiceList(AccessibilityServiceInfo.FEEDBACK_GENERIC)
    return services.any { it.resolveInfo.serviceInfo.packageName == context.packageName }
}
