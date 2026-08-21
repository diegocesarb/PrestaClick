package com.example.myapplication.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
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
    var loanToDelete by remember { mutableStateOf<com.example.myapplication.data.LoanEntity?>(null) }

    LaunchedEffect(debtorId) {
        viewModel.loadLoansForDebtor(debtorId)
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Préstamos", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onAddLoanClick,
                icon = { Icon(Icons.Rounded.Add, null) },
                text = { Text("Nuevo Préstamo") },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.padding(padding).fillMaxSize(),
            contentPadding = PaddingValues(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(loans) { loan ->
                LoanCard(
                    loan = loan,
                    onClick = { onLoanClick(loan.id) },
                    onDelete = { loanToDelete = loan }
                )
            }
        }

        if (loanToDelete != null) {
            AlertDialog(
                onDismissRequest = { loanToDelete = null },
                icon = { Icon(Icons.Rounded.Warning, contentDescription = null, tint = MaterialTheme.colorScheme.error) },
                title = { Text("¿Eliminar préstamo?") },
                text = { Text("Esta acción eliminará el préstamo y todas sus cuotas asociadas de forma permanente.") },
                confirmButton = {
                    Button(
                        onClick = {
                            viewModel.deleteLoan(loanToDelete!!)
                            loanToDelete = null
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                    ) {
                        Text("ELIMINAR")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { loanToDelete = null }) {
                        Text("CANCELAR")
                    }
                }
            )
        }
    }
}

@Composable
fun LoanCard(
    loan: com.example.myapplication.data.LoanEntity,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = MaterialTheme.shapes.medium,
                color = if (loan.estado == "FINALIZADO") MaterialTheme.colorScheme.tertiary.copy(alpha = 0.1f) 
                        else MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                modifier = Modifier.size(52.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = if (loan.estado == "FINALIZADO") Icons.Rounded.CheckCircle 
                                     else Icons.Rounded.AccountBalanceWallet,
                        contentDescription = null,
                        tint = if (loan.estado == "FINALIZADO") MaterialTheme.colorScheme.tertiary 
                               else MaterialTheme.colorScheme.primary
                    )
                }
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                if (loan.nombrePeriodo.isNotEmpty()) {
                    Text(
                        text = loan.nombrePeriodo,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                }
                Text(
                    text = "Monto: $${loan.montoPrestado}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Total a cobrar: $${loan.montoTotalCobro}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.secondary
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                if (loan.estado == "FINALIZADO") {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Rounded.Check,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp),
                            tint = MaterialTheme.colorScheme.tertiary
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            "FINALIZADO",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.tertiary,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                } else {
                    Text(
                        "ESTADO: ACTIVO",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            
            IconButton(onClick = onDelete) {
                Icon(
                    Icons.Rounded.Delete,
                    contentDescription = "Eliminar",
                    tint = MaterialTheme.colorScheme.error.copy(alpha = 0.6f)
                )
            }
        }
    }
}
