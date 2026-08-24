package com.tustockpro.booklibrary.data.remote.dto

import com.google.gson.annotations.SerializedName

data class GoogleBooksVolumesResponse(
    @SerializedName("items")
    val items: List<GoogleBookItemDto> = emptyList()
)

data class GoogleBookItemDto(
    @SerializedName("id")
    val id: String? = null,
    @SerializedName("volumeInfo")
    val volumeInfo: GoogleBookVolumeInfoDto? = null,
    @SerializedName("saleInfo")
    val saleInfo: GoogleBookSaleInfoDto? = null,
    @SerializedName("accessInfo")
    val accessInfo: GoogleBookAccessInfoDto? = null
)

data class GoogleBookVolumeInfoDto(
    @SerializedName("title")
    val title: String? = null,
    @SerializedName("authors")
    val authors: List<String>? = null,
    @SerializedName("description")
    val description: String? = null,
    @SerializedName("categories")
    val categories: List<String>? = null,
    @SerializedName("averageRating")
    val averageRating: Double? = null,
    @SerializedName("industryIdentifiers")
    val industryIdentifiers: List<GoogleBookIndustryIdentifierDto>? = null,
    @SerializedName("imageLinks")
    val imageLinks: GoogleBookImageLinksDto? = null,
    @SerializedName("publishedDate")
    val publishedDate: String? = null,
    @SerializedName("infoLink")
    val infoLink: String? = null
)

data class GoogleBookIndustryIdentifierDto(
    @SerializedName("type")
    val type: String? = null,
    @SerializedName("identifier")
    val identifier: String? = null
)

data class GoogleBookImageLinksDto(
    @SerializedName("thumbnail")
    val thumbnail: String? = null,
    @SerializedName("smallThumbnail")
    val smallThumbnail: String? = null
)

data class GoogleBookSaleInfoDto(
    @SerializedName("saleability")
    val saleability: String? = null
)

data class GoogleBookAccessInfoDto(
    @SerializedName("epub")
    val epub: GoogleBookAccessAvailabilityDto? = null,
    @SerializedName("pdf")
    val pdf: GoogleBookAccessAvailabilityDto? = null
)

data class GoogleBookAccessAvailabilityDto(
    @SerializedName("isAvailable")
    val isAvailable: Boolean? = null
)
