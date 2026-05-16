package com.itsm.caremycar.vehicle

import com.google.gson.annotations.SerializedName

data class MonthlyCostEstimate(
    val monthlyKm: Double,
    val litersNeeded: Double,
    val fuelCost: Double,
    val maintenanceCost: Double,
    val totalMonthlyCost: Double
)

data class MonthlyCostEstimateResponse(
    @SerializedName("monthlyKm")
    val monthlyKm: Double,
    @SerializedName("litersNeeded")
    val litersNeeded: Double,
    @SerializedName("fuelCost")
    val fuelCost: Double,
    @SerializedName("maintenanceCost")
    val maintenanceCost: Double,
    @SerializedName("totalMonthlyCost")
    val totalMonthlyCost: Double
)

fun MonthlyCostEstimateResponse.toMonthlyCostEstimate() = MonthlyCostEstimate(
    monthlyKm = monthlyKm,
    litersNeeded = litersNeeded,
    fuelCost = fuelCost,
    maintenanceCost = maintenanceCost,
    totalMonthlyCost = totalMonthlyCost
)
