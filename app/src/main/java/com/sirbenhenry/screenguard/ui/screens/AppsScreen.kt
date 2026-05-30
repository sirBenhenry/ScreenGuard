package com.sirbenhenry.screenguard.ui.screens

import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sirbenhenry.screenguard.data.entity.GoodApp
import com.sirbenhenry.screenguard.data.entity.MonitoredApp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

data class InstalledApp(val packageName: String, val appName: String)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppsScreen(
    monitoredApps: List<MonitoredApp>,
    goodApps: List<GoodApp>,
    onAddMonitored: (MonitoredApp) -> Unit,
    onRemoveMonitored: (MonitoredApp) -> Unit,
    onUpdateMonitored: (MonitoredApp) -> Unit,
    onAddGood: (GoodApp) -> Unit,
    onRemoveGood: (GoodApp) -> Unit
) {
    val context = LocalContext.current
    var installedApps by remember { mutableStateOf<List<InstalledApp>>(emptyList()) }
    var showAddMonitored by remember { mutableStateOf(false) }
    var showAddGood by remember { mutableStateOf(false) }
    var editApp by remember { mutableStateOf<MonitoredApp?>(null) }
    var removeConfirm by remember { mutableStateOf<MonitoredApp?>(null) }
    var tab by remember { mutableIntStateOf(0) }

    LaunchedEffect(Unit) {
        installedApps = withContext(Dispatchers.IO) {
            val pm = context.packageManager
            pm.getInstalledApplications(PackageManager.GET_META_DATA)
                .filter { it.flags and ApplicationInfo.FLAG_SYSTEM == 0 }
                .mapNotNull { info ->
                    val name = pm.getApplicationLabel(info).toString()
                    InstalledApp(info.packageName, name)
                }
                .sortedBy { it.appName }
        }
    }

    Column(Modifier.fillMaxSize()) {
        // Tab row
        TabRow(selectedTabIndex = tab, containerColor = MaterialTheme.colorScheme.surface) {
            Tab(selected = tab == 0, onClick = { tab = 0 }, text = { Text("Monitored") })
            Tab(selected = tab == 1, onClick = { tab = 1 }, text = { Text("Good Apps") })
        }

        when (tab) {
            0 -> MonitoredAppsTab(
                apps = monitoredApps,
                onAdd = { showAddMonitored = true },
                onEdit = { editApp = it },
                onRemoveRequest = { removeConfirm = it }
            )
            1 -> GoodAppsTab(
                apps = goodApps,
                onAdd = { showAddGood = true },
                onRemove = onRemoveGood
            )
        }
    }

    // Add monitored app dialog
    if (showAddMonitored) {
        AppPickerDialog(
            title = "Add monitored app",
            installed = installedApps,
            alreadyPicked = monitoredApps.map { it.packageName },
            onPick = { app ->
                onAddMonitored(MonitoredApp(
                    packageName = app.packageName,
                    appName = app.appName
                ))
                showAddMonitored = false
            },
            onDismiss = { showAddMonitored = false }
        )
    }

    // Add good app dialog
    if (showAddGood) {
        AppPickerDialog(
            title = "Add alternative app",
            installed = installedApps,
            alreadyPicked = goodApps.map { it.packageName },
            onPick = { app ->
                onAddGood(GoodApp(packageName = app.packageName, appName = app.appName))
                showAddGood = false
            },
            onDismiss = { showAddGood = false }
        )
    }

    // Edit monitored app dialog
    editApp?.let { app ->
        EditMonitoredDialog(
            app = app,
            onSave = { updated -> onUpdateMonitored(updated); editApp = null },
            onDismiss = { editApp = null }
        )
    }

    // Confirm remove — requires hold to delete (makes it hard to remove accidentally)
    removeConfirm?.let { app ->
        RemoveConfirmDialog(
            appName = app.appName,
            onConfirm = { onRemoveMonitored(app); removeConfirm = null },
            onDismiss = { removeConfirm = null }
        )
    }
}

