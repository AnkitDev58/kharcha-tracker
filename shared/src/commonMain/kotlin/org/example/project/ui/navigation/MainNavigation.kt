package org.example.project.ui.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import org.example.project.ui.addtransaction.AddEditTransactionScreen
import org.example.project.ui.budget.BudgetScreen
import org.example.project.ui.goals.GoalsScreen
import org.example.project.ui.home.HomeScreen
import org.example.project.ui.statistics.StatisticsScreen
import org.example.project.ui.transactions.TransactionsScreen
import org.koin.compose.viewmodel.koinViewModel

data class BottomNavItem(
    val label: String,
    val icon: ImageVector,
    val route: Any
)

val bottomNavItems = listOf(
    BottomNavItem("Home", Icons.Filled.Home, HomeRoute),
    BottomNavItem("Transactions", Icons.Filled.Receipt, TransactionsRoute()),
    BottomNavItem("Statistics", Icons.Filled.BarChart, StatisticsRoute),
    BottomNavItem("Budget", Icons.Filled.AccountBalance, BudgetRoute),
    BottomNavItem("Goals", Icons.Filled.Savings, GoalsRoute)
)

@Composable
fun MainNavigation() {
    val navController = rememberNavController()
    val currentEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentEntry?.destination?.route

    val showBottomBar = bottomNavItems.any { item ->
        if (item.route == TransactionsRoute()) {
            val isShow = currentRoute?.contains(item.route::class.simpleName ?: "") == true
            if (isShow) {
                val model = currentEntry?.toRoute<TransactionsRoute>()
                model?.date == null
            } else {
                false
            }
        } else {
            currentRoute?.contains(item.route::class.simpleName ?: "") == true
        }

    }

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    bottomNavItems.forEach { item ->
                        val isSelected = currentRoute?.contains(
                            item.route::class.simpleName ?: ""
                        ) == true
                        NavigationBarItem(
                            selected = isSelected,
                            onClick = {
                                navController.navigate(item.route) {
                                    popUpTo(HomeRoute) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = { Icon(item.icon, item.label) },
                            label = { Text(item.label) }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = HomeRoute,
            modifier = Modifier.padding(bottom = innerPadding.calculateBottomPadding()),

            enterTransition = {
                fadeIn(tween(300)) +
                        scaleIn(
                            initialScale = 0.96f,
                            animationSpec = tween(300)
                        )
            },

                    exitTransition = {
                fadeOut(tween(200)) +
                        scaleOut(
                            targetScale = 0.96f,
                            animationSpec = tween(200)
                        )
            }
//            enterTransition = {
//                fadeIn(tween(300)) +
//                        scaleIn(
//                            initialScale = 0.92f,
//                            animationSpec = tween(300)
//                        )
//            },
//
//            exitTransition = {
//                fadeOut(tween(200)) +
//                        scaleOut(
//                            targetScale = 1.05f,
//                            animationSpec = tween(200)
//                        )
//            }
        ) {
            composable<HomeRoute> {
                HomeScreen(
                    viewModel = koinViewModel(),
                    onAddTransactionClick = { navController.navigate(AddEditTransactionRoute()) },
                    onTransactionClick = { id -> navController.navigate(AddEditTransactionRoute(id)) }
                )
            }

            composable<TransactionsRoute> { backStackEntry ->
                TransactionsScreen(
                    viewModel = koinViewModel(),
                    onAddClick = { navController.navigate(AddEditTransactionRoute()) },
                    onTransactionClick = { id -> navController.navigate(AddEditTransactionRoute(id)) },
                    onNavigateBack = {
                        navController.popBackStack()
                    })
            }

            composable<StatisticsRoute> {
                StatisticsScreen(viewModel = koinViewModel()) {
                    navController.navigate(TransactionsRoute(it))
                }
            }

            composable<BudgetRoute> {
                BudgetScreen(viewModel = koinViewModel())
            }

            composable<GoalsRoute> {
                GoalsScreen(viewModel = koinViewModel())
            }

            composable<AddEditTransactionRoute> { backStackEntry ->
                val route: AddEditTransactionRoute = backStackEntry.toRoute()
                AddEditTransactionScreen(
                    viewModel = koinViewModel(),
                    transactionId = route.transactionId,
                    onNavigateBack = { navController.popBackStack() }
                )
            }
        }
    }
}
