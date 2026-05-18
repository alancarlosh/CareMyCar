package com.itsm.caremycar.screens.user.util

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class MonthlyCostInputValidatorTest {
    @Test
    fun `rejects invalid numeric input`() {
        val result = MonthlyCostInputValidator.validate(
            monthlyKm = "0",
            kmPerLiter = "13",
            fuelPrice = "24.5",
            maintenancePerKm = "0.8"
        )

        assertTrue(result is FormValidationResult.Invalid)
    }

    @Test
    fun `accepts valid numeric input`() {
        val result = MonthlyCostInputValidator.validate(
            monthlyKm = "1200",
            kmPerLiter = "13",
            fuelPrice = "24.5",
            maintenancePerKm = "0.8"
        )

        assertTrue(result is FormValidationResult.Valid)
        val input = (result as FormValidationResult.Valid).value
        assertEquals(1200.0, input.monthlyKm, 0.0)
        assertEquals(13.0, input.kmPerLiter, 0.0)
        assertEquals(24.5, input.fuelPrice, 0.0)
        assertEquals(0.8, input.maintenancePerKm, 0.0)
    }
}
