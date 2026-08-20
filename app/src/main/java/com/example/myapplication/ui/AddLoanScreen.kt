package com.example.myapplication.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
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
        topBar = { TopAppBar(title = { Text("Nuevo Préstamo") }) }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).padding(16.dp)) {
            if (errorMessage != null) {
                Text(errorMessage!!, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(bottom = 8.dp))
            }
            TextField(value = nombrePeriodo, onValueChange = { nombrePeriodo = it; errorMessage = null }, label = { Text("Nombre del Periodo (ej. Septiembre)") }, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(8.dp))
            TextField(value = montoPrestado, onValueChange = { montoPrestado = it; errorMessage = null }, label = { Text("Monto Prestado") }, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(8.dp))
            TextField(value = montoTotalCobro, onValueChange = { montoTotalCobro = it; errorMessage = null }, label = { Text("Monto Total a Cobrar") }, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(8.dp))
            TextField(value = valorCuotaDiaria, onValueChange = { valorCuotaDiaria = it; errorMessage = null }, label = { Text("Valor Cuota Diaria") }, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(16.dp))
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
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Crear Préstamo")
            }
        }
    }
}
