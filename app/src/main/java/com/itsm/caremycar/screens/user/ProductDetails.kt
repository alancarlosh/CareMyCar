package com.itsm.caremycar.screens.user

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.ReceiptLong
import androidx.compose.material.icons.outlined.Inventory2
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.Surface
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.itsm.caremycar.screens.user.components.ClientBackground
import com.itsm.caremycar.screens.user.components.ClientFeedbackText
import com.itsm.caremycar.screens.user.components.ClientLoadingPanel
import com.itsm.caremycar.screens.user.components.ClientBadgeTone
import com.itsm.caremycar.screens.user.components.ClientStatusBadge
import com.itsm.caremycar.screens.user.components.ClientInk
import com.itsm.caremycar.screens.user.components.ClientMint
import com.itsm.caremycar.screens.user.components.ClientSky
import com.itsm.caremycar.screens.user.components.ClientBlue
import com.itsm.caremycar.screens.user.components.ClientTopAppBar
import com.itsm.caremycar.screens.user.components.ClientPullToRefresh
import com.itsm.caremycar.screens.user.components.MarketplaceProductCard
import com.itsm.caremycar.screens.user.components.rememberClientFeedback
import com.itsm.caremycar.screens.user.util.formatMxn

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailsScreen(onBack: () -> Unit) {
    ClientBackground {
    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            ClientTopAppBar(
                title = "Productos",
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Regresar")
                    }
                }
            )
        }
    ) { innerPadding ->
        ProductDetailsContent(innerPadding = innerPadding)
    }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailsContent(
    innerPadding: PaddingValues = PaddingValues(),
    viewModel: ProductMarketplaceViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val feedbackMessage by rememberClientFeedback(events = viewModel.events)

    ClientPullToRefresh(
        isRefreshing = uiState.isRefreshing,
        onRefresh = viewModel::refresh,
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (uiState.isLoading && uiState.products.isEmpty()) {
                item {
                    ClientLoadingPanel(
                        title = "Cargando catálogo",
                        description = "Estamos preparando productos y compras recientes."
                    )
                }
            }

            uiState.loadError?.let { msg ->
                item { Text(msg, color = MaterialTheme.colorScheme.error) }
            }
            feedbackMessage?.let { message ->
                item { ClientFeedbackText(message = message) }
            }

            item {
                MarketplaceHeroCard(
                    productCount = uiState.products.size,
                    purchaseCount = uiState.purchases.size
                )
            }

            items(uiState.products, key = { it.id }) { part ->
                MarketplaceProductCard(
                    part = part,
                    isBuying = uiState.isBuying,
                    onBuyNow = { quantity -> viewModel.buyNow(part.id, quantity) }
                )
            }

            item {
                Text(
                    "Mis compras recientes",
                    style = MaterialTheme.typography.titleMedium,
                    color = ClientInk,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            if (uiState.purchases.isEmpty()) {
                item {
                    PurchaseEmptyCard()
                }
            } else {
                items(uiState.purchases, key = { it.id }) { order ->
                    PurchaseCard(
                        productName = order.partName ?: "Producto",
                        quantity = order.quantity,
                        totalPrice = order.totalPrice,
                        status = order.status
                    )
                }
            }
        }
    }
}

@Composable
private fun MarketplaceHeroCard(
    productCount: Int,
    purchaseCount: Int
) {
    ElevatedCard(
        colors = CardDefaults.cardColors(containerColor = ClientInk),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        shape = RoundedCornerShape(24.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        "CATÁLOGO",
                        style = MaterialTheme.typography.labelMedium,
                        color = ClientMint,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        "Tienda de refacciones",
                        style = MaterialTheme.typography.headlineSmall,
                        color = Color.White
                    )
                    Text(
                        "Refacciones verificadas por la agencia",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.7f)
                    )
                }
                Surface(
                    color = Color.White.copy(alpha = 0.11f),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Inventory2,
                        contentDescription = null,
                        tint = ClientMint,
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                MarketplaceSummaryMetric(
                    value = productCount.toString(),
                    label = "productos",
                    modifier = Modifier.weight(1f)
                )
                MarketplaceSummaryMetric(
                    value = purchaseCount.toString(),
                    label = "compras",
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun MarketplaceSummaryMetric(
    value: String,
    label: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        color = Color.White.copy(alpha = 0.09f),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge,
                color = Color.White
            )
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = 0.68f)
            )
        }
    }
}

@Composable
private fun PurchaseEmptyCard() {
    ElevatedCard(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Surface(color = ClientSky, shape = RoundedCornerShape(14.dp)) {
                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.ReceiptLong,
                    contentDescription = null,
                    tint = ClientInk,
                    modifier = Modifier.padding(10.dp)
                )
            }
            Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                Text("Sin compras todavía", style = MaterialTheme.typography.titleSmall, color = ClientInk)
                Text(
                    "Tus pedidos recientes aparecerán aquí.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = ClientInk.copy(alpha = 0.62f)
                )
            }
        }
    }
}

@Composable
private fun PurchaseCard(
    productName: String,
    quantity: Int,
    totalPrice: Double,
    status: String
) {
    ElevatedCard(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
                    Text(
                        text = "COMPRA",
                        style = MaterialTheme.typography.labelSmall,
                        color = ClientBlue,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(productName, style = MaterialTheme.typography.titleMedium, color = ClientInk)
                }
                ClientStatusBadge(
                    text = purchaseStatusLabel(status),
                    tone = purchaseTone(status)
                )
            }
            Surface(
                color = ClientSky.copy(alpha = 0.62f),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    PurchaseDetail(label = "Unidades", value = quantity.toString())
                    PurchaseDetail(label = "Total pagado", value = formatMxn(totalPrice))
                }
            }
        }
    }
}

@Composable
private fun PurchaseDetail(label: String, value: String) {
    Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = ClientInk.copy(alpha = 0.58f)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.titleSmall,
            color = ClientInk
        )
    }
}

private fun purchaseStatusLabel(status: String): String {
    return when (status.lowercase()) {
        "pending", "pendiente" -> "Pendiente"
        "confirmed", "confirmado" -> "Confirmado"
        "completed", "completado", "paid", "pagado" -> "Completado"
        "delivered", "entregado" -> "Entregado"
        "cancelled", "canceled", "cancelado" -> "Cancelado"
        else -> "En proceso"
    }
}

private fun purchaseTone(status: String): ClientBadgeTone {
    return when (status.lowercase()) {
        "completed", "completado", "paid", "pagado" -> ClientBadgeTone.Success
        "pending", "pendiente" -> ClientBadgeTone.Warning
        "cancelled", "canceled", "cancelado" -> ClientBadgeTone.Danger
        else -> ClientBadgeTone.Info
    }
}
