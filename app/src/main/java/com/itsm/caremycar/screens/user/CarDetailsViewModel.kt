package com.itsm.caremycar.screens.user

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.itsm.caremycar.repository.VehicleRepository
import com.itsm.caremycar.screens.user.util.FormValidationResult
import com.itsm.caremycar.screens.user.util.buildVehicleMileageUpdatePayload
import com.itsm.caremycar.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class CarDetailsViewModel @Inject constructor(
    private val vehicleRepository: VehicleRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CarDetailsUiState())
    val uiState: StateFlow<CarDetailsUiState> = _uiState.asStateFlow()
    private val _events = MutableSharedFlow<ClientFeedbackEvent>(extraBufferCapacity = 1)
    internal val events: SharedFlow<ClientFeedbackEvent> = _events.asSharedFlow()

    fun loadVehicle(vehicleId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, loadError = null)
            when (val result = vehicleRepository.getVehicleById(vehicleId)) {
                is Resource.Success -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        vehicle = result.data,
                        loadError = null
                    )
                }

                is Resource.Error -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        loadError = result.message
                    )
                }

                Resource.Loading -> Unit
            }
        }
    }

    fun updateVehicle(
        vehicleId: String,
        mileage: String
    ) {
        val current = _uiState.value.vehicle ?: return
        val payload = when (
            val validationResult = buildVehicleMileageUpdatePayload(
                mileage = mileage,
                currentMileage = current.currentMileage?.toInt() ?: 0
            )
        ) {
            is FormValidationResult.Valid -> validationResult.value
            is FormValidationResult.Invalid -> {
                emitMessage(validationResult.message, isError = true)
                return
            }
        }

        if (payload.isEmpty()) {
            emitMessage("No hay cambios para guardar.")
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSaving = true)
            when (val result = vehicleRepository.updateVehicle(vehicleId, payload)) {
                is Resource.Success -> {
                    emitMessage("Vehículo actualizado correctamente.")
                    _uiState.value = _uiState.value.copy(
                        isSaving = false,
                        vehicle = result.data
                    )
                }

                is Resource.Error -> {
                    emitMessage(result.message, isError = true)
                    _uiState.value = _uiState.value.copy(
                        isSaving = false
                    )
                }

                Resource.Loading -> Unit
            }
        }
    }

    private fun emitMessage(text: String, isError: Boolean = false) {
        _events.tryEmit(ClientFeedbackEvent.Message(text = text, isError = isError))
    }
}
