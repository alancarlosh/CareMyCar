package com.itsm.caremycar.screens.user.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun ClientSelectField(
    value: String,
    label: String,
    expanded: Boolean,
    options: List<String>,
    enabled: Boolean = true,
    onExpandedChange: (Boolean) -> Unit,
    onOptionSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { onExpandedChange(!expanded) }
    ) {
        OutlinedTextField(
            value = value,
            onValueChange = {},
            readOnly = true,
            enabled = enabled,
            label = { Text(label) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = modifier
                .menuAnchor(MenuAnchorType.PrimaryNotEditable, enabled = enabled)
                .fillMaxWidth(),
            singleLine = true,
            colors = clientFieldColors(),
            shape = RoundedCornerShape(18.dp)
        )
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { onExpandedChange(false) },
            containerColor = ClientSurface
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    colors = MenuDefaults.itemColors(textColor = ClientInk),
                    onClick = { onOptionSelected(option) }
                )
            }
        }
    }
}

@Composable
internal fun ClientDateField(
    value: String,
    label: String,
    onOpenPicker: () -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = value,
        onValueChange = {},
        readOnly = true,
        label = { Text(label) },
        trailingIcon = {
            IconButton(onClick = onOpenPicker) {
                Icon(Icons.Default.DateRange, contentDescription = "Seleccionar fecha")
            }
        },
        modifier = modifier.fillMaxWidth(),
        singleLine = true,
        colors = clientFieldColors(),
        shape = RoundedCornerShape(18.dp)
    )
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
internal fun ClientDatePickerDialog(
    initialSelectedDateMillis: Long,
    selectableDates: SelectableDates,
    onDismissRequest: () -> Unit,
    onDateSelected: (Long) -> Unit
) {
    val pickerState = rememberDatePickerState(
        initialSelectedDateMillis = initialSelectedDateMillis,
        selectableDates = selectableDates
    )
    DatePickerDialog(
        onDismissRequest = onDismissRequest,
        confirmButton = {
            ClientDialogAction(
                text = "Aceptar",
                onClick = {
                    pickerState.selectedDateMillis?.let(onDateSelected)
                    onDismissRequest()
                },
                tone = ClientBadgeTone.Info
            )
        },
        dismissButton = {
            ClientDialogAction(
                text = "Cancelar",
                onClick = onDismissRequest
            )
        },
        colors = androidx.compose.material3.DatePickerDefaults.colors(
            containerColor = Color.White
        )
    ) {
        DatePicker(state = pickerState)
    }
}
