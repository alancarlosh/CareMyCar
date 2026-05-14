package com.itsm.caremycar.vehicle

import com.google.gson.annotations.SerializedName

data class Part(
    val id: String,
    val userId: String,
    val name: String,
    val category: String,
    val make: String?,
    val year: Int?,
    val model: String?,
    val compatibility: List<String>,
    val price: Double,
    val quantity: Int,
    val createdAt: String?,
    val updatedAt: String?
)

data class PartDto(
    val id: String,
    @SerializedName("user_id")
    val userId: String,
    val name: String,
    val category: String,
    val make: String?,
    val year: Int?,
    val model: String?,
    val compatibility: List<String>?,
    val price: Double,
    val quantity: Int,
    @SerializedName("created_at")
    val createdAt: String?,
    @SerializedName("updated_at")
    val updatedAt: String?
)

data class PartListResponse(
    val items: List<PartDto>,
    val total: Int
)

data class PartDetailResponse(
    val part: PartDto
)

data class PartOptionsResponse(
    val categories: List<String>,
    val makes: List<String>,
    val years: List<Int>,
    val models: List<String>
)

data class CreatePartRequest(
    val name: String,
    val category: String,
    val make: String?,
    val year: Int?,
    val model: String?,
    val compatibility: List<String> = emptyList(),
    val price: Double,
    val quantity: Int
)

fun PartDto.toPart(): Part {
    return Part(
        id = id,
        userId = userId,
        name = name,
        category = category,
        make = make,
        year = year,
        model = model,
        compatibility = compatibility.orEmpty(),
        price = price,
        quantity = quantity,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}
