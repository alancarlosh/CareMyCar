package com.itsm.caremycar.screens.user

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import com.bumptech.glide.Glide
import com.itsm.caremycar.session.LogoutViewModel
import com.itsm.caremycar.vehicle.Vehicle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserScreen(
    onAddVehicleClick: () -> Unit = {},
    onVehicleClick: (String) -> Unit = {},
    onLogout: () -> Unit = {},
    shouldRefreshOnResume: Boolean = false,
    onRefreshHandled: () -> Unit = {},
    viewModel: VehicleViewModel = hiltViewModel(),
    logoutViewModel: LogoutViewModel = hiltViewModel()
) {
    val userPrimaryColor = Color(0xFF4FA3D1)
    val uiState by viewModel.uiState.collectAsState()
    val logoutUiState by logoutViewModel.uiState.collectAsState()
    var selectedTab by rememberSaveable { mutableIntStateOf(0) }
    var showLogoutDialog by rememberSaveable { mutableStateOf(false) }
    var showRemindersDialog by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(shouldRefreshOnResume) {
        if (shouldRefreshOnResume) {
            viewModel.refreshHome()
            onRefreshHandled()
        }
    }

    LaunchedEffect(logoutUiState.isLoggedOut) {
        if (logoutUiState.isLoggedOut) {
            logoutViewModel.consumeLoggedOut()
            onLogout()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                colors = topAppBarColors(
                    containerColor = userPrimaryColor,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White,
                    actionIconContentColor = Color.White
                ),
                title = { Text(if (selectedTab == 0) "Mis vehículos" else "Productos") },
                actions = {
                    if (selectedTab == 0) {
                        IconButton(onClick = viewModel::refreshHome) {
                            Icon(Icons.Default.Refresh, contentDescription = "Recargar")
                        }
                    }
                    IconButton(
                        onClick = {
                            viewModel.loadUpcomingReminders()
                            showRemindersDialog = true
                        }
                    ) {
                        BadgedBox(
                            badge = {
                                val count = uiState.reminders.size
                                if (count > 0) {
                                    Badge { Text(if (count > 99) "99+" else count.toString()) }
                                }
                            }
                        ) {
                            Icon(Icons.Default.Notifications, contentDescription = "Buzón")
                        }
                    }
                    IconButton(
                        onClick = { showLogoutDialog = true },
                        enabled = !logoutUiState.isLoggingOut
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.ExitToApp,
                            contentDescription = "Cerrar sesión"
                        )
                    }
                }
            )
        },
        bottomBar = {
            BottomAppBar(
                containerColor = userPrimaryColor,
                contentColor = Color.White
            ) {
                TextButton(
                    onClick = { selectedTab = 0 },
                    modifier = Modifier.weight(1f)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.DirectionsCar,
                            contentDescription = "Mis vehículos",
                            tint = if (selectedTab == 0) Color.White else Color.White.copy(alpha = 0.7f)
                        )
                        Text(
                            text = "  Mis vehículos",
                            color = if (selectedTab == 0) Color.White else Color.White.copy(alpha = 0.7f)
                        )
                    }
                }
                TextButton(
                    onClick = { selectedTab = 1 },
                    modifier = Modifier.weight(1f)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Storefront,
                            contentDescription = "Productos",
                            tint = if (selectedTab == 1) Color.White else Color.White.copy(alpha = 0.7f)
                        )
                        Text(
                            text = "  Productos",
                            color = if (selectedTab == 1) Color.White else Color.White.copy(alpha = 0.7f)
                        )
                    }
                }
            }
        },
        floatingActionButton = {
            if (selectedTab == 0) {
                FloatingActionButton(
                    onClick = onAddVehicleClick,
                    containerColor = userPrimaryColor,
                    contentColor = Color.White
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Agregar vehículo")
                }
            }
        }
    ) { innerPadding ->
        if (selectedTab == 0) {
            UserScreenContent(
                innerPadding = innerPadding,
                uiState = uiState,
                onRetry = viewModel::loadVehicles,
                onVehicleClick = onVehicleClick,
                onDeleteVehicleClick = viewModel::requestDeleteVehicle
            )
        } else {
            ProductDetailsContent(innerPadding = innerPadding)
        }
    }

    if (showRemindersDialog) {
        AlertDialog(
            onDismissRequest = { showRemindersDialog = false },
            title = { Text("Buzón de mantenimiento") },
            text = {
                if (uiState.isLoadingReminders) {
                    CircularProgressIndicator()
                } else if (uiState.reminders.isEmpty()) {
                    Text("No hay vehículos con mantenimiento próximo por ahora.")
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        uiState.reminders.take(6).forEach { reminder ->
                            val dueCount = reminder.items.count { it.status == "due" }
                            val upcomingCount = reminder.items.count { it.status == "upcoming" }
                            Text(
                                text = "${reminder.vehicleLabel}: $dueCount vencido(s), $upcomingCount próximo(s)"
                            )
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showRemindersDialog = false }) {
                    Text("Cerrar")
                }
            }
        )
    }

    val pendingDelete = uiState.vehiclePendingDelete
    if (pendingDelete != null) {
        DeleteVehicleDialog(
            vehicle = pendingDelete,
            isDeleting = uiState.isDeletingVehicle,
            error = uiState.deleteError,
            onConfirm = viewModel::confirmDeleteVehicle,
            onDismiss = viewModel::cancelDeleteVehicle
        )
    }

    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text("Cerrar sesión") },
            text = { Text("¿Seguro que deseas cerrar sesión?") },
            dismissButton = {
                TextButton(
                    onClick = { showLogoutDialog = false },
                    enabled = !logoutUiState.isLoggingOut
                ) {
                    Text("Cancelar")
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showLogoutDialog = false
                        logoutViewModel.logout()
                    },
                    enabled = !logoutUiState.isLoggingOut
                ) {
                    Text("Cerrar sesión")
                }
            }
        )
    }
}

