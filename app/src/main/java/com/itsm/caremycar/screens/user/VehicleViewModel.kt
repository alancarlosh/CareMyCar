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

    fun refreshFromPull() {
        viewModelScope.launch {
            updateState { copy(isRefreshing = true, loadError = null, remindersError = null) }
            val vehiclesResult = vehicleRepository.listVehicles()
            val remindersResult = vehicleRepository.getMaintenanceUpcoming()

            updateState {
                copy(
                    isRefreshing = false,
                    vehicles = (vehiclesResult as? Resource.Success)?.data ?: vehicles,
                    reminders = (remindersResult as? Resource.Success)?.data ?: reminders,
                    loadError = (vehiclesResult as? Resource.Error)?.message,
                    remindersError = (remindersResult as? Resource.Error)?.message
                )
            }
        }
    }

    fun loadVehicles() {
        viewModelScope.launch {
            updateState { copy(isLoading = true, loadError = null) }
            when (val result = vehicleRepository.listVehicles()) {
                is Resource.Success -> {
                    updateState {
                        copy(
                        isLoading = false,
                        vehicles = result.data,
                        loadError = null
                    )
                    }
                }

                is Resource.Error -> {
                    updateState {
                        copy(
                        isLoading = false,
                        loadError = result.message
                    )
                    }
                }

                Resource.Loading -> Unit
            }
        }
    }

    fun loadUpcomingReminders() {
        viewModelScope.launch {
            updateState { copy(isLoadingReminders = true, remindersError = null) }
            when (val result = vehicleRepository.getMaintenanceUpcoming()) {
                is Resource.Success -> {
                    updateState {
                        copy(
                        isLoadingReminders = false,
                        reminders = result.data,
                        remindersError = null
                    )
                    }
                }

                is Resource.Error -> {
                    updateState {
                        copy(
                        isLoadingReminders = false,
                        remindersError = result.message
                    )
                    }
                }

                Resource.Loading -> Unit
            }
        }
    }

    fun requestDeleteVehicle(vehicle: Vehicle) {
        updateState { copy(vehiclePendingDelete = vehicle, deleteError = null) }
    }

    fun cancelDeleteVehicle() {
        updateState {
            copy(
                vehiclePendingDelete = null,
                isDeletingVehicle = false,
                deleteError = null
            )
        }
    }

    fun confirmDeleteVehicle() {
        val pending = _uiState.value.vehiclePendingDelete ?: return
        viewModelScope.launch {
            updateState { copy(isDeletingVehicle = true, deleteError = null) }
            when (val result = vehicleRepository.deleteVehicle(pending.id)) {
                is Resource.Success -> {
                    updateState {
                        copy(
                            isDeletingVehicle = false,
                            vehiclePendingDelete = null,
                            removingVehicleId = pending.id,
                            deleteError = null
                        )
                    }

                    delay(260)

                    val remaining = _uiState.value.vehicles.filterNot { it.id == pending.id }
                    updateState { copy(vehicles = remaining, removingVehicleId = null) }
                    loadUpcomingReminders()
                }

                is Resource.Error -> {
                    updateState { copy(isDeletingVehicle = false, deleteError = result.message) }
                }

                Resource.Loading -> Unit
            }
        }
    }

    fun clearErrors() {
        updateState { copy(loadError = null, remindersError = null, deleteError = null) }
    }

    private fun updateState(transform: VehicleUiState.() -> VehicleUiState) {
        _uiState.value = _uiState.value.transform()
    }
}
