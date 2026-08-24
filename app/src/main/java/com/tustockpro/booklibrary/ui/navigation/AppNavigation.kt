package com.tustockpro.booklibrary.ui.navigation

import android.net.Uri
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AutoStories
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.ManageSearch
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.tustockpro.booklibrary.BookLibraryApplication
import com.tustockpro.booklibrary.domain.model.Book
import com.tustockpro.booklibrary.ui.screens.detail.BookDetailScreen
import com.tustockpro.booklibrary.ui.screens.favorites.FavoritesScreen
import com.tustockpro.booklibrary.ui.screens.home.HomeScreen
import com.tustockpro.booklibrary.ui.screens.search.SearchScreen
import com.tustockpro.booklibrary.ui.screens.settings.SettingsScreen
import com.tustockpro.booklibrary.ui.viewmodel.BookDetailViewModel
import com.tustockpro.booklibrary.ui.viewmodel.FavoritesViewModel
import com.tustockpro.booklibrary.ui.viewmodel.HomeViewModel
import com.tustockpro.booklibrary.ui.viewmodel.SettingsViewModel

private sealed class Destination(
    val route: String,
    val label: String,
    val icon: ImageVector
) {
    data object Home : Destination(
        route = "home",
        label = "Home",
        icon = Icons.Outlined.Home
    )

    data object Search : Destination(
        route = "search",
        label = "Buscar",
        icon = Icons.Outlined.ManageSearch
    )

    data object Favorites : Destination(
        route = "favorites",
        label = "Favoritos",
        icon = Icons.Outlined.FavoriteBorder
    )

    data object Settings : Destination(
        route = "settings",
        label = "Ajustes",
        icon = Icons.Outlined.Settings
    )

    data object Detail : Destination(
        route = "detail/{bookId}/{title}/{cover}",
        label = "Detalle",
        icon = Icons.Outlined.AutoStories
    )
}

private val bottomDestinations = listOf(
    Destination.Home,
    Destination.Search,
    Destination.Favorites,
    Destination.Settings
)

@Composable
fun AppNavigation(
    application: BookLibraryApplication,
    settingsViewModel: SettingsViewModel
) {
    val navController = rememberNavController()
    val homeViewModel: HomeViewModel = viewModel(
        factory = HomeViewModel.Factory(application.repository)
    )
    val favoritesViewModel: FavoritesViewModel = viewModel(
        factory = FavoritesViewModel.Factory(application.repository)
    )

    val userName by settingsViewModel.userName.collectAsState(
        initial = "Lector"
    )
    val favoriteGenre by settingsViewModel.favoriteGenre.collectAsState(
        initial = "Fantasía"
    )
    val sessionActive by settingsViewModel.sessionActive.collectAsState(
        initial = true
    )

    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route
    val showBottomBar =
        currentRoute in bottomDestinations.map { it.route }

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    bottomDestinations.forEach { destination ->
                        NavigationBarItem(
                            selected = currentRoute == destination.route,
                            onClick = {
                                navController.navigate(destination.route) {
                                    popUpTo(
                                        navController.graph
                                            .findStartDestination().id
                                    ) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = {
                                Icon(
                                    imageVector = destination.icon,
                                    contentDescription = destination.label
                                )
                            },
                            label = {
                                Text(destination.label)
                            }
                        )
                    }
                }
            }
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = Destination.Home.route,
            modifier = Modifier
        ) {
            composable(Destination.Home.route) {
                HomeScreen(
                    modifier = Modifier,
                    viewModel = homeViewModel,
                    userName = userName,
                    favoriteGenre = favoriteGenre,
                    sessionActive = sessionActive,
                    onOpenSearch = { category ->
                        homeViewModel.selectCategory(category)
                        navController.navigate(Destination.Search.route)
                    },
                    onBookClick = { book ->
                        navController.navigate(
                            buildDetailRoute(book)
                        )
                    },
                    onFavoriteToggle = homeViewModel::toggleFavorite
                )
            }

            composable(Destination.Search.route) {
                SearchScreen(
                    modifier = Modifier,
                    viewModel = homeViewModel,
                    onBookClick = { book ->
                        navController.navigate(
                            buildDetailRoute(book)
                        )
                    },
                    onFavoriteToggle = homeViewModel::toggleFavorite
                )
            }

            composable(Destination.Favorites.route) {
                FavoritesScreen(
                    modifier = Modifier,
                    viewModel = favoritesViewModel,
                    onBookClick = { book ->
                        navController.navigate(
                            buildDetailRoute(book)
                        )
                    }
                )
            }

            composable(Destination.Settings.route) {
                SettingsScreen(
                    modifier = Modifier,
                    viewModel = settingsViewModel
                )
            }

            composable(Destination.Detail.route) { entry ->
                val bookId = Uri.decode(
                    entry.arguments?.getString("bookId").orEmpty()
                )
                val title = Uri.decode(
                    entry.arguments?.getString("title").orEmpty()
                )
                val cover = Uri.decode(
                    entry.arguments?.getString("cover").orEmpty()
                )

                val detailViewModel: BookDetailViewModel = viewModel(
                    factory = BookDetailViewModel.Factory(
                        repository = application.repository,
                        initialBook = Book(
                            bookId = bookId,
                            title = title,
                            coverUrl = cover.ifBlank { null }
                        )
                    )
                )

                BookDetailScreen(
                    modifier = Modifier,
                    viewModel = detailViewModel,
                    onBack = {
                        navController.popBackStack()
                    }
                )
            }
        }
    }
}

private fun buildDetailRoute(
    book: Book
): String {
    val bookId =
        Uri.encode(book.bookId)
    val title =
        Uri.encode(book.title)
    val cover =
        Uri.encode(book.coverUrl.orEmpty())

    return "detail/$bookId/$title/$cover"
}
