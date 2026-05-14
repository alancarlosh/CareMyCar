package com.itsm.caremycar.screens.agency

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.itsm.caremycar.vehicle.Part

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CatalogoRefaccionesScreen(
    onNavigateBack: () -> Unit = {},
    onNavigateToAddParts: () -> Unit = {},
    onNavigateToEditPart: (String) -> Unit = {},
    shouldRefreshOnResume: Boolean = false,
    onRefreshHandled: () -> Unit = {},
    optimisticCreatedPart: Part? = null,
    onOptimisticPartHandled: () -> Unit = {},
    viewModel: CatalogViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showDeleteDialog by remember { mutableStateOf<Part?>(null) }

    LaunchedEffect(shouldRefreshOnResume) {
        if (shouldRefreshOnResume) {
            viewModel.loadParts()
            onRefreshHandled()
        }
    }

    LaunchedEffect(optimisticCreatedPart?.id) {
        optimisticCreatedPart?.let {
            viewModel.insertOrUpdatePartOptimistically(it)
            onOptimisticPartHandled()
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Catálogo de refacciones",
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
        containerColor = Color(0xFFF5F5F5)
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(20.dp)
        ) {

            // Barra de búsqueda
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                shape = RoundedCornerShape(50.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Search, "Buscar", tint = Color.Gray)
                    Spacer(modifier = Modifier.width(12.dp))
                    TextField(
                        value = uiState.searchQuery,
                        onValueChange = { viewModel.onSearchQueryChange(it) },
                        placeholder = { Text("Buscar refacción...", color = Color.Gray) },
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                        ),
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                }
            }

            // Botón "Añadir refacción"
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
                    .clickable { onNavigateToAddParts() },
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF4FA3D1)),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(Icons.Default.Add, "Añadir", tint = Color.White)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Añadir refacción", color = Color.White, fontWeight = FontWeight.Medium)
                }
            }

            if (uiState.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Color(0xFF4FA3D1))
                }
            } else if (uiState.error != null) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = uiState.error!!, color = Color.Red)
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(uiState.parts) { part ->
                        RefaccionListItem(
                            part = part,
                            onEditClick = { onNavigateToEditPart(part.id) },
                            onDeleteClick = { showDeleteDialog = part }
                        )
                    }
                }
            }
        }
    }

    if (showDeleteDialog != null) {
        DeleteConfirmationDialog(
            partName = showDeleteDialog!!.name,
            onDismiss = { showDeleteDialog = null },
            onConfirm = {
                viewModel.deletePart(showDeleteDialog!!.id)
                showDeleteDialog = null
            }
        )
    }
}

@Composable
fun RefaccionListItem(
    part: Part,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFE8F0F5)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Build, null, tint = Color(0xFF4FA3D1), modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(part.name, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text("${part.category} · $${part.price}", color = Color.Gray, fontSize = 14.sp)
                Text("Stock: ${part.quantity}", color = Color.Gray, fontSize = 12.sp)
            }
            IconButton(onClick = onEditClick) {
                Icon(Icons.Default.Edit, null, tint = Color(0xFF4FA3D1))
            }
            IconButton(onClick = onDeleteClick) {
                Icon(Icons.Default.Delete, null, tint = Color(0xFFE53935))
            }
        }
    }
}

@Composable
fun DeleteConfirmationDialog(partName: String, onDismiss: () -> Unit, onConfirm: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("¿Eliminar refacción?") },
        text = { Text("¿Seguro que deseas eliminar \"$partName\" del catálogo?") },
        confirmButton = {
            Button(onClick = onConfirm, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE53935))) {
                Text("Eliminar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
        }
    )
}
