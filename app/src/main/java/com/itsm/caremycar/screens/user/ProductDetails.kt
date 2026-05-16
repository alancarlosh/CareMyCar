package com.itsm.caremycar.screens.user

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.layout.width
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.itsm.caremycar.screens.user.components.ClientTopAppBar
import com.itsm.caremycar.screens.user.components.ClientHeroMetric
import com.itsm.caremycar.screens.user.components.MarketplaceProductCard
import com.itsm.caremycar.screens.user.components.rememberClientFeedback

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

@Composable
fun ProductDetailsContent(
    innerPadding: PaddingValues = PaddingValues(),
    viewModel: ProductMarketplaceViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val feedbackMessage by rememberClientFeedback(events = viewModel.events)

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 10.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        if (uiState.isLoading) {
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

@Composable
private fun MarketplaceHeroCard(
    productCount: Int,
    purchaseCount: Int
) {
    ElevatedCard(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        shape = RoundedCornerShape(24.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        "Marketplace",
                        style = MaterialTheme.typography.headlineSmall,
                        color = ClientInk
                    )
                    Text(
                        "Refacciones publicadas por agencia",
                        style = MaterialTheme.typography.bodyMedium,
                        color = ClientInk.copy(alpha = 0.66f)
                    )
                }
                Surface(
                    color = ClientSky,
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Inventory2,
                        contentDescription = null,
                        tint = ClientInk,
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                ClientHeroMetric(
                    icon = Icons.Outlined.Inventory2,
                    label = "Productos",
                    value = productCount.toString(),
                    modifier = Modifier.weight(1f)
                )
                ClientHeroMetric(
                    icon = Icons.AutoMirrored.Outlined.ReceiptLong,
                    label = "Compras",
                    value = purchaseCount.toString(),
                    modifier = Modifier.weight(1f)
                )
            }
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
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(productName, style = MaterialTheme.typography.titleMedium, color = ClientInk)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Surface(color = ClientMint.copy(alpha = 0.44f), shape = RoundedCornerShape(999.dp)) {
                    Text(
                        "Cantidad $quantity",
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        style = MaterialTheme.typography.labelMedium,
                        color = ClientInk
                    )
                }
                Surface(color = ClientSky, shape = RoundedCornerShape(999.dp)) {
                    Text(
                        "Total $$totalPrice",
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        style = MaterialTheme.typography.labelMedium,
                        color = ClientInk
                    )
                }
            }
            ClientStatusBadge(
                text = status,
                tone = purchaseTone(status)
            )
        }
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
