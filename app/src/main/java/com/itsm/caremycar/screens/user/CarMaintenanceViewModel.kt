package com.itsm.caremycar.screens.user

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.itsm.caremycar.repository.VehicleRepository
import com.itsm.caremycar.screens.user.util.FormValidationResult
import com.itsm.caremycar.screens.user.util.buildCreateMaintenanceRequest
import com.itsm.caremycar.screens.user.util.buildCreateServiceOrderRequest
import com.itsm.caremycar.screens.user.util.buildMaintenanceUpdatePayload
import com.itsm.caremycar.util.Resource
import com.itsm.caremycar.vehicle.MaintenanceRecommendation
import com.itsm.caremycar.vehicle.MaintenanceRecord
import com.itsm.caremycar.vehicle.ServiceOrder
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class CarMaintenanceViewModel @Inject constructor(
    private val vehicleRepository: VehicleRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(CarMaintenanceUiState())
    val uiState: StateFlow<CarMaintenanceUiState> = _uiState.asStateFlow()
    private val _events = MutableSharedFlow<ClientFeedbackEvent>(extraBufferCapacity = 1)
    internal val events: SharedFlow<ClientFeedbackEvent> = _events.asSharedFlow()
    private var loadMaintenanceJob: Job? = null
    private var loadQuoteJob: Job? = null

    fun loadMaintenance(vehicleId: String) {
        loadMaintenanceJob?.cancel()
        loadMaintenanceJob = viewModelScope.launch {
            updateState {
                copy(
                    isLoading = true,
                    items = emptyList(),
                    serviceOrders = emptyList(),
                    recommendations = emptyList(),
                    orderQuote = null,
                    orderQuoteKey = null,
                    loadError = null
                )
            }
            val loadResult = loadMaintenanceData(vehicleId)

            updateState {
                copy(
                    isLoading = false,
                    items = loadResult.items.orEmpty(),
                    recommendations = loadResult.recommendations.orEmpty(),
                    serviceOrders = loadResult.serviceOrders.orEmpty(),
                    loadError = loadResult.error
                )
            }
        }
    }

    private suspend fun loadMaintenanceData(vehicleId: String): MaintenanceLoadResult = coroutineScope {
        val maintenanceDeferred = async { vehicleRepository.listMaintenanceByVehicle(vehicleId) }
        val recommendationsDeferred = async { vehicleRepository.getMaintenanceRecommendations(vehicleId) }
        val serviceOrdersDeferred = async { vehicleRepository.listMyServiceOrders() }

        val maintenanceResult = maintenanceDeferred.await()
        val recommendationsResult = recommendationsDeferred.await()
        val serviceOrdersResult = serviceOrdersDeferred.await()

        MaintenanceLoadResult(
            items = maintenanceResult.successDataOrNull(),
            recommendations = recommendationsResult.successDataOrNull(),
            serviceOrders = serviceOrdersResult.successDataOrNull()
                ?.filter { it.vehicleId == vehicleId },
            error = firstErrorMessage(
                maintenanceResult,
                recommendationsResult,
                serviceOrdersResult
            )
        )
    }

    fun createServiceOrder(
        vehicleId: String,
        serviceType: String,
        scheduledDate: String,
        notes: String
    ) {
        val request = when (
            val validationResult = buildCreateServiceOrderRequest(
                vehicleId = vehicleId,
                serviceType = serviceType,
                scheduledDate = scheduledDate,
                notes = notes
            )
        ) {
            is FormValidationResult.Valid -> validationResult.value
            is FormValidationResult.Invalid -> {
                emitMessage(validationResult.message, isError = true)
                return
            }
        }

        val expectedQuoteKey = serviceOrderQuoteKey(vehicleId = vehicleId, serviceType = request.serviceType)
        if (_uiState.value.orderQuote == null || _uiState.value.orderQuoteKey != expectedQuoteKey) {
            emitMessage("Primero consulta la cotización del servicio.", isError = true)
            return
        }

        launchRequest(
            onStart = { copy(isSubmittingOrder = true) },
            request = { vehicleRepository.createServiceOrder(request) },
            onSuccess = { created ->
                emitMessage("Orden creada. Código de confirmación: ${created.completionToken}")
                copy(
                    isSubmittingOrder = false,
                    serviceOrders = listOf(created) + serviceOrders,
                    orderQuote = null,
                    orderQuoteKey = null
                )
            },
            onError = { message ->
                emitMessage(message, isError = true)
                copy(isSubmittingOrder = false)
            }
        )
    }

    fun loadServiceOrderQuote(
        vehicleId: String,
        serviceType: String
    ) {
        val st = serviceType.trim()
        if (st.isBlank()) {
            emitMessage("Selecciona un tipo de servicio para cotizar.", isError = true)
            return
        }
        val quoteKey = serviceOrderQuoteKey(vehicleId = vehicleId, serviceType = st)
        loadQuoteJob?.cancel()
        loadQuoteJob = viewModelScope.launch {
            updateState {
                copy(
                    isLoadingOrderQuote = true,
                    orderQuote = null,
                    orderQuoteKey = null
                )
            }
            when (val result = vehicleRepository.getServiceOrderQuote(vehicleId = vehicleId, serviceType = st)) {
                is Resource.Success -> {
                    updateState {
                        copy(
                            isLoadingOrderQuote = false,
                            orderQuote = result.data,
                            orderQuoteKey = quoteKey
                        )
                    }
                }

                is Resource.Error -> {
                    emitMessage(result.message, isError = true)
                    updateState {
                        copy(
                            isLoadingOrderQuote = false,
                            orderQuote = null,
                            orderQuoteKey = null
                        )
                    }
                }

                Resource.Loading -> Unit
            }
        }
    }

    fun clearServiceOrderQuote() {
        loadQuoteJob?.cancel()
        updateState { copy(orderQuote = null, orderQuoteKey = null, isLoadingOrderQuote = false) }
    }

    fun createMaintenance(
        vehicleId: String,
        serviceType: String,
        serviceDate: String,
        description: String,
        cost: String,
        mileage: String
    ) {
        val request = when (
            val validationResult = buildCreateMaintenanceRequest(
                vehicleId = vehicleId,
                serviceType = serviceType,
                serviceDate = serviceDate,
                description = description,
                cost = cost,
                mileage = mileage
            )
        ) {
            is FormValidationResult.Valid -> validationResult.value
            is FormValidationResult.Invalid -> {
                emitMessage(validationResult.message, isError = true)
                return
            }
        }

        launchRequest(
            onStart = { copy(isSaving = true) },
            request = { vehicleRepository.createMaintenance(request) },
            onSuccess = { created -> copy(isSaving = false, items = listOf(created) + items) },
            onError = { message ->
                emitMessage(message, isError = true)
                copy(isSaving = false)
            }
        )
    }

    fun requestEdit(item: MaintenanceRecord) {
        updateState { copy(selectedItemForEdit = item) }
    }

    fun dismissEdit() {
        updateState { copy(selectedItemForEdit = null) }
    }

    fun requestDelete(item: MaintenanceRecord) {
        updateState { copy(selectedItemForDelete = item) }
    }

    fun dismissDelete() {
        updateState { copy(selectedItemForDelete = null) }
    }

    fun updateMaintenance(
        maintenanceId: String,
        serviceType: String,
        serviceDate: String,
        description: String,
        cost: String,
        mileage: String
    ) {
        val payload = when (
            val validationResult = buildMaintenanceUpdatePayload(
                serviceType = serviceType,
                serviceDate = serviceDate,
                description = description,
                cost = cost,
                mileage = mileage
            )
        ) {
            is FormValidationResult.Valid -> validationResult.value
            is FormValidationResult.Invalid -> {
                emitMessage(validationResult.message, isError = true)
                return
            }
        }

        launchRequest(
            onStart = { copy(isSaving = true) },
            request = { vehicleRepository.updateMaintenance(maintenanceId, payload) },
            onSuccess = { updated ->
                copy(
                    isSaving = false,
                    selectedItemForEdit = null,
                    items = items.map { if (it.id == updated.id) updated else it }
                )
            },
            onError = { message ->
                emitMessage(message, isError = true)
                copy(isSaving = false)
            }
        )
    }

    fun confirmDeleteMaintenance() {
        val selected = _uiState.value.selectedItemForDelete ?: return
        launchRequest(
            onStart = { copy(isDeleting = true) },
            request = { vehicleRepository.deleteMaintenance(selected.id) },
            onSuccess = {
                copy(
                    isDeleting = false,
                    selectedItemForDelete = null,
                    items = items.filterNot { it.id == selected.id }
                )
            },
            onError = { message ->
                emitMessage(message, isError = true)
                copy(isDeleting = false)
            }
        )
    }

    private fun updateState(transform: CarMaintenanceUiState.() -> CarMaintenanceUiState) {
        _uiState.value = _uiState.value.transform()
    }

    private fun emitMessage(text: String, isError: Boolean = false) {
        _events.tryEmit(ClientFeedbackEvent.Message(text = text, isError = isError))
    }

    private fun <T> launchRequest(
        onStart: CarMaintenanceUiState.() -> CarMaintenanceUiState,
        request: suspend () -> Resource<T>,
        onSuccess: CarMaintenanceUiState.(T) -> CarMaintenanceUiState,
        onError: CarMaintenanceUiState.(String) -> CarMaintenanceUiState
    ) {
        viewModelScope.launch {
            updateState(onStart)
            when (val result = request()) {
                is Resource.Success -> updateState { onSuccess(result.data) }
                is Resource.Error -> updateState { onError(result.message) }
                Resource.Loading -> Unit
            }
        }
    }
}

private data class MaintenanceLoadResult(
    val items: List<MaintenanceRecord>?,
    val recommendations: List<MaintenanceRecommendation>?,
    val serviceOrders: List<ServiceOrder>?,
    val error: String?
)

private fun <T> Resource<T>.successDataOrNull(): T? {
    return (this as? Resource.Success)?.data
}

private fun firstErrorMessage(vararg results: Resource<*>): String? {
    return results.firstNotNullOfOrNull { result ->
        (result as? Resource.Error)?.message
    }
}

private fun serviceOrderQuoteKey(vehicleId: String, serviceType: String): String {
    return "$vehicleId|${serviceType.trim()}"
}
