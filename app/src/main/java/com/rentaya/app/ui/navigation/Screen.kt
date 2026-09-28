package com.rentaya.app.ui.navigation

sealed class Screen(val route: String) {
    object Onboarding : Screen("onboarding")
    object Login : Screen("login")
    object Register : Screen("register")
    object Search : Screen("search")
    object Results : Screen("results")
    object Detail : Screen("detail/{propertyId}") {
        fun createRoute(propertyId: String) = "detail/$propertyId"
    }
    object Favorites : Screen("favorites")
    object Messages : Screen("messages")
    object Chat : Screen("chat/{propertyId}") {
        fun createRoute(propertyId: String) = "chat/$propertyId"
    }
    object Profile : Screen("profile")
    object Settings : Screen("settings")
    object Credits : Screen("credits")
    object PublishProperty : Screen("publish_property")
}
