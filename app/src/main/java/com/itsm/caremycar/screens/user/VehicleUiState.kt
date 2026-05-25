package com.itsm.caremycar.screens.user

import com.itsm.caremycar.vehicle.MaintenanceDueSummary
import com.itsm.caremycar.vehicle.Vehicle

data class VehicleUiState(
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val isLoadingReminders: Boolean = false,
    val isDeletingVehicle: Boolean = false,
    val vehicles: List<Vehicle> = emptyList(),
    val reminders: List<MaintenanceDueSummary> = emptyList(),
    val removingVehicleId: String? = null,
    val vehiclePendingDelete: Vehicle? = null,
    val loadError: String? = null,
    val remindersError: String? = null,
    val deleteError: String? = null
)
