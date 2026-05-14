package com.itsm.caremycar.screens.user

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Card
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.itsm.caremycar.vehicle.MaintenanceRecommendation
import com.itsm.caremycar.vehicle.MaintenanceRecord
import com.itsm.caremycar.vehicle.ServiceOrder
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.text.NumberFormat
import java.util.Locale

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CarMaintenanceContent(
    vehicleId: String,
    viewModel: CarMaintenanceViewModel = hiltViewModel()
) {
    val serviceTypeOptions = listOf(
        "Cambio de aceite",
        "Afinación",
        "Frenos",
        "Alineación y balanceo",
        "Llantas",
        "Batería",
        "Inspección general",
        "Otro"
    )

    val uiState by viewModel.uiState.collectAsState()
    var serviceType by remember { mutableStateOf("") }
    var serviceTypeExpanded by remember { mutableStateOf(false) }
    var orderDate by remember { mutableStateOf("") }
    var showDatePicker by remember { mutableStateOf(false) }
    var orderNotes by remember { mutableStateOf("") }

    LaunchedEffect(vehicleId) {
        viewModel.loadMaintenance(vehicleId)
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        if (uiState.recommendations.isNotEmpty()) {
            item {
                RecommendationSection(
                    recommendations = uiState.recommendations,
                    onUseRecommendation = { rec ->
                        serviceType = rec.serviceLabel
                        orderDate = rec.dueDate
                        viewModel.clearServiceOrderQuote()
                    }
                )
            }
        }

        item {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
                )
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Solicitar servicio con agencia",
                        style = MaterialTheme.typography.titleSmall
                    )
                    ExposedDropdownMenuBox(
                        expanded = serviceTypeExpanded,
                        onExpandedChange = { serviceTypeExpanded = !serviceTypeExpanded }
                    ) {
                        OutlinedTextField(
                            value = serviceType,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Tipo de servicio *") },
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = serviceTypeExpanded)
                            },
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth(),
                            singleLine = true
                        )
                        DropdownMenu(
                            expanded = serviceTypeExpanded,
                            onDismissRequest = { serviceTypeExpanded = false }
                        ) {
                            serviceTypeOptions.forEach { option ->
                                DropdownMenuItem(
                                    text = { Text(option) },
                                    onClick = {
                                        serviceType = option
                                        serviceTypeExpanded = false
                                        viewModel.clearServiceOrderQuote()
                                    }
                                )
                            }
                        }
                    }
                    OutlinedTextField(
                        value = orderDate,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Fecha programada (YYYY-MM-DD) *") },
                        trailingIcon = {
                            IconButton(onClick = { showDatePicker = true }) {
                                Icon(Icons.Default.DateRange, contentDescription = "Seleccionar fecha")
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    if (showDatePicker) {
                        val pickerState = rememberDatePickerState(
                            initialSelectedDateMillis = orderDateToMillis(orderDate) ?: todayUtcMillis(),
                            selectableDates = object : SelectableDates {
                                override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                                    return utcTimeMillis >= todayUtcMillis()
                                }
                            }
                        )
                        DatePickerDialog(
                            onDismissRequest = { showDatePicker = false },
                            confirmButton = {
                                TextButton(
                                    onClick = {
                                        pickerState.selectedDateMillis?.let { millis ->
                                            orderDate = millisToDateString(millis)
                                            viewModel.clearServiceOrderQuote()
                                        }
                                        showDatePicker = false
                                    }
                                ) {
                                    Text("Aceptar")
                                }
                            },
                            dismissButton = {
                                TextButton(onClick = { showDatePicker = false }) { Text("Cancelar") }
                            }
                        ) {
                            DatePicker(state = pickerState)
                        }
                    }
                    OutlinedTextField(
                        value = orderNotes,
                        onValueChange = { orderNotes = it },
                        label = { Text("Notas para la agencia") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Button(
                        onClick = {
                            viewModel.loadServiceOrderQuote(
                                vehicleId = vehicleId,
                                serviceType = serviceType
                            )
                        },
                        enabled = !uiState.isLoadingOrderQuote,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        if (uiState.isLoadingOrderQuote) {
                            CircularProgressIndicator(modifier = Modifier.padding(end = 8.dp), strokeWidth = 2.dp)
                        }
                        Text("Ver cotización")
                    }
                    uiState.orderQuote?.let { quote ->
                        Text(
                            text = "Total sugerido: ${toMxn(quote.suggestedTotalMxn)}",
                            style = MaterialTheme.typography.titleSmall
                        )
                        Text(
                            text = "Productos: ${toMxn(quote.productsTotalMxn)} · Mano de obra: ${toMxn(quote.laborTotalMxn)}",
                            style = MaterialTheme.typography.bodySmall
                        )
                        quote.products.forEach { product ->
                            Text(
                                text = "- ${product.name} x${product.qty} (${toMxn(product.unitPriceMxn)})",
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                    Button(
                        onClick = {
                            viewModel.createServiceOrder(
                                vehicleId = vehicleId,
                                serviceType = serviceType,
                                scheduledDate = orderDate,
                                notes = orderNotes
                            )
                        },
                        enabled = !uiState.isSubmittingOrder && uiState.orderQuote != null,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        if (uiState.isSubmittingOrder) {
                            CircularProgressIndicator(modifier = Modifier.padding(end = 8.dp), strokeWidth = 2.dp)
                        }
                        Text("Solicitar servicio (con cotización)")
                    }
                }
            }
        }

        uiState.orderMessage?.let {
            item { Text(text = it, color = MaterialTheme.colorScheme.primary) }
        }

        uiState.error?.let {
            item { Text(text = it, color = MaterialTheme.colorScheme.error) }
        }

        if (uiState.isLoading) {
            item { CircularProgressIndicator() }
        }

        if (uiState.serviceOrders.isNotEmpty()) {
            item {
                Text(
                    text = "Órdenes de servicio",
                    style = MaterialTheme.typography.titleMedium
                )
            }
            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    uiState.serviceOrders.take(3).forEach { order ->
                        ServiceOrderCard(order)
                    }
                }
            }
        }

        items(uiState.items, key = { it.id }) { item ->
            ElevatedCard(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainerHighest
                )
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    androidx.compose.foundation.layout.Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = item.serviceType ?: "Servicio",
                            style = MaterialTheme.typography.titleMedium
                        )
                        androidx.compose.foundation.layout.Row {
                            IconButton(onClick = { viewModel.requestEdit(item) }) {
                                Icon(Icons.Default.Edit, contentDescription = "Editar")
                            }
                            IconButton(onClick = { viewModel.requestDelete(item) }) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Eliminar",
                                    tint = MaterialTheme.colorScheme.error
                                )
                            }
                        }
                    }
                    Text(text = "Fecha: ${item.serviceDate ?: "N/D"}")
                    Text(text = "Km: ${item.mileage ?: 0}")
                    Text(text = "Costo: ${toMxn(item.cost ?: 0.0)}")
                    item.description?.takeIf { it.isNotBlank() }?.let { desc ->
                        Text(text = desc, style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }
    }

    uiState.selectedItemForEdit?.let { item ->
        EditMaintenanceDialog(
            item = item,
            serviceTypeOptions = serviceTypeOptions,
            isSaving = uiState.isSaving,
            onDismiss = viewModel::dismissEdit,
            onSave = { serviceTypeValue, serviceDateValue, descriptionValue, costValue, mileageValue ->
                viewModel.updateMaintenance(
                    maintenanceId = item.id,
                    serviceType = serviceTypeValue,
                    serviceDate = serviceDateValue,
                    description = descriptionValue,
                    cost = costValue,
                    mileage = mileageValue
                )
            }
        )
    }

    uiState.selectedItemForDelete?.let { item ->
        AlertDialog(
            onDismissRequest = { if (!uiState.isDeleting) viewModel.dismissDelete() },
            title = { Text("Eliminar mantenimiento") },
            text = { Text("¿Deseas eliminar el registro de ${item.serviceType ?: "servicio"}?") },
            dismissButton = {
                TextButton(
                    onClick = viewModel::dismissDelete,
                    enabled = !uiState.isDeleting
                ) {
                    Text("Cancelar")
                }
            },
            confirmButton = {
                TextButton(
                    onClick = viewModel::confirmDeleteMaintenance,
                    enabled = !uiState.isDeleting
                ) {
                    Text("Eliminar")
                }
            }
        )
    }
}

