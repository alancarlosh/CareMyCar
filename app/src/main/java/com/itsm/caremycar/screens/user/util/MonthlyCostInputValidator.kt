package com.itsm.caremycar.screens.user.util

internal data class MonthlyCostInput(
    val monthlyKm: Double,
    val kmPerLiter: Double,
    val fuelPrice: Double,
    val maintenancePerKm: Double
)

internal object MonthlyCostInputValidator {
    fun validate(
        monthlyKm: String,
        kmPerLiter: String,
        fuelPrice: String,
        maintenancePerKm: String
    ): FormValidationResult<MonthlyCostInput> {
        val monthlyKmValue = monthlyKm.toDoubleOrNull()
        val kmPerLiterValue = kmPerLiter.toDoubleOrNull()
        val fuelPriceValue = fuelPrice.toDoubleOrNull()
        val maintenancePerKmValue = maintenancePerKm.toDoubleOrNull()

        if (
            monthlyKmValue == null || monthlyKmValue <= 0 ||
            kmPerLiterValue == null || kmPerLiterValue <= 0 ||
            fuelPriceValue == null || fuelPriceValue <= 0 ||
            maintenancePerKmValue == null || maintenancePerKmValue < 0
        ) {
            return FormValidationResult.Invalid(
                "Ingresa valores numéricos válidos para calcular el costo mensual."
            )
        }

        return FormValidationResult.Valid(
            MonthlyCostInput(
                monthlyKm = monthlyKmValue,
                kmPerLiter = kmPerLiterValue,
                fuelPrice = fuelPriceValue,
                maintenancePerKm = maintenancePerKmValue
            )
        )
    }
}