@Composable
private fun UserScreenContent(
    innerPadding: PaddingValues,
    uiState: VehicleUiState,
    onRetry: () -> Unit,
    onVehicleClick: (String) -> Unit,
    onDeleteVehicleClick: (Vehicle) -> Unit
) {
    when {
        uiState.isLoading -> {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }

        uiState.error != null && uiState.vehicles.isEmpty() -> {
            EmptyState(
                modifier = Modifier.padding(innerPadding),
                message = uiState.error,
                actionLabel = "Reintentar",
                onAction = onRetry
            )
        }

        uiState.vehicles.isEmpty() -> {
            EmptyState(
                modifier = Modifier.padding(innerPadding),
                message = "Aún no tienes vehículos registrados.",
                actionLabel = "Actualizar",
                onAction = onRetry
            )
        }

        else -> {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                if (uiState.isLoadingDetail) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.padding(end = 12.dp),
                            strokeWidth = 2.dp
                        )
                        Text("Cargando detalle...")
                    }
                }

                uiState.detailError?.let { detailError ->
                    Text(
                        text = detailError,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                    )
                }

                uiState.deleteError?.let { deleteError ->
                    Text(
                        text = deleteError,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                    )
                }

                LazyColumn(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 10.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(uiState.vehicles, key = { it.id }) { vehicle ->
                        val isRemoving = uiState.removingVehicleId == vehicle.id
                        AnimatedVisibility(
                            visible = !isRemoving,
                            enter = fadeIn() + expandVertically(),
                            exit = fadeOut() + shrinkVertically()
                        ) {
                            VehicleCard(
                                vehicle = vehicle,
                                onClick = { onVehicleClick(vehicle.id) },
                                onDeleteClick = { onDeleteVehicleClick(vehicle) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun EmptyState(
    modifier: Modifier = Modifier,
    message: String,
    actionLabel: String,
    onAction: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center
        )
        Button(
            onClick = onAction,
            modifier = Modifier.padding(top = 16.dp)
        ) {
            Text(actionLabel)
        }
    }
}

@Composable
fun VehicleCard(
    vehicle: Vehicle,
    onClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    val context = LocalContext.current
    val imageUrl = vehicle.imageUrls.firstOrNull()

    ElevatedCard(
        onClick = onClick,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHighest
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (imageUrl != null) {
                AndroidView(
                    factory = { ctx ->
                        android.widget.ImageView(ctx).apply {
                            scaleType = android.widget.ImageView.ScaleType.CENTER_CROP
                        }
                    },
                    modifier = Modifier
                        .width(96.dp)
                        .height(72.dp)
                        .clip(RoundedCornerShape(10.dp)),
                    update = { imageView ->
                        Glide.with(context).load(imageUrl).into(imageView)
                    }
                )
            } else {
                Box(
                    modifier = Modifier
                        .width(96.dp)
                        .height(72.dp)
                        .clip(RoundedCornerShape(10.dp))
                )
            }

            Column(
                modifier = Modifier
                    .padding(start = 12.dp)
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = vehicle.title,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = vehicle.subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "Km: ${vehicle.currentMileage?.toInt() ?: 0}",
                    style = MaterialTheme.typography.labelLarge
                )
            }

            IconButton(onClick = onDeleteClick) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Eliminar vehículo",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@Composable
private fun DeleteVehicleDialog(
    vehicle: Vehicle,
    isDeleting: Boolean,
    error: String?,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = { if (!isDeleting) onDismiss() },
        title = { Text("Eliminar vehículo") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("¿Seguro que deseas eliminar ${vehicle.title}? Esta acción no se puede deshacer.")
                if (error != null) {
                    Text(
                        text = error,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss, enabled = !isDeleting) {
                Text("Cancelar")
            }
        },
        confirmButton = {
            TextButton(onClick = onConfirm, enabled = !isDeleting) {
                if (isDeleting) {
                    CircularProgressIndicator(
                        modifier = Modifier.padding(end = 8.dp),
                        strokeWidth = 2.dp
                    )
                }
                Text("Eliminar")
            }
        }
    )
}