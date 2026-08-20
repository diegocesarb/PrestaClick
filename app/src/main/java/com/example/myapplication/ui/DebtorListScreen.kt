package com.example.myapplication.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.myapplication.viewmodel.LoanViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DebtorListScreen(
    viewModel: LoanViewModel,
    onDebtorClick: (Int) -> Unit
) {
    val debtors by viewModel.allDebtors.collectAsState()
    var showDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Deudores") }) },
        floatingActionButton = {
            FloatingActionButton(onClick = { showDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Agregar Deudor")
            }
        }
    ) { padding ->
        LazyColumn(modifier = Modifier.padding(padding)) {
            items(debtors) { debtor ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                        .clickable { onDebtorClick(debtor.id) }
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = debtor.nombre, style = MaterialTheme.typography.titleMedium)
                            Text(text = debtor.telefono, style = MaterialTheme.typography.bodySmall)
                        }
                        IconButton(onClick = { viewModel.deleteDebtor(debtor) }) {
                            Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = MaterialTheme.colorScheme.error)
                        }
                    }
                }
            }
        }

        if (showDialog) {
            AddDebtorDialog(
                onDismiss = { showDialog = false },
                onConfirm = { nombre, telefono, direccion ->
                    viewModel.addDebtor(nombre, telefono, direccion)
                    showDialog = false
                }
            )
        }
    }
}

@Composable
fun AddDebtorDialog(onDismiss: () -> Unit, onConfirm: (String, String, String) -> Unit) {
    var nombre by remember { mutableStateOf("") }
    var telefono by remember { mutableStateOf("") }
    var direccion by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Nuevo Deudor") },
        text = {
            Column {
                TextField(value = nombre, onValueChange = { nombre = it }, label = { Text("Nombre") })
                TextField(value = telefono, onValueChange = { telefono = it }, label = { Text("Teléfono") })
                TextField(value = direccion, onValueChange = { direccion = it }, label = { Text("Dirección") })
            }
        },
        confirmButton = {
            Button(onClick = { onConfirm(nombre, telefono, direccion) }) { Text("Guardar") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
        }
    )
}
