package com.itsm.caremycar.vehicle

import com.google.gson.annotations.SerializedName

data class CreateServiceOrderRequest(
    @SerializedName("vehicle_id")
    val vehicleId: String,
    @SerializedName("service_type")
    val serviceType: String,
    @SerializedName("scheduled_date")
    val scheduledDate: String,
    @SerializedName("estimated_cost")
    val estimatedCost: Double? = null,
    @SerializedName("user_notes")
    val userNotes: String? = null
)

data class StartServiceOrderRequest(
    @SerializedName("agency_notes")
    val agencyNotes: String? = null
)

data class CompleteServiceOrderRequest(
    @SerializedName("completion_token")
    val completionToken: String,
    @SerializedName("final_cost")
    val finalCost: Double? = null,
    @SerializedName("agency_notes")
    val agencyNotes: String? = null,
    val mileage: Int? = null
)

data class CancelServiceOrderRequest(
    @SerializedName("agency_notes")
    val agencyNotes: String? = null
)

data class ServiceOrderListResponse(
    val items: List<ServiceOrderDto>
)

data class ServiceOrderDetailResponse(
    val order: ServiceOrderDto
)

data class ServiceOrderQuoteResponse(
    @SerializedName("vehicle_id")
    val vehicleId: String,
    @SerializedName("service_type")
    val serviceType: String,
    val quote: ServiceQuoteDto
)

data class ServiceQuoteDto(
    @SerializedName("service_key")
    val serviceKey: String,
    val prediction: ServiceQuotePredictionDto?,
    val products: List<ServiceQuoteProductDto>?,
    @SerializedName("products_total_mxn")
    val productsTotalMxn: Double?,
    @SerializedName("labor_total_mxn")
    val laborTotalMxn: Double?,
    @SerializedName("suggested_total_mxn")
    val suggestedTotalMxn: Double?
)

data class ServiceQuotePredictionDto(
    @SerializedName("estimated_cost_mxn")
    val estimatedCostMxn: Double?,
    @SerializedName("service_type")
    val serviceType: String?,
    @SerializedName("model_used")
    val modelUsed: String?
)

data class ServiceQuoteProductDto(
    val sku: String?,
    val name: String?,
    val qty: Int?,
    @SerializedName("unit_price_mxn")
    val unitPriceMxn: Double?
)

data class ServiceQuoteProduct(
    val sku: String,
    val name: String,
    val qty: Int,
    val unitPriceMxn: Double
)

data class ServiceQuote(
    val serviceKey: String,
    val modelUsed: String,
    val estimatedCostMxn: Double,
    val products: List<ServiceQuoteProduct>,
    val productsTotalMxn: Double,
    val laborTotalMxn: Double,
    val suggestedTotalMxn: Double
)

data class ServiceOrderVehicleSnapshotDto(
    val make: String?,
    val model: String?,
    val year: Int?
)

data class ServiceOrderVehicleSnapshot(
    val make: String?,
    val model: String?,
    val year: Int?
) {
    val label: String
        get() = listOfNotNull(make, model).joinToString(" ").ifBlank { "Vehículo" }
}

data class ServiceOrderDto(
    val id: String,
    @SerializedName("user_id")
    val userId: String,
    @SerializedName("vehicle_id")
    val vehicleId: String,
    @SerializedName("vehicle_snapshot")
    val vehicleSnapshot: ServiceOrderVehicleSnapshotDto?,
    @SerializedName("service_type")
    val serviceType: String?,
    @SerializedName("scheduled_date")
    val scheduledDate: String?,
    val status: String?,
    @SerializedName("estimated_cost")
    val estimatedCost: Double?,
    @SerializedName("final_cost")
    val finalCost: Double?,
    @SerializedName("cost_breakdown")
    val costBreakdown: ServiceOrderCostBreakdownDto?,
    @SerializedName("user_notes")
    val userNotes: String?,
    @SerializedName("agency_notes")
    val agencyNotes: String?,
    @SerializedName("completion_token")
    val completionToken: String?,
    @SerializedName("check_in_at")
    val checkInAt: String?,
    @SerializedName("completed_at")
    val completedAt: String?,
    @SerializedName("created_at")
    val createdAt: String?,
    @SerializedName("updated_at")
    val updatedAt: String?,
    @SerializedName("user_name")
    val userName: String?,
    @SerializedName("user_email")
    val userEmail: String?
)

