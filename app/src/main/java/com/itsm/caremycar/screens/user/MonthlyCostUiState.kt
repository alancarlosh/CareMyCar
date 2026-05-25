package com.itsm.caremycar.screens.user

import com.itsm.caremycar.vehicle.MonthlyCostEstimate

data class MonthlyCostUiState(
    val isCalculating: Boolean = false,
    val isRefreshing: Boolean = false,
    val estimate: MonthlyCostEstimate? = null,
    val error: String? = null
)
