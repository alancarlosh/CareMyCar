package com.itsm.caremycar.vehicle

import com.google.gson.annotations.SerializedName

data class Vehicle(
    val id: String,
    val make: String?,
    val model: String?,
    val year: Int?,
    val color: String?,
    val vehicleType: String?,
    val transmission: String?,
    val fuelType: String?,
    val currentMileage: Double?,
    val imageUrls: List<String>
) {
    val title: String
        get() = listOfNotNull(make, model).joinToString(" ").ifBlank { "Vehiculo sin nombre" }

    val subtitle: String
        get() = buildString {
            year?.let { append(it) }
            if (isNotBlank() && !color.isNullOrBlank()) append(" · ")
            color?.takeIf { it.isNotBlank() }?.let { append(it) }
            if (isBlank()) append("Sin datos generales")
        }
}

data class VehicleListResponse(
    val items: List<VehicleDto>
)

data class VehicleDetailResponse(
    val vehicle: VehicleDto
)

data class DeleteVehicleResponse(
    val status: String
)

data class VehicleDto(
    val id: String,
    val make: String?,
    val model: String?,
    val year: Int?,
    val color: String?,
    @SerializedName("vehicle_type")
    val vehicleType: String?,
    val transmission: String?,
    @SerializedName("fuel_type")
    val fuelType: String?,
    @SerializedName("current_mileage")
    val currentMileage: Double?,
    @SerializedName("image_urls")
    val imageUrls: List<String>?
)

data class CreateVehicleRequest(
    @SerializedName("catalog_vehicle_id")
    val catalogVehicleId: String? = null,
    val make: String? = null,
    val model: String? = null,
    val year: Int,
    @SerializedName("current_mileage")
    val currentMileage: Int,
    val color: String? = null,
    @SerializedName("fuel_type")
    val fuelType: String? = null,
    val transmission: String? = null,
    @SerializedName("vehicle_type")
    val vehicleType: String? = null
)

data class CatalogVehicleListResponse(
    val items: List<CatalogVehicleDto>
)

data class CatalogVehicleDto(
    val id: String,
    val make: String,
    val model: String,
    @SerializedName("vehicle_type")
    val vehicleType: String,
    @SerializedName("fuel_type")
    val fuelType: String,
    val transmission: String,
    @SerializedName("image_urls")
    val imageUrls: List<String>?
)

data class CatalogVehicle(
    val id: String,
    val make: String,
    val model: String,
    val vehicleType: String,
    val fuelType: String,
    val transmission: String,
    val imageUrls: List<String>
)

fun VehicleDto.toVehicle(): Vehicle {
    return Vehicle(
        id = id,
        make = make,
        model = model,
        year = year,
        color = color,
        vehicleType = vehicleType,
        transmission = transmission,
        fuelType = fuelType,
        currentMileage = currentMileage,
        imageUrls = imageUrls.orEmpty()
    )
}

fun CatalogVehicleDto.toCatalogVehicle(): CatalogVehicle {
    return CatalogVehicle(
        id = id,
        make = make,
        model = model,
        vehicleType = vehicleType,
        fuelType = fuelType,
        transmission = transmission,
        imageUrls = imageUrls.orEmpty()
    )
}
