package com.itsm.caremycar.screens.user.components.maintenance

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.outlined.Build
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.itsm.caremycar.screens.user.CarMaintenanceUiState
import com.itsm.caremycar.screens.user.components.ClientInk
import com.itsm.caremycar.screens.user.components.ClientBlue
import com.itsm.caremycar.screens.user.components.ClientMetricChip
import com.itsm.caremycar.screens.user.components.ClientSky
import com.itsm.caremycar.screens.user.components.ClientSurface
import com.itsm.caremycar.screens.user.components.ClientPanel
import com.itsm.caremycar.screens.user.components.ClientSectionHeader
import com.itsm.caremycar.screens.user.components.ClientBadgeTone
import com.itsm.caremycar.screens.user.components.ClientStatusBadge
import com.itsm.caremycar.screens.user.components.ClientPrimaryButton
import com.itsm.caremycar.screens.user.components.ClientSecondaryButton
import com.itsm.caremycar.screens.user.components.ClientSelectField
import com.itsm.caremycar.screens.user.components.ClientDateField
import com.itsm.caremycar.screens.user.components.ClientDatePickerDialog
import com.itsm.caremycar.screens.user.components.ClientSurfaceMuted
import com.itsm.caremycar.screens.user.components.clientFieldColors
import com.itsm.caremycar.screens.user.util.formatMxn
import com.itsm.caremycar.vehicle.MaintenanceRecommendation
import com.itsm.caremycar.vehicle.MaintenanceRecord
import com.itsm.caremycar.vehicle.ServiceOrder
import com.itsm.caremycar.vehicle.ServiceQuote

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun ServiceRequestCard(
    serviceTypeOptions: List<String>,
    serviceType: String,
    serviceTypeExpanded: Boolean,
    orderDate: String,
    orderNotes: String,
    showDatePicker: Boolean,
    uiState: CarMaintenanceUiState,
    onServiceTypeExpandedChange: (Boolean) -> Unit,
    onServiceTypeSelected: (String) -> Unit,
    onDatePickerVisibilityChange: (Boolean) -> Unit,
    onOrderDateSelected: (String) -> Unit,
    onOrderNotesChange: (String) -> Unit,
    onLoadQuote: () -> Unit,
    onCreateServiceOrder: () -> Unit
) {
    ClientPanel {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            ClientSectionHeader(
                eyebrow = "Solicitud",
                title = "Agenda un servicio",
                description = "Selecciona el tipo de trabajo y confirma una cotización antes de enviarlo."
            )
            ClientSelectField(
                value = serviceType,
                label = "Tipo de servicio *",
                expanded = serviceTypeExpanded,
                options = serviceTypeOptions,
                onExpandedChange = onServiceTypeExpandedChange,
                onOptionSelected = onServiceTypeSelected
            )
            ClientDateField(
                value = orderDate,
                label = "Fecha programada *",
                onOpenPicker = { onDatePickerVisibilityChange(true) }
            )
            if (showDatePicker) {
                ClientDatePickerDialog(
                    initialSelectedDateMillis = orderDateToMillis(orderDate) ?: todayMillis(),
                    selectableDates = object : SelectableDates {
                        override fun isSelectableDate(utcTimeMillis: Long): Boolean = utcTimeMillis >= todayMillis()
                    },
                    onDismissRequest = { onDatePickerVisibilityChange(false) },
                    onDateSelected = { millis -> onOrderDateSelected(millisToDateString(millis)) }
                )
            }
            OutlinedTextField(
                value = orderNotes,
                onValueChange = onOrderNotesChange,
                label = { Text("Notas para la agencia") },
                modifier = Modifier.fillMaxWidth(),
                colors = clientFieldColors()
            )
            ClientSecondaryButton(
                text = "Ver cotización",
                onClick = onLoadQuote,
                enabled = !uiState.isLoadingOrderQuote,
                modifier = Modifier.fillMaxWidth()
            )
            uiState.orderQuote?.let { quote -> QuoteCard(quote) }
            ClientPrimaryButton(
                text = "Solicitar servicio",
                onClick = onCreateServiceOrder,
                enabled = !uiState.isSubmittingOrder && uiState.orderQuote != null,
                modifier = Modifier.fillMaxWidth(),
                leadingContent = {
                    if (uiState.isSubmittingOrder) {
                        CircularProgressIndicator(modifier = Modifier.padding(end = 8.dp), strokeWidth = 2.dp)
                    }
                }
            )
        }
    }
}

