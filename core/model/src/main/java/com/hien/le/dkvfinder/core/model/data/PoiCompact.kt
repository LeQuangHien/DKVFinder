package com.hien.le.dkvfinder.core.model.data

data class PoiCompact(
    val id: Int?,
    val title: String?,
    val address: String?,
    val town: String?,
    val telephone: String?,
    val distance: Double?,
    val distanceUnit: Int?,
    val latitude: Double?,
    val longitude: Double?,
    val isFavorite: Boolean = false,
)

fun Poi.mapToPoiCompact(): PoiCompact {
    return PoiCompact(
        id = this.id,
        title = this.addressInfo?.title,
        address = formatAddress(this.addressInfo),
        town = this.addressInfo?.town,
        telephone = this.addressInfo?.contactTelephone1 ?: this.addressInfo?.contactTelephone2,
        distance = this.addressInfo?.distance,
        distanceUnit = this.addressInfo?.distanceUnit,
        latitude = this.addressInfo?.latitude,
        longitude = this.addressInfo?.longitude,
    )
}

private fun formatAddress(addressInfo: AddressInfo?): String {
    return listOfNotNull(
        addressInfo?.addressLine1,
        addressInfo?.addressLine2,
        addressInfo?.postcode,
        addressInfo?.town,
    ).joinToString(separator = ", ").ifEmpty { "Address not available" }
}
