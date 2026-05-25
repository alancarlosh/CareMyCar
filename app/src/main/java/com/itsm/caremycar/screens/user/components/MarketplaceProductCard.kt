package com.itsm.caremycar.screens.user.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.itsm.caremycar.screens.user.util.MarketplaceQuantityValidator
import com.itsm.caremycar.screens.user.util.formatMxn
import com.itsm.caremycar.vehicle.Part

@Composable
internal fun MarketplaceProductCard(
    part: Part,
    isBuying: Boolean,
    onBuyNow: (quantity: Int) -> Unit
) {
    var quantityText by remember(part.id) { mutableStateOf("1") }
    val quantityState = MarketplaceQuantityValidator.validate(
        quantityText = quantityText,
        stock = part.quantity,
        unitPrice = part.price
    )
    val currentQuantityForButtons = (quantityState.quantity ?: 1).coerceAtLeast(1)

    ElevatedCard(
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        shape = RoundedCornerShape(22.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
                Text(
                    text = part.name,
                    style = MaterialTheme.typography.titleLarge,
                    color = ClientInk
                )
                Text(
                    text = "${part.make.orEmpty()} ${part.model.orEmpty()} ${part.year ?: ""}".trim(),
                    style = MaterialTheme.typography.bodyLarge,
                    color = ClientInk.copy(alpha = 0.62f)
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                ClientStatusBadge(
                    text = when {
                        part.quantity <= 0 -> "Sin stock"
                        part.quantity <= 3 -> "Últimas ${part.quantity}"
                        else -> "${part.quantity} disponibles"
                    },
                    tone = when {
                        part.quantity <= 0 -> ClientBadgeTone.Danger
                        part.quantity <= 3 -> ClientBadgeTone.Warning
                        else -> ClientBadgeTone.Success
                    }
                )
                ClientStatusBadge(
                    text = formatMxn(part.price),
                    tone = ClientBadgeTone.Info
                )
            }

            Surface(
                color = ClientSurfaceMuted,
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                            Text(
                                text = "Cantidad",
                                style = MaterialTheme.typography.labelLarge,
                                color = ClientInk.copy(alpha = 0.62f)
                            )
                            Text(
                                text = "Selecciona unidades",
                                style = MaterialTheme.typography.bodySmall,
                                color = ClientInk.copy(alpha = 0.55f)
                            )
                        }
                        ClientQuantityStepper(
                            value = currentQuantityForButtons,
                            onDecrease = {
                                val next = (currentQuantityForButtons - 1).coerceAtLeast(1)
                                quantityText = next.toString()
                            },
                            onIncrease = {
                                val next = (currentQuantityForButtons + 1).coerceAtMost(part.quantity)
                                quantityText = next.toString()
                            },
                            canDecrease = part.quantity > 0 && currentQuantityForButtons > 1,
                            canIncrease = part.quantity > 0 && currentQuantityForButtons < part.quantity
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Total estimado",
                            style = MaterialTheme.typography.bodyMedium,
                            color = ClientInk.copy(alpha = 0.66f)
                        )
                        Text(
                            text = if (quantityState.isValid) {
                                formatMxn(quantityState.estimatedTotal)
                            } else {
                                "--"
                            },
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = ClientBlue
                        )
                    }
                }
            }

            if (quantityText.isNotBlank() && !quantityState.isValid && part.quantity > 0) {
                ClientInlineAlert(
                    text = "Selecciona una cantidad entre 1 y ${part.quantity}.",
                    tone = ClientBadgeTone.Danger
                )
            }

            ClientPrimaryButton(
                text = when {
                    part.quantity <= 0 -> "Sin inventario"
                    isBuying -> "Procesando compra..."
                    else -> "Comprar ahora"
                },
                onClick = { onBuyNow(quantityState.quantity ?: 0) },
                enabled = !isBuying && part.quantity > 0 && quantityState.isValid,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
