package com.itsm.caremycar.screens.user.util

import com.itsm.caremycar.vehicle.CreateMaintenanceRequest
import com.itsm.caremycar.vehicle.CreateServiceOrderRequest
import java.time.LocalDate
import java.time.format.DateTimeParseException

internal fun buildCreateServiceOrderRequest(
    vehicleId: String,
    serviceType: String,
    scheduledDate: String,
    notes: String,
    today: LocalDate = LocalDate.now()
): FormValidationResult<CreateServiceOrderRequest> {
    val normalizedServiceType = serviceType.trim()
    val normalizedScheduledDate = scheduledDate.trim()

    if (normalizedServiceType.isBlank() || normalizedScheduledDate.isBlank()) {
        return FormValidationResult.Invalid("service_type y scheduled_date son requeridos.")
    }

    val selectedDate = when (
        val dateValidation = validateDate(
            value = normalizedScheduledDate,
            formatError = "scheduled_date debe tener formato YYYY-MM-DD.",
            invalidDateError = "scheduled_date no es una fecha válida."
        )
    ) {
        is DateValidationResult.Valid -> dateValidation.value
        is DateValidationResult.Invalid -> return FormValidationResult.Invalid(dateValidation.message)
    }

    if (selectedDate.isBefore(today)) {
        return FormValidationResult.Invalid("No puedes programar una fecha anterior a hoy.")
    }

    return FormValidationResult.Valid(
        CreateServiceOrderRequest(
            vehicleId = vehicleId,
            serviceType = normalizedServiceType,
            scheduledDate = normalizedScheduledDate,
            estimatedCost = null,
            userNotes = notes.trim().ifBlank { null }
        )
    )
}

internal fun buildCreateMaintenanceRequest(
    vehicleId: String,
    serviceType: String,
    serviceDate: String,
    description: String,
    cost: String,
    mileage: String
): FormValidationResult<CreateMaintenanceRequest> {
    val normalizedServiceType = serviceType.trim()
    val normalizedServiceDate = serviceDate.trim()

    if (normalizedServiceType.isBlank() || normalizedServiceDate.isBlank()) {
        return FormValidationResult.Invalid("service_type y service_date son requeridos (YYYY-MM-DD).")
    }

    if (!isDateFormatValid(normalizedServiceDate)) {
        return FormValidationResult.Invalid("service_date debe tener formato YYYY-MM-DD.")
    }

    val normalizedCost = cost.trim()
    val costValue = normalizedCost.toDoubleOrNull()
    if (normalizedCost.isNotBlank() && costValue == null) {
        return FormValidationResult.Invalid("cost debe ser numérico.")
    }

    val normalizedMileage = mileage.trim()
    val mileageValue = normalizedMileage.toIntOrNull()
    if (normalizedMileage.isNotBlank() && mileageValue == null) {
        return FormValidationResult.Invalid("mileage debe ser entero.")
    }

    return FormValidationResult.Valid(
        CreateMaintenanceRequest(
            vehicleId = vehicleId,
            serviceType = normalizedServiceType,
            serviceDate = normalizedServiceDate,
            description = description.trim().ifBlank { null },
            cost = costValue,
            mileage = mileageValue
        )
    )
}

internal fun buildMaintenanceUpdatePayload(
    serviceType: String,
    serviceDate: String,
    description: String,
    cost: String,
    mileage: String
): FormValidationResult<Map<String, Any>> {
    val payload = mutableMapOf<String, Any>()

    serviceType.trim().takeIf { it.isNotBlank() }?.let { payload["service_type"] = it }
    serviceDate.trim().takeIf { it.isNotBlank() }?.let { normalizedServiceDate ->
        if (!isDateFormatValid(normalizedServiceDate)) {
            return FormValidationResult.Invalid("service_date debe tener formato YYYY-MM-DD.")
        }
        payload["service_date"] = normalizedServiceDate
    }
    description.trim().takeIf { it.isNotBlank() }?.let { payload["description"] = it }

    val normalizedCost = cost.trim()
    if (normalizedCost.isNotBlank()) {
        val costValue = normalizedCost.toDoubleOrNull()
            ?: return FormValidationResult.Invalid("cost debe ser numérico.")
        payload["cost"] = costValue
    }

    val normalizedMileage = mileage.trim()
    if (normalizedMileage.isNotBlank()) {
        val mileageValue = normalizedMileage.toIntOrNull()
            ?: return FormValidationResult.Invalid("mileage debe ser entero.")
        payload["mileage"] = mileageValue
    }

    if (payload.isEmpty()) {
        return FormValidationResult.Invalid("Agrega al menos un cambio para actualizar.")
    }

    return FormValidationResult.Valid(payload)
}

private val dateRegex = Regex("^\\d{4}-\\d{2}-\\d{2}$")

private sealed interface DateValidationResult {
    data class Valid(val value: LocalDate) : DateValidationResult
    data class Invalid(val message: String) : DateValidationResult
}

private fun validateDate(
    value: String,
    formatError: String,
    invalidDateError: String
): DateValidationResult {
    if (!isDateFormatValid(value)) {
        return DateValidationResult.Invalid(formatError)
    }

    return try {
        DateValidationResult.Valid(LocalDate.parse(value))
    } catch (_: DateTimeParseException) {
        DateValidationResult.Invalid(invalidDateError)
    }
}

private fun isDateFormatValid(value: String): Boolean = dateRegex.matches(value)