data class ServiceOrderCostBreakdownDto(
    val prediction: ServiceQuotePredictionDto?,
    val products: List<ServiceQuoteProductDto>?,
    @SerializedName("products_total_mxn")
    val productsTotalMxn: Double?,
    @SerializedName("labor_total_mxn")
    val laborTotalMxn: Double?
)

data class ServiceOrderCostBreakdown(
    val estimatedCostMxn: Double,
    val modelUsed: String,
    val products: List<ServiceQuoteProduct>,
    val productsTotalMxn: Double,
    val laborTotalMxn: Double
)

data class ServiceOrder(
    val id: String,
    val userId: String,
    val vehicleId: String,
    val vehicleSnapshot: ServiceOrderVehicleSnapshot,
    val serviceType: String,
    val scheduledDate: String,
    val status: String,
    val estimatedCost: Double?,
    val finalCost: Double?,
    val costBreakdown: ServiceOrderCostBreakdown?,
    val userNotes: String,
    val agencyNotes: String,
    val completionToken: String,
    val userName: String,
    val userEmail: String
)

fun ServiceOrderCostBreakdownDto.toServiceOrderCostBreakdown(): ServiceOrderCostBreakdown {
    return ServiceOrderCostBreakdown(
        estimatedCostMxn = prediction?.estimatedCostMxn ?: 0.0,
        modelUsed = prediction?.modelUsed.orEmpty(),
        products = products.orEmpty().map { it.toServiceQuoteProduct() },
        productsTotalMxn = productsTotalMxn ?: 0.0,
        laborTotalMxn = laborTotalMxn ?: 0.0
    )
}

fun ServiceQuoteProductDto.toServiceQuoteProduct(): ServiceQuoteProduct {
    return ServiceQuoteProduct(
        sku = sku.orEmpty(),
        name = name.orEmpty(),
        qty = qty ?: 0,
        unitPriceMxn = unitPriceMxn ?: 0.0
    )
}

fun ServiceQuoteDto.toServiceQuote(): ServiceQuote {
    return ServiceQuote(
        serviceKey = serviceKey,
        modelUsed = prediction?.modelUsed.orEmpty(),
        estimatedCostMxn = prediction?.estimatedCostMxn ?: 0.0,
        products = products.orEmpty().map { it.toServiceQuoteProduct() },
        productsTotalMxn = productsTotalMxn ?: 0.0,
        laborTotalMxn = laborTotalMxn ?: 0.0,
        suggestedTotalMxn = suggestedTotalMxn ?: 0.0
    )
}

fun ServiceOrderVehicleSnapshotDto.toServiceOrderVehicleSnapshot(): ServiceOrderVehicleSnapshot {
    return ServiceOrderVehicleSnapshot(
        make = make,
        model = model,
        year = year
    )
}

fun ServiceOrderDto.toServiceOrder(): ServiceOrder {
    return ServiceOrder(
        id = id,
        userId = userId,
        vehicleId = vehicleId,
        vehicleSnapshot = (vehicleSnapshot ?: ServiceOrderVehicleSnapshotDto(null, null, null)).toServiceOrderVehicleSnapshot(),
        serviceType = serviceType.orEmpty(),
        scheduledDate = scheduledDate.orEmpty(),
        status = status.orEmpty(),
        estimatedCost = estimatedCost,
        finalCost = finalCost,
        costBreakdown = costBreakdown?.toServiceOrderCostBreakdown(),
        userNotes = userNotes.orEmpty(),
        agencyNotes = agencyNotes.orEmpty(),
        completionToken = completionToken.orEmpty(),
        userName = userName.orEmpty(),
        userEmail = userEmail.orEmpty()
    )
}
