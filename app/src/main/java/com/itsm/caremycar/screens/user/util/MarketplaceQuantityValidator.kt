package com.itsm.caremycar.screens.user.util

internal data class MarketplaceQuantityState(
    val quantity: Int?,
    val isValid: Boolean,
    val estimatedTotal: Double
)

internal fun sanitizeWholeNumberInput(input: String, maxLength: Int = 4): String {
    return input.filter { it.isDigit() }.take(maxLength)
}

internal object MarketplaceQuantityValidator {
    fun validate(quantityText: String, stock: Int, unitPrice: Double): MarketplaceQuantityState {
        val quantity = quantityText.toIntOrNull()
        val isValid = quantity != null && quantity in 1..stock
        return MarketplaceQuantityState(
            quantity = quantity,
            isValid = isValid,
            estimatedTotal = if (isValid) unitPrice * quantity else 0.0
        )
    }
}
