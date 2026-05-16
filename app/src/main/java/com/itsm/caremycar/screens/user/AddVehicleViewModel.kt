package com.itsm.caremycar.screens.user

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.itsm.caremycar.repository.VehicleRepository
import com.itsm.caremycar.screens.user.util.FormValidationResult
import com.itsm.caremycar.screens.user.util.buildCreateVehicleRequest
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
class AddVehicleViewModel @Inject constructor(
    private val vehicleRepository: VehicleRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddVehicleUiState())
    val uiState: StateFlow<AddVehicleUiState> = _uiState.asStateFlow()
    private val _events = MutableSharedFlow<ClientFeedbackEvent>(extraBufferCapacity = 1)
    internal val events: SharedFlow<ClientFeedbackEvent> = _events.asSharedFlow()

    init {
        loadCatalogVehicles()
    }

    fun loadCatalogVehicles() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isCatalogLoading = true, loadError = null)
            when (val result = vehicleRepository.listCatalogVehicles()) {
                is Resource.Success -> {
                    _uiState.value = _uiState.value.copy(
                        isCatalogLoading = false,
                        catalogVehicles = result.data,
                        loadError = null
                    )
                }

                is Resource.Error -> {
                    _uiState.value = _uiState.value.copy(
                        isCatalogLoading = false,
                        loadError = result.message
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
        val request = when (
            val validationResult = buildCreateVehicleRequest(
                catalogVehicleId = catalogVehicleId,
                year = year,
                mileage = mileage,
                color = color
            )
        ) {
            is FormValidationResult.Valid -> validationResult.value
            is FormValidationResult.Invalid -> {
                emitMessage(validationResult.message, isError = true)
                return
            }
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            when (val result = vehicleRepository.createVehicle(request)) {
                is Resource.Success -> {
                    _events.tryEmit(AddVehicleEvent.Created)
                    _uiState.value = _uiState.value.copy(
                        isLoading = false
                    )
                }

                is Resource.Error -> {
                    emitMessage(result.message, isError = true)
                    _uiState.value = _uiState.value.copy(
                        isLoading = false
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

internal sealed interface AddVehicleEvent : ClientFeedbackEvent {
    data object Created : AddVehicleEvent
}
