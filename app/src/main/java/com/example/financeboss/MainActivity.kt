package com.example.financeboss

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier

import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel


import androidx.navigation.compose.currentBackStackEntryAsState


import androidx.navigation.compose.rememberNavController
import com.example.financeboss.di.AppContainer
import com.example.financeboss.ui.navigation.NavGraph
import com.example.financeboss.ui.navigation.Screen
import com.example.financeboss.ui.theme.FinanceBossTheme



data class BottomNavItem(val screen: Screen, val label: String, val icon: ImageVector)



class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val container = (application as FinanceApplication).container

        setContent {

            val mainViewModel: MainViewModel = viewModel(
                factory = MainViewModelFactory(
                    container.userPreferences,
                    container.transactionRepository,
                    container.goalRepository
                )
            )


            val isDark by mainViewModel.isDarkMode.collectAsStateWithLifecycle(false)


            FinanceBossTheme(darkTheme = isDark) {
                MainScreen(
                    container=container,
                    isDarkMode = isDark,
                    onToggleDarkMode = {mainViewModel.toggleDarkMode(!isDark)}
                )
            }
        }
    }
}



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    container: AppContainer,
    isDarkMode: Boolean,
    onToggleDarkMode: () -> Unit
) {
    val navController = rememberNavController()
    val navBackStack by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStack?.destination?.route

    val bottomNavItems = listOf(
        BottomNavItem(Screen.Home, "Home", Icons.Filled.Home),
        BottomNavItem(Screen.Transactions, "Transactions", Icons.Filled.Receipt),
        BottomNavItem(Screen.Insights, "Insights", Icons.Filled.BarChart),
        BottomNavItem(Screen.Goals, "Goals", Icons.Filled.Flag)
    )

    val showBottomBar = bottomNavItems.any { it.screen.route == currentRoute }

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    bottomNavItems.forEach { item ->
                        NavigationBarItem(
                            selected = currentRoute == item.screen.route,
                            onClick = {
                                navController.navigate(item.screen.route) {
                                    popUpTo(Screen.Home.route) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = { Icon(item.icon, contentDescription = item.label) },
                            label = { Text(item.label) }
                        )
                    }
                }
            }
        }
    ) {  padding ->
        Box(modifier = Modifier.padding(padding)) {
            NavGraph(
                navController = navController,
                container = container,
                isDarkMode = isDarkMode,
                onToggleDarkMode = onToggleDarkMode,
                modifier = Modifier.padding(padding)
            )
        }
    }
}