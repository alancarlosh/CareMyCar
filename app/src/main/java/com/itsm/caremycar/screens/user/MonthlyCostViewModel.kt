package com.itsm.caremycar.screens.user

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.itsm.caremycar.repository.VehicleRepository
import com.itsm.caremycar.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MonthlyCostViewModel @Inject constructor(
    private val repository: VehicleRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(MonthlyCostUiState())
    val uiState: StateFlow<MonthlyCostUiState> = _uiState.asStateFlow()

    fun calculate(
        monthlyKm: String,
        kmPerLiter: String,
        fuelPrice: String,
        maintenancePerKm: String
    ) {
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
            _uiState.value = _uiState.value.copy(
                error = "Ingresa valores numéricos válidos para calcular el costo mensual."
            )
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isCalculating = true, error = null)
            when (
                val result = repository.getMonthlyCostEstimate(
                    monthlyKm = monthlyKmValue,
                    kmPerLiter = kmPerLiterValue,
                    fuelPrice = fuelPriceValue,
                    maintenancePerKm = maintenancePerKmValue
                )
            ) {
                is Resource.Success -> {
                    _uiState.value = _uiState.value.copy(
                        isCalculating = false,
                        estimate = result.data,
                        error = null
                    )
                }

                is Resource.Error -> {
                    _uiState.value = _uiState.value.copy(
                        isCalculating = false,
                        error = result.message
                    )
                }

                Resource.Loading -> Unit
            }
        }
    }
}
