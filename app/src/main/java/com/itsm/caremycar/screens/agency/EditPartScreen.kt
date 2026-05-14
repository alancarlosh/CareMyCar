package com.itsm.caremycar.screens.agency

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditPartScreen(
    partId: String,
    onNavigateBack: () -> Unit = {},
    viewModel: EditPartViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    var partName by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("") }
    var selectedYear by remember { mutableStateOf("") }
    var selectedModel by remember { mutableStateOf("") }
    var selectedMake by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var quantity by remember { mutableStateOf("") }

    // Initial load
    LaunchedEffect(partId) {
        viewModel.loadPartAndCatalog(partId)
    }

    // Sync form with loaded data
    LaunchedEffect(uiState.part) {
        uiState.part?.let { part ->
            partName = part.name
            selectedCategory = part.category
            selectedYear = part.year?.toString() ?: ""
            selectedModel = part.model ?: ""
            selectedMake = part.make ?: ""
            price = part.price.toString()
            quantity = part.quantity.toString()
        }
    }

    LaunchedEffect(uiState.isSuccess) {
        if (uiState.isSuccess) {
            Toast.makeText(context, "Refacción actualizada con éxito", Toast.LENGTH_SHORT).show()
            viewModel.consumeSuccess()
            onNavigateBack()
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Editar Refacción",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = Color.White
                    )
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color(0xFF4FA3D1)
                ),
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver",
                            tint = Color.White
                        )
                    }
                }
            )
        },
        bottomBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                    .background(if (uiState.isLoading) Color.Gray else Color(0xFF4FA3D1))
                    .clickable(enabled = !uiState.isLoading) {
                        viewModel.updatePart(
                            partId = partId,
                            name = partName,
                            category = selectedCategory,
                            make = selectedMake,
                            year = selectedYear,
                            model = selectedModel,
                            price = price,
                            quantity = quantity
                        )
                    },
                contentAlignment = Alignment.Center
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(color = Color.White)
                } else {
                    Text(
                        "Guardar Cambios",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                }
            }
        },
        containerColor = Color(0xFFF5F5F5)
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            if (uiState.error != null) {
                Text(text = uiState.error!!, color = Color.Red, fontSize = 14.sp)
            }

            AgencyCustomTextField(
                label = "Nombre de la pieza *",
                value = partName,
                onValueChange = { partName = it },
                placeholder = "Nombre"
            )

            AgencyCustomDropdownField(
                label = "Categoría *",
                selectedOption = selectedCategory,
                onOptionSelected = { selectedCategory = it },
                options = listOf("Motor", "Frenos", "Suspensión", "Eléctrico", "Carrocería"),
                placeholder = "Seleccione una categoría"
            )

            AgencyCustomDropdownField(
                label = "Marca",
                selectedOption = selectedMake,
                onOptionSelected = { 
                    selectedMake = it 
                    selectedModel = "" 
                    viewModel.onMakeSelected(it)
                },
                options = uiState.availableMakes,
                placeholder = "Seleccione Marca"
            )

            AgencyCustomDropdownField(
                label = "Modelo",
                selectedOption = selectedModel,
                onOptionSelected = { selectedModel = it },
                options = uiState.availableModels,
                placeholder = "Seleccione Modelo",
                enabled = selectedMake.isNotBlank()
            )

            AgencyCustomDropdownField(
                label = "Año",
                selectedOption = selectedYear,
                onOptionSelected = { selectedYear = it },
                options = uiState.availableYears,
                placeholder = "Seleccione año"
            )

            AgencyCustomTextField(
                label = "Precio *",
                value = price,
                onValueChange = { price = it },
                placeholder = "Ingrese Precio final"
            )

            AgencyCustomTextField(
                label = "Cantidad *",
                value = quantity,
                onValueChange = { quantity = it },
                placeholder = "Ingrese cantidad disponible"
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
