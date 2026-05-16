package com.itsm.caremycar.screens.user

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import com.bumptech.glide.Glide
import com.itsm.caremycar.screens.user.components.ClientBackground
import com.itsm.caremycar.screens.user.components.ClientBlue
import com.itsm.caremycar.screens.user.components.ClientFeedbackText
import com.itsm.caremycar.screens.user.components.ClientInk
import com.itsm.caremycar.screens.user.components.ClientLoadingPanel
import com.itsm.caremycar.screens.user.components.ClientMetricChip
import com.itsm.caremycar.screens.user.components.ClientPrimaryButton
import com.itsm.caremycar.screens.user.components.ClientInlineAlert
import com.itsm.caremycar.screens.user.components.ClientBadgeTone
import com.itsm.caremycar.screens.user.components.ClientVehicleImagePlaceholder
import com.itsm.caremycar.screens.user.components.ClientTopAppBar
import com.itsm.caremycar.screens.user.components.ClientSelectField
import com.itsm.caremycar.screens.user.components.ClientStepHeader
import com.itsm.caremycar.screens.user.components.clientFieldColors
import com.itsm.caremycar.screens.user.components.rememberClientFeedback
import java.util.Locale

@Composable
fun AddVehicle(
    onBack: () -> Unit,
    onVehicleCreated: () -> Unit,
    viewModel: AddVehicleViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var selectedMake by remember { mutableStateOf("") }
    var selectedModel by remember { mutableStateOf("") }
    var makeExpanded by remember { mutableStateOf(false) }
    var modelExpanded by remember { mutableStateOf(false) }
    var year by remember { mutableStateOf("") }
    var mileage by remember { mutableStateOf("") }
    var color by remember { mutableStateOf("") }
    val context = LocalContext.current
    val feedbackMessage by rememberClientFeedback(events = viewModel.events)

    LaunchedEffect(viewModel) {
        viewModel.events.collect { event ->
            if (event is AddVehicleEvent.Created) {
                onVehicleCreated()
            }
        }
    }

    val makes = remember(uiState.catalogVehicles) {
        uiState.catalogVehicles
            .map { it.make }
            .distinct()
            .sortedBy { it.lowercase(Locale.getDefault()) }
    }
    val modelsForMake = remember(uiState.catalogVehicles, selectedMake) {
        uiState.catalogVehicles
            .filter { it.make == selectedMake }
            .map { it.model }
            .sorted()
    }
    val selectedCatalogVehicle = remember(uiState.catalogVehicles, selectedMake, selectedModel) {
        uiState.catalogVehicles.find {
            it.make == selectedMake && it.model == selectedModel
        }
    }

    ClientBackground {
    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            ClientTopAppBar(
                title = "Agregar vehículo",
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Regresar")
                    }
                },
                actions = {
                    TextButton(onClick = onBack) {
                        Text("Cancelar")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Nuevo vehículo",
                    style = MaterialTheme.typography.headlineSmall,
                    color = ClientInk
                )
                Text(
                    text = "Selecciona una ficha del catálogo y agrega los datos propios de tu auto.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = ClientInk.copy(alpha = 0.72f)
                )

                if (uiState.isCatalogLoading) {
                    ClientLoadingPanel(
                        title = "Cargando catálogo",
                        description = "Estamos preparando las fichas disponibles para tu vehículo."
                    )
                }

                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(18.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        ClientStepHeader(
                            step = "01",
                            title = "Elige una ficha"
                        )
                        Text(
                            text = "Primero selecciona marca y modelo del catálogo.",
                            style = MaterialTheme.typography.bodySmall,
                            color = ClientInk.copy(alpha = 0.66f)
                        )

                        ClientSelectField(
                            value = selectedMake,
                            label = "Marca *",
                            expanded = makeExpanded,
                            options = makes,
                            onExpandedChange = { makeExpanded = it },
                            onOptionSelected = { make ->
                                selectedMake = make
                                selectedModel = ""
                                makeExpanded = false
                            }
                        )

                        ClientSelectField(
                            value = selectedModel,
                            label = "Modelo *",
                            expanded = modelExpanded,
                            options = modelsForMake,
                            enabled = selectedMake.isNotBlank(),
                            onExpandedChange = { modelExpanded = it },
                            onOptionSelected = { model ->
                                selectedModel = model
                                modelExpanded = false
                            }
                        )
                    }
                }

                if (selectedCatalogVehicle != null) {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = Color.White
                        ),
                        shape = RoundedCornerShape(18.dp)
                    ) {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(0.dp)
                        ) {
                            val firstImageUrl = selectedCatalogVehicle.imageUrls.firstOrNull()
                            if (firstImageUrl != null) {
                                AndroidView(
                                    factory = { ctx ->
                                        android.widget.ImageView(ctx).apply {
                                            adjustViewBounds = false
                                            scaleType = android.widget.ImageView.ScaleType.CENTER_CROP
                                        }
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(190.dp)
                                        .clip(RoundedCornerShape(topStart = 18.dp, topEnd = 18.dp)),
                                    update = { imageView ->
                                        Glide.with(context).load(firstImageUrl).into(imageView)
                                    }
                                )
                            } else {
                                ClientVehicleImagePlaceholder(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(190.dp)
                                        .clip(RoundedCornerShape(topStart = 18.dp, topEnd = 18.dp)),
                                    label = "Ficha sin imagen"
                                )
                            }
                            Column(
                                modifier = Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                            ClientStepHeader(
                                step = "02",
                                title = "Vehículo seleccionado"
                            )
                            Text(
                                text = "${selectedCatalogVehicle.make} ${selectedCatalogVehicle.model}",
                                style = MaterialTheme.typography.titleLarge,
                                color = ClientInk
                            )
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                ClientMetricChip("Tipo", selectedCatalogVehicle.vehicleType)
                                ClientMetricChip("Combustible", selectedCatalogVehicle.fuelType)
                            }
                            ClientMetricChip("Transmisión", selectedCatalogVehicle.transmission)
                            }
                        }
                    }
                }

                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(18.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        ClientStepHeader(
                            step = "03",
                            title = "Datos de tu auto"
                        )
                        OutlinedTextField(
                            value = year,
                            onValueChange = { year = it },
                            label = { Text("Año *") },
                            colors = clientFieldColors(),
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                        )
                        OutlinedTextField(
                            value = mileage,
                            onValueChange = { mileage = it },
                            label = { Text("Kilometraje actual *") },
                            colors = clientFieldColors(),
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                        )
                        OutlinedTextField(
                            value = color,
                            onValueChange = { color = it },
                            label = { Text("Color") },
                            colors = clientFieldColors(),
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                    }
                }

                uiState.loadError?.let { error ->
                    ClientInlineAlert(
                        text = error,
                        tone = ClientBadgeTone.Danger
                    )
                }

                feedbackMessage?.let { message ->
                    ClientFeedbackText(message = message)
                }

                ClientPrimaryButton(
                    text = "Guardar vehículo",
                    onClick = {
                        viewModel.createVehicle(
                            catalogVehicleId = selectedCatalogVehicle?.id,
                            year = year,
                            mileage = mileage,
                            color = color
                        )
                    },
                    enabled = !uiState.isLoading && selectedCatalogVehicle != null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    leadingContent = {
                        if (uiState.isLoading) {
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
}
