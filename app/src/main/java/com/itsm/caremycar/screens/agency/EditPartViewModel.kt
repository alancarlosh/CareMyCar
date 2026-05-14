package com.itsm.caremycar.screens.agency

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.itsm.caremycar.repository.VehicleRepository
import com.itsm.caremycar.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditPartViewModel @Inject constructor(
    private val repository: VehicleRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(EditPartUiState())
    val uiState: StateFlow<EditPartUiState> = _uiState.asStateFlow()

    fun loadPartAndCatalog(partId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            
            // Load Catalog first for dropdowns
            val catalogResult = repository.listCatalogVehicles()
            if (catalogResult is Resource.Success) {
                val vehicles = catalogResult.data ?: emptyList()
                val makes = vehicles.map { it.make }.distinct().sorted()
                _uiState.update { it.copy(catalogVehicles = vehicles, availableMakes = makes) }
            }

            // Load the Part details
            when (val result = repository.getPartById(partId)) {
                is Resource.Success -> {
                    val part = result.data
                    _uiState.update { it.copy(isLoading = false, part = part) }
                    if (part?.make != null) {
                        onMakeSelected(part.make)
                    }
                }
                is Resource.Error -> {
                    _uiState.update { it.copy(isLoading = false, error = result.message) }
                }
                is Resource.Loading -> {}
            }
        }
    }

    fun onMakeSelected(make: String) {
        val models = _uiState.value.catalogVehicles
            .filter { it.make == make }
            .map { it.model }
            .distinct()
            .sorted()
        _uiState.update { it.copy(availableModels = models) }
    }

    fun updatePart(
        partId: String,
        name: String,
        category: String,
        make: String?,
        year: String?,
        model: String?,
        price: String,
        quantity: String
    ) {
        val priceDouble = price.toDoubleOrNull()
        val quantityInt = quantity.toIntOrNull()
        val yearInt = year?.toIntOrNull()

        if (name.isBlank() || category.isBlank() || priceDouble == null || quantityInt == null) {
            _uiState.update { it.copy(error = "Valores inválidos") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            val payload = mapOf(
                "name" to name,
                "category" to category,
                "make" to (make ?: ""),
                "year" to (yearInt ?: 0),
                "model" to (model ?: ""),
                "price" to priceDouble,
                "quantity" to quantityInt
            )

            when (val result = repository.updatePart(partId, payload)) {
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
