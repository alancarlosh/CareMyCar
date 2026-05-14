package com.itsm.caremycar.screens.agency

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowDown
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
fun AddOrderScreen(
    onNavigateBack: () -> Unit = {},
    viewModel: AddOrderViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    var clientName by remember { mutableStateOf("") }
    var vin by remember { mutableStateOf("") }
    var selectedYear by remember { mutableStateOf("") }
    var selectedMake by remember { mutableStateOf("") }
    var selectedModel by remember { mutableStateOf("") }
    var selectedPartName by remember { mutableStateOf("") }
    var selectedQuantity by remember { mutableStateOf("") }

    LaunchedEffect(uiState.isSuccess) {
        if (uiState.isSuccess) {
            Toast.makeText(context, "Pedido creado con éxito", Toast.LENGTH_SHORT).show()
            viewModel.consumeSuccess()
            onNavigateBack()
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Añadir Pedido",
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
                        viewModel.createOrder(
                            clientName = clientName,
                            vin = vin,
                            make = selectedMake,
                            year = selectedYear,
                            model = selectedModel,
                            quantity = selectedQuantity
                        )
                    },
                contentAlignment = Alignment.Center
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(color = Color.White)
                } else {
                    Text(
                        "Confirmar Pedido",
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
                label = "Ingrese nombre del cliente *",
                value = clientName,
                onValueChange = { clientName = it },
                placeholder = "Nombre"
            )

            AgencyCustomTextField(
                label = "Ingrese VIN *",
                value = vin,
                onValueChange = { vin = it },
                placeholder = "VIN del vehículo"
            )

            AgencyCustomDropdownField(
                label = "Marca *",
                selectedOption = selectedMake,
                onOptionSelected = { 
                    selectedMake = it 
                    selectedModel = ""
                    selectedPartName = ""
                    viewModel.onMakeSelected(it)
                },
                options = uiState.availableMakes,
                placeholder = "Seleccione Marca"
            )

            AgencyCustomDropdownField(
                label = "Año del Modelo *",
                selectedOption = selectedYear,
                onOptionSelected = { selectedYear = it },
                options = uiState.availableYears,
                placeholder = "Año"
            )

            AgencyCustomDropdownField(
                label = "Modelo *",
                selectedOption = selectedModel,
                onOptionSelected = { 
                    selectedModel = it 
                    selectedPartName = ""
                    viewModel.onModelSelected(selectedMake, it)
                },
                options = uiState.availableModels,
                placeholder = "Modelo",
                enabled = selectedMake.isNotBlank()
            )

            AgencyCustomDropdownField(
                label = "Seleccione Refacción *",
                selectedOption = selectedPartName,
                onOptionSelected = { 
                    selectedPartName = it 
                    viewModel.onPartSelected(it)
                },
                options = uiState.availablePartsForModel.map { it.name },
                placeholder = "Pieza",
                enabled = selectedModel.isNotBlank()
            )

            AgencyCustomDropdownField(
                label = "Cantidad *",
                selectedOption = selectedQuantity,
                onOptionSelected = { selectedQuantity = it },
                options = (1..5).map { it.toString() },
                placeholder = "Cantidad",
                enabled = selectedPartName.isNotBlank()
            )

            if (uiState.selectedPart != null) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F0F5))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Detalles de la pieza:", fontWeight = FontWeight.Bold)
                        Text("Precio Unitario: $${uiState.selectedPart!!.price}")
                        Text("Stock Disponible: ${uiState.selectedPart!!.quantity}")
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
