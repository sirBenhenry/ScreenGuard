package com.sirbenhenry.screenguard

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sirbenhenry.screenguard.ui.screens.*
import com.sirbenhenry.screenguard.ui.theme.ScreenGuardTheme
import com.sirbenhenry.screenguard.util.ReleaseInfo
import com.sirbenhenry.screenguard.util.UpdateChecker
import com.sirbenhenry.screenguard.viewmodel.MainViewModel
import kotlinx.coroutines.launch

data class NavItem(val label: String, val icon: ImageVector)

class MainActivity : ComponentActivity() {
    private val vm: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ScreenGuardTheme {
                ScreenGuardApp(vm)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ScreenGuardApp(vm: MainViewModel) {
    val scope = rememberCoroutineScope()
    var currentTab by remember { mutableIntStateOf(0) }
    val homeState by vm.homeState.collectAsStateWithLifecycle()
    val statsState by vm.statsState.collectAsStateWithLifecycle()
    val monitoredApps by vm.monitoredApps.collectAsStateWithLifecycle()
    val goodApps by vm.goodApps.collectAsStateWithLifecycle()

    var updateInfo by remember { mutableStateOf<ReleaseInfo?>(null) }
    var updateProgress by remember { mutableIntStateOf(-1) }
    var showUpdateDialog by remember { mutableStateOf(false) }

    val navItems = listOf(
        NavItem("Home", Icons.Default.Home),
        NavItem("Stats", Icons.Default.BarChart),
        NavItem("Apps", Icons.Default.Apps),
        NavItem("Settings", Icons.Default.Settings)
    )

    fun checkUpdate() {
        scope.launch {
            val info = UpdateChecker.checkForUpdate(vm.getApplication())
            if (info != null) {
                updateInfo = info
                showUpdateDialog = true
            }
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            NavigationBar {
                navItems.forEachIndexed { index, item ->
                    NavigationBarItem(
                        selected = currentTab == index,
                        onClick = { currentTab = index },
                        icon = { Icon(item.icon, item.label) },
                        label = { Text(item.label) }
                    )
                }
            }
        }
    ) { padding ->
        Box(Modifier.padding(padding)) {
            when (currentTab) {
                0 -> HomeScreen(homeState, statsState.streakRecords)
                1 -> StatsScreen(statsState)
                2 -> AppsScreen(
                    monitoredApps = monitoredApps,
                    goodApps = goodApps,
                    onAddMonitored = vm::addMonitoredApp,
                    onRemoveMonitored = vm::removeMonitoredApp,
                    onUpdateMonitored = vm::updateMonitoredApp,
                    onAddGood = vm::addGoodApp,
                    onRemoveGood = vm::removeGoodApp
                )
                3 -> SettingsScreen(onCheckUpdate = ::checkUpdate)
            }
        }
    }

    // Update dialog
    if (showUpdateDialog && updateInfo != null) {
        AlertDialog(
            onDismissRequest = { showUpdateDialog = false },
            title = { Text("Update available: v${updateInfo!!.version}") },
            text = {
                Column {
                    if (updateInfo!!.releaseNotes.isNotBlank()) {
                        Text(updateInfo!!.releaseNotes.take(300), style = MaterialTheme.typography.bodySmall)
                        Spacer(Modifier.height(8.dp))
                    }
                    if (updateProgress >= 0) {
                        Text("Downloading: $updateProgress%")
                        LinearProgressIndicator(
                            progress = { updateProgress / 100f },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            },
            confirmButton = {
                if (updateProgress < 0) {
                    Button(onClick = {
                        scope.launch {
                            UpdateChecker.downloadAndInstall(
                                vm.getApplication(),
                                updateInfo!!.apkUrl
                            ) { updateProgress = it }
                        }
                    }) { Text("Update now") }
                }
            },
            dismissButton = { TextButton(onClick = { showUpdateDialog = false }) { Text("Later") } }
        )
    }
}
