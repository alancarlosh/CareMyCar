package com.itsm.caremycar.screens.user

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.itsm.caremycar.repository.VehicleRepository
import com.itsm.caremycar.util.Resource
import com.itsm.caremycar.vehicle.Order
import com.itsm.caremycar.vehicle.Part
import com.itsm.caremycar.vehicle.toOrder
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ProductMarketplaceUiState(
    val isLoading: Boolean = false,
    val isBuying: Boolean = false,
    val products: List<Part> = emptyList(),
    val purchases: List<Order> = emptyList(),
    val error: String? = null,
    val message: String? = null
)

@HiltViewModel
class ProductMarketplaceViewModel @Inject constructor(
    private val repository: VehicleRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(ProductMarketplaceUiState())
    val uiState: StateFlow<ProductMarketplaceUiState> = _uiState.asStateFlow()

    init {
        refresh()
    }

    fun refresh() {
        loadProducts()
        loadMyPurchases()
    }

    private fun loadProducts() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            when (val result = repository.listMarketplaceProducts(query = null, category = null, page = 1, limit = 100)) {
                is Resource.Success -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        products = result.data.first
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

    private fun loadMyPurchases() {
        viewModelScope.launch {
            when (val result = repository.listMyPurchases(status = null, page = 1, limit = 20)) {
                is Resource.Success -> {
                    _uiState.value = _uiState.value.copy(
                        purchases = result.data.items.map { it.toOrder() }
                    )
                }

                is Resource.Error -> {
                    _uiState.value = _uiState.value.copy(
                        error = _uiState.value.error ?: result.message
                    )
                }

                Resource.Loading -> Unit
            }
        }
    }

    fun buyNow(partId: String, quantity: Int) {
        val selectedPart = _uiState.value.products.find { it.id == partId }
        if (quantity <= 0) {
            _uiState.value = _uiState.value.copy(error = "La cantidad debe ser mayor a 0.")
            return
        }
        if (selectedPart != null && quantity > selectedPart.quantity) {
            _uiState.value = _uiState.value.copy(error = "No puedes comprar más del inventario disponible.")
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isBuying = true, error = null, message = null)
            when (val result = repository.purchaseMarketplaceProduct(partId = partId, quantity = quantity)) {
                is Resource.Success -> {
                    _uiState.value = _uiState.value.copy(
                        isBuying = false,
                        message = "Compra realizada (${result.data.quantity} pza). Estado: ${result.data.status}"
                    )
                    refresh()
                }

                is Resource.Error -> {
                    _uiState.value = _uiState.value.copy(
                        isBuying = false,
                        error = result.message
                    )
                }

                Resource.Loading -> Unit
            }
        }
    }
}
