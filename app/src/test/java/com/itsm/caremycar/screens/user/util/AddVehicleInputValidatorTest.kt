package com.itsm.caremycar.screens.user.util

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class AddVehicleInputValidatorTest {
    @Test
    fun `create vehicle rejects incomplete input`() {
        val result = buildCreateVehicleRequest(
            catalogVehicleId = null,
            year = "",
            mileage = "",
            color = ""
        )

        assertTrue(result is FormValidationResult.Invalid)
        assertEquals(
            "Selecciona un vehículo del catálogo y completa año/kilometraje.",
            (result as FormValidationResult.Invalid).message
        )
    }

    @Test
    fun `create vehicle normalizes valid input`() {
        val result = buildCreateVehicleRequest(
            catalogVehicleId = "catalog-1",
            year = " 2024 ",
            mileage = " 12500 ",
            color = "  Rojo  "
        )

        assertTrue(result is FormValidationResult.Valid)
        val request = (result as FormValidationResult.Valid).value
        assertEquals("catalog-1", request.catalogVehicleId)
        assertEquals(2024, request.year)
        assertEquals(12500, request.currentMileage)
        assertEquals("Rojo", request.color)
    }
}
