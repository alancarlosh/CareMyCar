package com.itsm.caremycar.screens.user.util

import java.time.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class MaintenanceInputValidatorTest {
    @Test
    fun `service order rejects past dates`() {
        val result = buildCreateServiceOrderRequest(
            vehicleId = "vehicle-1",
            serviceType = "Cambio de aceite",
            scheduledDate = "2026-05-14",
            notes = "",
            today = LocalDate.of(2026, 5, 15)
        )

        assertTrue(result is FormValidationResult.Invalid)
        assertEquals(
            "No puedes programar una fecha anterior a hoy.",
            (result as FormValidationResult.Invalid).message
        )
    }

    @Test
    fun `service order normalizes valid input`() {
        val result = buildCreateServiceOrderRequest(
            vehicleId = "vehicle-1",
            serviceType = "  Frenos  ",
            scheduledDate = " 2026-05-16 ",
            notes = "  Revisar ruido  ",
            today = LocalDate.of(2026, 5, 15)
        )

        assertTrue(result is FormValidationResult.Valid)
        val request = (result as FormValidationResult.Valid).value
        assertEquals("Frenos", request.serviceType)
        assertEquals("2026-05-16", request.scheduledDate)
        assertEquals("Revisar ruido", request.userNotes)
    }

    @Test
    fun `create maintenance converts optional numeric fields`() {
        val result = buildCreateMaintenanceRequest(
            vehicleId = "vehicle-1",
            serviceType = " Afinación ",
            serviceDate = "2026-05-15",
            description = "  Servicio completo  ",
            cost = " 1250.50 ",
            mileage = " 45000 "
        )

        assertTrue(result is FormValidationResult.Valid)
        val request = (result as FormValidationResult.Valid).value
        assertEquals("Afinación", request.serviceType)
        assertEquals("Servicio completo", request.description)
        assertEquals(1250.50, request.cost ?: 0.0, 0.0)
        assertEquals(45000, request.mileage)
    }

    @Test
    fun `update payload rejects empty changes`() {
        val result = buildMaintenanceUpdatePayload(
            serviceType = "",
            serviceDate = "",
            description = "",
            cost = "",
            mileage = ""
        )

        assertTrue(result is FormValidationResult.Invalid)
        assertEquals(
            "Agrega al menos un cambio para actualizar.",
            (result as FormValidationResult.Invalid).message
        )
    }

    @Test
    fun `update payload includes only provided values`() {
        val result = buildMaintenanceUpdatePayload(
            serviceType = "",
            serviceDate = "2026-05-15",
            description = "",
            cost = "900",
            mileage = ""
        )

        assertTrue(result is FormValidationResult.Valid)
        assertEquals(
            mapOf(
                "service_date" to "2026-05-15",
                "cost" to 900.0
            ),
            (result as FormValidationResult.Valid).value
        )
    }
}
