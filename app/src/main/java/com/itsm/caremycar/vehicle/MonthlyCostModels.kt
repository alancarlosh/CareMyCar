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
    @SerializedName(value = "monthlyKm", alternate = ["monthly_km"])
    val monthlyKm: Double,
    @SerializedName(value = "litersNeeded", alternate = ["liters_needed"])
    val litersNeeded: Double,
    @SerializedName(value = "fuelCost", alternate = ["fuel_cost"])
    val fuelCost: Double,
    @SerializedName(value = "maintenanceCost", alternate = ["maintenance_cost"])
    val maintenanceCost: Double,
    @SerializedName(value = "totalMonthlyCost", alternate = ["total_monthly_cost"])
    val totalMonthlyCost: Double
)

fun MonthlyCostEstimateResponse.toMonthlyCostEstimate() = MonthlyCostEstimate(
    monthlyKm = monthlyKm,
    litersNeeded = litersNeeded,
    fuelCost = fuelCost,
    maintenanceCost = maintenanceCost,
    totalMonthlyCost = totalMonthlyCost
)
