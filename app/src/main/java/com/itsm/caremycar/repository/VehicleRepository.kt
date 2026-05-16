package com.itsm.caremycar.repository

import com.itsm.caremycar.api.ApiService
import com.itsm.caremycar.util.Resource
import com.itsm.caremycar.vehicle.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class VehicleRepository @Inject constructor(
    private val apiService: ApiService
) {

    // --- VEHICLES ---

    suspend fun listVehicles(): Resource<List<Vehicle>> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.listVehicles()
                if (response.isSuccessful && response.body() != null) {
                    Resource.Success(response.body()!!.items.map { it.toVehicle() })
                } else {
                    Resource.Error(parseBackendError(response.errorBody()?.string()) ?: "No se pudieron cargar tus vehículos.")
                }
            } catch (e: Exception) { Resource.Error(e.localizedMessage ?: "Error de conexión") }
        }
    }

    suspend fun getVehicleById(vehicleId: String): Resource<Vehicle> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getVehicleById(vehicleId)
                if (response.isSuccessful && response.body() != null) {
                    Resource.Success(response.body()!!.vehicle.toVehicle())
                } else {
                    Resource.Error(parseBackendError(response.errorBody()?.string()) ?: "Vehículo no encontrado.")
                }
            } catch (e: Exception) { Resource.Error(e.localizedMessage ?: "Error de conexión") }
        }
    }

    suspend fun createVehicle(request: CreateVehicleRequest): Resource<Vehicle> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.createVehicle(request)
                if (response.isSuccessful && response.body() != null) {
                    Resource.Success(response.body()!!.vehicle.toVehicle())
                } else {
                    Resource.Error(parseBackendError(response.errorBody()?.string()) ?: "No se pudo crear el vehículo.")
                }
            } catch (e: Exception) { Resource.Error(e.localizedMessage ?: "Error de conexión") }
        }
    }

    suspend fun updateVehicle(vehicleId: String, payload: Map<String, Any>): Resource<Vehicle> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.updateVehicle(vehicleId, payload)
                if (response.isSuccessful && response.body() != null) {
                    Resource.Success(response.body()!!.vehicle.toVehicle())
                } else {
                    Resource.Error(parseBackendError(response.errorBody()?.string()) ?: "No se pudo actualizar el vehículo.")
                }
            } catch (e: Exception) { Resource.Error(e.localizedMessage ?: "Error de conexión") }
        }
    }

    suspend fun deleteVehicle(vehicleId: String): Resource<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.deleteVehicle(vehicleId)
                if (response.isSuccessful) {
                    Resource.Success(Unit)
                } else {
                    Resource.Error(parseBackendError(response.errorBody()?.string()) ?: "No se pudo eliminar el vehículo.")
                }
            } catch (e: Exception) { Resource.Error(e.localizedMessage ?: "Error de conexión") }
        }
    }

    suspend fun listCatalogVehicles(): Resource<List<CatalogVehicle>> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.listCatalogVehicles()
                if (response.isSuccessful && response.body() != null) {
                    Resource.Success(response.body()!!.items.map { it.toCatalogVehicle() })
                } else {
                    Resource.Error(
                        parseBackendError(response.errorBody()?.string())
                            ?: "No se pudo cargar el catálogo de vehículos."
                    )
                }
            } catch (e: Exception) {
                Resource.Error(e.localizedMessage ?: "Error de conexión")
            }
        }
    }

    // --- PARTS ---

    suspend fun listParts(query: String?, category: String?, page: Int, limit: Int): Resource<Pair<List<Part>, Int>> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.listParts(query, category, page, limit)
                if (response.isSuccessful && response.body() != null) {
                    val body = response.body()!!
                    Resource.Success(body.items.map { it.toPart() } to body.total)
                } else {
                    Resource.Error(parseBackendError(response.errorBody()?.string()) ?: "No se pudieron cargar las refacciones.")
                }
            } catch (e: Exception) { Resource.Error(e.localizedMessage ?: "Error de conexión") }
        }
    }

    suspend fun getPartOptions(make: String? = null): Resource<PartOptionsResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getPartOptions(make)
                if (response.isSuccessful && response.body() != null) {
                    Resource.Success(response.body()!!)
                } else {
                    Resource.Error("No se pudieron cargar las opciones.")
                }
            } catch (e: Exception) { Resource.Error(e.localizedMessage ?: "Error de conexión") }
        }
    }

    suspend fun createPart(request: CreatePartRequest): Resource<Part> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.createPart(request)
                if (response.isSuccessful && response.body() != null) {
                    Resource.Success(response.body()!!.part.toPart())
                } else {
                    Resource.Error(parseBackendError(response.errorBody()?.string()) ?: "No se pudo crear la refacción.")
                }
            } catch (e: Exception) { Resource.Error(e.localizedMessage ?: "Error de conexión") }
        }
    }

    suspend fun getPartById(partId: String): Resource<Part> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getPartById(partId)
                if (response.isSuccessful && response.body() != null) {
                    Resource.Success(response.body()!!.part.toPart())
                } else {
                    Resource.Error(parseBackendError(response.errorBody()?.string()) ?: "No se pudo cargar la refacción.")
                }
            } catch (e: Exception) { Resource.Error(e.localizedMessage ?: "Error de conexión") }
        }
    }

    suspend fun updatePart(partId: String, payload: Map<String, Any>): Resource<Part> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.updatePart(partId, payload)
                if (response.isSuccessful && response.body() != null) {
                    Resource.Success(response.body()!!.part.toPart())
                } else {
                    Resource.Error(parseBackendError(response.errorBody()?.string()) ?: "No se pudo actualizar la refacción.")
                }
            } catch (e: Exception) { Resource.Error(e.localizedMessage ?: "Error de conexión") }
        }
    }

    suspend fun deletePart(partId: String): Resource<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.deletePart(partId)
                if (response.isSuccessful) {
                    Resource.Success(Unit)
                } else {
                    Resource.Error(parseBackendError(response.errorBody()?.string()) ?: "No se pudo eliminar la refacción.")
                }
            } catch (e: Exception) { Resource.Error(e.localizedMessage ?: "Error de conexión") }
        }
    }

    // --- ORDERS (PART ORDERS) ---

    suspend fun listOrders(query: String?, status: String?, page: Int, limit: Int): Resource<OrderListResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.listOrders(query, status, page, limit)
                if (response.isSuccessful && response.body() != null) {
                    Resource.Success(response.body()!!)
                } else {
                    Resource.Error(parseBackendError(response.errorBody()?.string()) ?: "No se pudieron cargar los pedidos.")
                }
            } catch (e: Exception) { Resource.Error(e.localizedMessage ?: "Error de conexión") }
        }
    }

    suspend fun listMarketplaceProducts(query: String?, category: String?, page: Int, limit: Int): Resource<Pair<List<Part>, Int>> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.listMarketplaceProducts(query, category, page, limit)
                if (response.isSuccessful && response.body() != null) {
                    val body = response.body()!!
                    Resource.Success(body.items.map { it.toPart() } to body.total)
                } else {
                    Resource.Error(parseBackendError(response.errorBody()?.string()) ?: "No se pudieron cargar los productos.")
                }
            } catch (e: Exception) { Resource.Error(e.localizedMessage ?: "Error de conexión") }
        }
    }

    suspend fun purchaseMarketplaceProduct(partId: String, quantity: Int): Resource<Order> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.purchaseMarketplaceProduct(
                    MarketplacePurchaseRequest(partId = partId, quantity = quantity)
                )
                if (response.isSuccessful && response.body() != null) {
                    Resource.Success(response.body()!!.order.toOrder())
                } else {
                    Resource.Error(parseBackendError(response.errorBody()?.string()) ?: "No se pudo completar la compra.")
                }
            } catch (e: Exception) { Resource.Error(e.localizedMessage ?: "Error de conexión") }
        }
    }

    suspend fun listMyPurchases(status: String?, page: Int, limit: Int): Resource<OrderListResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.listMyPurchases(status, page, limit)
                if (response.isSuccessful && response.body() != null) {
                    Resource.Success(response.body()!!)
                } else {
                    Resource.Error(parseBackendError(response.errorBody()?.string()) ?: "No se pudieron cargar tus compras.")
                }
            } catch (e: Exception) { Resource.Error(e.localizedMessage ?: "Error de conexión") }
        }
    }

    suspend fun getSalesDailyReport(date: String? = null): Resource<SalesDailyReport> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getSalesDailyReport(date)
                if (response.isSuccessful && response.body() != null) {
                    Resource.Success(response.body()!!.report.toSalesDailyReport())
                } else {
                    Resource.Error(parseBackendError(response.errorBody()?.string()) ?: "No se pudo cargar el reporte diario.")
                }
            } catch (e: Exception) { Resource.Error(e.localizedMessage ?: "Error de conexión") }
        }
    }

    suspend fun downloadSalesDailyReportPdf(date: String? = null): Resource<ByteArray> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.downloadSalesDailyReportPdf(date)
                if (response.isSuccessful && response.body() != null) {
                    Resource.Success(response.body()!!.bytes())
                } else {
                    Resource.Error(parseBackendError(response.errorBody()?.string()) ?: "No se pudo generar el PDF de ventas.")
                }
            } catch (e: Exception) { Resource.Error(e.localizedMessage ?: "Error de conexión") }
        }
    }

    suspend fun createOrder(request: CreateOrderRequest): Resource<Order> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.createOrder(request)
                if (response.isSuccessful && response.body() != null) {
                    Resource.Success(response.body()!!.order.toOrder())
                } else {
                    Resource.Error(parseBackendError(response.errorBody()?.string()) ?: "No se pudo crear el pedido.")
                }
            } catch (e: Exception) { Resource.Error(e.localizedMessage ?: "Error de conexión") }
        }
    }

    suspend fun getOrderOptions(make: String? = null, model: String? = null, year: Int? = null): Resource<OrderOptionsResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getOrderOptions(make, model, year)
                if (response.isSuccessful && response.body() != null) {
                    Resource.Success(response.body()!!)
                } else {
                    Resource.Error("No se pudieron cargar las opciones.")
                }
            } catch (e: Exception) { Resource.Error(e.localizedMessage ?: "Error de conexión") }
        }
    }

    suspend fun updateOrder(orderId: String, payload: Map<String, Any>): Resource<Order> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.updateOrder(orderId, payload)
                if (response.isSuccessful && response.body() != null) {
                    Resource.Success(response.body()!!.order.toOrder())
                } else {
                    Resource.Error(parseBackendError(response.errorBody()?.string()) ?: "No se pudo actualizar el pedido.")
                }
            } catch (e: Exception) { Resource.Error(e.localizedMessage ?: "Error de conexión") }
        }
    }

    suspend fun deleteOrder(orderId: String): Resource<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.deleteOrder(orderId)
                if (response.isSuccessful) {
                    Resource.Success(Unit)
                } else { Resource.Error("Error al eliminar pedido.") }
            } catch (e: Exception) { Resource.Error(e.localizedMessage ?: "Error de conexión") }
        }
    }

    // --- MAINTENANCE ---

    suspend fun listMaintenanceByVehicle(vehicleId: String): Resource<List<MaintenanceRecord>> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.listMaintenanceByVehicle(vehicleId)
                if (response.isSuccessful && response.body() != null) {
                    Resource.Success(response.body()!!.items.map { it.toMaintenanceRecord() })
                } else { Resource.Error("Error al cargar mantenimiento.") }
            } catch (e: Exception) { Resource.Error(e.localizedMessage ?: "Error de conexión") }
        }
    }

    suspend fun createMaintenance(request: CreateMaintenanceRequest): Resource<MaintenanceRecord> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.createMaintenance(request)
                if (response.isSuccessful && response.body() != null) {
                    Resource.Success(response.body()!!.maintenance.toMaintenanceRecord())
                } else { Resource.Error("No se pudo crear el registro.") }
            } catch (e: Exception) { Resource.Error(e.localizedMessage ?: "Error de conexión") }
        }
    }

    suspend fun updateMaintenance(maintenanceId: String, payload: Map<String, Any>): Resource<MaintenanceRecord> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.updateMaintenance(maintenanceId, payload)
                if (response.isSuccessful && response.body() != null) {
                    Resource.Success(response.body()!!.maintenance.toMaintenanceRecord())
                } else { Resource.Error("No se pudo actualizar el registro.") }
            } catch (e: Exception) { Resource.Error(e.localizedMessage ?: "Error de conexión") }
        }
    }

    suspend fun deleteMaintenance(maintenanceId: String): Resource<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.deleteMaintenance(maintenanceId)
                if (response.isSuccessful) {
                    Resource.Success(Unit)
                } else { Resource.Error("No se pudo eliminar el registro.") }
            } catch (e: Exception) { Resource.Error(e.localizedMessage ?: "Error de conexión") }
        }
    }

    suspend fun getMaintenanceRecommendations(vehicleId: String): Resource<List<MaintenanceRecommendation>> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getMaintenanceRecommendations(vehicleId)
                if (response.isSuccessful && response.body() != null) {
                    Resource.Success(response.body()!!.recommendations.map { it.toMaintenanceRecommendation() })
                } else { Resource.Error("Error al obtener recomendaciones.") }
            } catch (e: Exception) { Resource.Error(e.localizedMessage ?: "Error de conexión") }
        }
    }

    suspend fun getMaintenanceUpcoming(): Resource<List<MaintenanceDueSummary>> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getMaintenanceUpcoming()
                if (response.isSuccessful && response.body() != null) {
                    Resource.Success(response.body()!!.items.map { it.toMaintenanceDueSummary() })
                } else { Resource.Error("Error al cargar recordatorios.") }
            } catch (e: Exception) { Resource.Error(e.localizedMessage ?: "Error de conexión") }
        }
    }

    suspend fun getMaintenanceUpcomingAll(): Resource<List<MaintenanceDueSummary>> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getMaintenanceUpcomingAll()
                if (response.isSuccessful && response.body() != null) {
                    Resource.Success(response.body()!!.items.map { it.toMaintenanceDueSummary() })
                } else { Resource.Error("Error al cargar recordatorios globales.") }
            } catch (e: Exception) { Resource.Error(e.localizedMessage ?: "Error de conexión") }
        }
    }

    // --- SERVICE ORDERS ---

    suspend fun createServiceOrder(request: CreateServiceOrderRequest): Resource<ServiceOrder> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.createServiceOrder(request)
                if (response.isSuccessful && response.body() != null) {
                    Resource.Success(response.body()!!.order.toServiceOrder())
                } else { Resource.Error("No se pudo crear la orden de servicio.") }
            } catch (e: Exception) { Resource.Error(e.localizedMessage ?: "Error de conexión") }
        }
    }

    suspend fun getServiceOrderQuote(vehicleId: String, serviceType: String): Resource<ServiceQuote> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getServiceOrderQuote(vehicleId, mapOf("service_type" to serviceType))
                if (response.isSuccessful && response.body() != null) {
                    Resource.Success(response.body()!!.quote.toServiceQuote())
                } else { Resource.Error("Error al obtener cotización.") }
            } catch (e: Exception) { Resource.Error(e.localizedMessage ?: "Error de conexión") }
        }
    }

    suspend fun listMyServiceOrders(): Resource<List<ServiceOrder>> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.listMyServiceOrders()
                if (response.isSuccessful && response.body() != null) {
                    Resource.Success(response.body()!!.items.map { it.toServiceOrder() })
                } else { Resource.Error("No se pudieron cargar tus órdenes.") }
            } catch (e: Exception) { Resource.Error(e.localizedMessage ?: "Error de conexión") }
        }
    }

    suspend fun startServiceOrder(orderId: String, agencyNotes: String?): Resource<ServiceOrder> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.startServiceOrder(orderId, StartServiceOrderRequest(agencyNotes))
                if (response.isSuccessful && response.body() != null) {
                    Resource.Success(response.body()!!.order.toServiceOrder())
                } else { Resource.Error("No se pudo iniciar la orden.") }
            } catch (e: Exception) { Resource.Error(e.localizedMessage ?: "Error de conexión") }
        }
    }

    suspend fun completeServiceOrder(orderId: String, completionToken: String, finalCost: Double?, agencyNotes: String?, mileage: Int?): Resource<ServiceOrder> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.completeServiceOrder(orderId, CompleteServiceOrderRequest(completionToken, finalCost, agencyNotes, mileage))
                if (response.isSuccessful && response.body() != null) {
                    Resource.Success(response.body()!!.order.toServiceOrder())
                } else { Resource.Error("No se pudo finalizar la orden.") }
            } catch (e: Exception) { Resource.Error(e.localizedMessage ?: "Error de conexión") }
        }
    }

    suspend fun cancelServiceOrder(orderId: String, notes: String?): Resource<ServiceOrder> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.cancelServiceOrder(orderId, CancelServiceOrderRequest(notes))
                if (response.isSuccessful && response.body() != null) {
                    Resource.Success(response.body()!!.order.toServiceOrder())
                } else { Resource.Error("No se pudo cancelar la orden.") }
            } catch (e: Exception) { Resource.Error(e.localizedMessage ?: "Error de conexión") }
        }
    }

    suspend fun downloadServiceOrdersReport(from: String? = null, to: String? = null, status: String? = "FINALIZADO"): Resource<ByteArray> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.downloadServiceOrdersReport(from, to, status)
                if (response.isSuccessful && response.body() != null) {
                    Resource.Success(response.body()!!.bytes())
                } else { Resource.Error("Error al generar el reporte PDF.") }
            } catch (e: Exception) { Resource.Error(e.localizedMessage ?: "Error de conexión") }
        }
    }

    // --- TOOLS ---

    suspend fun getMonthlyCostEstimate(
        monthlyKm: Double,
        kmPerLiter: Double,
        fuelPrice: Double,
        maintenancePerKm: Double
    ): Resource<MonthlyCostEstimate> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getMonthlyCostEstimate(
                    monthlyKm = monthlyKm,
                    kmPerLiter = kmPerLiter,
                    fuelPrice = fuelPrice,
                    maintenancePerKm = maintenancePerKm
                )
                if (response.isSuccessful && response.body() != null) {
                    Resource.Success(response.body()!!.toMonthlyCostEstimate())
                } else {
                    Resource.Error(
                        parseBackendError(response.errorBody()?.string())
                            ?: "No se pudo calcular el costo mensual."
                    )
                }
            } catch (e: Exception) {
                Resource.Error(e.localizedMessage ?: "Error de conexión")
            }
        }
    }

    suspend fun listAllServiceOrders(status: String? = null): Resource<List<ServiceOrder>> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.listAllServiceOrders(status)
                if (response.isSuccessful && response.body() != null) {
                    Resource.Success(response.body()!!.items.map { it.toServiceOrder() })
                } else { Resource.Error("Error al cargar las órdenes.") }
            } catch (e: Exception) { Resource.Error(e.localizedMessage ?: "Error de conexión") }
        }
    }

    private fun parseBackendError(errorBody: String?): String? {
        if (errorBody.isNullOrBlank()) return null
        val singleError = Regex("\"error\"\\s*:\\s*\"([^\"]+)\"").find(errorBody)?.groupValues?.getOrNull(1)
        if (!singleError.isNullOrBlank()) return singleError
        val errorsArray = Regex("\"errors\"\\s*:\\s*\\[(.*?)]").find(errorBody)?.groupValues?.getOrNull(1)
        if (!errorsArray.isNullOrBlank()) {
            return errorsArray.split(",").map { it.trim().trim('"') }.filter { it.isNotBlank() }.joinToString("\n").ifBlank { null }
        }
        return null
    }
}
