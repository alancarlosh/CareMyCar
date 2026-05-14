package com.itsm.caremycar.api

import com.itsm.caremycar.session.LoginRequest
import com.itsm.caremycar.session.LoginResponse
import com.itsm.caremycar.session.RegisterRequest
import com.itsm.caremycar.session.RegisterResponse
import com.itsm.caremycar.vehicle.* 
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.PATCH
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.Streaming

interface ApiService {
    @POST("api/auth/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @POST("api/auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<RegisterResponse>

    // Parts
    @GET("api/parts")
    suspend fun listParts(
        @Query("q") query: String?,
        @Query("category") category: String?,
        @Query("page") page: Int,
        @Query("limit") limit: Int
    ): Response<PartListResponse>

    @POST("api/parts")
    suspend fun createPart(@Body request: CreatePartRequest): Response<PartDetailResponse>

    @GET("api/parts/options")
    suspend fun getPartOptions(
        @Query("make") make: String? = null
    ): Response<PartOptionsResponse>

    @GET("api/parts/{partId}")
    suspend fun getPartById(@Path("partId") partId: String): Response<PartDetailResponse>

    @PUT("api/parts/{partId}")
    suspend fun updatePart(
        @Path("partId") partId: String,
        @Body payload: Map<String, @JvmSuppressWildcards Any>
    ): Response<PartDetailResponse>

    @DELETE("api/parts/{partId}")
    suspend fun deletePart(@Path("partId") partId: String): Response<Unit>

    // Orders
    @GET("api/orders")
    suspend fun listOrders(
        @Query("q") query: String?,
        @Query("status") status: String?,
        @Query("page") page: Int,
        @Query("limit") limit: Int
    ): Response<OrderListResponse>

    @GET("api/orders/marketplace/products")
    suspend fun listMarketplaceProducts(
        @Query("q") query: String?,
        @Query("category") category: String?,
        @Query("page") page: Int,
        @Query("limit") limit: Int
    ): Response<PartListResponse>

    @POST("api/orders/marketplace/purchase")
    suspend fun purchaseMarketplaceProduct(
        @Body request: MarketplacePurchaseRequest
    ): Response<OrderDetailResponse>

    @GET("api/orders/purchases/my")
    suspend fun listMyPurchases(
        @Query("status") status: String?,
        @Query("page") page: Int,
        @Query("limit") limit: Int
    ): Response<OrderListResponse>

    @GET("api/orders/sales/daily-report")
    suspend fun getSalesDailyReport(
        @Query("date") date: String? = null
    ): Response<SalesDailyReportResponse>

    @Streaming
    @GET("api/orders/sales/daily-report/pdf")
    suspend fun downloadSalesDailyReportPdf(
        @Query("date") date: String? = null
    ): Response<ResponseBody>

    @POST("api/orders")
    suspend fun createOrder(@Body request: CreateOrderRequest): Response<OrderDetailResponse>

    @GET("api/orders/options")
    suspend fun getOrderOptions(
        @Query("make") make: String? = null,
        @Query("model") model: String? = null,
        @Query("year") year: Int? = null
    ): Response<OrderOptionsResponse>

    @GET("api/orders/{orderId}")
    suspend fun getOrderById(@Path("orderId") orderId: String): Response<OrderDetailResponse>

    @PUT("api/orders/{orderId}")
    suspend fun updateOrder(
        @Path("orderId") orderId: String,
        @Body payload: Map<String, @JvmSuppressWildcards Any>
    ): Response<OrderDetailResponse>

    @DELETE("api/orders/{orderId}")
    suspend fun deleteOrder(@Path("orderId") orderId: String): Response<Unit>

    // Vehicles
    @GET("api/vehicles")
    suspend fun listVehicles(): Response<VehicleListResponse>

    @GET("api/catalog/vehicles")
    suspend fun listCatalogVehicles(): Response<CatalogVehicleListResponse>

    @GET("api/vehicles/{vehicleId}")
    suspend fun getVehicleById(@Path("vehicleId") vehicleId: String): Response<VehicleDetailResponse>

    @POST("api/vehicles")
    suspend fun createVehicle(@Body request: CreateVehicleRequest): Response<VehicleDetailResponse>

    @DELETE("api/vehicles/{vehicleId}")
    suspend fun deleteVehicle(@Path("vehicleId") vehicleId: String): Response<DeleteVehicleResponse>

    @PUT("api/vehicles/{vehicleId}")
    suspend fun updateVehicle(
        @Path("vehicleId") vehicleId: String,
        @Body payload: Map<String, @JvmSuppressWildcards Any>
    ): Response<VehicleDetailResponse>

    // Maintenance
    @GET("api/maintenance/{vehicleId}")
    suspend fun listMaintenanceByVehicle(@Path("vehicleId") vehicleId: String): Response<MaintenanceListResponse>

    @GET("api/maintenance/insights/recommendations/{vehicleId}")
    suspend fun getMaintenanceRecommendations(
        @Path("vehicleId") vehicleId: String
    ): Response<MaintenanceRecommendationsResponse>

    @GET("api/maintenance/insights/upcoming")
    suspend fun getMaintenanceUpcoming(): Response<MaintenanceUpcomingResponse>

    @GET("api/maintenance/insights/upcoming/all")
    suspend fun getMaintenanceUpcomingAll(): Response<MaintenanceUpcomingResponse>

    @POST("api/maintenance")
    suspend fun createMaintenance(@Body request: CreateMaintenanceRequest): Response<MaintenanceDetailResponse>

    @PUT("api/maintenance/{maintenanceId}")
    suspend fun updateMaintenance(
        @Path("maintenanceId") maintenanceId: String,
        @Body payload: Map<String, @JvmSuppressWildcards Any>
    ): Response<MaintenanceDetailResponse>

    @DELETE("api/maintenance/{maintenanceId}")
    suspend fun deleteMaintenance(@Path("maintenanceId") maintenanceId: String): Response<MaintenanceDeleteResponse>

    // Service Orders
    @POST("api/service-orders")
    suspend fun createServiceOrder(@Body request: CreateServiceOrderRequest): Response<ServiceOrderDetailResponse>

    @POST("api/service-orders/quote/{vehicleId}")
    suspend fun getServiceOrderQuote(
        @Path("vehicleId") vehicleId: String,
        @Body payload: Map<String, @JvmSuppressWildcards String>
    ): Response<ServiceOrderQuoteResponse>

    @GET("api/service-orders/my")
    suspend fun listMyServiceOrders(): Response<ServiceOrderListResponse>

    @GET("api/service-orders")
    suspend fun listAllServiceOrders(
        @Query("status") status: String? = null
    ): Response<ServiceOrderListResponse>

    @PATCH("api/service-orders/{orderId}/start")
    suspend fun startServiceOrder(
        @Path("orderId") orderId: String,
        @Body request: StartServiceOrderRequest
    ): Response<ServiceOrderDetailResponse>

    @PATCH("api/service-orders/{orderId}/complete")
    suspend fun completeServiceOrder(
        @Path("orderId") orderId: String,
        @Body request: CompleteServiceOrderRequest
    ): Response<ServiceOrderDetailResponse>

    @PATCH("api/service-orders/{orderId}/cancel")
    suspend fun cancelServiceOrder(
        @Path("orderId") orderId: String,
        @Body request: CancelServiceOrderRequest
    ): Response<ServiceOrderDetailResponse>

    @Streaming
    @GET("api/service-orders/report")
    suspend fun downloadServiceOrdersReport(
        @Query("from") from: String? = null,
        @Query("to") to: String? = null,
        @Query("status") status: String? = "FINALIZADO"
    ): Response<ResponseBody>
}
