package com.example.myapplication.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.myapplication.data.DailyInstallmentEntity
import com.example.myapplication.util.PdfGenerator
import com.example.myapplication.viewmodel.LoanViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoanDetailScreen(
    loanId: Int,
    debtorId: Int,
    viewModel: LoanViewModel
) {
    val context = LocalContext.current
    val installments by viewModel.selectedLoanInstallments.collectAsState()
    val loans by viewModel.selectedDebtorLoans.collectAsState()
    val debtors by viewModel.allDebtors.collectAsState()
    
    val loan = loans.find { it.id == loanId }
    val debtor = debtors.find { it.id == debtorId }

    var selectedInstallment by remember { mutableStateOf<DailyInstallmentEntity?>(null) }
    var showPaymentDialog by remember { mutableStateOf(false) }

    LaunchedEffect(loanId) {
        viewModel.loadInstallmentsForLoan(loanId)
        viewModel.loadLoansForDebtor(debtorId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalle de Préstamo") },
                actions = {
                    IconButton(onClick = {
                        if (debtor != null && loan != null) {
                            PdfGenerator.generateLoanReport(context, debtor, loan, installments)
                        }
                    }) {
                        Icon(Icons.Default.PictureAsPdf, contentDescription = "PDF")
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).padding(16.dp)) {
            Text("Deudor: ${debtor?.nombre ?: "Cargando..."}", style = MaterialTheme.typography.titleLarge)
            loan?.let {
                if (it.nombrePeriodo.isNotEmpty()) {
                    Text("Periodo: ${it.nombrePeriodo}", style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.primary)
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            
            LazyVerticalGrid(
                columns = GridCells.Fixed(5),
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(8.dp)
            ) {
                items(installments) { installment ->
                    val color = when (installment.estadoPago) {
                        "PAGADO" -> Color.Green
                        "ATRASADO" -> Color.Red
                        else -> Color.Gray
                    }
                    Box(
                        modifier = Modifier
                            .padding(4.dp)
                            .aspectRatio(1f)
                            .background(color)
                            .clickable {
                                selectedInstallment = installment
                                showPaymentDialog = true
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = installment.numeroDia.toString(), color = Color.White)
                    }
                }
            }
        }

        if (showPaymentDialog && selectedInstallment != null) {
            PaymentDialog(
                installment = selectedInstallment!!,
                onDismiss = { showPaymentDialog = false },
                onConfirm = { amount ->
                    viewModel.payInstallment(selectedInstallment!!, amount)
                    showPaymentDialog = false
                }
            )
        }
    }
}

@Composable
fun PaymentDialog(
    installment: DailyInstallmentEntity,
    onDismiss: () -> Unit,
    onConfirm: (Double) -> Unit
) {
    var amount by remember { mutableStateOf(installment.montoEsperado.toString()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Registrar Pago - Día ${installment.numeroDia}") },
        text = {
            Column {
                Text("Cuota esperada: $${installment.montoEsperado}")
                TextField(value = amount, onValueChange = { amount = it }, label = { Text("Monto Pagado") })
            }
        },
        confirmButton = {
            Button(onClick = { onConfirm(amount.toDoubleOrNull() ?: 0.0) }) { Text("Pagar") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
        }
    )
}
