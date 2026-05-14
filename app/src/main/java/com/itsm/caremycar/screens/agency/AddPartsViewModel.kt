package com.itsm.caremycar.screens.agency

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.itsm.caremycar.repository.VehicleRepository
import com.itsm.caremycar.util.Resource
import com.itsm.caremycar.vehicle.CreatePartRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddPartsViewModel @Inject constructor(
    private val repository: VehicleRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddPartsUiState())
    val uiState: StateFlow<AddPartsUiState> = _uiState.asStateFlow()

    init {
        loadVehicleCatalog()
    }

    private fun loadVehicleCatalog() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            when (val result = repository.listCatalogVehicles()) {
                is Resource.Success -> {
                    val vehicles = result.data ?: emptyList()
                    val makes = vehicles.map { it.make }.distinct().sorted()
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            catalogVehicles = vehicles,
                            availableMakes = makes,
                            availableModels = emptyList() // Reset models
                        )
                    }
                }
                is Resource.Error -> {
                    _uiState.update { it.copy(isLoading = false, error = result.message) }
                }
                is Resource.Loading -> { /* No-op */ }
            }
        }
    }

    fun onMakeSelected(make: String) {
        val models = _uiState.value.catalogVehicles
            .filter { it.make.equals(make, ignoreCase = true) }
            .map { it.model }
            .distinct()
            .sorted()
        _uiState.update { it.copy(availableModels = models) }
    }

    fun addPart(
        name: String,
        category: String,
        make: String?,
        year: String?,
        model: String?,
        price: String,
        quantity: String
    ) {
        if (name.isBlank() || category.isBlank() || price.isBlank() || quantity.isBlank()) {
            _uiState.update { it.copy(error = "Por favor completa los campos obligatorios") }
            return
        }

        val priceDouble = price.toDoubleOrNull()
        val quantityInt = quantity.toIntOrNull()
        val yearInt = year?.toIntOrNull()

        if (priceDouble == null || quantityInt == null) {
            _uiState.update { it.copy(error = "Precio y cantidad deben ser numéricos") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            val request = CreatePartRequest(
                name = name,
                category = category,
                make = make,
                year = yearInt,
                model = model,
                price = priceDouble,
                quantity = quantityInt
            )

            when (val result = repository.createPart(request)) {
                is Resource.Success -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            isSuccess = true,
                            createdPart = result.data
                        )
                    }
                }
                is Resource.Error -> {
                    _uiState.update { it.copy(isLoading = false, error = result.message) }
                }
                is Resource.Loading -> {}
            }
        }
    }

    fun consumeSuccess() {
        _uiState.update { it.copy(isSuccess = false, createdPart = null) }
    }
}
