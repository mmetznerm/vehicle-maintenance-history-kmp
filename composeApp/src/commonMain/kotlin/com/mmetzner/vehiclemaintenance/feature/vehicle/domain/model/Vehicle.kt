package com.mmetzner.vehiclemaintenance.feature.vehicle.domain.model

data class Vehicle(
    val plate: String,
    val model: String,
    val brand: String,
    val year: Int,
    val maintenances: List<Maintenance>? = null,
    val isPendingSync: Boolean = false,
    val id: String? = null,
    val color: String = ""
)


