package com.itsm.caremycar.screens.user

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import com.itsm.caremycar.screens.user.components.ClientInk
import com.itsm.caremycar.screens.user.components.ClientEmptyState
import com.itsm.caremycar.screens.user.components.ClientDialog
import com.itsm.caremycar.screens.user.components.ClientLoadingPanel
import com.itsm.caremycar.screens.user.components.ClientInlineAlert
import com.itsm.caremycar.screens.user.components.ClientBadgeTone
import com.itsm.caremycar.screens.user.components.ClientDialogAction
import com.itsm.caremycar.screens.user.components.ClientMetricChip
import com.itsm.caremycar.screens.user.components.ClientSectionHeader
import com.itsm.caremycar.screens.user.components.ClientFeedbackText
import com.itsm.caremycar.screens.user.components.rememberClientFeedback
import com.itsm.caremycar.screens.user.components.maintenance.EditMaintenanceDialog
import com.itsm.caremycar.screens.user.components.maintenance.MaintenanceRecordCard
import com.itsm.caremycar.screens.user.components.maintenance.RecommendationSection
import com.itsm.caremycar.screens.user.components.maintenance.ServiceOrderCard
import com.itsm.caremycar.screens.user.components.maintenance.ServiceRequestCard

private val serviceTypeOptions = listOf(
    "Cambio de aceite",
    "Afinación",
    "Frenos",
    "Alineación y balanceo",
    "Llantas",
    "Batería",
    "Inspección general",
    "Otro"
)

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CarMaintenanceContent(
    vehicleId: String,
    viewModel: CarMaintenanceViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var serviceType by remember(vehicleId) { mutableStateOf("") }
    var serviceTypeExpanded by remember(vehicleId) { mutableStateOf(false) }
    var orderDate by remember(vehicleId) { mutableStateOf("") }
    var showDatePicker by remember(vehicleId) { mutableStateOf(false) }
    var orderNotes by remember(vehicleId) { mutableStateOf("") }
    val feedbackMessage by rememberClientFeedback(
        events = viewModel.events,
        resetKey = vehicleId
    )

    LaunchedEffect(vehicleId) {
        viewModel.loadMaintenance(vehicleId)
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                ClientSectionHeader(
                    eyebrow = "Control",
                    title = "Mantenimiento",
                    description = "Programa servicios, consulta cotizaciones y mantén visible el historial."
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    ClientMetricChip("Registros", uiState.items.size.toString())
                    ClientMetricChip("Órdenes", uiState.serviceOrders.size.toString())
                }
            }
        }

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
            ServiceRequestCard(
                serviceTypeOptions = serviceTypeOptions,
                serviceType = serviceType,
                serviceTypeExpanded = serviceTypeExpanded,
                orderDate = orderDate,
                orderNotes = orderNotes,
                showDatePicker = showDatePicker,
                uiState = uiState,
                onServiceTypeExpandedChange = { serviceTypeExpanded = it },
                onServiceTypeSelected = { option ->
                    serviceType = option
                    serviceTypeExpanded = false
                    viewModel.clearServiceOrderQuote()
                },
                onDatePickerVisibilityChange = { showDatePicker = it },
                onOrderDateSelected = { date ->
                    orderDate = date
                    viewModel.clearServiceOrderQuote()
                },
                onOrderNotesChange = { orderNotes = it },
                onLoadQuote = {
                    viewModel.loadServiceOrderQuote(
                        vehicleId = vehicleId,
                        serviceType = serviceType
                    )
                },
                onCreateServiceOrder = {
                    viewModel.createServiceOrder(
                        vehicleId = vehicleId,
                        serviceType = serviceType,
                        scheduledDate = orderDate,
                        notes = orderNotes
                    )
                }
            )
        }

        feedbackMessage?.let { message ->
            item {
                ClientFeedbackText(message = message)
            }
        }

        uiState.loadError?.let {
            item {
                ClientInlineAlert(
                    text = it,
                    tone = ClientBadgeTone.Danger
                )
            }
        }

        if (uiState.isLoading) {
            item {
                ClientLoadingPanel(
                    title = "Cargando mantenimiento",
                    description = "Estamos preparando historial, órdenes y recomendaciones."
                )
            }
        }

        if (uiState.serviceOrders.isNotEmpty()) {
            item {
                ClientSectionHeader(
                    eyebrow = "Seguimiento",
                    title = "Órdenes de servicio"
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

        if (uiState.items.isNotEmpty()) {
            item {
                ClientSectionHeader(
                    eyebrow = "Historial",
                    title = "Servicios registrados"
                )
            }
        } else if (!uiState.isLoading) {
            item {
                ClientEmptyState(
                    title = "Sin historial todavía",
                    description = "Cuando registres servicios, aquí aparecerá la trazabilidad completa del vehículo."
                )
            }
        }

        items(uiState.items, key = { it.id }) { item ->
            MaintenanceRecordCard(
                item = item,
                onEdit = { viewModel.requestEdit(item) },
                onDelete = { viewModel.requestDelete(item) }
            )
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
        ClientDialog(
            onDismissRequest = { if (!uiState.isDeleting) viewModel.dismissDelete() },
            title = "Eliminar mantenimiento",
            text = { Text("¿Deseas eliminar el registro de ${item.serviceType ?: "servicio"}?") },
            dismissButton = {
                ClientDialogAction(
                    text = "Cancelar",
                    onClick = viewModel::dismissDelete,
                    enabled = !uiState.isDeleting
                )
            },
            confirmButton = {
                ClientDialogAction(
                    text = "Eliminar",
                    onClick = viewModel::confirmDeleteMaintenance,
                    enabled = !uiState.isDeleting,
                    tone = ClientBadgeTone.Danger
                )
            }
        )
    }
}
