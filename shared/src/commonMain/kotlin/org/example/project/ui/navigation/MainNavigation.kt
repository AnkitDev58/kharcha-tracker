package org.example.project.ui.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.PermanentDrawerSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import org.example.project.ui.addtransaction.AddEditTransactionScreen
import org.example.project.ui.annualexpense.AnnualExpenseScreen
import org.example.project.ui.auth.LoginScreen
import org.example.project.ui.auth.OtpScreen
import org.example.project.ui.budget.BudgetScreen
import org.example.project.ui.calendar.CalendarScreen
import org.example.project.ui.components.AdaptiveContent
import org.example.project.ui.components.AdaptiveScreenWrapper
import org.example.project.ui.components.WindowSizeClass
import org.example.project.ui.goals.GoalsScreen
import org.example.project.ui.home.HomeScreen
import org.example.project.ui.investment.InvestmentScreen
import org.example.project.ui.loan.LoanScreen
import org.example.project.ui.reports.ReportsScreen
import org.example.project.ui.settings.SettingsScreen
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

/** Routes that should show the bottom navigation bar. */
private val bottomBarRoutes = setOf(
    HomeRoute::class.simpleName,
    TransactionsRoute::class.simpleName,
    StatisticsRoute::class.simpleName,
    BudgetRoute::class.simpleName,
    GoalsRoute::class.simpleName
)

@Composable
fun MainNavigation() {
    val navController = rememberNavController()
    val currentEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentEntry?.destination?.route

    // Show navigation elements only on the 5 primary destinations (not on date-filtered transactions)
    val showNavigation = bottomBarRoutes.any { routeName ->
        currentRoute?.contains(routeName ?: "") == true
    } && run {
        // Hide when TransactionsRoute has a date filter (i.e. navigated from calendar)
        val isDateTransactions = currentRoute?.contains(
            TransactionsRoute::class.simpleName ?: ""
        ) == true
        if (isDateTransactions) {
            currentEntry?.toRoute<TransactionsRoute>()?.date == null
        } else true
    }
     AdaptiveContent { sizeClass ->
        if (sizeClass == WindowSizeClass.COMPACT) {
            Scaffold(
                bottomBar = {
                    if (showNavigation) {
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
                }, modifier = Modifier.fillMaxSize()
            ) { innerPadding ->
                NavHostContent(
                    navController = navController,
                    sizeClass = sizeClass,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = if (showNavigation) innerPadding.calculateBottomPadding() else 0.dp)
                )
            }
        } else {
            // Tablet and Desktop Layout
            Row(Modifier.fillMaxSize()) {
                if (showNavigation) {
                    if (sizeClass == WindowSizeClass.MEDIUM) {
                        NavigationRail(
                            containerColor = MaterialTheme.colorScheme.surface,
                            modifier = Modifier.fillMaxHeight()
                        ) {
                            Spacer(Modifier.height(16.dp))
                            bottomNavItems.forEach { item ->
                                val isSelected = currentRoute?.contains(
                                    item.route::class.simpleName ?: ""
                                ) == true
                                NavigationRailItem(
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
                    } else {
                        PermanentDrawerSheet(
                            modifier = Modifier.width(240.dp)
                        ) {
                            Spacer(Modifier.height(16.dp))
                            bottomNavItems.forEach { item ->
                                val isSelected = currentRoute?.contains(
                                    item.route::class.simpleName ?: ""
                                ) == true
                                NavigationDrawerItem(
                                    label = { Text(item.label) },
                                    selected = isSelected,
                                    onClick = {
                                        navController.navigate(item.route) {
                                            popUpTo(HomeRoute) { saveState = true }
                                            launchSingleTop = true
                                            restoreState = true
                                        }
                                    },
                                    icon = { Icon(item.icon, item.label) },
                                    modifier = Modifier.padding(horizontal = 12.dp)
                                )
                            }
                        }
                    }
                }

                Scaffold(
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->
                    NavHostContent(
                        navController = navController,
                        sizeClass = sizeClass,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun NavHostContent(
    navController: androidx.navigation.NavHostController,
    sizeClass: WindowSizeClass,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = HomeRoute,
        modifier = modifier,
        enterTransition = {
            fadeIn(tween(300)) + scaleIn(initialScale = 0.96f, animationSpec = tween(300))
        },
        exitTransition = {
            fadeOut(tween(200)) + scaleOut(targetScale = 0.96f, animationSpec = tween(200))
        }
    ) {

        // ── Auth screens ───────────────────────────────────────────────────

        composable<LoginRoute> {
            LoginScreen(
                onSendOtp = { identifier ->
                    navController.navigate(OtpRoute(identifier))
                }
            )
        }

        composable<OtpRoute> { backStackEntry ->
            val route: OtpRoute = backStackEntry.toRoute()
            OtpScreen(
                identifier = route.identifier,
                onVerifyOtp = {
                    navController.navigate(HomeRoute) {
                        popUpTo(LoginRoute) { inclusive = true }
                    }
                },
                onResendOtp = { /* Handle resend */ },
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // ── Bottom nav screens ─────────────────────────────────────────────

        composable<HomeRoute> {
            HomeScreen(
                viewModel = koinViewModel(),
                onAddTransactionClick = { navController.navigate(AddEditTransactionRoute()) },
                onTransactionClick = { id -> navController.navigate(AddEditTransactionRoute(id)) },
                onNavigateToCalendar = { navController.navigate(CalendarRoute) },
                onNavigateToReports = { navController.navigate(ReportsRoute) },
                onNavigateToAnnualBills = { navController.navigate(AnnualExpenseRoute) },
                onNavigateToLoans = { navController.navigate(LoanRoute) },
                onNavigateToInvestments = { navController.navigate(InvestmentRoute) },
                onNavigateToSettings = { navController.navigate(SettingsRoute) }
            )
        }

        composable<TransactionsRoute> {
            TransactionsScreen(
                viewModel = koinViewModel(),
                onAddClick = { navController.navigate(AddEditTransactionRoute()) },
                onTransactionClick = { id -> navController.navigate(AddEditTransactionRoute(id)) },
                sizeClass = sizeClass,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        @Suppress("DEPRECATION")
        composable<StatisticsRoute> {
            StatisticsScreen(viewModel = koinViewModel()) { date ->
                navController.navigate(TransactionsRoute(date))
            }
        }

        composable<BudgetRoute> {
            BudgetScreen(viewModel = koinViewModel())
        }

        composable<GoalsRoute> {
            GoalsScreen(viewModel = koinViewModel())
        }

        // ── Add / Edit transaction ─────────────────────────────────────────

        composable<AddEditTransactionRoute> { backStackEntry ->
            val route: AddEditTransactionRoute = backStackEntry.toRoute()
            AddEditTransactionScreen(
                viewModel = koinViewModel(),
                transactionId = route.transactionId,
                sizeClass = sizeClass,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // ── New feature screens ────────────────────────────────────────────

        composable<CalendarRoute> {
            CalendarScreen(
                viewModel = koinViewModel(),
                onDayClick = { dateStr ->
                    navController.navigate(TransactionsRoute(date = dateStr))
                }
            )
        }

        composable<ReportsRoute> {
            ReportsScreen(
                viewModel = koinViewModel(),
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable<AnnualExpenseRoute> {
            AnnualExpenseScreen(
                viewModel = koinViewModel(),
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable<LoanRoute> {
            LoanScreen(
                viewModel = koinViewModel(),
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable<InvestmentRoute> {
            InvestmentScreen(
                viewModel = koinViewModel(),
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable<SettingsRoute> {
            SettingsScreen(
                viewModel = koinViewModel(),
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
