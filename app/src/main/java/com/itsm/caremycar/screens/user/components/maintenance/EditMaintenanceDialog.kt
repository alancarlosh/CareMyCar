package com.itsm.caremycar.screens.user.components.maintenance

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.itsm.caremycar.screens.user.components.ClientDialog
import com.itsm.caremycar.screens.user.components.ClientDialogAction
import com.itsm.caremycar.screens.user.components.ClientBadgeTone
import com.itsm.caremycar.screens.user.components.ClientSelectField
import com.itsm.caremycar.screens.user.components.ClientDateField
import com.itsm.caremycar.screens.user.components.ClientDatePickerDialog
import com.itsm.caremycar.screens.user.components.clientFieldColors
import com.itsm.caremycar.vehicle.MaintenanceRecord

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun EditMaintenanceDialog(
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

    ClientDialog(
        onDismissRequest = { if (!isSaving) onDismiss() },
        title = "Editar mantenimiento",
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                ClientSelectField(
                    value = serviceType,
                    label = "Tipo",
                    expanded = serviceTypeExpanded,
                    options = serviceTypeOptions,
                    onExpandedChange = { serviceTypeExpanded = it },
                    onOptionSelected = {
                        serviceType = it
                        serviceTypeExpanded = false
                    }
                )
                ClientDateField(
                    value = serviceDate,
                    label = "Fecha",
                    onOpenPicker = { showEditDatePicker = true }
                )
                if (showEditDatePicker) {
                    ClientDatePickerDialog(
                        initialSelectedDateMillis = orderDateToMillis(serviceDate) ?: todayMillis(),
                        selectableDates = object : SelectableDates {
                            override fun isSelectableDate(utcTimeMillis: Long): Boolean = utcTimeMillis >= todayMillis()
                        },
                        onDismissRequest = { showEditDatePicker = false },
                        onDateSelected = { serviceDate = millisToDateString(it) }
                    )
                }
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Descripción") },
                    colors = clientFieldColors()
                )
                OutlinedTextField(
                    value = cost,
                    onValueChange = { cost = it },
                    label = { Text("Costo") },
                    colors = clientFieldColors()
                )
                OutlinedTextField(
                    value = mileage,
                    onValueChange = { mileage = it },
                    label = { Text("Kilometraje") },
                    colors = clientFieldColors()
                )
            }
        },
        dismissButton = {
            ClientDialogAction(
                text = "Cancelar",
                onClick = onDismiss,
                enabled = !isSaving
            )
        },
        confirmButton = {
            ClientDialogAction(
                text = "Guardar",
                onClick = { onSave(serviceType, serviceDate, description, cost, mileage) },
                enabled = !isSaving,
                tone = ClientBadgeTone.Info
            )
        }
    )
}
