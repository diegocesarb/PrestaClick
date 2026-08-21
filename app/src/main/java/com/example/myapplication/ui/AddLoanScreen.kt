package com.example.myapplication.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CalendarMonth
import androidx.compose.material.icons.rounded.CurrencyExchange
import androidx.compose.material.icons.rounded.Payments
import androidx.compose.material.icons.rounded.Savings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.myapplication.viewmodel.LoanViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddLoanScreen(
    debtorId: Int,
    viewModel: LoanViewModel,
    onNavigateBack: () -> Unit
) {
    var montoPrestado by remember { mutableStateOf("") }
    var montoTotalCobro by remember { mutableStateOf("") }
    var valorCuotaDiaria by remember { mutableStateOf("") }
    var nombrePeriodo by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Nuevo Préstamo", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (errorMessage != null) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = errorMessage!!,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        modifier = Modifier.padding(12.dp),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
            
            OutlinedTextField(
                value = nombrePeriodo,
                onValueChange = { nombrePeriodo = it; errorMessage = null },
                label = { Text("Nombre del Periodo") },
                placeholder = { Text("Ej. Septiembre") },
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = { Icon(Icons.Rounded.CalendarMonth, null) },
                shape = RoundedCornerShape(12.dp)
            )
            
            OutlinedTextField(
                value = montoPrestado,
                onValueChange = { montoPrestado = it; errorMessage = null },
                label = { Text("Monto Prestado") },
                prefix = { Text("$") },
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = { Icon(Icons.Rounded.Savings, null) },
                shape = RoundedCornerShape(12.dp)
            )
            
            OutlinedTextField(
                value = montoTotalCobro,
                onValueChange = { montoTotalCobro = it; errorMessage = null },
                label = { Text("Monto Total a Cobrar") },
                prefix = { Text("$") },
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = { Icon(Icons.Rounded.CurrencyExchange, null) },
                shape = RoundedCornerShape(12.dp)
            )
            
            OutlinedTextField(
                value = valorCuotaDiaria,
                onValueChange = { valorCuotaDiaria = it; errorMessage = null },
                label = { Text("Valor Cuota Diaria") },
                prefix = { Text("$") },
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = { Icon(Icons.Rounded.Payments, null) },
                shape = RoundedCornerShape(12.dp)
            )
            
            Spacer(modifier = Modifier.weight(1f))
            
            Button(
                onClick = {
                    val prestado = montoPrestado.toDoubleOrNull() ?: 0.0
                    val total = montoTotalCobro.toDoubleOrNull() ?: 0.0
                    val cuota = valorCuotaDiaria.toDoubleOrNull() ?: 0.0

                    when {
                        nombrePeriodo.isBlank() -> errorMessage = "El nombre del periodo es obligatorio"
                        prestado <= 0 -> errorMessage = "El monto prestado debe ser mayor a cero"
                        total <= 0 -> errorMessage = "El monto total debe ser mayor a cero"
                        cuota <= 0 -> errorMessage = "La cuota diaria debe ser mayor a cero"
                        else -> {
                            viewModel.addLoan(debtorId, prestado, total, cuota, nombrePeriodo)
                            onNavigateBack()
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Crear Préstamo", style = MaterialTheme.typography.titleMedium)
            }
        }
    }
}
