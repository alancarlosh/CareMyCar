package com.itsm.caremycar.screens.user

import com.itsm.caremycar.vehicle.Vehicle

data class CarDetailsUiState(
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val vehicle: Vehicle? = null,
    val error: String? = null,
    val successMessage: String? = null
)
