package com.itsm.caremycar.screens.user

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.togetherWith
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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.outlined.Payments
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.DirectionsCar
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import com.bumptech.glide.Glide
import com.itsm.caremycar.session.LogoutViewModel
import com.itsm.caremycar.screens.user.components.ClientBackground
import com.itsm.caremycar.screens.user.components.ClientBlue
import com.itsm.caremycar.screens.user.components.ClientDialog
import com.itsm.caremycar.screens.user.components.ClientInk
import com.itsm.caremycar.screens.user.components.ClientLoadingPanel
import com.itsm.caremycar.screens.user.components.ClientMetricChip
import com.itsm.caremycar.screens.user.components.ClientMint
import com.itsm.caremycar.screens.user.components.ClientSky
import com.itsm.caremycar.screens.user.components.ClientTopAppBar
import com.itsm.caremycar.screens.user.components.ClientSurface
import com.itsm.caremycar.screens.user.components.ClientSurfaceMuted
import com.itsm.caremycar.screens.user.components.ClientBadgeTone
import com.itsm.caremycar.screens.user.components.ClientStatusBadge
import com.itsm.caremycar.screens.user.components.ClientPrimaryButton
import com.itsm.caremycar.screens.user.components.ClientSecondaryButton
import com.itsm.caremycar.screens.user.components.ClientVehicleImagePlaceholder
import com.itsm.caremycar.screens.user.components.ClientDialogAction
import com.itsm.caremycar.screens.user.components.ClientInlineAlert
import com.itsm.caremycar.screens.user.components.ClientHeroMetric
import com.itsm.caremycar.screens.user.components.ClientPullToRefresh
import com.itsm.caremycar.vehicle.MaintenanceDueSummary
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

    ClientBackground {
    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            ClientTopAppBar(
                title = when (selectedTab) {
                    0 -> "Mis vehículos"
                    1 -> "Productos"
                    else -> "Costos"
                },
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
                                val count = uiState.reminders.sumOf { reminder ->
                                    reminder.items.count { item ->
                                        item.status == "due" || item.status == "upcoming"
                                    }
                                }
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
            ClientBottomNavigation(
                selectedTab = selectedTab,
                onSelectedTabChange = { selectedTab = it }
            )
        },
        floatingActionButton = {
            if (selectedTab == 0) {
                FloatingActionButton(
                    onClick = onAddVehicleClick,
                    containerColor = ClientBlue,
                    contentColor = Color.White
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Agregar vehículo")
                }
            }
        }
    ) { innerPadding ->
        AnimatedContent(
            targetState = selectedTab,
            transitionSpec = { fadeIn() togetherWith fadeOut() },
            label = "client-tab-content"
        ) { tab ->
            when (tab) {
                0 -> {
                    UserScreenContent(
                        innerPadding = innerPadding,
                        uiState = uiState,
                        onRefresh = viewModel::refreshFromPull,
                        onRetry = viewModel::loadVehicles,
                        onAddVehicleClick = onAddVehicleClick,
                        onVehicleClick = onVehicleClick,
                        onDeleteVehicleClick = viewModel::requestDeleteVehicle
                    )
                }
                1 -> ProductDetailsContent(innerPadding = innerPadding)
                else -> MonthlyCostContent(innerPadding = innerPadding)
            }
        }
    }
    }

    if (showRemindersDialog) {
        ClientDialog(
            onDismissRequest = { showRemindersDialog = false },
            title = "Mantenimiento",
            text = {
                if (uiState.isLoadingReminders) {
                    ClientLoadingPanel(
                        title = "Consultando recordatorios",
                        description = "Revisamos el estado de tus próximos mantenimientos."
                    )
                } else if (uiState.remindersError != null) {
                    ClientInlineAlert(
                        text = uiState.remindersError.orEmpty(),
                        tone = ClientBadgeTone.Danger
                    )
                } else if (uiState.reminders.isEmpty()) {
                    ClientInlineAlert(
                        text = "No tienes mantenimientos vencidos ni próximos por ahora.",
                        tone = ClientBadgeTone.Success
                    )
                } else {
                    MaintenanceInboxContent(reminders = uiState.reminders)
                }
            },
            confirmButton = {
                ClientDialogAction(
                    text = "Cerrar",
                    onClick = { showRemindersDialog = false },
                    tone = ClientBadgeTone.Info
                )
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
        ClientDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = "Cerrar sesión",
            text = { Text("¿Seguro que deseas cerrar sesión?") },
            dismissButton = {
                ClientDialogAction(
                    text = "Cancelar",
                    onClick = { showLogoutDialog = false },
                    enabled = !logoutUiState.isLoggingOut
                )
            },
            confirmButton = {
                ClientDialogAction(
                    text = "Cerrar sesión",
                    onClick = {
                        showLogoutDialog = false
                        logoutViewModel.logout()
                    },
                    enabled = !logoutUiState.isLoggingOut,
                    tone = ClientBadgeTone.Info
                )
            }
        )
    }
}

