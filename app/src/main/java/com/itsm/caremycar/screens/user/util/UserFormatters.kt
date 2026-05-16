package com.itsm.caremycar.screens.user.util

import java.text.NumberFormat
import java.util.Locale

private val mxLocale = Locale.Builder().setLanguage("es").setRegion("MX").build()

internal fun formatMxn(amount: Double): String {
    return NumberFormat.getCurrencyInstance(mxLocale).format(amount)
}
