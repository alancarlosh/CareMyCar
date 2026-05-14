package com.itsm.caremycar.screens.agency

import com.itsm.caremycar.vehicle.CatalogVehicle
import com.itsm.caremycar.vehicle.Part

data class AddOrderUiState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val error: String? = null,
    
    // Data from Prototype
    val prototypeVehicles: List<CatalogVehicle> = emptyList(),
    
    // Data from Catalog (MongoDB)
    val allCatalogParts: List<Part> = emptyList(),
    
    // Dynamic Filter Lists
    val availableMakes: List<String> = emptyList(),
    val availableModels: List<String> = emptyList(),
    val availablePartsForModel: List<Part> = emptyList(),
    val availableYears: List<String> = (2000..2026).map { it.toString() }.sortedDescending(),
    
    // Selection state
    val selectedPart: Part? = null
)
