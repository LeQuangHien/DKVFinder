package com.hien.le.dkvfinder.core.model.data

import com.hien.le.dkvfinder.core.database.entity.PoiEntity

fun PoiEntity.asDataModel(): PoiCompact {
    return PoiCompact(
        id = this.id,
        title = this.title,
        address = this.address,
        town = this.town,
        telephone = this.telephone,
        distance = this.distance,
        distanceUnit = this.distanceUnit,
        latitude = this.latitude,
        longitude = this.longitude,
        isFavorite = this.isFavorite,
    )
}

fun PoiCompact.asEntity(): PoiEntity {
    return PoiEntity(
        id = this.id ?: 0,
        title = this.title,
        address = this.address,
        town = this.town,
        telephone = this.telephone,
        distance = this.distance,
        distanceUnit = this.distanceUnit,
        latitude = this.latitude,
        longitude = this.longitude,
        isFavorite = this.isFavorite,
    )
}
