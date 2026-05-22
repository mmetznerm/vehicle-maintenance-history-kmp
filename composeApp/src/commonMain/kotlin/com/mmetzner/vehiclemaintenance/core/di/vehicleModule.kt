package com.mmetzner.vehiclemaintenance.core.di

import com.mmetzner.vehiclemaintenance.feature.vehicle.domain.repository.VehicleRepository
import com.mmetzner.vehiclemaintenance.feature.vehicle.data.VehicleRepositoryImpl
import com.mmetzner.vehiclemaintenance.feature.vehicle.presentation.VehicleSearchViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val vehicleModule = module {
    single<VehicleRepository> { VehicleRepositoryImpl(get(), get()) }
    viewModel { VehicleSearchViewModel(get()) }
}