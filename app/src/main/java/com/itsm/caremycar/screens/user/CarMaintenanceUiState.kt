package com.itsm.caremycar.screens.user

import com.itsm.caremycar.vehicle.MaintenanceRecommendation
import com.itsm.caremycar.vehicle.MaintenanceRecord
import com.itsm.caremycar.vehicle.ServiceOrder
import com.itsm.caremycar.vehicle.ServiceQuote

data class CarMaintenanceUiState(
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val isLoadingOrderQuote: Boolean = false,
    val isSubmittingOrder: Boolean = false,
    val isDeleting: Boolean = false,
    val items: List<MaintenanceRecord> = emptyList(),
    val serviceOrders: List<ServiceOrder> = emptyList(),
    val orderQuote: ServiceQuote? = null,
    val recommendations: List<MaintenanceRecommendation> = emptyList(),
    val selectedItemForEdit: MaintenanceRecord? = null,
    val selectedItemForDelete: MaintenanceRecord? = null,
    val error: String? = null,
    val orderMessage: String? = null
)
