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

    /**
     * MOCK DATA FOR PROTOTYPE (Vehículos de catálogo)
     */
    suspend fun listCatalogVehicles(): Resource<List<CatalogVehicle>> {
        return withContext(Dispatchers.IO) {
            val mockItems = listOf(
                CatalogVehicle("toyota_corolla", "Toyota", "Corolla", "sedan", "gasolina", "automatica", listOf("https://alden.mx/wp-content/uploads/2025/01/toyota-corolla-3.webp")),
                CatalogVehicle("honda_civic", "Honda", "Civic", "sedan", "gasolina", "manual", listOf("https://acroadtrip.blob.core.windows.net/catalogo-imagenes/s/RT_V_2d38e1a6a430401099b785deaf510a2b.webp")),
                CatalogVehicle("ford_ranger", "Ford", "Ranger", "pickup", "diesel", "manual", listOf("https://i.blogs.es/818b47/ford-ranger-raptor_/1366_2000.jpg")),
                CatalogVehicle("nissan_sentra", "Nissan", "Sentra", "sedan", "gasolina", "automatica", listOf("https://www.nissannamicholula.com.mx/inventoryphotos/14041/24197nssn0100010264/ip/1.jpg")),
                CatalogVehicle("volkswagen_jetta", "Volkswagen", "Jetta", "sedan", "gasolina", "automatica", listOf("https://us.as.com/autos/wp-content/uploads/2024/06/pixelcut-export-2024-06-26T091457.427-1264x734.jpg")),
                CatalogVehicle("chevrolet_onix", "Chevrolet", "Onix", "hatchback", "gasolina", "manual", listOf("https://www.autoasesor.com/chevrolet/imagenes/onix2023.png")),
                CatalogVehicle("chevrolet_tracker", "Chevrolet", "Tracker", "suv", "gasolina", "automatica", listOf("https://www.chevrolet.com.mx/content/dam/chevrolet/na/mx/es/index/crossovers-suvs/2025-tracker/specs/01-images/trims/2025-tracker-ls.jpg?imwidth=2400")),
                CatalogVehicle("hyundai_tucson", "Hyundai", "Tucson", "suv", "hibrido", "automatica", listOf("https://di-uploads-pod27.dealerinspire.com/classichyundai/uploads/2024/05/2025-Hyundai-Tucson.jpg")),
                CatalogVehicle("kia_rio", "Kia", "Rio", "hatchback", "gasolina", "manual", listOf("https://www.diariomotor.com/imagenes/2022/01/kia-rio-vista-lateral-794707.jpg?class=XL")),
                CatalogVehicle("mazda_3", "Mazda", "3", "sedan", "gasolina", "automatica", listOf("https://autoanalitica.com.mx/wp-content/uploads/2024/06/Mazda3-2025-2048x1365.jpg")),
                CatalogVehicle("toyota_hilux", "Toyota", "Hilux", "pickup", "diesel", "manual", listOf("https://espaillatmotors.com/website/wp-content/uploads/2024/11/1-22.jpg")),
                CatalogVehicle("tesla_model3", "Tesla", "Model3", "sedan", "electrico", "automatica", listOf("https://cdn.wheel-size.com/thumbs/e7/7e/e77ea9ffa03676c4ba1a585985c6937c.jpg")),
                CatalogVehicle("jeep_compass", "Jeep", "Compass", "suv", "gasolina", "automatica", listOf("https://www.megautos.com/wp-content/uploads/2024/04/Compass_blackhawk_001_.jpg"))
            )
            Resource.Success(mockItems)
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
