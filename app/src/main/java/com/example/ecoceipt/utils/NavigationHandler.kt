package com.example.tim_sam_2.utils

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.ecoceipt.ui.views.DashboardView
import com.example.ecoceipt.ui.views.ItemListView
import com.example.ecoceipt.ui.views.ProfileView
import com.example.ecoceipt.ui.views.ScanView
import com.example.ecoceipt.viewmodels.DashboardViewModel
import com.example.ecoceipt.viewmodels.ProfileViewModel
import androidx.lifecycle.viewmodel.compose.viewModel


// Define your navigation routes
sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    object Dashboard : Screen("dashboard", "Dashboard", Icons.Filled.Dashboard)
    object Scan : Screen("scan", "Scan", Icons.Filled.QrCodeScanner)
    object Items : Screen("items", "Items", Icons.Filled.List)
}

// List of bottom navigation items
val bottomNavItems = listOf(
    Screen.Dashboard,
    Screen.Scan,
    Screen.Items
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = {
            BottomNavigationBar(navController = navController)
        }
    ) { innerPadding ->
        NavigationGraph(
            navController = navController,
            modifier = Modifier.padding(innerPadding)
        )
    }
}

@Composable
fun BottomNavigationBar(navController: NavController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        bottomNavItems.forEach { screen ->
            NavigationBarItem(
                icon = { Icon(screen.icon, contentDescription = screen.title) },
                label = { Text(screen.title) },
                selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.primary,
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                    unselectedIconColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    unselectedTextColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    indicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                ),
                onClick = {
                    navController.navigate(screen.route) {
                        // This logic is correct. It pops the back stack to the start
                        // destination (Dashboard) before navigating. This ensures you
                        // don't build up a large stack of screens.
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    }
}

@Composable
fun NavigationGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Dashboard.route,
        modifier = modifier
    ) {
        composable(Screen.Dashboard.route) {
            val viewModel: DashboardViewModel = viewModel()
            DashboardView(navController = navController, viewModel = viewModel)
        }
        composable(Screen.Scan.route) {
            ScanView()
        }
        composable(Screen.Items.route) {
            ItemListView()
        }

        composable("profile") {
            // CRITICAL FIX: You MUST pass the navController to the ProfileView.
            // This connects it to the main navigation graph, allowing the
            // BottomNavigationBar to correctly pop it off the stack.
            val viewModel: ProfileViewModel = viewModel()
            ProfileView(navController = navController, viewModel = viewModel)
        }
    }
}