@Composable
private fun MonitoredAppsTab(
    apps: List<MonitoredApp>,
    onAdd: () -> Unit,
    onEdit: (MonitoredApp) -> Unit,
    onRemoveRequest: (MonitoredApp) -> Unit
) {
    Box(Modifier.fillMaxSize()) {
        if (apps.isEmpty()) {
            Column(
                Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text("🚫", fontSize = 48.sp)
                Spacer(Modifier.height(12.dp))
                Text("No monitored apps", fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onSurface)
                Spacer(Modifier.height(4.dp))
                Text("Add Instagram, YouTube, TikTok...", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        } else {
            LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                items(apps, key = { it.packageName }) { app ->
                    MonitoredAppCard(app, onEdit, onRemoveRequest)
                }
                item { Spacer(Modifier.height(72.dp)) }
            }
        }

        FloatingActionButton(
            onClick = onAdd,
            modifier = Modifier.align(Alignment.BottomEnd).padding(24.dp),
            containerColor = MaterialTheme.colorScheme.primary
        ) {
            Icon(Icons.Default.Add, "Add app")
        }
    }
}

@Composable
private fun MonitoredAppCard(
    app: MonitoredApp,
    onEdit: (MonitoredApp) -> Unit,
    onRemove: (MonitoredApp) -> Unit
) {
    Surface(shape = RoundedCornerShape(14.dp), color = MaterialTheme.colorScheme.surface, modifier = Modifier.fillMaxWidth()) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text(app.appName, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                Spacer(Modifier.height(2.dp))
                Text(
                    "Weekday: ${app.dailyLimitMinutes}m · Weekend: ${app.weekendLimitMinutes}m · Cooldown: ${app.baseCooldownSeconds}s",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            IconButton(onClick = { onEdit(app) }) {
                Icon(Icons.Default.Edit, "Edit", tint = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            IconButton(onClick = { onRemove(app) }) {
                Icon(Icons.Default.Delete, "Remove", tint = Color(0xFFFF6666))
            }
        }
    }
}

@Composable
private fun GoodAppsTab(
    apps: List<GoodApp>,
    onAdd: () -> Unit,
    onRemove: (GoodApp) -> Unit
) {
    Box(Modifier.fillMaxSize()) {
        Column(Modifier.padding(16.dp)) {
            Text(
                "These appear on the cooldown screen as alternatives to scrolling.",
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(12.dp))
            apps.forEach { app ->
                Surface(shape = RoundedCornerShape(12.dp), color = MaterialTheme.colorScheme.surface, modifier = Modifier.fillMaxWidth()) {
                    Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                        Text(app.appName, Modifier.weight(1f), color = MaterialTheme.colorScheme.onSurface)
                        IconButton(onClick = { onRemove(app) }) {
                            Icon(Icons.Default.Close, "Remove", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
                Spacer(Modifier.height(8.dp))
            }
        }

        FloatingActionButton(
            onClick = onAdd,
            modifier = Modifier.align(Alignment.BottomEnd).padding(24.dp),
            containerColor = MaterialTheme.colorScheme.secondary
        ) {
            Icon(Icons.Default.Add, "Add good app")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AppPickerDialog(
    title: String,
    installed: List<InstalledApp>,
    alreadyPicked: List<String>,
    onPick: (InstalledApp) -> Unit,
    onDismiss: () -> Unit
) {
    var query by remember { mutableStateOf("") }
    val filtered = installed.filter {
        it.packageName !in alreadyPicked &&
        (query.isBlank() || it.appName.contains(query, ignoreCase = true))
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            Column {
                OutlinedTextField(
                    value = query,
                    onValueChange = { query = it },
                    placeholder = { Text("Search apps...") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                Spacer(Modifier.height(8.dp))
                LazyColumn(modifier = Modifier.heightIn(max = 320.dp)) {
                    items(filtered) { app ->
                        ListItem(
                            headlineContent = { Text(app.appName) },
                            supportingContent = { Text(app.packageName, fontSize = 11.sp) },
                            modifier = Modifier.clickable { onPick(app) }
                        )
                        HorizontalDivider()
                    }
                }
            }
        },
        confirmButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EditMonitoredDialog(
    app: MonitoredApp,
    onSave: (MonitoredApp) -> Unit,
    onDismiss: () -> Unit
) {
    var limitMin by remember { mutableIntStateOf(app.dailyLimitMinutes) }
    var weekendMin by remember { mutableIntStateOf(app.weekendLimitMinutes) }
    var cooldownSecs by remember { mutableIntStateOf(app.baseCooldownSeconds) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(app.appName) },
        text = {
            Column {
                Text("Weekday limit: ${limitMin}m", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Slider(value = limitMin.toFloat(), onValueChange = { limitMin = it.toInt() }, valueRange = 5f..180f, steps = 34)

                Spacer(Modifier.height(8.dp))
                Text("Weekend limit: ${weekendMin}m", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Slider(value = weekendMin.toFloat(), onValueChange = { weekendMin = it.toInt() }, valueRange = 5f..240f, steps = 46)

                Spacer(Modifier.height(8.dp))
                Text("Base cooldown: ${cooldownSecs}s", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text("(escalates each open: ${cooldownSecs}s → ${(cooldownSecs*1.5).toInt()}s → ${cooldownSecs*2}s → ${(cooldownSecs*2.5).toInt()}s)", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Slider(value = cooldownSecs.toFloat(), onValueChange = { cooldownSecs = it.toInt() }, valueRange = 10f..120f, steps = 22)
            }
        },
        confirmButton = {
            TextButton(onClick = { onSave(app.copy(dailyLimitMinutes = limitMin, weekendLimitMinutes = weekendMin, baseCooldownSeconds = cooldownSecs)) }) {
                Text("Save")
            }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}

@Composable
private fun RemoveConfirmDialog(appName: String, onConfirm: () -> Unit, onDismiss: () -> Unit) {
    var holdProgress by remember { mutableFloatStateOf(0f) }
    var holding by remember { mutableStateOf(false) }

    LaunchedEffect(holding) {
        if (holding) {
            val steps = 30
            repeat(steps) {
                kotlinx.coroutines.delay(50)
                holdProgress = (it + 1).toFloat() / steps
            }
            onConfirm()
        } else {
            holdProgress = 0f
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Remove $appName?") },
        text = {
            Column {
                Text(
                    "Removing makes it easy to scroll without limits. Are you sure?",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.height(16.dp))
                Text("Hold button to confirm:", fontSize = 12.sp, color = Color(0xFFFF8888))
                Spacer(Modifier.height(8.dp))
                LinearProgressIndicator(
                    progress = { holdProgress },
                    modifier = Modifier.fillMaxWidth(),
                    color = Color(0xFFFF4444)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {},
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3A1010)),
                modifier = Modifier
                    .pointerInput(Unit) {
                        awaitPointerEventScope {
                            while (true) {
                                val event = awaitPointerEvent()
                                holding = event.changes.any { it.pressed }
                            }
                        }
                    }
            ) { Text("Hold to remove") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Keep it") } }
    )
}
