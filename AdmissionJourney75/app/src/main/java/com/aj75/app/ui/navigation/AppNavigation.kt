package com.aj75.app.ui.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Checklist
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.aj75.app.ui.calendar.CalendarScreen
import com.aj75.app.ui.home.HomeScreen
import com.aj75.app.ui.progress.ProgressScreen
import com.aj75.app.ui.routine.RoutineScreen
import com.aj75.app.ui.settings.SettingsScreen
import com.aj75.app.ui.tools.ToolsScreen

sealed class Tab(val route: String, val label: String, val icon: ImageVector) {
    data object Home : Tab("home", "Home", Icons.Filled.Home)
    data object Routine : Tab("routine", "Routine", Icons.Filled.Checklist)
    data object Progress : Tab("progress", "Progress", Icons.Filled.BarChart)
    data object Calendar : Tab("calendar", "75 Days", Icons.Filled.CalendarMonth)
    data object Tools : Tab("tools", "Tools", Icons.Filled.Timer)
    data object Settings : Tab("settings", "Settings", Icons.Filled.Settings)
}

private val tabs = listOf(Tab.Home, Tab.Routine, Tab.Progress, Tab.Calendar, Tab.Tools, Tab.Settings)

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    Scaffold(
        containerColor = Color.Transparent,
        bottomBar = {
            val backStackEntry by navController.currentBackStackEntryAsState()
            val currentDestination = backStackEntry?.destination
            FloatingGlassTabBar(
                tabs = tabs,
                isSelected = { tab -> currentDestination?.hierarchy?.any { it.route == tab.route } == true },
                onTabSelected = { tab ->
                    navController.navigate(tab.route) {
                        popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
            )
        },
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Tab.Home.route,
            modifier = Modifier.padding(innerPadding),
            enterTransition = { fadeIn(tween(240)) + slideInVertically(tween(240)) { height -> height / 14 } },
            exitTransition = { fadeOut(tween(140)) },
            popEnterTransition = { fadeIn(tween(240)) },
            popExitTransition = { fadeOut(tween(140)) },
        ) {
            composable(Tab.Home.route) { HomeScreen(onNavigateToTab = { route -> navController.navigate(route) }) }
            composable(Tab.Routine.route) { RoutineScreen() }
            composable(Tab.Progress.route) { ProgressScreen() }
            composable(Tab.Calendar.route) { CalendarScreen() }
            composable(Tab.Tools.route) { ToolsScreen() }
            composable(Tab.Settings.route) { SettingsScreen() }
        }
    }
}

/** A floating, frosted-glass tab bar (iOS-style) instead of a flush Material NavigationBar. */
@Composable
private fun FloatingGlassTabBar(
    tabs: List<Tab>,
    isSelected: (Tab) -> Boolean,
    onTabSelected: (Tab) -> Unit,
) {
    val shape = RoundedCornerShape(30.dp)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = 14.dp, vertical = 10.dp)
            .shadow(elevation = 18.dp, shape = shape, ambientColor = Color.Black.copy(alpha = 0.45f), spotColor = Color.Black.copy(alpha = 0.45f))
            .clip(shape)
            .background(Brush.verticalGradient(listOf(Color(0xFF1E293B).copy(alpha = 0.92f), Color(0xFF0B1220).copy(alpha = 0.97f))))
            .border(1.dp, Brush.verticalGradient(listOf(Color(0x40FFFFFF), Color(0x14FFFFFF))), shape)
            .padding(vertical = 8.dp, horizontal = 4.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        tabs.forEach { tab ->
            val selected = isSelected(tab)
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(18.dp))
                    .background(if (selected) MaterialTheme.colorScheme.primary.copy(alpha = 0.18f) else Color.Transparent)
                    .clickable { onTabSelected(tab) }
                    .padding(vertical = 8.dp),
            ) {
                Icon(
                    tab.icon,
                    contentDescription = tab.label,
                    tint = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(22.dp),
                )
                Text(
                    tab.label,
                    fontSize = 9.sp,
                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                    color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                )
            }
        }
    }
}
