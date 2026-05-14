package com.itsm.caremycar.screens.agency

import com.itsm.caremycar.vehicle.CatalogVehicle
import com.itsm.caremycar.vehicle.Part

data class AddPartsUiState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val createdPart: Part? = null,
    val error: String? = null,
    val catalogVehicles: List<CatalogVehicle> = emptyList(),
    val availableMakes: List<String> = emptyList(),
    val availableModels: List<String> = emptyList(),
    val availableYears: List<String> = (2000..2026).map { it.toString() }.sortedDescending()
)
