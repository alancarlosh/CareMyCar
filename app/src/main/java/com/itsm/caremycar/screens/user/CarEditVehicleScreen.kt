package com.itsm.caremycar.screens.user

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.itsm.caremycar.screens.user.components.ClientInk
import com.itsm.caremycar.screens.user.components.ClientLoadingPanel
import com.itsm.caremycar.screens.user.components.ClientInlineAlert
import com.itsm.caremycar.screens.user.components.ClientBadgeTone
import com.itsm.caremycar.screens.user.components.ClientPrimaryButton
import com.itsm.caremycar.screens.user.components.ClientPanel
import com.itsm.caremycar.screens.user.components.ClientSectionHeader
import com.itsm.caremycar.screens.user.components.ClientMetricChip
import com.itsm.caremycar.screens.user.components.ClientFeedbackText
import com.itsm.caremycar.screens.user.components.clientFieldColors
import com.itsm.caremycar.screens.user.components.rememberClientFeedback

@Composable
fun CarEditVehicleContent(
    vehicleId: String,
    viewModel: CarDetailsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var make by remember { mutableStateOf("") }
    var model by remember { mutableStateOf("") }
    var year by remember { mutableStateOf("") }
    var mileage by remember { mutableStateOf("") }
    var color by remember { mutableStateOf("") }
    var fuelType by remember { mutableStateOf("") }
    var transmission by remember { mutableStateOf("") }
    var vehicleType by remember { mutableStateOf("") }
    val feedbackMessage by rememberClientFeedback(
        events = viewModel.events,
        resetKey = vehicleId
    )

    LaunchedEffect(vehicleId) {
        viewModel.loadVehicle(vehicleId)
    }

    LaunchedEffect(uiState.vehicle?.id) {
        val vehicle = uiState.vehicle ?: return@LaunchedEffect
        make = vehicle.make.orEmpty()
        model = vehicle.model.orEmpty()
        year = vehicle.year?.toString().orEmpty()
        mileage = vehicle.currentMileage?.toInt()?.toString().orEmpty()
        color = vehicle.color.orEmpty()
        fuelType = vehicle.fuelType.orEmpty()
        transmission = vehicle.transmission.orEmpty()
        vehicleType = vehicle.vehicleType.orEmpty()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        if (uiState.isLoading) {
            ClientLoadingPanel(
                title = "Cargando ficha",
                description = "Estamos recuperando la información más reciente del vehículo."
            )
        }

        uiState.loadError?.let {
            ClientInlineAlert(
                text = it,
                tone = ClientBadgeTone.Danger,
                modifier = Modifier.padding(bottom = 10.dp)
            )
        }

        feedbackMessage?.let {
            ClientFeedbackText(
                message = it,
                modifier = Modifier.padding(bottom = 10.dp)
            )
        }

        ClientSectionHeader(
            eyebrow = "Ficha",
            title = "Datos del vehículo",
            description = "Consulta la información base y actualiza el kilometraje cuando sea necesario."
        )

        ClientPanel {
            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = listOf(make, model).filter { it.isNotBlank() }.joinToString(" "),
                    style = MaterialTheme.typography.titleMedium,
                    color = ClientInk
                )
                androidx.compose.foundation.layout.Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    ClientMetricChip("Año", year.ifBlank { "-" })
                    ClientMetricChip("Tipo", vehicleType.ifBlank { "-" })
                }
                androidx.compose.foundation.layout.Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    ClientMetricChip("Combustible", fuelType.ifBlank { "-" })
                    ClientMetricChip("Caja", transmission.ifBlank { "-" })
                }
                ClientMetricChip("Color", color.ifBlank { "-" })
            }
        }
        ClientSectionHeader(
            eyebrow = "Actualización",
            title = "Kilometraje",
            description = "Mantener este dato al día mejora la precisión de las recomendaciones."
        )
        OutlinedTextField(
            value = mileage,
            onValueChange = { mileage = it },
            label = { Text("Kilometraje actual") },
            modifier = Modifier
                .fillMaxWidth(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            colors = clientFieldColors()
        )
        ClientPrimaryButton(
            text = "Guardar cambios",
            onClick = {
                viewModel.updateVehicle(
                    vehicleId = vehicleId,
                    mileage = mileage
                )
            },
            enabled = !uiState.isSaving && !uiState.isLoading,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 6.dp),
            leadingContent = {
                if (uiState.isSaving) {
                    CircularProgressIndicator(
                        modifier = Modifier.padding(end = 8.dp),
                        strokeWidth = 2.dp
                    )
                }
            }
        )
    }
}
