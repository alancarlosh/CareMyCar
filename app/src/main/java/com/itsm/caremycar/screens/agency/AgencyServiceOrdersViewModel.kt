package com.itsm.caremycar.screens.agency

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.itsm.caremycar.repository.VehicleRepository
import com.itsm.caremycar.util.Resource
import com.itsm.caremycar.vehicle.ServiceOrder
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AgencyServiceOrdersUiState(
    val isLoading: Boolean = false,
    val isUpdating: Boolean = false,
    val items: List<ServiceOrder> = emptyList(),
    val selectedStatus: String? = null,
    val error: String? = null,
    val message: String? = null
)

@HiltViewModel
class AgencyServiceOrdersViewModel @Inject constructor(
    private val vehicleRepository: VehicleRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(AgencyServiceOrdersUiState())
    val uiState: StateFlow<AgencyServiceOrdersUiState> = _uiState.asStateFlow()

    init {
        loadOrders()
    }

    fun loadOrders(status: String? = _uiState.value.selectedStatus) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null, selectedStatus = status)
            when (val result = vehicleRepository.listAllServiceOrders(status)) {
                is Resource.Success -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        items = result.data
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

    fun startOrder(orderId: String, notes: String?) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isUpdating = true, error = null, message = null)
            when (val result = vehicleRepository.startServiceOrder(orderId, notes)) {
                is Resource.Success -> {
                    _uiState.value = _uiState.value.copy(
                        isUpdating = false,
                        message = "Orden iniciada."
                    )
                    loadOrders()
                }

                is Resource.Error -> {
                    _uiState.value = _uiState.value.copy(isUpdating = false, error = result.message)
                }

                Resource.Loading -> Unit
            }
        }
    }

    fun completeOrder(
        orderId: String,
        token: String,
        finalCost: Double?,
        notes: String?,
        mileage: Int?
    ) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isUpdating = true, error = null, message = null)
            when (val result = vehicleRepository.completeServiceOrder(orderId, token, finalCost, notes, mileage)) {
                is Resource.Success -> {
                    _uiState.value = _uiState.value.copy(
                        isUpdating = false,
                        message = "Orden finalizada correctamente."
                    )
                    loadOrders()
                }

                is Resource.Error -> {
                    _uiState.value = _uiState.value.copy(isUpdating = false, error = result.message)
                }

                Resource.Loading -> Unit
            }
        }
    }

    fun cancelOrder(orderId: String, notes: String?) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isUpdating = true, error = null, message = null)
            when (val result = vehicleRepository.cancelServiceOrder(orderId, notes)) {
                is Resource.Success -> {
                    _uiState.value = _uiState.value.copy(
                        isUpdating = false,
                        message = "Orden cancelada."
                    )
                    loadOrders()
                }

                is Resource.Error -> {
                    _uiState.value = _uiState.value.copy(isUpdating = false, error = result.message)
                }

                Resource.Loading -> Unit
            }
        }
    }

    fun generateReport(from: String?, to: String?) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isUpdating = true, error = null, message = null)
            when (val result = vehicleRepository.downloadServiceOrdersReport(from = from, to = to, status = "FINALIZADO")) {
                is Resource.Success -> {
                    _uiState.value = _uiState.value.copy(
                        isUpdating = false,
                        message = "PDF generado (${result.data.size} bytes)."
                    )
                }

                is Resource.Error -> {
                    _uiState.value = _uiState.value.copy(isUpdating = false, error = result.message)
                }

                Resource.Loading -> Unit
            }
        }
    }
}
