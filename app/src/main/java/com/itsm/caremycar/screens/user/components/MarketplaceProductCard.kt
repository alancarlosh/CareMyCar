package com.itsm.caremycar.screens.user.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.itsm.caremycar.screens.user.util.MarketplaceQuantityValidator
import com.itsm.caremycar.screens.user.util.formatMxn
import com.itsm.caremycar.screens.user.util.sanitizeWholeNumberInput
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
        Box(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(part.name, style = MaterialTheme.typography.titleLarge, color = ClientInk)
                Text(
                    "${part.make.orEmpty()} ${part.model.orEmpty()} ${part.year ?: ""}".trim(),
                    color = ClientInk.copy(alpha = 0.72f)
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    ClientStatusBadge(
                        text = when {
                            part.quantity <= 0 -> "Sin stock"
                            part.quantity <= 3 -> "Stock bajo · ${part.quantity}"
                            else -> "Disponible · ${part.quantity}"
                        },
                        tone = when {
                            part.quantity <= 0 -> ClientBadgeTone.Danger
                            part.quantity <= 3 -> ClientBadgeTone.Warning
                            else -> ClientBadgeTone.Success
                        }
                    )
                    ClientMetricChip("Precio", formatMxn(part.price))
                }
                Text(
                    text = "Cantidad",
                    style = MaterialTheme.typography.labelLarge,
                    color = ClientInk.copy(alpha = 0.68f)
                )
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
                OutlinedTextField(
                    value = quantityText,
                    onValueChange = { input ->
                        quantityText = sanitizeWholeNumberInput(input)
                    },
                    label = { Text("Cantidad a comprar") },
                    colors = clientFieldColors(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
                if (quantityText.isNotBlank() && !quantityState.isValid) {
                    ClientInlineAlert(
                        text = "Ingresa una cantidad entre 1 y ${part.quantity}.",
                        tone = ClientBadgeTone.Danger
                    )
                }
                if (quantityState.isValid) {
                    Text(
                        "Costo estimado: ${formatMxn(quantityState.estimatedTotal)}",
                        style = MaterialTheme.typography.titleSmall,
                        color = ClientBlue
                    )
                }
                ClientPrimaryButton(
                    text = if (part.quantity > 0) "Comprar ahora" else "Sin inventario",
                    onClick = { onBuyNow(quantityState.quantity ?: 0) },
                    enabled = !isBuying && part.quantity > 0 && quantityState.isValid,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}
