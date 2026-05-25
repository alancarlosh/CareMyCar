package com.itsm.caremycar.screens.user

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.Surface
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.DirectionsCar
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.itsm.caremycar.screens.user.components.ClientInk
import com.itsm.caremycar.screens.user.components.ClientMint
import com.itsm.caremycar.screens.user.components.ClientSky
import com.itsm.caremycar.screens.user.components.ClientLoadingPanel
import com.itsm.caremycar.screens.user.components.ClientInlineAlert
import com.itsm.caremycar.screens.user.components.ClientBadgeTone
import com.itsm.caremycar.screens.user.components.ClientPrimaryButton
import com.itsm.caremycar.screens.user.components.ClientPanel
import com.itsm.caremycar.screens.user.components.ClientSectionHeader
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
    var registeredMileage by remember { mutableStateOf("") }
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
        registeredMileage = mileage
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

        Surface(
            color = ClientInk,
            shape = RoundedCornerShape(24.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
                        Text(
                            text = "VEHÍCULO REGISTRADO",
                            style = MaterialTheme.typography.labelSmall,
                            color = ClientMint,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = listOf(make, model).filter { it.isNotBlank() }.joinToString(" "),
                            style = MaterialTheme.typography.titleLarge,
                            color = Color.White
                        )
                    }
                    Surface(
                        color = Color.White.copy(alpha = 0.12f),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.DirectionsCar,
                            contentDescription = null,
                            tint = ClientMint,
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    VehicleInfoCell("Año", year.ifBlank { "-" }, Modifier.weight(1f))
                    VehicleInfoCell("Tipo", vehicleType.ifBlank { "-" }, Modifier.weight(1f))
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    VehicleInfoCell("Combustible", fuelType.ifBlank { "-" }, Modifier.weight(1f))
                    VehicleInfoCell("Transmisión", transmission.ifBlank { "-" }, Modifier.weight(1f))
                }
                VehicleInfoCell("Color", color.ifBlank { "-" }, Modifier.fillMaxWidth())
            }
        }

        ClientSectionHeader(
            eyebrow = "Actualización",
            title = "Kilometraje",
            description = "Mantener este dato al día mejora la precisión de las recomendaciones."
        )

        ClientPanel {
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                Surface(
                    color = ClientSky,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Lectura registrada",
                            style = MaterialTheme.typography.bodyMedium,
                            color = ClientInk.copy(alpha = 0.68f)
                        )
                        Text(
                            text = "${registeredMileage.ifBlank { "-" }} km",
                            style = MaterialTheme.typography.titleMedium,
                            color = ClientInk
                        )
                    }
                }
                OutlinedTextField(
                    value = mileage,
                    onValueChange = { input -> mileage = input.filter { it.isDigit() }.take(8) },
                    label = { Text("Nuevo kilometraje") },
                    placeholder = { Text("Ej. 105000") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    colors = clientFieldColors()
                )
                ClientPrimaryButton(
                    text = "Actualizar kilometraje",
                    onClick = {
                        viewModel.updateVehicle(
                            vehicleId = vehicleId,
                            mileage = mileage
                        )
                    },
                    enabled = !uiState.isSaving && !uiState.isLoading,
                    modifier = Modifier.fillMaxWidth(),
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
    }
}

@Composable
private fun VehicleInfoCell(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        color = Color.White.copy(alpha = 0.09f),
        shape = RoundedCornerShape(14.dp)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.spacedBy(3.dp)
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = Color.White.copy(alpha = 0.62f)
            )
            Text(
                text = value,
                style = MaterialTheme.typography.titleSmall,
                color = Color.White
            )
        }
    }
}
