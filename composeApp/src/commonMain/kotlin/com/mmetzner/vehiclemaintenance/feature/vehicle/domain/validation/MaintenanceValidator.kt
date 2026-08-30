package com.mmetzner.vehiclemaintenance.feature.vehicle.domain.validation

import org.jetbrains.compose.resources.StringResource
import vehiclemaintenance.composeapp.generated.resources.*

object MaintenanceValidator {
    fun validate(
        date: String,
        mileage: String,
        totalValue: String,
        description: String
    ): StringResource? {
        val parsedMileage = mileage.toIntOrNull()
        val parsedTotalValue = totalValue.toCurrencyDoubleOrNull()

        return when {
            date.isBlank() -> Res.string.error_maintenance_date_required
            mileage.isBlank() -> Res.string.error_maintenance_odometer_required
            parsedMileage == null || parsedMileage < 0 -> Res.string.error_maintenance_odometer_invalid
            totalValue.isBlank() -> Res.string.error_maintenance_cost_required
            parsedTotalValue == null || parsedTotalValue < 0.0 -> Res.string.error_maintenance_cost_invalid
            description.isBlank() -> Res.string.error_maintenance_description_required
            description.trim().length > 500 -> Res.string.error_maintenance_description_too_long
            else -> null
        }
    }
}

fun String.toCurrencyDoubleOrNull(): Double? {
    return filter { char ->
        char.isDigit() || char == '.' || char == ','
    }
        .replace(',', '.')
        .toDoubleOrNull()
}
