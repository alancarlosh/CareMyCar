package com.itsm.caremycar.screens.user

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.itsm.caremycar.repository.VehicleRepository
import com.itsm.caremycar.screens.user.util.FormValidationResult
import com.itsm.caremycar.screens.user.util.MonthlyCostInputValidator
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
        maintenancePerKm: String,
        fromRefresh: Boolean = false
    ) {
        val validation = MonthlyCostInputValidator.validate(
            monthlyKm = monthlyKm,
            kmPerLiter = kmPerLiter,
            fuelPrice = fuelPrice,
            maintenancePerKm = maintenancePerKm
        )
        if (validation is FormValidationResult.Invalid) {
            _uiState.value = _uiState.value.copy(
                isRefreshing = false,
                error = validation.message
            )
            return
        }
        val input = (validation as FormValidationResult.Valid).value

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isCalculating = !fromRefresh,
                isRefreshing = fromRefresh,
                error = null
            )
            when (
                val result = repository.getMonthlyCostEstimate(
                    monthlyKm = input.monthlyKm,
                    kmPerLiter = input.kmPerLiter,
                    fuelPrice = input.fuelPrice,
                    maintenancePerKm = input.maintenancePerKm
                )
            ) {
                is Resource.Success -> {
                    _uiState.value = _uiState.value.copy(
                        isCalculating = false,
                        isRefreshing = false,
                        estimate = result.data,
                        error = null
                    )
                }

                is Resource.Error -> {
                    _uiState.value = _uiState.value.copy(
                        isCalculating = false,
                        isRefreshing = false,
                        error = result.message
                    )
                }

                Resource.Loading -> Unit
            }
        }
    }
}
