package com.example.financeboss.ui.navigation


import android.app.Application
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.financeboss.di.AppContainer
import com.example.financeboss.ui.goals.GoalScreen
import com.example.financeboss.ui.goals.GoalViewModel
import com.example.financeboss.ui.goals.GoalViewModelFactory
import com.example.financeboss.ui.home.HomeScreen
import com.example.financeboss.ui.home.HomeViewModel
import com.example.financeboss.ui.home.HomeViewModelFactory
import com.example.financeboss.ui.insights.InsightsScreen
import com.example.financeboss.ui.insights.InsightsViewModel
import com.example.financeboss.ui.insights.InsightsViewModelFactory
import com.example.financeboss.ui.transactions.AddEditTransactionScreen
import com.example.financeboss.ui.transactions.AddEditTransactionViewModel
import com.example.financeboss.ui.transactions.AddEditTransactionViewModelFactory
import com.example.financeboss.ui.transactions.TransactionScreen
import com.example.financeboss.ui.transactions.TransactionViewModel
import com.example.financeboss.ui.transactions.TransactionViewModelFactory


@Composable
fun NavGraph(navController: NavHostController,
             container: AppContainer,
             isDarkMode: Boolean=false,
             onToggleDarkMode: () -> Unit={},
             modifier: Modifier = Modifier) {



    NavHost(navController = navController, startDestination = Screen.Home.route) {

        composable(Screen.Home.route) {
            val viewModel: HomeViewModel = viewModel(
                factory = HomeViewModelFactory(container.transactionRepository)
            )
            HomeScreen(
                onAddTransaction = { navController.navigate(Screen.AddEditTransaction.createRoute()) },
                onSeeAllTransactions = { navController.navigate(Screen.Transactions.route) },
                viewModel = viewModel,
                isDarkMode = isDarkMode,
                onToggleDarkMode = onToggleDarkMode
            )
        }

        composable(Screen.Transactions.route) {
            val viewModel: TransactionViewModel = viewModel(
                factory = TransactionViewModelFactory(container.transactionRepository)
            )
            TransactionScreen(
                onAddTransaction = { navController.navigate(Screen.AddEditTransaction.createRoute()) },
                onEditTransaction = { id -> navController.navigate(Screen.AddEditTransaction.createRoute(id)) },
                onBack = { navController.popBackStack() },
                viewModel = viewModel
            )
        }

        composable(
            route = Screen.AddEditTransaction.route,
            arguments = listOf(navArgument("transactionId") {
                type = NavType.LongType; defaultValue = -1L
            })
        ) { backStackEntry ->
            val transactionId = backStackEntry.arguments?.getLong("transactionId")?.takeIf { it != -1L }
            val viewModel: AddEditTransactionViewModel = viewModel(
                factory = AddEditTransactionViewModelFactory(container.transactionRepository)
            )
            AddEditTransactionScreen(
                transactionId = transactionId,
                onBack = { navController.popBackStack() },
                viewModel = viewModel
            )
        }

        composable(Screen.Insights.route) {
            val viewModel: InsightsViewModel=viewModel(
                factory= InsightsViewModelFactory(container.transactionRepository)
            )
            InsightsScreen(viewModel=viewModel) }
        composable(Screen.Goals.route) {
            val viewModel: GoalViewModel = viewModel(
                factory = GoalViewModelFactory(
                    container.goalRepository,
                    container.transactionRepository
                )
            )
            GoalScreen(viewModel = viewModel) }
    }
}