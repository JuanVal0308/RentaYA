package com.rentaya.app.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.rentaya.app.data.UserPreferences
import com.rentaya.app.ui.screens.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RentaYaApp(
    userPreferences: UserPreferences,
    isLoggedIn: Boolean
) {
    val navController = rememberNavController()
    val currentRoute by navController.currentBackStackEntryAsState()
    val route = currentRoute?.destination?.route

    val showBottomBar = route in listOf(
        Screen.Search.route,
        Screen.Favorites.route,
        Screen.Messages.route,
        Screen.Profile.route
    )

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.Search, "Buscar") },
                        label = { Text("Buscar") },
                        selected = route == Screen.Search.route,
                        onClick = {
                            navController.navigate(Screen.Search.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.Favorite, "Favoritos") },
                        label = { Text("Favoritos") },
                        selected = route == Screen.Favorites.route,
                        onClick = {
                            navController.navigate(Screen.Favorites.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.Chat, "Chat") },
                        label = { Text("Chat") },
                        selected = route == Screen.Messages.route,
                        onClick = {
                            navController.navigate(Screen.Messages.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.Person, "Perfil") },
                        label = { Text("Perfil") },
                        selected = route == Screen.Profile.route,
                        onClick = {
                            navController.navigate(Screen.Profile.route) {
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
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = if (isLoggedIn) Screen.Search.route else Screen.Onboarding.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Onboarding.route) {
                OnboardingScreen(
                    onStart = {
                        navController.navigate(Screen.Login.route) {
                            popUpTo(Screen.Onboarding.route) { inclusive = true }
                        }
                    },
                    onLogin = {
                        navController.navigate(Screen.Login.route)
                    }
                )
            }
            
            composable(Screen.Login.route) {
                LoginScreen(
                    onLoginSuccess = {
                        navController.navigate(Screen.Search.route) {
                            popUpTo(0) { inclusive = true }
                        }
                    },
                    onRegister = {
                        navController.navigate(Screen.Register.route)
                    },
                    userPreferences = userPreferences
                )
            }
            
            composable(Screen.Register.route) {
                RegisterScreen(
                    onRegisterSuccess = {
                        navController.navigate(Screen.Search.route) {
                            popUpTo(0) { inclusive = true }
                        }
                    },
                    onBack = { navController.navigateUp() },
                    userPreferences = userPreferences
                )
            }
            
            composable(Screen.Search.route) {
                SearchScreen(
                    onPropertyClick = { propertyId ->
                        navController.navigate(Screen.Detail.createRoute(propertyId))
                    },
                    onFilterResults = {
                        navController.navigate(Screen.Results.route)
                    }
                )
            }
            
            composable(Screen.Results.route) {
                ResultsScreen(
                    onPropertyClick = { propertyId ->
                        navController.navigate(Screen.Detail.createRoute(propertyId))
                    },
                    onBack = { navController.navigateUp() }
                )
            }
            
            composable(
                route = Screen.Detail.route,
                arguments = listOf(navArgument("propertyId") { type = NavType.StringType })
            ) { backStackEntry ->
                val propertyId = backStackEntry.arguments?.getString("propertyId") ?: ""
                DetailScreen(
                    propertyId = propertyId,
                    onBack = { navController.navigateUp() },
                    onContact = { propertyId ->
                        navController.navigate(Screen.Chat.createRoute(propertyId))
                    }
                )
            }
            
            composable(Screen.Favorites.route) {
                FavoritesScreen(
                    onPropertyClick = { propertyId ->
                        navController.navigate(Screen.Detail.createRoute(propertyId))
                    }
                )
            }
            
            composable(Screen.Messages.route) {
                MessagesScreen(
                    onChatClick = { propertyId ->
                        navController.navigate(Screen.Chat.createRoute(propertyId))
                    },
                    onSearch = {
                        // Misma navegación que la pestaña "Buscar" de la barra inferior.
                        navController.navigate(Screen.Search.route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
            
            composable(
                route = Screen.Chat.route,
                arguments = listOf(navArgument("propertyId") { type = NavType.StringType })
            ) { backStackEntry ->
                val propertyId = backStackEntry.arguments?.getString("propertyId") ?: ""
                ChatScreen(
                    propertyId = propertyId,
                    onBack = { navController.navigateUp() }
                )
            }
            
            composable(Screen.Profile.route) {
                ProfileScreen(
                    onSettings = {
                        navController.navigate(Screen.Settings.route)
                    },
                    onCredits = {
                        navController.navigate(Screen.Credits.route)
                    },
                    onPublish = {
                        navController.navigate(Screen.PublishProperty.route)
                    },
                    onMyProperties = {
                        navController.navigate(Screen.MyProperties.route)
                    },
                    userPreferences = userPreferences
                )
            }
            
            composable(Screen.Settings.route) {
                SettingsScreen(
                    onBack = { navController.navigateUp() },
                    onLogout = {
                        navController.navigate(Screen.Onboarding.route) {
                            popUpTo(0) { inclusive = true }
                        }
                    },
                    userPreferences = userPreferences
                )
            }
            
            composable(Screen.Credits.route) {
                CreditsScreen(
                    onBack = { navController.navigateUp() }
                )
            }
            
            composable(Screen.PublishProperty.route) {
                PublishPropertyScreen(
                    onBack = { navController.navigateUp() },
                    onSuccess = { navController.navigateUp() }
                )
            }

            composable(Screen.MyProperties.route) {
                MyPropertiesScreen(
                    onBack = { navController.navigateUp() },
                    onPropertyClick = { propertyId ->
                        navController.navigate(Screen.Detail.createRoute(propertyId))
                    },
                    onPublish = {
                        navController.navigate(Screen.PublishProperty.route)
                    }
                )
            }
        }
    }
}
