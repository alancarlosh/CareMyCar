package com.itsm.caremycar.screens.user

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.itsm.caremycar.repository.VehicleRepository
import com.itsm.caremycar.util.Resource
import com.itsm.caremycar.vehicle.CreateVehicleRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class AddVehicleViewModel @Inject constructor(
    private val vehicleRepository: VehicleRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddVehicleUiState())
    val uiState: StateFlow<AddVehicleUiState> = _uiState.asStateFlow()

    init {
        loadCatalogVehicles()
    }

    fun loadCatalogVehicles() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isCatalogLoading = true, error = null)
            when (val result = vehicleRepository.listCatalogVehicles()) {
                is Resource.Success -> {
                    _uiState.value = _uiState.value.copy(
                        isCatalogLoading = false,
                        catalogVehicles = result.data,
                        error = null
                    )
                }

                is Resource.Error -> {
                    _uiState.value = _uiState.value.copy(
                        isCatalogLoading = false,
                        error = result.message
                    )
                }

                Resource.Loading -> Unit
            }
        }
    }

    fun createVehicle(
        catalogVehicleId: String?,
        year: String,
        mileage: String,
        color: String
    ) {
        val yearInt = year.trim().toIntOrNull()
        val mileageInt = mileage.trim().toIntOrNull()

        if (catalogVehicleId.isNullOrBlank() || yearInt == null || mileageInt == null) {
            _uiState.value = _uiState.value.copy(
                isLoading = false,
                isSuccess = false,
                error = "Selecciona un vehículo del catálogo y completa año/kilometraje."
            )
            return
        }

        val request = CreateVehicleRequest(
            catalogVehicleId = catalogVehicleId,
            year = yearInt,
            currentMileage = mileageInt,
            color = color.trim().ifBlank { null }
        )

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, isSuccess = false, error = null)
            when (val result = vehicleRepository.createVehicle(request)) {
                is Resource.Success -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        isSuccess = true,
                        error = null
                    )
                }

                is Resource.Error -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        isSuccess = false,
                        error = result.message
                    )
                }

                Resource.Loading -> Unit
            }
        }
    }

    fun consumeSuccess() {
        _uiState.value = _uiState.value.copy(isSuccess = false)
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}
