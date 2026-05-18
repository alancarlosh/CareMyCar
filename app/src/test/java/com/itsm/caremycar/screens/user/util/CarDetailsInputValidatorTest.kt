package com.itsm.caremycar.screens.user.util

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class CarDetailsInputValidatorTest {
    @Test
    fun `rejects non numeric mileage`() {
        val result = buildVehicleMileageUpdatePayload("abc", currentMileage = 1000)

        assertTrue(result is FormValidationResult.Invalid)
    }

    @Test
    fun `returns empty payload when mileage does not change`() {
        val result = buildVehicleMileageUpdatePayload("1000", currentMileage = 1000)

        assertTrue(result is FormValidationResult.Valid)
        assertTrue((result as FormValidationResult.Valid).value.isEmpty())
    }

    @Test
    fun `builds payload when mileage changes`() {
        val result = buildVehicleMileageUpdatePayload("1250", currentMileage = 1000)

        assertTrue(result is FormValidationResult.Valid)
        assertEquals(1250, (result as FormValidationResult.Valid).value["current_mileage"])
    }
}
