package com.example.myapplication.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Payments
import androidx.compose.material.icons.rounded.PictureAsPdf
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
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
    val isCompleted = installments.isNotEmpty() && installments.all { it.estadoPago == "PAGADO" }

    var selectedInstallment by remember { mutableStateOf<DailyInstallmentEntity?>(null) }
    var showPaymentDialog by remember { mutableStateOf(false) }

    LaunchedEffect(loanId) {
        viewModel.loadInstallmentsForLoan(loanId)
        viewModel.loadLoansForDebtor(debtorId)
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Plan de Cobro", fontWeight = FontWeight.Bold) },
                actions = {
                    IconButton(onClick = {
                        if (debtor != null && loan != null) {
                            PdfGenerator.generateLoanReport(context, debtor, loan, installments)
                        }
                    }) {
                        Icon(Icons.Rounded.PictureAsPdf, contentDescription = "PDF")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize()) {
            // Header Card
            Card(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = debtor?.nombre ?: "Cargando...",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    loan?.let {
                        if (it.nombrePeriodo.isNotEmpty()) {
                            Text(
                                text = "Periodo: ${it.nombrePeriodo}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                    if (isCompleted) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Rounded.CheckCircle, null, tint = MaterialTheme.colorScheme.tertiary, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                "CICLO COMPLETADO",
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.tertiary,
                                fontWeight = FontWeight.Black
                            )
                        }
                    }
                }
            }
            
            Text(
                "Ciclo de 30 Días",
                modifier = Modifier.padding(horizontal = 16.dp),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.secondary
            )

            LazyVerticalGrid(
                columns = GridCells.Fixed(5),
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(installments) { installment ->
                    val color = when (installment.estadoPago) {
                        "PAGADO" -> MaterialTheme.colorScheme.tertiary
                        "ATRASADO" -> MaterialTheme.colorScheme.error
                        else -> MaterialTheme.colorScheme.secondary.copy(alpha = 0.4f)
                    }
                    Box(
                        modifier = Modifier
                            .aspectRatio(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(color)
                            .clickable {
                                selectedInstallment = installment
                                showPaymentDialog = true
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = installment.numeroDia.toString(),
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleMedium
                        )
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
        icon = { Icon(Icons.Rounded.Payments, null, tint = MaterialTheme.colorScheme.primary) },
        title = { Text("Registrar Pago - Día ${installment.numeroDia}", textAlign = TextAlign.Center) },
        text = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    "Cuota esperada: $${installment.montoEsperado}",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = amount,
                    onValueChange = { amount = it },
                    label = { Text("Monto Recibido") },
                    prefix = { Text("$") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(amount.toDoubleOrNull() ?: 0.0) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) { Text("Confirmar Pago") }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth()
            ) { Text("Cancelar") }
        }
    )
}
