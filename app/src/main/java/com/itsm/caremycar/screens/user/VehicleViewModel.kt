package com.itsm.caremycar.screens.user

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.itsm.caremycar.repository.VehicleRepository
import com.itsm.caremycar.util.Resource
import com.itsm.caremycar.vehicle.Vehicle
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VehicleViewModel @Inject constructor(
    private val vehicleRepository: VehicleRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(VehicleUiState())
    val uiState: StateFlow<VehicleUiState> = _uiState.asStateFlow()

    init {
        refreshHome()
    }

    fun refreshHome() {
        loadVehicles()
        loadUpcomingReminders()
    }

    fun loadVehicles() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null, detailError = null)
            when (val result = vehicleRepository.listVehicles()) {
                is Resource.Success -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        vehicles = result.data,
                        error = null
                    )
                }

                is Resource.Error -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = result.message
                    )
                }

                Resource.Loading -> Unit
            }
        }
    }

    fun loadUpcomingReminders() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoadingReminders = true)
            when (val result = vehicleRepository.getMaintenanceUpcoming()) {
                is Resource.Success -> {
                    _uiState.value = _uiState.value.copy(
                        isLoadingReminders = false,
                        reminders = result.data
                    )
                }

                is Resource.Error -> {
                    _uiState.value = _uiState.value.copy(
                        isLoadingReminders = false,
                        error = _uiState.value.error ?: result.message
                    )
                }

                Resource.Loading -> Unit
            }
        }
    }

    fun loadVehicleDetail(vehicleId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoadingDetail = true, detailError = null)
            when (val result = vehicleRepository.getVehicleById(vehicleId)) {
                is Resource.Success -> {
                    _uiState.value = _uiState.value.copy(
                        isLoadingDetail = false,
                        selectedVehicle = result.data,
                        detailError = null
                    )
                }

                is Resource.Error -> {
                    _uiState.value = _uiState.value.copy(
                        isLoadingDetail = false,
                        detailError = result.message
                    )
                }

                Resource.Loading -> Unit
            }
        }
    }

    fun clearSelectedVehicle() {
        _uiState.value = _uiState.value.copy(selectedVehicle = null, detailError = null)
    }

    fun requestDeleteVehicle(vehicle: Vehicle) {
        _uiState.value = _uiState.value.copy(vehiclePendingDelete = vehicle, deleteError = null)
    }

    fun cancelDeleteVehicle() {
        _uiState.value = _uiState.value.copy(
            vehiclePendingDelete = null,
            isDeletingVehicle = false,
            deleteError = null
        )
    }

    fun confirmDeleteVehicle() {
        val pending = _uiState.value.vehiclePendingDelete ?: return
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isDeletingVehicle = true, deleteError = null)
            when (val result = vehicleRepository.deleteVehicle(pending.id)) {
                is Resource.Success -> {
                    _uiState.value = _uiState.value.copy(
                        isDeletingVehicle = false,
                        vehiclePendingDelete = null,
                        removingVehicleId = pending.id,
                        deleteError = null
                    )

                    delay(260)

                    val remaining = _uiState.value.vehicles.filterNot { it.id == pending.id }
                    val selected = _uiState.value.selectedVehicle?.takeIf { it.id != pending.id }
                    _uiState.value = _uiState.value.copy(
                        vehicles = remaining,
                        selectedVehicle = selected,
                        removingVehicleId = null
                    )
                    loadUpcomingReminders()
                }

                is Resource.Error -> {
                    _uiState.value = _uiState.value.copy(
                        isDeletingVehicle = false,
                        deleteError = result.message
                    )
                }

                Resource.Loading -> Unit
            }
        }
    }

    fun clearErrors() {
        _uiState.value = _uiState.value.copy(error = null, detailError = null, deleteError = null)
    }
}
