package com.example.myapplication.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Info
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
    var showAddDialog by remember { mutableStateOf(false) }
    var selectedDebtorForDetail by remember { mutableStateOf<com.example.myapplication.data.DebtorEntity?>(null) }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Deudores") }) },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }) {
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
                        .clickable { selectedDebtorForDetail = debtor }
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

        if (showAddDialog) {
            AddDebtorDialog(
                onDismiss = { showAddDialog = false },
                onConfirm = { nombre, telefono, direccion, observaciones ->
                    viewModel.addDebtor(nombre, telefono, direccion, observaciones)
                    showAddDialog = false
                }
            )
        }

        if (selectedDebtorForDetail != null) {
            DebtorDetailDialog(
                debtor = selectedDebtorForDetail!!,
                onDismiss = { selectedDebtorForDetail = null },
                onViewLoans = {
                    onDebtorClick(selectedDebtorForDetail!!.id)
                    selectedDebtorForDetail = null
                }
            )
        }
    }
}

@Composable
fun DebtorDetailDialog(
    debtor: com.example.myapplication.data.DebtorEntity,
    onDismiss: () -> Unit,
    onViewLoans: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Información del Deudor") },
        text = {
            Column {
                Text("Nombre: ${debtor.nombre}", style = MaterialTheme.typography.bodyLarge)
                Spacer(modifier = Modifier.height(8.dp))
                Text("Teléfono: ${debtor.telefono}")
                Text("Dirección: ${debtor.direccion}")
                Spacer(modifier = Modifier.height(8.dp))
                Text("Observaciones:", style = MaterialTheme.typography.titleSmall)
                Text(
                    text = if (debtor.observaciones.isBlank()) "Sin observaciones" else debtor.observaciones,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        },
        confirmButton = {
            Button(onClick = onViewLoans) { Text("Ver Préstamos") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cerrar") }
        }
    )
}

@Composable
fun AddDebtorDialog(onDismiss: () -> Unit, onConfirm: (String, String, String, String) -> Unit) {
    var nombre by remember { mutableStateOf("") }
    var telefono by remember { mutableStateOf("") }
    var direccion by remember { mutableStateOf("") }
    var observaciones by remember { mutableStateOf("") }
    var showError by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Nuevo Deudor") },
        text = {
            Column {
                TextField(
                    value = nombre,
                    onValueChange = { nombre = it; if(it.isNotBlank()) showError = false },
                    label = { Text("Nombre") },
                    isError = showError,
                    supportingText = { if(showError) Text("El nombre es obligatorio") },
                    modifier = Modifier.fillMaxWidth()
                )
                TextField(value = telefono, onValueChange = { telefono = it }, label = { Text("Teléfono") }, modifier = Modifier.fillMaxWidth())
                TextField(value = direccion, onValueChange = { direccion = it }, label = { Text("Dirección") }, modifier = Modifier.fillMaxWidth())
                TextField(value = observaciones, onValueChange = { observaciones = it }, label = { Text("Observaciones (Opcional)") }, modifier = Modifier.fillMaxWidth())
            }
        },
        confirmButton = {
            Button(onClick = {
                if (nombre.isNotBlank()) {
                    onConfirm(nombre, telefono, direccion, observaciones)
                } else {
                    showError = true
                }
            }) { Text("Guardar") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
        }
    )
}
