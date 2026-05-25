package com.itsm.caremycar.screens.user

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.itsm.caremycar.repository.VehicleRepository
import com.itsm.caremycar.util.Resource
import com.itsm.caremycar.vehicle.toOrder
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductMarketplaceViewModel @Inject constructor(
    private val repository: VehicleRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(ProductMarketplaceUiState())
    val uiState: StateFlow<ProductMarketplaceUiState> = _uiState.asStateFlow()
    private val _events = MutableSharedFlow<ClientFeedbackEvent>(extraBufferCapacity = 1)
    internal val events: SharedFlow<ClientFeedbackEvent> = _events.asSharedFlow()

    init {
        loadProducts()
        loadMyPurchases()
    }

    fun refresh() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isRefreshing = true, loadError = null)
            val productsResult = repository.listMarketplaceProducts(
                query = null,
                category = null,
                page = 1,
                limit = 100
            )
            val purchasesResult = repository.listMyPurchases(status = null, page = 1, limit = 20)

            val products = (productsResult as? Resource.Success)?.data?.first
                ?: _uiState.value.products
            val purchases = (purchasesResult as? Resource.Success)?.data?.items
                ?.map { it.toOrder() }
                ?: _uiState.value.purchases
            val error = when {
                productsResult is Resource.Error -> productsResult.message
                purchasesResult is Resource.Error -> purchasesResult.message
                else -> null
            }

            _uiState.value = _uiState.value.copy(
                isRefreshing = false,
                products = products,
                purchases = purchases,
                loadError = error
            )
        }
    }

    private fun loadProducts() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, loadError = null)
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
                        loadError = result.message
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
                        loadError = _uiState.value.loadError ?: result.message
                    )
                }

                Resource.Loading -> Unit
            }
        }
    }

    fun buyNow(partId: String, quantity: Int) {
        val selectedPart = _uiState.value.products.find { it.id == partId }
        if (quantity <= 0) {
            emitMessage("La cantidad debe ser mayor a 0.", isError = true)
            return
        }
        if (selectedPart != null && quantity > selectedPart.quantity) {
            emitMessage("No puedes comprar más del inventario disponible.", isError = true)
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isBuying = true)
            when (val result = repository.purchaseMarketplaceProduct(partId = partId, quantity = quantity)) {
                is Resource.Success -> {
                    emitMessage(
                        "Compra realizada: ${result.data.quantity} pieza(s). Estado: ${purchaseStatusLabel(result.data.status)}."
                    )
                    _uiState.value = _uiState.value.copy(
                        isBuying = false
                    )
                    refresh()
                }

                is Resource.Error -> {
                    emitMessage(result.message, isError = true)
                    _uiState.value = _uiState.value.copy(
                        isBuying = false
                    )
                }

                Resource.Loading -> Unit
            }
        }
    }

    private fun emitMessage(text: String, isError: Boolean = false) {
        _events.tryEmit(ClientFeedbackEvent.Message(text = text, isError = isError))
    }

    private fun purchaseStatusLabel(status: String): String {
        return when (status.lowercase()) {
            "pending", "pendiente" -> "Pendiente"
            "confirmed", "confirmado" -> "Confirmado"
            "completed", "completado", "paid", "pagado" -> "Completado"
            "delivered", "entregado" -> "Entregado"
            "cancelled", "canceled", "cancelado" -> "Cancelado"
            else -> "En proceso"
        }
    }
}
