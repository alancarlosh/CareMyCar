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
import com.itsm.caremycar.vehicle.Part

@HiltViewModel
class CatalogViewModel @Inject constructor(
    private val repository: VehicleRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CatalogUiState())
    val uiState: StateFlow<CatalogUiState> = _uiState.asStateFlow()

    init {
        loadParts()
    }

    fun loadParts() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            val result = repository.listParts(
                query = _uiState.value.searchQuery.ifBlank { null },
                category = _uiState.value.selectedCategory,
                page = 1,
                limit = 50
            )
            when (result) {
                is Resource.Success -> {
                    // Result.data ya es Pair<List<Part>, Int>, no necesita safe call (?) si ya sabemos que es Success
                    val parts = result.data?.first ?: emptyList()
                    _uiState.update { it.copy(isLoading = false, parts = parts) }
                }
                is Resource.Error -> {
                    _uiState.update { it.copy(isLoading = false, error = result.message) }
                }
                is Resource.Loading -> {}
            }
        }
    }

    fun onSearchQueryChange(newQuery: String) {
        _uiState.update { it.copy(searchQuery = newQuery) }
        loadParts()
    }

    fun onCategoryChange(newCategory: String) {
        _uiState.update { it.copy(selectedCategory = newCategory) }
        loadParts()
    }

    fun deletePart(partId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isDeleting = true) }
            when (val result = repository.deletePart(partId)) {
                is Resource.Success -> {
                    _uiState.update { it.copy(isDeleting = false, deleteSuccess = true) }
                    loadParts()
                }
                is Resource.Error -> {
                    _uiState.update { it.copy(isDeleting = false, error = result.message) }
                }
                is Resource.Loading -> {}
            }
        }
    }

    fun insertOrUpdatePartOptimistically(part: Part) {
        _uiState.update { state ->
            val withoutCreated = state.parts.filterNot { it.id == part.id }
            state.copy(parts = listOf(part) + withoutCreated)
        }
    }

    fun consumeDeleteSuccess() {
        _uiState.update { it.copy(deleteSuccess = false) }
    }
}
