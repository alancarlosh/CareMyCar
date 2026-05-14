package com.itsm.caremycar.screens.agency

import com.itsm.caremycar.vehicle.Order

data class OrdersAgencyUiState(
    val isLoading: Boolean = false,
    val isUpdatingStatus: Boolean = false,
    val isExportingPdf: Boolean = false,
    val orders: List<Order> = emptyList(),
    val reportDate: String? = null,
    val reportTotalOrders: Int = 0,
    val reportTotalSales: Double = 0.0,
    val reportPending: Int = 0,
    val reportConfirmed: Int = 0,
    val reportDelivered: Int = 0,
    val reportCanceled: Int = 0,
    val error: String? = null,
    val message: String? = null,
    val exportedPdfBytes: ByteArray? = null,
    val exportedPdfFileName: String? = null,
    val searchQuery: String = "",
    val selectedStatus: String = "all",
    val allCount: Int = 0,
    val pendingCount: Int = 0
)
