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
import com.itsm.caremycar.vehicle.Part

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddPartsScreen(
    onNavigateBack: () -> Unit = {},
    onPartCreated: (Part) -> Unit = {},
    viewModel: AddPartsViewModel = hiltViewModel()
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

    LaunchedEffect(uiState.isSuccess) {
        if (uiState.isSuccess) {
            Toast.makeText(context, "Refacción añadida con éxito", Toast.LENGTH_SHORT).show()
            uiState.createdPart?.let(onPartCreated)
            viewModel.consumeSuccess()
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Añadir Refacción",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = Color.White
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF4FA3D1),
                    scrolledContainerColor = Color.Unspecified,
                    navigationIconContentColor = Color.Unspecified,
                    titleContentColor = Color.Unspecified,
                    actionIconContentColor = Color.Unspecified
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
                        viewModel.addPart(
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
                        "Confirmar Refacción",
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
                options = listOf("frenos", "suspension", "motor", "transmision", "electrico", "filtros", "aceites", "llantas", "carroceria", "otros"),
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

@Composable
fun AgencyCustomTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            fontSize = 14.sp,
            color = Color.Gray,
            modifier = Modifier.padding(start = 4.dp, bottom = 4.dp)
        )
        TextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text(placeholder, color = Color.LightGray) },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color(0xFFE8F0F5),
                unfocusedContainerColor = Color(0xFFE8F0F5),
                disabledContainerColor = Color(0xFFE8F0F5),
                focusedIndicatorColor = Color(0xFF4FA3D1),
                unfocusedIndicatorColor = Color.Transparent,
            ),
            shape = RoundedCornerShape(8.dp),
            singleLine = true
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AgencyCustomDropdownField(
    label: String,
    selectedOption: String,
    onOptionSelected: (String) -> Unit,
    options: List<String>,
    placeholder: String,
    enabled: Boolean = true
) {
    var expanded by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            fontSize = 14.sp,
            color = if (enabled) Color.Gray else Color.LightGray,
            modifier = Modifier.padding(start = 4.dp, bottom = 4.dp)
        )
        ExposedDropdownMenuBox(
            expanded = expanded && enabled,
            onExpandedChange = { if (enabled) expanded = !expanded }
        ) {
            TextField(
                value = selectedOption,
                onValueChange = {},
                readOnly = true,
                enabled = enabled,
                placeholder = { Text(placeholder, color = Color.LightGray) },
                trailingIcon = {
                    Icon(
                        Icons.Default.KeyboardArrowDown,
                        contentDescription = null,
                        tint = if (enabled) Color(0xFF4FA3D1) else Color.LightGray
                    )
                },
                modifier = Modifier
                    .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                    .fillMaxWidth()
                    .height(56.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color(0xFFE8F0F5),
                    unfocusedContainerColor = Color(0xFFE8F0F5),
                    disabledContainerColor = Color(0xFFF2F2F2),
                    focusedIndicatorColor = Color(0xFF4FA3D1),
                    unfocusedIndicatorColor = Color.Transparent,
                ),
                shape = RoundedCornerShape(8.dp)
            )
            ExposedDropdownMenu(
                expanded = expanded && enabled,
                onDismissRequest = { expanded = false },
                modifier = Modifier.background(Color.White)
            ) {
                options.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option) },
                        onClick = {
                            onOptionSelected(option)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}
