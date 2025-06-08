package com.example.nevil_watch.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.nevil_watch.navigation.AppNavigation
import com.example.nevil_watch.navigation.BottomNavItem
import com.example.nevil_watch.navigation.Screen
import com.example.nevil_watch.components.StatusBar
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    onSignOut: () -> Unit = {}
) {
    val navController = rememberNavController()
    var showSignInMessage by remember { mutableStateOf(true) }
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(showSignInMessage) {
        if (showSignInMessage) {
            snackbarHostState.showSnackbar(
                message = "Welcome to Nevil Watch Palace!",
                duration = SnackbarDuration.Short
            )
            showSignInMessage = false
        }
    }
    
    Scaffold(
        topBar = { 
            Box(modifier = Modifier.padding(top = 8.dp)) {
                StatusBar()
            }
        },
        bottomBar = { BottomNavigationBar(navController = navController) },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            AppNavigation(
                navController = navController,
                onSignOut = onSignOut
            )
        }
    }
}

@Composable
fun BottomNavigationBar(navController: NavHostController) {
    NavigationBar {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route?.substringBefore('/')

        BottomNavItem.items.forEach { item ->
            NavigationBarItem(
                icon = { Icon(item.icon, contentDescription = item.title) },
                label = { Text(text = item.title) },
                selected = currentRoute == item.route,
                onClick = {
                    navController.navigate(item.route) {
                        navController.graph.startDestinationRoute?.let { route ->
                            popUpTo(route) {
                                saveState = true
                            }
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    }
} 