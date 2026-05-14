package com.itsm.caremycar.screens.agency

import android.content.ContentValues
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import com.itsm.caremycar.vehicle.Order
import java.io.File
import java.io.FileOutputStream
import java.text.NumberFormat
import java.util.Locale

enum class EstadoPedido(val label: String, val color: Color, val icon: ImageVector) {
    PENDIENTE("Pendiente", Color(0xFFFFA000), Icons.Default.Info),
    CONFIRMADO("Confirmado", Color(0xFF1976D2), Icons.Default.CheckCircle),
    ENTREGADO("Entregado", Color(0xFF388E3C), Icons.Default.LocalShipping),
    CANCELADO("Cancelado", Color(0xFFD32F2F), Icons.Default.Close);

    companion object {
        fun fromString(status: String): EstadoPedido {
            return when (status.lowercase()) {
                "confirmed" -> CONFIRMADO
                "delivered" -> ENTREGADO
                "canceled" -> CANCELADO
                else -> PENDIENTE
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PedidosScreen(
    onNavigateBack: () -> Unit = {},
    viewModel: OrdersAgencyViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    var selectedOrder by remember { mutableStateOf<Order?>(null) }

    LaunchedEffect(uiState.exportedPdfBytes, uiState.exportedPdfFileName) {
        val bytes = uiState.exportedPdfBytes ?: return@LaunchedEffect
        val fileName = uiState.exportedPdfFileName ?: "ventas.pdf"
        val savedAt = savePdfToDownloads(context, fileName, bytes)
        if (savedAt != null) {
            Toast.makeText(context, "PDF exportado en: $savedAt", Toast.LENGTH_LONG).show()
        } else {
            Toast.makeText(context, "No se pudo guardar el PDF", Toast.LENGTH_LONG).show()
        }
        viewModel.consumeExportedPdf()
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Ventas",
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp,
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
                },
                actions = {
                    Box(modifier = Modifier.padding(end = 16.dp)) {
                        Text(
                            text = "Pendientes: ${uiState.pendingCount}",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                }
            )
        },
        containerColor = Color(0xFFF5F5F5),
        floatingActionButton = {}
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(20.dp)
        ) {
            SearchBar(
                searchText = uiState.searchQuery,
                onSearchTextChange = { viewModel.onSearchQueryChange(it) }
            )

            Spacer(modifier = Modifier.height(16.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("Reporte diario de ventas", fontWeight = FontWeight.Bold)
                    Text("Fecha: ${uiState.reportDate ?: "-"}")
                    Text("Ventas: ${toMxn(uiState.reportTotalSales)} · Órdenes: ${uiState.reportTotalOrders}")
                    Text(
                        "Pendientes ${uiState.reportPending} · Confirmadas ${uiState.reportConfirmed} · Entregadas ${uiState.reportDelivered} · Canceladas ${uiState.reportCanceled}",
                        color = Color.Gray,
                        fontSize = 12.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Button(
                        onClick = { viewModel.exportDailyReportPdf(uiState.reportDate) },
                        enabled = !uiState.isExportingPdf
                    ) {
                        if (uiState.isExportingPdf) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                strokeWidth = 2.dp,
                                color = Color.White
                            )
                        } else {
                            Text("Exportar reporte a PDF")
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            uiState.message?.let {
                Text(text = it, color = Color(0xFF2E7D32), fontSize = 13.sp)
            }
            uiState.error?.let {
                Text(text = it, color = Color(0xFFC62828), fontSize = 13.sp)
            }

            if (uiState.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Color(0xFF4FA3D1))
                }
            } else if (uiState.orders.isEmpty()) {
                EmptyState()
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(uiState.orders) { order ->
                        PedidoCard(
                            order = order,
                            onClick = { selectedOrder = order }
                        )
                    }
                }
            }
        }
    }

    if (selectedOrder != null) {
        OrderDetailsDialog(
            order = selectedOrder!!,
            isUpdating = uiState.isUpdatingStatus,
            onDismiss = { selectedOrder = null },
            onStatusChange = { newStatus ->
                viewModel.updateOrderStatus(selectedOrder!!.id, newStatus)
                selectedOrder = null
            }
        )
    }
}

@Composable
private fun SearchBar(
    searchText: String,
    onSearchTextChange: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
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
                value = searchText,
                onValueChange = onSearchTextChange,
                placeholder = { Text("Buscar por cliente o pieza...", color = Color.Gray) },
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
}

@Composable
private fun EmptyState() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(Icons.Default.Inbox, null, modifier = Modifier.size(64.dp), tint = Color.Gray)
            Text("No hay pedidos registrados", color = Color.Gray)
        }
    }
}

