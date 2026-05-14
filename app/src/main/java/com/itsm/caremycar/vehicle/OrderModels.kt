package com.itsm.caremycar.vehicle

import com.google.gson.annotations.SerializedName

data class Order(
    val id: String,
    val userId: String,
    val buyerId: String?,
    val clientName: String,
    val vin: String,
    val make: String,
    val year: Int,
    val model: String,
    val partId: String,
    val partName: String?,
    val quantity: Int,
    val unitPrice: Double,
    val totalPrice: Double,
    val status: String,
    val createdAt: String?,
    val updatedAt: String?
)

data class OrderDto(
    val id: String,
    @SerializedName("user_id")
    val userId: String,
    @SerializedName("buyer_id")
    val buyerId: String?,
    @SerializedName("client_name")
    val clientName: String,
    val vin: String,
    val make: String,
    val year: Int,
    val model: String,
    @SerializedName("part_id")
    val partId: String,
    @SerializedName("part_name")
    val partName: String?,
    val quantity: Int,
    @SerializedName("unit_price")
    val unitPrice: Double,
    @SerializedName("total_price")
    val totalPrice: Double,
    val status: String,
    @SerializedName("created_at")
    val createdAt: String?,
    @SerializedName("updated_at")
    val updatedAt: String?
)

data class OrderListResponse(
    val items: List<OrderDto>,
    val total: Int,
    @SerializedName("all_count")
    val allCount: Int,
    @SerializedName("pending_count")
    val pendingCount: Int
)

data class OrderDetailResponse(
    val order: OrderDto
)

data class OrderOptionsResponse(
    val years: List<Int>,
    val makes: List<String>,
    val models: List<String>,
    val parts: List<PartSummaryDto>
)

data class PartSummaryDto(
    val id: String,
    val name: String,
    val category: String,
    val make: String?,
    val model: String?,
    val year: Int?,
    val price: Double,
    @SerializedName("available_quantity")
    val availableQuantity: Int
)

data class CreateOrderRequest(
    @SerializedName("client_name")
    val clientName: String,
    val vin: String,
    val make: String,
    val year: Int,
    val model: String,
    @SerializedName("part_id")
    val partId: String,
    val quantity: Int,
    val status: String = "pending"
)

data class MarketplacePurchaseRequest(
    @SerializedName("part_id")
    val partId: String,
    val quantity: Int = 1
)

data class SalesDailyReportResponse(
    val report: SalesDailyReportDto
)

data class SalesDailyReportDto(
    val date: String,
    @SerializedName("total_orders")
    val totalOrders: Int,
    @SerializedName("total_sales")
    val totalSales: Double,
    @SerializedName("pending_count")
    val pendingCount: Int,
    @SerializedName("confirmed_count")
    val confirmedCount: Int,
    @SerializedName("delivered_count")
    val deliveredCount: Int,
    @SerializedName("canceled_count")
    val canceledCount: Int,
    val items: List<OrderDto>
)

data class SalesDailyReport(
    val date: String,
    val totalOrders: Int,
    val totalSales: Double,
    val pendingCount: Int,
    val confirmedCount: Int,
    val deliveredCount: Int,
    val canceledCount: Int,
    val items: List<Order>
)

fun SalesDailyReportDto.toSalesDailyReport(): SalesDailyReport {
    return SalesDailyReport(
        date = date,
        totalOrders = totalOrders,
        totalSales = totalSales,
        pendingCount = pendingCount,
        confirmedCount = confirmedCount,
        deliveredCount = deliveredCount,
        canceledCount = canceledCount,
        items = items.map { it.toOrder() }
    )
}

fun OrderDto.toOrder(): Order {
    return Order(
        id = id,
        userId = userId,
        buyerId = buyerId,
        clientName = clientName,
        vin = vin,
        make = make,
        year = year,
        model = model,
        partId = partId,
        partName = partName,
        quantity = quantity,
        unitPrice = unitPrice,
        totalPrice = totalPrice,
        status = status,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}
