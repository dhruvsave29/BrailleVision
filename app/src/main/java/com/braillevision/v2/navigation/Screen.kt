package com.braillevision.v2.navigation

import androidx.annotation.StringRes
import com.braillevision.v2.R

sealed class Screen(
    val route: String,
    @StringRes val titleRes: Int
) {
    data object Camera : Screen(
        route = "camera",
        titleRes = R.string.app_name
    )

    data object Result : Screen(
        route = "result",
        titleRes = R.string.recognized_text
    )

    data object History : Screen(
        route = "history",
        titleRes = R.string.history
    )

    data object Settings : Screen(
        route = "settings",
        titleRes = R.string.settings
    )

    companion object {
        val screens = listOf(Camera, Result, History, Settings)
        
        fun fromRoute(route: String?): Screen {
            return when (route?.substringBefore("/")) {
                Camera.route -> Camera
                Result.route -> Result
                History.route -> History
                Settings.route -> Settings
                else -> Camera
            }
        }
    }
}
