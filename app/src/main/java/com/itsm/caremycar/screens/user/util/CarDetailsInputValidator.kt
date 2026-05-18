package com.itsm.caremycar.screens.user.util

internal fun buildVehicleMileageUpdatePayload(
    mileage: String,
    currentMileage: Int
): FormValidationResult<Map<String, Any>> {
    val mileageValue = mileage.trim().toIntOrNull()
        ?: return FormValidationResult.Invalid("El kilometraje debe ser numérico.")

    if (mileageValue < 0) {
        return FormValidationResult.Invalid("El kilometraje no puede ser negativo.")
    }

    if (mileageValue == currentMileage) {
        return FormValidationResult.Valid(emptyMap())
    }

    return FormValidationResult.Valid(mapOf("current_mileage" to mileageValue))
}
