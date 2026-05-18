package com.itsm.caremycar.screens.user.util

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class MarketplaceQuantityValidatorTest {
    @Test
    fun `sanitize quantity keeps digits only`() {
        assertEquals("1234", sanitizeWholeNumberInput("1a2.3-456"))
    }

    @Test
    fun `valid quantity calculates total`() {
        val result = MarketplaceQuantityValidator.validate("2", stock = 5, unitPrice = 120.0)

        assertTrue(result.isValid)
        assertEquals(2, result.quantity)
        assertEquals(240.0, result.estimatedTotal, 0.0)
    }

    @Test
    fun `quantity over stock is invalid`() {
        val result = MarketplaceQuantityValidator.validate("6", stock = 5, unitPrice = 120.0)

        assertFalse(result.isValid)
        assertEquals(0.0, result.estimatedTotal, 0.0)
    }
}
