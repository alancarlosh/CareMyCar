package com.itsm.caremycar.screens.agency

import com.itsm.caremycar.vehicle.Part

data class CatalogUiState(
    val isLoading: Boolean = false,
    val parts: List<Part> = emptyList(),
    val error: String? = null,
    val searchQuery: String = "",
    val selectedCategory: String = "all",
    val isDeleting: Boolean = false,
    val deleteSuccess: Boolean = false
)