@Composable
fun PedidoCard(
    order: Order,
    onClick: () -> Unit
) {
    val status = EstadoPedido.fromString(order.status)
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.size(48.dp).clip(CircleShape).background(status.color.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(status.icon, null, tint = status.color)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(order.clientName, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text(order.partName ?: "Sin nombre", color = Color.Gray, fontSize = 14.sp)
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(toMxn(order.totalPrice), fontWeight = FontWeight.Bold, color = Color(0xFF4FA3D1))
                Text(status.label, color = status.color, fontSize = 12.sp)
            }
        }
    }
}

@Composable
fun OrderDetailsDialog(
    order: Order,
    isUpdating: Boolean,
    onDismiss: () -> Unit,
    onStatusChange: (String) -> Unit
) {
    val allowedTransitions = remember(order.status) { allowedStatusTransitions(order.status) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(modifier = Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Text("Detalles del Pedido", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Text("Cliente: ${order.clientName}")
                Text("Pieza: ${order.partName}")
                Text("Vehículo: ${order.make} ${order.model} (${order.year})")
                Text("VIN: ${order.vin}")
                Text("Total: ${toMxn(order.totalPrice)}")
                HorizontalDivider()
                Text("Cambiar Estado:", fontWeight = FontWeight.Bold)
                if (allowedTransitions.isEmpty()) {
                    Text(
                        text = "Este pedido ya no permite cambios de estado.",
                        color = Color.Gray,
                        fontSize = 13.sp
                    )
                } else {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                        allowedTransitions.forEach { statusKey ->
                            statusButton(
                                estado = EstadoPedido.fromString(statusKey),
                                statusKey = statusKey,
                                enabled = !isUpdating,
                                onStatusChange = onStatusChange
                            )
                        }
                    }
                }
                Button(onClick = onDismiss, modifier = Modifier.fillMaxWidth(), enabled = !isUpdating) { Text("Cerrar") }
            }
        }
    }
}

@Composable
fun statusButton(estado: EstadoPedido, statusKey: String, enabled: Boolean, onStatusChange: (String) -> Unit) {
    IconButton(onClick = { onStatusChange(statusKey) }, enabled = enabled) {
        Icon(estado.icon, contentDescription = estado.label, tint = estado.color)
    }
}

private fun allowedStatusTransitions(currentStatus: String): List<String> {
    return when (currentStatus.lowercase()) {
        "pending" -> listOf("confirmed", "canceled")
        "confirmed" -> listOf("delivered", "canceled")
        else -> emptyList()
    }
}

private fun toMxn(amount: Double): String {
    val localeMx = Locale.Builder().setLanguage("es").setRegion("MX").build()
    return NumberFormat.getCurrencyInstance(localeMx).format(amount)
}

private fun savePdfToDownloads(context: android.content.Context, fileName: String, bytes: ByteArray): String? {
    return try {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val values = ContentValues().apply {
                put(MediaStore.Downloads.DISPLAY_NAME, fileName)
                put(MediaStore.Downloads.MIME_TYPE, "application/pdf")
                put(MediaStore.Downloads.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
                put(MediaStore.Downloads.IS_PENDING, 1)
            }
            val resolver = context.contentResolver
            val uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values) ?: return null
            resolver.openOutputStream(uri)?.use { it.write(bytes) } ?: return null
            values.clear()
            values.put(MediaStore.Downloads.IS_PENDING, 0)
            resolver.update(uri, values, null, null)
            uri.toString()
        } else {
            val dir = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS) ?: return null
            if (!dir.exists()) dir.mkdirs()
            val file = File(dir, fileName)
            FileOutputStream(file).use { it.write(bytes) }
            file.absolutePath
        }
    } catch (_: Exception) {
        null
    }
}
