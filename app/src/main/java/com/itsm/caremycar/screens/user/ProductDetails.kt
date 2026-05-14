package com.itsm.caremycar.screens.user

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailsScreen(onBack: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                colors = topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary
                ),
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Regresar")
                    }
                },
                title = { Text("Productos") }
            )
        }
    ) { innerPadding ->
        ProductDetailsContent(innerPadding = innerPadding)
    }
}

@Composable
fun ProductDetailsContent(
    innerPadding: PaddingValues = PaddingValues(),
    viewModel: ProductMarketplaceViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 10.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        if (uiState.isLoading) {
            item {
                Box(modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp)) {
                    CircularProgressIndicator()
                }
            }
        }

        uiState.error?.let { msg ->
            item { Text(msg, color = MaterialTheme.colorScheme.error) }
        }
        uiState.message?.let { msg ->
            item { Text(msg, color = MaterialTheme.colorScheme.primary) }
        }

        item {
            Text("Productos publicados por agencia", style = MaterialTheme.typography.titleMedium)
        }

        items(uiState.products, key = { it.id }) { part ->
            var quantityText by remember(part.id) { mutableStateOf("1") }
            val quantityValue = quantityText.toIntOrNull()
            val validQuantity = quantityValue != null && quantityValue in 1..part.quantity
            val estimatedTotal = if (validQuantity) part.price * (quantityValue ?: 0) else 0.0
            val currentQuantityForButtons = (quantityValue ?: 1).coerceAtLeast(1)

            ElevatedCard(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainerHighest
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
            ) {
                Box(modifier = Modifier.fillMaxSize().padding(12.dp)) {
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(part.name, style = MaterialTheme.typography.titleSmall)
                        Text("${part.make.orEmpty()} ${part.model.orEmpty()} ${part.year ?: ""}".trim())
                        Text("Stock: ${part.quantity} · Precio unitario: ${toMxn(part.price)}")
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Button(
                                onClick = {
                                    val next = (currentQuantityForButtons - 1).coerceAtLeast(1)
                                    quantityText = next.toString()
                                },
                                enabled = part.quantity > 0 && currentQuantityForButtons > 1
                            ) {
                                Text("-")
                            }
                            Button(
                                onClick = {
                                    val next = (currentQuantityForButtons + 1).coerceAtMost(part.quantity)
                                    quantityText = next.toString()
                                },
                                enabled = part.quantity > 0 && currentQuantityForButtons < part.quantity
                            ) {
                                Text("+")
                            }
                        }
                        OutlinedTextField(
                            value = quantityText,
                            onValueChange = { input ->
                                quantityText = input.filter { it.isDigit() }.take(4)
                            },
                            label = { Text("Cantidad a comprar") },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.fillMaxWidth()
                        )
                        if (quantityText.isNotBlank() && !validQuantity) {
                            Text(
                                text = "Ingresa una cantidad entre 1 y ${part.quantity}.",
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                        if (validQuantity) {
                            Text("Costo estimado: ${toMxn(estimatedTotal)}")
                        }
                        Button(
                            onClick = { viewModel.buyNow(part.id, quantityValue ?: 0) },
                            enabled = !uiState.isBuying && part.quantity > 0 && validQuantity
                        ) {
                            Text(if (part.quantity > 0) "Comprar ahora" else "Sin inventario")
                        }
                    }
                }
            }
        }

        item {
            Text("Mis compras recientes", style = MaterialTheme.typography.titleMedium)
        }

        if (uiState.purchases.isEmpty()) {
            item {
                Text("No has realizado compras todavía.")
            }
        } else {
            items(uiState.purchases, key = { it.id }) { order ->
                ElevatedCard(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    androidx.compose.foundation.layout.Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        Text(order.partName ?: "Producto", style = MaterialTheme.typography.titleSmall)
                        Row {
                            Text("Cantidad: ${order.quantity}")
                            Spacer(modifier = Modifier.width(12.dp))
                            Text("Total: $${order.totalPrice}")
                        }
                        Text("Estado: ${order.status}")
                    }
                }
            }
        }
    }
}

private fun toMxn(amount: Double): String {
    val localeMx = Locale.Builder().setLanguage("es").setRegion("MX").build()
    return NumberFormat.getCurrencyInstance(localeMx).format(amount)
}
