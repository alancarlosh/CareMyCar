package com.itsm.caremycar.vehicle

import com.google.gson.annotations.SerializedName

data class MaintenanceRecord(
    val id: String,
    val serviceType: String?,
    val description: String?,
    val cost: Double?,
    val mileage: Int?,
    val serviceDate: String?
)

data class MaintenanceRecordDto(
    val id: String,
    @SerializedName("service_type")
    val serviceType: String?,
    val description: String?,
    val cost: Double?,
    val mileage: Int?,
    @SerializedName("service_date")
    val serviceDate: String?
)

data class MaintenanceListResponse(
    val items: List<MaintenanceRecordDto>
)

data class MaintenanceDetailResponse(
    val maintenance: MaintenanceRecordDto
)

data class MaintenanceDeleteResponse(
    val status: String
)

data class MaintenanceRecommendationsResponse(
    @SerializedName("vehicle_id")
    val vehicleId: String,
    val recommendations: List<MaintenanceRecommendationDto>
)

data class MaintenanceUpcomingResponse(
    val items: List<MaintenanceDueSummaryDto>
)

data class MaintenanceDueSummaryDto(
    @SerializedName("vehicle_id")
    val vehicleId: String,
    @SerializedName("vehicle_label")
    val vehicleLabel: String?,
    @SerializedName("current_mileage")
    val currentMileage: Int?,
    @SerializedName("user_email")
    val userEmail: String?,
    @SerializedName("user_name")
    val userName: String?,
    val items: List<MaintenanceRecommendationDto>?
)

data class MaintenanceDueSummary(
    val vehicleId: String,
    val vehicleLabel: String,
    val currentMileage: Int,
    val userEmail: String,
    val userName: String,
    val items: List<MaintenanceRecommendation>
)

data class MaintenanceRecommendationDto(
    @SerializedName("service_key")
    val serviceKey: String,
    @SerializedName("service_label")
    val serviceLabel: String,
    @SerializedName("due_date")
    val dueDate: String,
    @SerializedName("due_km")
    val dueKm: Int,
    @SerializedName("days_left")
    val daysLeft: Int,
    @SerializedName("km_left")
    val kmLeft: Int,
    val status: String,
    val recommended: Boolean
)

data class MaintenanceRecommendation(
    val serviceKey: String,
    val serviceLabel: String,
    val dueDate: String,
    val dueKm: Int,
    val daysLeft: Int,
    val kmLeft: Int,
    val status: String,
    val recommended: Boolean
)

data class CreateMaintenanceRequest(
    @SerializedName("vehicle_id")
    val vehicleId: String,
    @SerializedName("service_type")
    val serviceType: String,
    @SerializedName("service_date")
    val serviceDate: String,
    val description: String? = null,
    val cost: Double? = null,
    val mileage: Int? = null
)

fun MaintenanceRecordDto.toMaintenanceRecord(): MaintenanceRecord {
    return MaintenanceRecord(
        id = id,
        serviceType = serviceType,
        description = description,
        cost = cost,
        mileage = mileage,
        serviceDate = serviceDate
    )
}

fun MaintenanceRecommendationDto.toMaintenanceRecommendation(): MaintenanceRecommendation {
    return MaintenanceRecommendation(
        serviceKey = serviceKey,
        serviceLabel = serviceLabel,
        dueDate = dueDate,
        dueKm = dueKm,
        daysLeft = daysLeft,
        kmLeft = kmLeft,
        status = status,
        recommended = recommended
    )
}

fun MaintenanceDueSummaryDto.toMaintenanceDueSummary(): MaintenanceDueSummary {
    return MaintenanceDueSummary(
        vehicleId = vehicleId,
        vehicleLabel = vehicleLabel ?: "Vehículo",
        currentMileage = currentMileage ?: 0,
        userEmail = userEmail.orEmpty(),
        userName = userName.orEmpty(),
        items = items.orEmpty().map { it.toMaintenanceRecommendation() }
    )
}
