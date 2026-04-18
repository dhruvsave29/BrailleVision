package com.braillevision.v2.navigation

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.braillevision.v2.ui.camera.CameraScreen
import com.braillevision.v2.ui.history.HistoryScreen
import com.braillevision.v2.ui.result.ResultScreen
import com.braillevision.v2.ui.settings.SettingsScreen

@Composable
fun BrailleVisionNavGraph(
    navController: NavHostController,
    startDestination: String = Screen.Camera.route
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(route = Screen.Camera.route) {
            CameraScreen(
                onNavigateToResult = { imageUri ->
                    navController.navigate("${Screen.Result.route}?imageUri=${Uri.encode(imageUri.toString())}")
                },
                onNavigateToHistory = {
                    navController.navigate(Screen.History.route)
                },
                onNavigateToSettings = {
                    navController.navigate(Screen.Settings.route)
                }
            )
        }

        composable(
            route = "${Screen.Result.route}?imageUri={imageUri}",
            arguments = listOf(
                navArgument("imageUri") { 
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                }
            )
        ) { backStackEntry ->
            val imageUriString = backStackEntry.arguments?.getString("imageUri")
            val imageUri = remember(imageUriString) {
                imageUriString?.let { Uri.parse(it) }
            }
            
            ResultScreen(
                imageUri = imageUri,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(route = Screen.History.route) {
            HistoryScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToResult = { imageUri ->
                    navController.navigate("${Screen.Result.route}?imageUri=${Uri.encode(imageUri.toString())}")
                }
            )
        }

        composable(route = Screen.Settings.route) {
            SettingsScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}
