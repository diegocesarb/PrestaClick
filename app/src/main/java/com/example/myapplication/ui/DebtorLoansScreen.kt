package com.example.myapplication.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.myapplication.viewmodel.LoanViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DebtorLoansScreen(
    debtorId: Int,
    viewModel: LoanViewModel,
    onLoanClick: (Int) -> Unit,
    onAddLoanClick: () -> Unit
) {
    val loans by viewModel.selectedDebtorLoans.collectAsState()

    LaunchedEffect(debtorId) {
        viewModel.loadLoansForDebtor(debtorId)
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Préstamos del Deudor") }) },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddLoanClick) {
                Icon(Icons.Default.Add, contentDescription = "Nuevo Préstamo")
            }
        }
    ) { padding ->
        LazyColumn(modifier = Modifier.padding(padding)) {
            items(loans) { loan ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                        .clickable { onLoanClick(loan.id) }
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Monto: $${loan.montoPrestado}", style = MaterialTheme.typography.titleMedium)
                        if (loan.nombrePeriodo.isNotEmpty()) {
                            Text("Periodo: ${loan.nombrePeriodo}", style = MaterialTheme.typography.bodyMedium)
                        }
                        if (loan.estado == "FINALIZADO") {
                            Surface(
                                color = androidx.compose.ui.graphics.Color.Green,
                                shape = MaterialTheme.shapes.small,
                                modifier = Modifier.padding(top = 4.dp)
                            ) {
                                Text(
                                    "PRÉSTAMO FINALIZADO",
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = androidx.compose.ui.graphics.Color.White
                                )
                            }
                        } else {
                            Text("Estado: ACTIVO", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
                        }
                    }
                }
            }
        }
    }
}
