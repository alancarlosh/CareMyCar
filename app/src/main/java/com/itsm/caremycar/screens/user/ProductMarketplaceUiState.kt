package com.itsm.caremycar.screens.user

import com.itsm.caremycar.vehicle.Order
import com.itsm.caremycar.vehicle.Part

data class ProductMarketplaceUiState(
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val isBuying: Boolean = false,
    val products: List<Part> = emptyList(),
    val purchases: List<Order> = emptyList(),
    val loadError: String? = null
)
