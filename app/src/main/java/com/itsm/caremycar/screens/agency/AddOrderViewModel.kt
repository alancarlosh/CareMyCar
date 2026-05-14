package com.itsm.caremycar.screens.agency

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.itsm.caremycar.repository.VehicleRepository
import com.itsm.caremycar.util.Resource
import com.itsm.caremycar.vehicle.CreateOrderRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddOrderViewModel @Inject constructor(
    private val repository: VehicleRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddOrderUiState())
    val uiState: StateFlow<AddOrderUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            
            val vehiclesResult = repository.listCatalogVehicles()
            val vehicles = if (vehiclesResult is Resource.Success) vehiclesResult.data ?: emptyList() else emptyList()
            
            val partsResult = repository.listParts(query = null, category = null, page = 1, limit = 100)
            val parts = if (partsResult is Resource.Success) partsResult.data?.first ?: emptyList() else emptyList()

            // Makes from prototype list
            val finalMakes = vehicles.map { it.make }.distinct().sorted()

            _uiState.update { 
                it.copy(
                    isLoading = false,
                    prototypeVehicles = vehicles,
                    allCatalogParts = parts,
                    availableMakes = finalMakes
                ) 
            }
        }
    }

    fun onMakeSelected(make: String) {
        val finalModels = _uiState.value.prototypeVehicles
            .filter { it.make.equals(make, ignoreCase = true) }
            .map { it.model }
            .distinct()
            .sorted()

        _uiState.update { 
            it.copy(
                availableModels = finalModels, 
                availablePartsForModel = emptyList(),
                selectedPart = null 
            ) 
        }
    }

    fun onModelSelected(make: String, model: String) {
        // Filter Parts from Catalog (MongoDB) that match this vehicle
        val parts = _uiState.value.allCatalogParts
            .filter { 
                it.make?.equals(make, ignoreCase = true) == true && 
                it.model?.equals(model, ignoreCase = true) == true 
            }
            .sortedBy { it.name }
        
        _uiState.update { it.copy(availablePartsForModel = parts, selectedPart = null) }
    }

    fun onPartSelected(partName: String) {
        val part = _uiState.value.availablePartsForModel.find { it.name == partName }
        _uiState.update { it.copy(selectedPart = part) }
    }

    fun createOrder(
        clientName: String,
        vin: String,
        make: String,
        year: String,
        model: String,
        quantity: String
    ) {
        val part = _uiState.value.selectedPart
        if (part == null || clientName.isBlank() || vin.isBlank() || quantity.isBlank()) {
            _uiState.update { it.copy(error = "Por favor selecciona una refacción válida y llena todos los campos") }
            return
        }

        val qtyInt = quantity.toIntOrNull() ?: 0
        if (qtyInt <= 0 || qtyInt > part.quantity) {
            _uiState.update { it.copy(error = "Stock insuficiente o cantidad inválida") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            val request = CreateOrderRequest(
                clientName = clientName,
                vin = vin,
                make = make,
                year = year.toIntOrNull() ?: 0,
                model = model,
                partId = part.id,
                quantity = qtyInt
            )

            when (val result = repository.createOrder(request)) {
                is Resource.Success -> {
                    _uiState.update { it.copy(isLoading = false, isSuccess = true) }
                }
                is Resource.Error -> {
                    _uiState.update { it.copy(isLoading = false, error = result.message) }
                }
                is Resource.Loading -> {}
            }
        }
    }

    fun consumeSuccess() {
        _uiState.update { it.copy(isSuccess = false) }
    }
}