@Composable
private fun QuoteCard(quote: ServiceQuote) {
    androidx.compose.material3.Surface(
        modifier = Modifier.fillMaxWidth(),
        color = ClientSky.copy(alpha = 0.62f),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text(
                text = "COTIZACIÓN ESTIMADA",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.SemiBold,
                color = ClientBlue
            )
            Text(
                text = formatMxn(quote.suggestedTotalMxn),
                style = MaterialTheme.typography.headlineMedium,
                color = ClientInk
            )
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                QuoteDetailMetric(
                    label = "Refacciones",
                    value = formatMxn(quote.productsTotalMxn),
                    modifier = Modifier.weight(1f)
                )
                QuoteDetailMetric(
                    label = "Mano de obra",
                    value = formatMxn(quote.laborTotalMxn),
                    modifier = Modifier.weight(1f)
                )
            }
            if (quote.products.isNotEmpty()) {
                quote.products.forEach { product ->
                    Text(
                        "${product.name} · ${product.qty} pza. · ${formatMxn(product.unitPriceMxn)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = ClientInk.copy(alpha = 0.72f)
                    )
                }
            }
        }
    }
}

@Composable
private fun QuoteDetailMetric(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    androidx.compose.material3.Surface(
        modifier = modifier,
        color = Color.White.copy(alpha = 0.76f),
        shape = RoundedCornerShape(14.dp)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.spacedBy(3.dp)
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = ClientInk.copy(alpha = 0.6f)
            )
            Text(
                text = value,
                style = MaterialTheme.typography.titleSmall,
                color = ClientInk
            )
        }
    }
}

@Composable
internal fun MaintenanceRecordCard(item: MaintenanceRecord, onEdit: () -> Unit, onDelete: () -> Unit) {
    ElevatedCard(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .padding(2.dp)
                    ) {
                        androidx.compose.material3.Surface(
                            color = ClientSurfaceMuted,
                            shape = androidx.compose.foundation.shape.RoundedCornerShape(14.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Build,
                                contentDescription = null,
                                tint = ClientInk,
                                modifier = Modifier.padding(10.dp)
                            )
                        }
                    }
                    Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                        Text(
                            item.serviceType ?: "Servicio",
                            style = MaterialTheme.typography.titleMedium,
                            color = ClientInk
                        )
                        Text(
                            item.serviceDate ?: "Fecha no disponible",
                            style = MaterialTheme.typography.bodySmall,
                            color = ClientInk.copy(alpha = 0.62f)
                        )
                    }
                }
                Row {
                    IconButton(onClick = onEdit) {
                        Icon(Icons.Default.Edit, contentDescription = "Editar")
                    }
                    IconButton(onClick = onDelete) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = "Eliminar",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                ClientMetricChip("Km", (item.mileage ?: 0).toString())
                ClientMetricChip("Costo", formatMxn(item.cost ?: 0.0))
            }
            item.description?.takeIf { it.isNotBlank() }?.let {
                androidx.compose.material3.Surface(
                    color = ClientSurfaceMuted,
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp)
                ) {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodySmall,
                        color = ClientInk.copy(alpha = 0.78f),
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp)
                    )
                }
            }
        }
    }
}

