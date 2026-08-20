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

    Scaffold(
        topBar = { TopAppBar(title = { Text("Nuevo Préstamo") }) }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).padding(16.dp)) {
            TextField(value = nombrePeriodo, onValueChange = { nombrePeriodo = it }, label = { Text("Nombre del Periodo (ej. Septiembre)") }, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(8.dp))
            TextField(value = montoPrestado, onValueChange = { montoPrestado = it }, label = { Text("Monto Prestado") }, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(8.dp))
            TextField(value = montoTotalCobro, onValueChange = { montoTotalCobro = it }, label = { Text("Monto Total a Cobrar") }, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(8.dp))
            TextField(value = valorCuotaDiaria, onValueChange = { valorCuotaDiaria = it }, label = { Text("Valor Cuota Diaria") }, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    viewModel.addLoan(
                        debtorId,
                        montoPrestado.toDoubleOrNull() ?: 0.0,
                        montoTotalCobro.toDoubleOrNull() ?: 0.0,
                        valorCuotaDiaria.toDoubleOrNull() ?: 0.0,
                        nombrePeriodo
                    )
                    onNavigateBack()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Crear Préstamo")
            }
        }
    }
}