@Composable
private fun ServiceOrderCard(order: ServiceOrder) {
    ElevatedCard(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
                text = "${order.serviceType.ifBlank { "Servicio" }} · ${order.status}",
                style = MaterialTheme.typography.titleSmall
            )
            Text("Fecha programada: ${order.scheduledDate.ifBlank { "N/D" }}")
            Text("Costo estimado: ${toMxn(order.estimatedCost ?: 0.0)}")
            order.costBreakdown?.let { breakdown ->
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
            if (order.completionToken.isNotBlank()) {
                Text("Código de confirmación: ${order.completionToken}")
            }
            if (order.userNotes.isNotBlank()) {
                Text(order.userNotes, style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}

private fun toMxn(amount: Double): String {
    val localeMx = Locale.Builder().setLanguage("es").setRegion("MX").build()
    return NumberFormat.getCurrencyInstance(localeMx).format(amount)
}

@RequiresApi(Build.VERSION_CODES.O)
private fun millisToDateString(millis: Long): String {
    return Instant.ofEpochMilli(millis)
        .atZone(ZoneId.systemDefault())
        .toLocalDate()
        .toString()
}

@RequiresApi(Build.VERSION_CODES.O)
private fun orderDateToMillis(orderDate: String): Long? {
    return try {
        LocalDate.parse(orderDate)
            .atStartOfDay(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli()
    } catch (_: Exception) {
        null
    }
}

@RequiresApi(Build.VERSION_CODES.O)
private fun todayUtcMillis(): Long {
    return LocalDate.now()
        .atStartOfDay(ZoneId.systemDefault())
        .toInstant()
        .toEpochMilli()
}

@Composable
private fun RecommendationSection(
    recommendations: List<MaintenanceRecommendation>,
    onUseRecommendation: (MaintenanceRecommendation) -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Recomendaciones automáticas",
                style = MaterialTheme.typography.titleSmall
            )
            recommendations
                .filter { it.recommended }
                .sortedBy { it.daysLeft }
                .take(3)
                .forEach { rec ->
                    val statusText = when (rec.status) {
                        "due" -> "Vencido"
                        "upcoming" -> "Próximo"
                        else -> "OK"
                    }
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text("${rec.serviceLabel} · $statusText")
                        Text("Fecha objetivo: ${rec.dueDate} · Km objetivo: ${rec.dueKm}")
                        TextButton(
                            onClick = { onUseRecommendation(rec) },
                            modifier = Modifier.padding(0.dp)
                        ) {
                            Text("Usar esta recomendación")
                        }
                    }
                }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EditMaintenanceDialog(
    item: MaintenanceRecord,
    serviceTypeOptions: List<String>,
    isSaving: Boolean,
    onDismiss: () -> Unit,
    onSave: (String, String, String, String, String) -> Unit
) {
    var serviceType by remember(item.id) { mutableStateOf(item.serviceType.orEmpty()) }
    var serviceTypeExpanded by remember(item.id) { mutableStateOf(false) }
    var serviceDate by remember(item.id) { mutableStateOf(item.serviceDate.orEmpty()) }
    var showEditDatePicker by remember(item.id) { mutableStateOf(false) }
    var description by remember(item.id) { mutableStateOf(item.description.orEmpty()) }
    var cost by remember(item.id) { mutableStateOf(item.cost?.toString().orEmpty()) }
    var mileage by remember(item.id) { mutableStateOf(item.mileage?.toString().orEmpty()) }

    AlertDialog(
        onDismissRequest = { if (!isSaving) onDismiss() },
        title = { Text("Editar mantenimiento") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                ExposedDropdownMenuBox(
                    expanded = serviceTypeExpanded,
                    onExpandedChange = { serviceTypeExpanded = !serviceTypeExpanded }
                ) {
                    OutlinedTextField(
                        value = serviceType,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Tipo") },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = serviceTypeExpanded)
                        },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )
                    DropdownMenu(
                        expanded = serviceTypeExpanded,
                        onDismissRequest = { serviceTypeExpanded = false }
                    ) {
                        serviceTypeOptions.forEach { option ->
                            DropdownMenuItem(
                                text = { Text(option) },
                                onClick = {
                                    serviceType = option
                                    serviceTypeExpanded = false
                                }
                            )
                        }
                    }
                }
                OutlinedTextField(
                    value = serviceDate,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Fecha") },
                    trailingIcon = {
                        IconButton(onClick = { showEditDatePicker = true }) {
                            Icon(Icons.Default.DateRange, contentDescription = "Seleccionar fecha")
                        }
                    }
                )
                if (showEditDatePicker) {
                    val pickerState = rememberDatePickerState(
                        initialSelectedDateMillis = orderDateToMillis(serviceDate) ?: todayUtcMillis(),
                        selectableDates = object : SelectableDates {
                            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                                return utcTimeMillis >= todayUtcMillis()
                            }
                        }
                    )
                    DatePickerDialog(
                        onDismissRequest = { showEditDatePicker = false },
                        confirmButton = {
                            TextButton(
                                onClick = {
                                    pickerState.selectedDateMillis?.let {
                                        serviceDate = millisToDateString(it)
                                    }
                                    showEditDatePicker = false
                                }
                            ) { Text("Aceptar") }
                        },
                        dismissButton = {
                            TextButton(onClick = { showEditDatePicker = false }) { Text("Cancelar") }
                        }
                    ) {
                        DatePicker(state = pickerState)
                    }
                }
                OutlinedTextField(value = description, onValueChange = { description = it }, label = { Text("Descripción") })
                OutlinedTextField(value = cost, onValueChange = { cost = it }, label = { Text("Costo") })
                OutlinedTextField(value = mileage, onValueChange = { mileage = it }, label = { Text("Kilometraje") })
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss, enabled = !isSaving) {
                Text("Cancelar")
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onSave(serviceType, serviceDate, description, cost, mileage) },
                enabled = !isSaving
            ) {
                Text("Guardar")
            }
        }
    )
}