@Composable
internal fun ServiceOrderCard(order: ServiceOrder) {
    ElevatedCard(
        colors = CardDefaults.cardColors(containerColor = ClientSurface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = "SERVICIO SOLICITADO",
                        style = MaterialTheme.typography.labelSmall,
                        color = ClientBlue,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        order.serviceType.ifBlank { "Servicio" },
                        style = MaterialTheme.typography.titleMedium,
                        color = ClientInk
                    )
                }
                ClientStatusBadge(
                    text = serviceOrderStatusLabel(order.status),
                    tone = serviceOrderTone(order.status)
                )
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                ServiceOrderMetric(
                    label = "Fecha",
                    value = order.scheduledDate.ifBlank { "Sin fecha" },
                    modifier = Modifier.weight(1f)
                )
                ServiceOrderMetric(
                    label = "Estimado",
                    value = formatMxn(order.estimatedCost ?: 0.0),
                    modifier = Modifier.weight(1f)
                )
            }
            order.costBreakdown?.let { breakdown ->
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        "Refacciones ${formatMxn(breakdown.productsTotalMxn)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = ClientInk.copy(alpha = 0.68f)
                    )
                    Text(
                        "Mano de obra ${formatMxn(breakdown.laborTotalMxn)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = ClientInk.copy(alpha = 0.68f)
                    )
                }
            }
            if (order.completionToken.isNotBlank()) {
                androidx.compose.material3.Surface(
                    color = ClientSky,
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            "Código de confirmación",
                            style = MaterialTheme.typography.bodySmall,
                            color = ClientInk.copy(alpha = 0.68f)
                        )
                        Text(
                            order.completionToken,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = ClientInk
                        )
                    }
                }
            }
            if (order.userNotes.isNotBlank()) {
                Text(
                    order.userNotes,
                    style = MaterialTheme.typography.bodySmall,
                    color = ClientInk.copy(alpha = 0.7f)
                )
            }
        }
    }
}

@Composable
private fun ServiceOrderMetric(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    androidx.compose.material3.Surface(
        modifier = modifier,
        color = ClientSurfaceMuted,
        shape = RoundedCornerShape(14.dp)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.spacedBy(3.dp)
        ) {
            Text(label, style = MaterialTheme.typography.labelSmall, color = ClientInk.copy(alpha = 0.56f))
            Text(value, style = MaterialTheme.typography.titleSmall, color = ClientInk)
        }
    }
}

@Composable
internal fun RecommendationSection(
    recommendations: List<MaintenanceRecommendation>,
    onUseRecommendation: (MaintenanceRecommendation) -> Unit
) {
    ClientPanel {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            ClientSectionHeader(
                eyebrow = "Sugerencias",
                title = "Recomendaciones automáticas",
                description = "Prioriza lo que está más cerca de vencer o ya requiere atención."
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
                    ElevatedCard(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    rec.serviceLabel,
                                    style = MaterialTheme.typography.titleMedium,
                                    color = ClientInk
                                )
                                ClientStatusBadge(
                                    text = statusText,
                                    tone = recommendationTone(rec.status)
                                )
                            }
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                ClientMetricChip("Fecha", rec.dueDate)
                                ClientMetricChip("Km objetivo", rec.dueKm.toString())
                            }
                            ClientSecondaryButton(
                                text = "Usar recomendación",
                                onClick = { onUseRecommendation(rec) },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
        }
    }
}

private fun serviceOrderTone(status: String): ClientBadgeTone {
    return when (status.lowercase()) {
        "completed", "completado", "done" -> ClientBadgeTone.Success
        "scheduled", "programado", "pending", "pendiente" -> ClientBadgeTone.Warning
        "cancelled", "canceled", "cancelado" -> ClientBadgeTone.Danger
        else -> ClientBadgeTone.Info
    }
}

private fun serviceOrderStatusLabel(status: String): String {
    return when (status.lowercase()) {
        "scheduled", "programado" -> "Programado"
        "pending", "pendiente" -> "Pendiente"
        "in_progress", "in progress", "en_proceso" -> "En proceso"
        "completed", "completado", "done" -> "Completado"
        "cancelled", "canceled", "cancelado" -> "Cancelado"
        else -> "En seguimiento"
    }
}

private fun recommendationTone(status: String): ClientBadgeTone {
    return when (status) {
        "due" -> ClientBadgeTone.Danger
        "upcoming" -> ClientBadgeTone.Warning
        else -> ClientBadgeTone.Success
    }
}