@Composable
private fun MaintenanceInboxContent(reminders: List<MaintenanceDueSummary>) {
    val dueCount = reminders.sumOf { reminder ->
        reminder.items.count { it.status == "due" }
    }
    val upcomingCount = reminders.sumOf { reminder ->
        reminder.items.count { it.status == "upcoming" }
    }

    Column(
        modifier = Modifier
            .heightIn(max = 450.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Surface(
            color = ClientSky,
            shape = RoundedCornerShape(18.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(9.dp)
            ) {
                Text(
                    text = "Resumen de alertas",
                    style = MaterialTheme.typography.titleMedium,
                    color = ClientInk
                )
                Text(
                    text = "Prioriza los servicios pendientes de tus vehículos.",
                    style = MaterialTheme.typography.bodySmall,
                    color = ClientInk.copy(alpha = 0.66f)
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    ClientStatusBadge(
                        text = "$dueCount vencido(s)",
                        tone = if (dueCount > 0) ClientBadgeTone.Danger else ClientBadgeTone.Neutral
                    )
                    ClientStatusBadge(
                        text = "$upcomingCount próximo(s)",
                        tone = if (upcomingCount > 0) ClientBadgeTone.Warning else ClientBadgeTone.Neutral
                    )
                }
            }
        }

        reminders.take(6).forEach { reminder ->
            MaintenanceReminderVehicleRow(reminder = reminder)
        }
    }
}

@Composable
private fun MaintenanceReminderVehicleRow(reminder: MaintenanceDueSummary) {
    val dueCount = reminder.items.count { it.status == "due" }
    val upcomingCount = reminder.items.count { it.status == "upcoming" }

    Surface(
        color = ClientSurfaceMuted,
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(color = Color.White, shape = RoundedCornerShape(12.dp)) {
                Icon(
                    imageVector = Icons.Outlined.DirectionsCar,
                    contentDescription = null,
                    tint = ClientBlue,
                    modifier = Modifier.padding(10.dp)
                )
            }
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(7.dp)
            ) {
                Text(
                    text = reminder.vehicleLabel,
                    style = MaterialTheme.typography.titleSmall,
                    color = ClientInk
                )
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    if (dueCount > 0) {
                        ClientStatusBadge(
                            text = "$dueCount vencido(s)",
                            tone = ClientBadgeTone.Danger
                        )
                    }
                    if (upcomingCount > 0) {
                        ClientStatusBadge(
                            text = "$upcomingCount próximo(s)",
                            tone = ClientBadgeTone.Warning
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ClientBottomNavigation(
    selectedTab: Int,
    onSelectedTabChange: (Int) -> Unit
) {
    Surface(
        color = ClientInk,
        shadowElevation = 8.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            ClientBottomNavigationItem(
                selected = selectedTab == 0,
                label = "Vehículos",
                icon = { tint -> Icon(Icons.Default.DirectionsCar, contentDescription = "Mis vehículos", tint = tint) },
                onClick = { onSelectedTabChange(0) },
                modifier = Modifier.weight(1f)
            )
            ClientBottomNavigationItem(
                selected = selectedTab == 1,
                label = "Productos",
                icon = { tint -> Icon(Icons.Default.Storefront, contentDescription = "Productos", tint = tint) },
                onClick = { onSelectedTabChange(1) },
                modifier = Modifier.weight(1f)
            )
            ClientBottomNavigationItem(
                selected = selectedTab == 2,
                label = "Costos",
                icon = { tint -> Icon(Icons.Outlined.Payments, contentDescription = "Costos", tint = tint) },
                onClick = { onSelectedTabChange(2) },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun ClientBottomNavigationItem(
    selected: Boolean,
    label: String,
    icon: @Composable (Color) -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        modifier = modifier,
        color = if (selected) ClientSurface else Color.Transparent,
        shape = RoundedCornerShape(18.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 11.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            val tint = if (selected) ClientInk else Color.White.copy(alpha = 0.72f)
            icon(tint)
            Text(
                text = label,
                modifier = Modifier.padding(start = 6.dp),
                style = MaterialTheme.typography.labelMedium,
                color = tint,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun UserScreenContent(
    innerPadding: PaddingValues,
    uiState: VehicleUiState,
    onRefresh: () -> Unit,
    onRetry: () -> Unit,
    onAddVehicleClick: () -> Unit,
    onVehicleClick: (String) -> Unit,
    onDeleteVehicleClick: (Vehicle) -> Unit
) {
    ClientPullToRefresh(
        isRefreshing = uiState.isRefreshing,
        onRefresh = onRefresh,
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
    ) {
        when {
            uiState.isLoading && uiState.vehicles.isEmpty() -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    ClientLoadingPanel(
                        title = "Cargando garaje",
                        description = "Estamos reuniendo tus vehículos y recordatorios."
                    )
                }
            }

            uiState.loadError != null && uiState.vehicles.isEmpty() -> {
                EmptyGarageState(
                    modifier = Modifier,
                    message = uiState.loadError,
                    actionLabel = "Reintentar",
                    onAction = onRetry,
                    secondaryActionLabel = null,
                    onSecondaryAction = null
                )
            }

            uiState.vehicles.isEmpty() -> {
                EmptyGarageState(
                    modifier = Modifier,
                    message = "Aún no tienes vehículos registrados.",
                    actionLabel = "Agregar vehículo",
                    onAction = onAddVehicleClick,
                    secondaryActionLabel = "Actualizar",
                    onSecondaryAction = onRetry
                )
            }

            else -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                ) {
                    GarageHeroCard(
                        vehicleCount = uiState.vehicles.size,
                        reminderCount = uiState.reminders.size,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)
                    )

                    uiState.deleteError?.let { deleteError ->
                        ClientInlineAlert(
                            text = deleteError,
                            tone = ClientBadgeTone.Danger,
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
}

@Composable
private fun EmptyGarageState(
    modifier: Modifier = Modifier,
    message: String,
    actionLabel: String,
    onAction: () -> Unit,
    secondaryActionLabel: String?,
    onSecondaryAction: (() -> Unit)?
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 18.dp),
        verticalArrangement = Arrangement.Center
    ) {
        GarageHeroCard(
            vehicleCount = 0,
            reminderCount = 0
        )

        androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(14.dp))

        ElevatedCard(
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
            shape = RoundedCornerShape(24.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 22.dp, vertical = 28.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Surface(
                    color = ClientSky,
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.DirectionsCar,
                        contentDescription = null,
                        tint = ClientBlue,
                        modifier = Modifier
                            .padding(16.dp)
                            .size(30.dp)
                    )
                }
                Text(
                    text = "Tu cochera está lista",
                    style = MaterialTheme.typography.titleLarge,
                    color = ClientInk
                )
                Text(
                    text = message,
                    style = MaterialTheme.typography.bodyLarge,
                    color = ClientInk.copy(alpha = 0.72f),
                    textAlign = TextAlign.Center
                )
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    ClientPrimaryButton(
                        text = actionLabel,
                        onClick = onAction
                    )
                    if (secondaryActionLabel != null && onSecondaryAction != null) {
                        ClientSecondaryButton(
                            text = secondaryActionLabel,
                            onClick = onSecondaryAction
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun GarageHeroCard(
    vehicleCount: Int,
    reminderCount: Int,
    modifier: Modifier = Modifier
) {
    ElevatedCard(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        shape = RoundedCornerShape(24.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = "Tu cochera",
                        style = MaterialTheme.typography.headlineSmall,
                        color = ClientInk
                    )
                    Text(
                        text = "Control rápido de tus vehículos",
                        style = MaterialTheme.typography.bodyMedium,
                        color = ClientInk.copy(alpha = 0.62f)
                    )
                }
                Surface(
                    color = ClientSky,
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.DirectionsCar,
                        contentDescription = null,
                        tint = ClientBlue,
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                ClientHeroMetric(
                    icon = Icons.Outlined.DirectionsCar,
                    label = "Vehículos",
                    value = vehicleCount.toString(),
                    modifier = Modifier.weight(1f)
                )
                ClientHeroMetric(
                    icon = Icons.Outlined.CalendarMonth,
                    label = "Recordatorios",
                    value = reminderCount.toString(),
                    modifier = Modifier.weight(1f)
                )
            }
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
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        shape = RoundedCornerShape(22.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column {
            if (imageUrl != null) {
                AndroidView(
                    factory = { ctx ->
                        android.widget.ImageView(ctx).apply {
                            scaleType = android.widget.ImageView.ScaleType.CENTER_CROP
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(158.dp)
                        .clip(RoundedCornerShape(topStart = 22.dp, topEnd = 22.dp)),
                    update = { imageView ->
                        Glide.with(context).load(imageUrl).into(imageView)
                    }
                )
            } else {
                ClientVehicleImagePlaceholder(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(158.dp)
                        .clip(RoundedCornerShape(topStart = 22.dp, topEnd = 22.dp)),
                    label = "Vehículo sin foto"
                )
            }

            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(7.dp)
                ) {
                    Text(
                        text = vehicle.title,
                        style = MaterialTheme.typography.titleLarge,
                        color = ClientInk
                    )
                    Text(
                        text = vehicle.subtitle,
                        style = MaterialTheme.typography.bodyMedium,
                        color = ClientInk.copy(alpha = 0.64f)
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        ClientMetricChip(
                            label = "Km",
                            value = (vehicle.currentMileage?.toInt() ?: 0).toString()
                        )
                        vehicle.vehicleType?.takeIf { it.isNotBlank() }?.let { type ->
                            ClientMetricChip(label = "Tipo", value = type)
                        }
                    }
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
}

@Composable
private fun DeleteVehicleDialog(
    vehicle: Vehicle,
    isDeleting: Boolean,
    error: String?,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    ClientDialog(
        onDismissRequest = { if (!isDeleting) onDismiss() },
        title = "Eliminar vehículo",
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
            ClientDialogAction(
                text = "Cancelar",
                onClick = onDismiss,
                enabled = !isDeleting
            )
        },
        confirmButton = {
            ClientDialogAction(
                text = "Eliminar",
                onClick = onConfirm,
                enabled = !isDeleting,
                tone = ClientBadgeTone.Danger,
                leadingContent = {
                    if (isDeleting) {
                        CircularProgressIndicator(
                            modifier = Modifier.padding(end = 8.dp),
                            strokeWidth = 2.dp
                        )
                    }
                }
            )
        }
    )
}
