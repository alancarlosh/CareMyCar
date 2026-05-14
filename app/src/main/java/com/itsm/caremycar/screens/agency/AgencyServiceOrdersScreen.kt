package com.itsm.caremycar.screens.agency

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.itsm.caremycar.vehicle.ServiceOrder
import androidx.compose.ui.text.input.KeyboardType
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AgencyServiceOrdersScreen(
    onNavigateBack: () -> Unit,
    viewModel: AgencyServiceOrdersViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var reportFrom by remember { mutableStateOf("") }
    var reportTo by remember { mutableStateOf("") }
    var showFromPicker by remember { mutableStateOf(false) }
    var showToPicker by remember { mutableStateOf(false) }
    var reportRangeError by remember { mutableStateOf<String?>(null) }
    var completionDialogOrder by remember { mutableStateOf<ServiceOrder?>(null) }
    var completionToken by remember { mutableStateOf("") }
    var completionCost by remember { mutableStateOf("") }
    var completionMileage by remember { mutableStateOf("") }
    var completionNotes by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Gestión de servicios", color = Color.White, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color(0xFF4FA3D1))
            )
        },
        containerColor = Color(0xFFF5F5F5)
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                StatusChip(label = "Todos", selected = uiState.selectedStatus == null) {
                    viewModel.loadOrders(null)
                }
                StatusChip(label = "PROGRAMADO", selected = uiState.selectedStatus == "PROGRAMADO") {
                    viewModel.loadOrders("PROGRAMADO")
                }
                StatusChip(label = "EN_PROCESO", selected = uiState.selectedStatus == "EN_PROCESO") {
                    viewModel.loadOrders("EN_PROCESO")
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                StatusChip(label = "FINALIZADO", selected = uiState.selectedStatus == "FINALIZADO") {
                    viewModel.loadOrders("FINALIZADO")
                }
                StatusChip(label = "CANCELADO", selected = uiState.selectedStatus == "CANCELADO") {
                    viewModel.loadOrders("CANCELADO")
                }
            }

            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh)
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("Reporte PDF de servicios finalizados", style = MaterialTheme.typography.titleSmall)
                    OutlinedTextField(
                        value = reportFrom,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Desde (YYYY-MM-DD)") },
                        trailingIcon = {
                            IconButton(onClick = { showFromPicker = true }) {
                                Icon(Icons.Default.DateRange, contentDescription = "Seleccionar fecha desde")
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = reportTo,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Hasta (YYYY-MM-DD)") },
                        trailingIcon = {
                            IconButton(onClick = { showToPicker = true }) {
                                Icon(Icons.Default.DateRange, contentDescription = "Seleccionar fecha hasta")
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    reportRangeError?.let { Text(text = it, color = MaterialTheme.colorScheme.error) }
                    Button(
                        onClick = {
                            reportRangeError = null
                            if (reportFrom.isNotBlank() && reportTo.isNotBlank()) {
                                val fromDate = parseDateOrNull(reportFrom)
                                val toDate = parseDateOrNull(reportTo)
                                if (fromDate != null && toDate != null && fromDate.isAfter(toDate)) {
                                    reportRangeError = "La fecha 'Desde' no puede ser mayor que 'Hasta'."
                                    return@Button
                                }
                            }
                            viewModel.generateReport(
                                from = reportFrom.ifBlank { null },
                                to = reportTo.ifBlank { null }
                            )
                        },
                        enabled = !uiState.isUpdating,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Generar PDF")
                    }
                }
            }

            if (showFromPicker) {
                val fromPickerState = rememberDatePickerState(
                    initialSelectedDateMillis = dateStringToMillis(reportFrom) ?: todayMillis(),
                    selectableDates = object : SelectableDates {
                        override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                            return utcTimeMillis >= todayMillis()
                        }
                    }
                )
                DatePickerDialog(
                    onDismissRequest = { showFromPicker = false },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                fromPickerState.selectedDateMillis?.let {
                                    reportFrom = millisToDateString(it)
                                }
                                reportRangeError = null
                                showFromPicker = false
                            }
                        ) { Text("Aceptar") }
                    },
                    dismissButton = {
                        TextButton(onClick = { showFromPicker = false }) { Text("Cancelar") }
                    }
                ) {
                    DatePicker(state = fromPickerState)
                }
            }

            if (showToPicker) {
                val toPickerState = rememberDatePickerState(
                    initialSelectedDateMillis = dateStringToMillis(reportTo) ?: todayMillis(),
                    selectableDates = object : SelectableDates {
                        override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                            return utcTimeMillis >= todayMillis()
                        }
                    }
                )
                DatePickerDialog(
                    onDismissRequest = { showToPicker = false },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                toPickerState.selectedDateMillis?.let {
                                    reportTo = millisToDateString(it)
                                }
                                reportRangeError = null
                                showToPicker = false
                            }
                        ) { Text("Aceptar") }
                    },
                    dismissButton = {
                        TextButton(onClick = { showToPicker = false }) { Text("Cancelar") }
                    }
                ) {
                    DatePicker(state = toPickerState)
                }
            }

            uiState.message?.let {
                Text(text = it, color = MaterialTheme.colorScheme.primary)
            }
            uiState.error?.let {
                Text(text = it, color = MaterialTheme.colorScheme.error)
            }

            if (uiState.isLoading) {
                CircularProgressIndicator()
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(uiState.items, key = { it.id }) { item ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(
                                text = "${item.vehicleSnapshot.label} (${item.serviceType})",
                                style = MaterialTheme.typography.titleSmall
                            )
                            val owner = buildString {
                                if (item.userName.isNotBlank()) append(item.userName)
                                if (item.userEmail.isNotBlank()) {
                                    if (isNotEmpty()) append(" · ")
                                    append(item.userEmail)
                                }
                            }
                            if (owner.isNotBlank()) {
                                Text(owner, style = MaterialTheme.typography.bodySmall)
                            }
                            Text("Fecha: ${item.scheduledDate} · Estado: ${item.status}")
                            Text("Costo estimado: ${toMxn(item.estimatedCost ?: 0.0)} · Costo final: ${toMxn(item.finalCost ?: 0.0)}")
                            item.costBreakdown?.let { breakdown ->
                                Text(
                                    text = "Productos: ${toMxn(breakdown.productsTotalMxn)} · Mano de obra: ${toMxn(breakdown.laborTotalMxn)}",
                                    style = MaterialTheme.typography.bodySmall
                                )
                                breakdown.products.forEach { product ->
                                    Text(
                                        text = "- ${product.name} x${product.qty} (${toMxn(product.unitPriceMxn)})",
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                }
                            }
                            if ((item.status == "PROGRAMADO" || item.status == "EN_PROCESO") && item.completionToken.isNotBlank()) {
                                Text("Token esperado: ${item.completionToken}", style = MaterialTheme.typography.bodySmall)
                            }
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                if (item.status == "PROGRAMADO") {
                                    AssistChip(
                                        onClick = { viewModel.startOrder(item.id, null) },
                                        label = { Text("Iniciar") },
                                        leadingIcon = { Icon(Icons.Default.PlayArrow, contentDescription = null) }
                                    )
                                }
                                if (item.status == "EN_PROCESO") {
                                    AssistChip(
                                        onClick = {
                                            completionDialogOrder = item
                                            completionToken = ""
                                            completionCost = ""
                                            completionMileage = ""
                                            completionNotes = ""
                                        },
                                        label = { Text("Finalizar") },
                                        leadingIcon = { Icon(Icons.Default.Check, contentDescription = null) }
                                    )
                                }
                                if (item.status == "PROGRAMADO" || item.status == "EN_PROCESO") {
                                    AssistChip(
                                        onClick = { viewModel.cancelOrder(item.id, null) },
                                        label = { Text("Cancelar") },
                                        leadingIcon = { Icon(Icons.Default.Close, contentDescription = null) }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    completionDialogOrder?.let { order ->
        AlertDialog(
            onDismissRequest = { if (!uiState.isUpdating) completionDialogOrder = null },
            title = { Text("Finalizar orden") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Orden: ${order.vehicleSnapshot.label} · ${order.serviceType}")
                    OutlinedTextField(
                        value = completionToken,
                        onValueChange = { completionToken = it },
                        label = { Text("Token de confirmación *") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = completionCost,
                        onValueChange = { completionCost = it },
                        label = { Text("Costo final") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                    )
                    OutlinedTextField(
                        value = completionMileage,
                        onValueChange = { completionMileage = it },
                        label = { Text("Kilometraje (opcional)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                    OutlinedTextField(
                        value = completionNotes,
                        onValueChange = { completionNotes = it },
                        label = { Text("Notas de agencia") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { completionDialogOrder = null },
                    enabled = !uiState.isUpdating
                ) { Text("Cancelar") }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.completeOrder(
                            orderId = order.id,
                            token = completionToken.trim(),
                            finalCost = completionCost.trim().toDoubleOrNull(),
                            notes = completionNotes.trim().ifBlank { null },
                            mileage = completionMileage.trim().toIntOrNull()
                        )
                        completionDialogOrder = null
                    },
                    enabled = !uiState.isUpdating && completionToken.isNotBlank()
                ) { Text("Finalizar") }
            }
        )
    }
}

@Composable
private fun StatusChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        label = { Text(label) }
    )
}

private fun toMxn(amount: Double): String {
    val localeMx = Locale.Builder().setLanguage("es").setRegion("MX").build()
    return NumberFormat.getCurrencyInstance(localeMx).format(amount)
}

private fun millisToDateString(millis: Long): String {
    return Instant.ofEpochMilli(millis)
        .atZone(ZoneId.systemDefault())
        .toLocalDate()
        .toString()
}

private fun dateStringToMillis(date: String): Long? {
    return try {
        LocalDate.parse(date)
            .atStartOfDay(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli()
    } catch (_: Exception) {
        null
    }
}

private fun parseDateOrNull(date: String): LocalDate? {
    return try {
        LocalDate.parse(date)
    } catch (_: Exception) {
        null
    }
}

private fun todayMillis(): Long {
    return LocalDate.now()
        .atStartOfDay(ZoneId.systemDefault())
        .toInstant()
        .toEpochMilli()
}
