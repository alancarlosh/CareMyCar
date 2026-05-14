package com.itsm.caremycar.screens.user

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
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
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
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

    LaunchedEffect(uiState.isSuccess) {
        if (uiState.isSuccess) {
            viewModel.consumeSuccess()
            onVehicleCreated()
        }
    }

    val makes = uiState.catalogVehicles
        .map { it.make }
        .distinct()
        .sortedBy { it.lowercase(Locale.getDefault()) }
    val modelsForMake = uiState.catalogVehicles
        .filter { it.make == selectedMake }
        .map { it.model }
        .sorted()
    val selectedCatalogVehicle = uiState.catalogVehicles.find {
        it.make == selectedMake && it.model == selectedModel
    }

    Scaffold(
        topBar = {
            TopAppBar(
                colors = topAppBarColors(
                    containerColor = Color(0xFF4FA3D1),
                    titleContentColor = MaterialTheme.colorScheme.primary
                ),
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Regresar")
                    }
                },
                title = { Text("Agregar vehículo") },
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
                    text = "Selecciona del catálogo y completa los datos restantes",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                if (uiState.isCatalogLoading) {
                    CircularProgressIndicator(modifier = Modifier.padding(vertical = 8.dp))
                }

                ExposedDropdownMenuBox(
                    expanded = makeExpanded,
                    onExpandedChange = { makeExpanded = !makeExpanded }
                ) {
                    OutlinedTextField(
                        value = selectedMake,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Marca *") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = makeExpanded) },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )
                    DropdownMenu(
                        expanded = makeExpanded,
                        onDismissRequest = { makeExpanded = false }
                    ) {
                        makes.forEach { make ->
                            DropdownMenuItem(
                                text = { Text(make) },
                                onClick = {
                                    selectedMake = make
                                    selectedModel = ""
                                    makeExpanded = false
                                }
                            )
                        }
                    }
                }

                ExposedDropdownMenuBox(
                    expanded = modelExpanded,
                    onExpandedChange = { modelExpanded = !modelExpanded }
                ) {
                    OutlinedTextField(
                        value = selectedModel,
                        onValueChange = {},
                        readOnly = true,
                        enabled = selectedMake.isNotBlank(),
                        label = { Text("Modelo *") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = modelExpanded) },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )
                    DropdownMenu(
                        expanded = modelExpanded,
                        onDismissRequest = { modelExpanded = false }
                    ) {
                        modelsForMake.forEach { model ->
                            DropdownMenuItem(
                                text = { Text(model) },
                                onClick = {
                                    selectedModel = model
                                    modelExpanded = false
                                }
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = selectedCatalogVehicle?.vehicleType.orEmpty(),
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Tipo de vehículo") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    enabled = selectedCatalogVehicle != null
                )
                OutlinedTextField(
                    value = selectedCatalogVehicle?.fuelType.orEmpty(),
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Combustible") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    enabled = selectedCatalogVehicle != null
                )
                OutlinedTextField(
                    value = selectedCatalogVehicle?.transmission.orEmpty(),
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Transmisión") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    enabled = selectedCatalogVehicle != null
                )

                val firstImageUrl = selectedCatalogVehicle?.imageUrls?.firstOrNull()
                if (firstImageUrl != null) {
                    AndroidView(
                        factory = { ctx ->
                            android.widget.ImageView(ctx).apply {
                                adjustViewBounds = false
                                scaleType = android.widget.ImageView.ScaleType.CENTER_CROP
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth(0.92f)
                            .height(190.dp)
                            .align(Alignment.CenterHorizontally)
                            .clip(RoundedCornerShape(14.dp))
                            .padding(vertical = 4.dp),
                        update = { imageView ->
                            Glide.with(context).load(firstImageUrl).into(imageView)
                        }
                    )
                }

                OutlinedTextField(
                    value = year,
                    onValueChange = { year = it },
                    label = { Text("Año *") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                OutlinedTextField(
                    value = mileage,
                    onValueChange = { mileage = it },
                    label = { Text("Kilometraje actual *") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                OutlinedTextField(
                    value = color,
                    onValueChange = { color = it },
                    label = { Text("Color") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                uiState.error?.let { error ->
                    Text(
                        text = error,
                        color = MaterialTheme.colorScheme.error
                    )
                }

                Button(
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
                        .padding(top = 8.dp)
                ) {
                    if (uiState.isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.padding(end = 8.dp),
                            strokeWidth = 2.dp
                        )
                    }
                    Text("Guardar vehículo")
                }
            }
        }
    }
}
