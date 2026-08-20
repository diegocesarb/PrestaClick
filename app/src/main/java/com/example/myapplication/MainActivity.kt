package com.example.myapplication

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.myapplication.ui.*
import com.example.myapplication.ui.theme.MyApplicationTheme
import com.example.myapplication.viewmodel.LoanViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_SECURE,
            WindowManager.LayoutParams.FLAG_SECURE
        )
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                val navController = rememberNavController()
                val viewModel: LoanViewModel = viewModel()

                NavHost(navController = navController, startDestination = "debtorList") {
                    composable("debtorList") {
                        DebtorListScreen(
                            viewModel = viewModel,
                            onDebtorClick = { debtorId ->
                                navController.navigate("debtorLoans/$debtorId")
                            }
                        )
                    }
                    composable(
                        "debtorLoans/{debtorId}",
                        arguments = listOf(navArgument("debtorId") { type = NavType.IntType })
                    ) { backStackEntry ->
                        val debtorId = backStackEntry.arguments?.getInt("debtorId") ?: 0
                        DebtorLoansScreen(
                            debtorId = debtorId,
                            viewModel = viewModel,
                            onLoanClick = { loanId ->
                                navController.navigate("loanDetail/$loanId/$debtorId")
                            },
                            onAddLoanClick = {
                                navController.navigate("addLoan/$debtorId")
                            }
                        )
                    }
                    composable(
                        "addLoan/{debtorId}",
                        arguments = listOf(navArgument("debtorId") { type = NavType.IntType })
                    ) { backStackEntry ->
                        val debtorId = backStackEntry.arguments?.getInt("debtorId") ?: 0
                        AddLoanScreen(
                            debtorId = debtorId,
                            viewModel = viewModel,
                            onNavigateBack = {
                                navController.popBackStack()
                            }
                        )
                    }
                    composable(
                        "loanDetail/{loanId}/{debtorId}",
                        arguments = listOf(
                            navArgument("loanId") { type = NavType.IntType },
                            navArgument("debtorId") { type = NavType.IntType }
                        )
                    ) { backStackEntry ->
                        val loanId = backStackEntry.arguments?.getInt("loanId") ?: 0
                        val debtorId = backStackEntry.arguments?.getInt("debtorId") ?: 0
                        LoanDetailScreen(
                            loanId = loanId,
                            debtorId = debtorId,
                            viewModel = viewModel
                        )
                    }
                }
            }
        }
    }
}
