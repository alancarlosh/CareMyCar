package com.itsm.caremycar.screens.user.util

import com.itsm.caremycar.vehicle.CreateVehicleRequest

internal fun buildCreateVehicleRequest(
    catalogVehicleId: String?,
    year: String,
    mileage: String,
    color: String
): FormValidationResult<CreateVehicleRequest> {
    val yearValue = year.trim().toIntOrNull()
    val mileageValue = mileage.trim().toIntOrNull()

    if (catalogVehicleId.isNullOrBlank() || yearValue == null || mileageValue == null) {
        return FormValidationResult.Invalid(
            "Selecciona un vehículo del catálogo y completa año/kilometraje."
        )
    }

    return FormValidationResult.Valid(
        CreateVehicleRequest(
            catalogVehicleId = catalogVehicleId,
            year = yearValue,
            currentMileage = mileageValue,
            color = color.trim().ifBlank { null }
        )
    )
}
