package com.example

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.FilterVintage
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.PrivacyTip
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.StarRate
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Divider
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.MalaViewModel
import com.example.ui.screens.CounterScreen
import com.example.ui.screens.InsightsScreen
import com.example.ui.screens.MantrasScreen
import com.example.ui.screens.PrivacyPolicyScreen
import com.example.ui.theme.MyApplicationTheme
import kotlinx.coroutines.launch

enum class MalaScreen(val title: String) {
    COUNTER("Jaap Counter"),
    MANTRAS("Mantra Goals"),
    INSIGHTS("Daily Insights"),
    PRIVACY_POLICY("Privacy Policy")
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                MalaJaapApp()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MalaJaapApp(viewModel: MalaViewModel = viewModel()) {
    var currentScreen by remember { mutableStateOf(MalaScreen.COUNTER) }
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    val activeMantra by viewModel.activeMantra.collectAsStateWithLifecycle()
    val allMantras by viewModel.allMantras.collectAsStateWithLifecycle()
    val currentBead by viewModel.currentBead.collectAsStateWithLifecycle()
    val sessionMalas by viewModel.sessionMalas.collectAsStateWithLifecycle()
    val todayTotalJaaps by viewModel.todayTotalJaaps.collectAsStateWithLifecycle()
    val todayTotalMalas by viewModel.todayTotalMalas.collectAsStateWithLifecycle()
    val dailySummaries by viewModel.dailySummaries.collectAsStateWithLifecycle()
    val recentSessions by viewModel.recentSessions.collectAsStateWithLifecycle()
    val lifetimeJaaps by viewModel.lifetimeJaaps.collectAsStateWithLifecycle()
    val lifetimeMalas by viewModel.lifetimeMalas.collectAsStateWithLifecycle()
    val streak by viewModel.currentStreak.collectAsStateWithLifecycle()
    val soundEnabled by viewModel.soundEnabled.collectAsStateWithLifecycle()
    val vibrationEnabled by viewModel.vibrationEnabled.collectAsStateWithLifecycle()
    val showLyrics by viewModel.showLyrics.collectAsStateWithLifecycle()
    val showCountingHint by viewModel.showCountingHint.collectAsStateWithLifecycle()
    val justCompletedMala by viewModel.justCompletedMala.collectAsStateWithLifecycle()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                modifier = Modifier.width(300.dp)
            ) {
                // Drawer Header
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.primaryContainer)
                        .padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.FilterVintage,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(40.dp)
                    )
                    Text(
                        text = "Sadhana Pulse",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    )
                    Text(
                        text = "Spiritual Meditation & Chanting",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                    )
                }

                Divider()

                Spacer(modifier = Modifier.height(8.dp))

                // Drawer Menu Items
                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.SelfImprovement, contentDescription = null) },
                    label = { Text("Jaap Counter") },
                    selected = currentScreen == MalaScreen.COUNTER,
                    onClick = {
                        currentScreen = MalaScreen.COUNTER
                        scope.launch { drawerState.close() }
                    },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )

                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.MenuBook, contentDescription = null) },
                    label = { Text("Mantras & Goals") },
                    selected = currentScreen == MalaScreen.MANTRAS,
                    onClick = {
                        currentScreen = MalaScreen.MANTRAS
                        scope.launch { drawerState.close() }
                    },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )

                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.BarChart, contentDescription = null) },
                    label = { Text("Daily Insights") },
                    selected = currentScreen == MalaScreen.INSIGHTS,
                    onClick = {
                        currentScreen = MalaScreen.INSIGHTS
                        scope.launch { drawerState.close() }
                    },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )

                Divider(modifier = Modifier.padding(vertical = 12.dp))

                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.StarRate, contentDescription = null) },
                    label = { Text("Rate Us") },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        try {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=${context.packageName}"))
                            context.startActivity(intent)
                        } catch (_: Exception) {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=${context.packageName}"))
                            context.startActivity(intent)
                        }
                    },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )

                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.Share, contentDescription = null) },
                    label = { Text("Share App") },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        val shareIntent = Intent().apply {
                            action = Intent.ACTION_SEND
                            type = "text/plain"
                            putExtra(Intent.EXTRA_SUBJECT, "Mala Jaap - Spiritual Companion")
                            putExtra(Intent.EXTRA_TEXT, "Download Mala Jaap for your daily mantra chanting, meditation, and spiritual growth! https://play.google.com/store/apps/details?id=${context.packageName}")
                        }
                        context.startActivity(Intent.createChooser(shareIntent, "Share Mala Jaap via"))
                    },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )

                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.PrivacyTip, contentDescription = null) },
                    label = { Text("Privacy Policy") },
                    selected = currentScreen == MalaScreen.PRIVACY_POLICY,
                    onClick = {
                        currentScreen = MalaScreen.PRIVACY_POLICY
                        scope.launch { drawerState.close() }
                    },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )
            }
        }
    ) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.FilterVintage,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (currentScreen == MalaScreen.PRIVACY_POLICY) "Privacy Policy" else "Sadhana Pulse",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            )
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(
                                imageVector = Icons.Default.Menu,
                                contentDescription = "Open Navigation Menu",
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    },
                    actions = {
                        // Streak Pill (hidden on privacy policy screen for cleanliness)
                        if (currentScreen != MalaScreen.PRIVACY_POLICY) {
                            Surface(
                                shape = CircleShape,
                                color = Color(0xFFFF6D00).copy(alpha = 0.15f),
                                modifier = Modifier
                                    .padding(end = 16.dp)
                                    .testTag("streak_pill")
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                                ) {
                                    Icon(
                                        Icons.Default.LocalFireDepartment,
                                        contentDescription = "Streak",
                                        tint = Color(0xFFFF6D00),
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "$streak",
                                        style = MaterialTheme.typography.labelMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFFFF6D00),
                                            fontSize = 12.sp
                                        )
                                    )
                                }
                            }
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = MaterialTheme.colorScheme.background
                    )
                )
            },
            bottomBar = {
                if (currentScreen != MalaScreen.PRIVACY_POLICY) {
                    NavigationBar(
                            containerColor = MaterialTheme.colorScheme.surface,
                            tonalElevation = 6.dp,
                            modifier = Modifier.testTag("bottom_nav_bar")
                        ) {
                            NavigationBarItem(
                                selected = currentScreen == MalaScreen.COUNTER,
                                onClick = { currentScreen = MalaScreen.COUNTER },
                                icon = {
                                    Icon(Icons.Default.SelfImprovement, contentDescription = "Jaap Counter")
                                },
                                label = { Text("Jaap") },
                                colors = NavigationBarItemDefaults.colors(
                                    indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                                    selectedIconColor = MaterialTheme.colorScheme.primary
                                ),
                                modifier = Modifier.testTag("nav_item_counter")
                            )

                            NavigationBarItem(
                                selected = currentScreen == MalaScreen.MANTRAS,
                                onClick = { currentScreen = MalaScreen.MANTRAS },
                                icon = {
                                    Icon(Icons.Default.MenuBook, contentDescription = "Mantra Goals")
                                },
                                label = { Text("Mantras & Goals") },
                                colors = NavigationBarItemDefaults.colors(
                                    indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                                    selectedIconColor = MaterialTheme.colorScheme.primary
                                ),
                                modifier = Modifier.testTag("nav_item_mantras")
                            )

                            NavigationBarItem(
                                selected = currentScreen == MalaScreen.INSIGHTS,
                                onClick = { currentScreen = MalaScreen.INSIGHTS },
                                icon = {
                                    Icon(Icons.Default.BarChart, contentDescription = "Insights & Charts")
                                },
                                label = { Text("Insights") },
                                colors = NavigationBarItemDefaults.colors(
                                    indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                                    selectedIconColor = MaterialTheme.colorScheme.primary
                                ),
                                modifier = Modifier.testTag("nav_item_insights")
                            )
                        }
                }
            }
        ) { innerPadding ->
            when (currentScreen) {
                MalaScreen.COUNTER -> {
                    CounterScreen(
                        viewModel = viewModel,
                        activeMantra = activeMantra,
                        allMantras = allMantras,
                        currentBead = currentBead,
                        sessionMalas = sessionMalas,
                        todayTotalMalas = todayTotalMalas,
                        soundEnabled = soundEnabled,
                        vibrationEnabled = vibrationEnabled,
                        showLyrics = showLyrics,
                        showCountingHint = showCountingHint,
                        justCompletedMala = justCompletedMala,
                        onNavigateToMantras = { currentScreen = MalaScreen.MANTRAS },
                        modifier = Modifier.padding(innerPadding)
                    )
                }

                MalaScreen.MANTRAS -> {
                    MantrasScreen(
                        viewModel = viewModel,
                        mantras = allMantras,
                        activeMantra = activeMantra,
                        currentBead = currentBead,
                        onMantraSelected = {
                            currentScreen = MalaScreen.COUNTER
                        },
                        modifier = Modifier.padding(innerPadding)
                    )
                }

                MalaScreen.INSIGHTS -> {
                    InsightsScreen(
                        dailySummaries = dailySummaries,
                        recentSessions = recentSessions,
                        activeMantra = activeMantra,
                        allMantras = allMantras,
                        streak = streak,
                        todayJaaps = todayTotalJaaps,
                        todayMalas = todayTotalMalas,
                        lifetimeJaaps = lifetimeJaaps,
                        lifetimeMalas = lifetimeMalas,
                        modifier = Modifier.padding(innerPadding)
                    )
                }

                MalaScreen.PRIVACY_POLICY -> {
                    PrivacyPolicyScreen(
                        onBackClick = { currentScreen = MalaScreen.COUNTER },
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}
