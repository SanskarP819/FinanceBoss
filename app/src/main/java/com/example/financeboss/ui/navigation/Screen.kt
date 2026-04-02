package com.example.financeboss.ui.navigation



sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Transactions : Screen("transactions")
    object AddEditTransaction : Screen("add_edit_transaction?transactionId={transactionId}") {
        fun createRoute(transactionId: Long? = null) =
            if (transactionId != null) "add_edit_transaction?transactionId=$transactionId"
            else "add_edit_transaction"
    }
    object Insights : Screen("insights")
    object Goals : Screen("goals")
}