package com.example.nevil_watch.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.nevil_watch.screens.*
import com.example.nevil_watch.viewmodel.HomeViewModel
import androidx.lifecycle.viewmodel.compose.viewModel

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Categories : Screen("categories")
    object Cart : Screen("cart")
    object Profile : Screen("profile")
    object Settings : Screen("settings")
    object ShippingAddress : Screen("shipping_address")
    object WatchDetail : Screen("watch_detail/{watchId}") {
        fun createRoute(watchId: String) = "watch_detail/$watchId"
    }
    object CategoryWatches : Screen("category_watches/{categoryId}") {
        fun createRoute(categoryId: String) = "category_watches/$categoryId"
    }
}

@Composable
fun AppNavigation(
    navController: NavHostController,
    onSignOut: () -> Unit
) {
    val homeViewModel: HomeViewModel = viewModel()
    
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ) {
        composable(Screen.Home.route) {
            HomeScreen(
                onWatchClick = { watchId ->
                    navController.navigate(Screen.WatchDetail.createRoute(watchId))
                }
            )
        }
        
        composable(Screen.Categories.route) {
            CategoryScreen(
                onCategoryClick = { categoryId ->
                    navController.navigate(Screen.CategoryWatches.createRoute(categoryId))
                }
            )
        }
        
        composable(Screen.Cart.route) {
            CartScreen(
                onCheckout = {
                    // Handle checkout navigation here
                }
            )
        }
        
        composable(Screen.Profile.route) {
            ProfileScreen(
                onSignOut = onSignOut,
                onSettingsClick = {
                    navController.navigate(Screen.Settings.route)
                },
                onShippingAddressClick = {
                    navController.navigate(Screen.ShippingAddress.route)
                }
            )
        }

        composable(Screen.Settings.route) {
            SettingsScreen(
                onNavigateBack = { navController.navigateUp() }
            )
        }
        
        composable(Screen.ShippingAddress.route) {
            ShippingAddressScreen(
                onNavigateBack = { navController.navigateUp() }
            )
        }
        
        composable(
            route = Screen.WatchDetail.route,
            arguments = listOf(
                navArgument("watchId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val watchId = backStackEntry.arguments?.getString("watchId") ?: return@composable
            WatchDetailScreen(
                watchId = watchId,
                onBackClick = { navController.navigateUp() }
            )
        }
        
        composable(
            route = Screen.CategoryWatches.route,
            arguments = listOf(
                navArgument("categoryId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val categoryId = backStackEntry.arguments?.getString("categoryId") ?: return@composable
            HomeScreen(
                categoryId = categoryId,
                onWatchClick = { watchId ->
                    navController.navigate(Screen.WatchDetail.createRoute(watchId))
                }
            )
        }
    }
} 