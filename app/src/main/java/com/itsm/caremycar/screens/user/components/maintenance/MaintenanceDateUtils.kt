package com.itsm.caremycar.screens.user.components.maintenance

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

@RequiresApi(Build.VERSION_CODES.O)
internal fun millisToDateString(millis: Long): String {
    return Instant.ofEpochMilli(millis)
        .atZone(ZoneId.systemDefault())
        .toLocalDate()
        .toString()
}

@RequiresApi(Build.VERSION_CODES.O)
internal fun orderDateToMillis(orderDate: String): Long? {
    return try {
        LocalDate.parse(orderDate)
            .atStartOfDay(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli()
    } catch (_: Exception) {
        null
    }
}

@RequiresApi(Build.VERSION_CODES.O)
internal fun todayMillis(): Long {
    return LocalDate.now()
        .atStartOfDay(ZoneId.systemDefault())
        .toInstant()
        .toEpochMilli()
}
