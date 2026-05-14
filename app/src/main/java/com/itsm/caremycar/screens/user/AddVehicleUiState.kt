package com.itsm.caremycar.screens.user

import com.itsm.caremycar.vehicle.CatalogVehicle

data class AddVehicleUiState(
    val isCatalogLoading: Boolean = false,
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val catalogVehicles: List<CatalogVehicle> = emptyList(),
    val error: String? = null
)
