package com.example.myapplication.ui

import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("PrestaClick", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { showAddDialog = true },
                icon = { Icon(Icons.Rounded.Add, "Agregar") },
                text = { Text("Nuevo Deudor") },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            )
        },
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            // Créditos en el BottomBar para evitar superposición con el FAB
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(bottom = 16.dp, top = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "PrestaClick",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "© 2026 César Bacarreza",
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                    color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.7f),
                    textAlign = TextAlign.Center
                )
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize(),
            contentPadding = PaddingValues(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(debtors) { debtor ->
                DebtorCard(
                    debtor = debtor,
                    onClick = { selectedDebtorForDetail = debtor },
                    onDelete = { viewModel.deleteDebtor(debtor) }
                )
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
fun DebtorCard(
    debtor: com.example.myapplication.data.DebtorEntity,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = androidx.compose.foundation.shape.CircleShape,
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                modifier = Modifier.size(48.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Rounded.Person,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = debtor.nombre,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Rounded.Phone,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                        tint = MaterialTheme.colorScheme.secondary
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = debtor.telefono,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
            }
            
            IconButton(onClick = onDelete) {
                Icon(
                    Icons.Rounded.Delete,
                    contentDescription = "Eliminar",
                    tint = MaterialTheme.colorScheme.error.copy(alpha = 0.8f)
                )
            }
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
        icon = { Icon(Icons.Rounded.Info, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
        title = { Text("Detalle del Deudor", textAlign = TextAlign.Center) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                DetailItem(Icons.Rounded.Badge, "Nombre", debtor.nombre)
                DetailItem(Icons.Rounded.Phone, "Teléfono", debtor.telefono)
                DetailItem(Icons.Rounded.Place, "Dirección", debtor.direccion)
                DetailItem(
                    Icons.Rounded.Notes,
                    "Observaciones",
                    if (debtor.observaciones.isBlank()) "Sin observaciones" else debtor.observaciones
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onViewLoans,
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.medium
            ) {
                Icon(Icons.Rounded.Payments, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Ver Préstamos")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss, modifier = Modifier.fillMaxWidth()) {
                Text("Cerrar")
            }
        }
    )
}

@Composable
fun DetailItem(icon: ImageVector, label: String, value: String) {
    Row(verticalAlignment = Alignment.Top) {
        Icon(
            icon,
            contentDescription = null,
            modifier = Modifier.size(20.dp).padding(top = 2.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.secondary)
            Text(value, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurface)
        }
    }
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
        title = { Text("Nuevo Deudor", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = nombre,
                    onValueChange = { nombre = it; if(it.isNotBlank()) showError = false },
                    label = { Text("Nombre Completo") },
                    isError = showError,
                    supportingText = { if(showError) Text("El nombre es obligatorio") },
                    modifier = Modifier.fillMaxWidth(),
                    leadingIcon = { Icon(Icons.Rounded.Person, null) },
                    singleLine = true
                )
                OutlinedTextField(
                    value = telefono,
                    onValueChange = { telefono = it },
                    label = { Text("Teléfono") },
                    modifier = Modifier.fillMaxWidth(),
                    leadingIcon = { Icon(Icons.Rounded.Phone, null) },
                    singleLine = true
                )
                OutlinedTextField(
                    value = direccion,
                    onValueChange = { direccion = it },
                    label = { Text("Dirección") },
                    modifier = Modifier.fillMaxWidth(),
                    leadingIcon = { Icon(Icons.Rounded.Place, null) }
                )
                OutlinedTextField(
                    value = observaciones,
                    onValueChange = { observaciones = it },
                    label = { Text("Observaciones (Opcional)") },
                    modifier = Modifier.fillMaxWidth(),
                    leadingIcon = { Icon(Icons.Rounded.Notes, null) },
                    minLines = 2
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (nombre.isNotBlank()) {
                        onConfirm(nombre, telefono, direccion, observaciones)
                    } else {
                        showError = true
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) { Text("Guardar Deudor") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss, modifier = Modifier.fillMaxWidth()) { Text("Cancelar") }
        }
    )
}
