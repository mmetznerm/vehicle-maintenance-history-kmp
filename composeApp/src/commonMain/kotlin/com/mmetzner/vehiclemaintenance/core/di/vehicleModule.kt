package com.mmetzner.vehiclemaintenance.core.di

import com.mmetzner.vehiclemaintenance.feature.vehicle.data.VehicleRepositoryImpl
import com.mmetzner.vehiclemaintenance.feature.vehicle.data.remote.VehicleRemoteDataSource
import com.mmetzner.vehiclemaintenance.feature.vehicle.domain.repository.VehicleRepository
import com.mmetzner.vehiclemaintenance.feature.vehicle.presentation.addmaintenance.AddMaintenanceViewModel
import com.mmetzner.vehiclemaintenance.feature.vehicle.presentation.addvehicle.AddVehicleViewModel
import com.mmetzner.vehiclemaintenance.feature.vehicle.presentation.search.VehicleSearchViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val vehicleModule = module {
    single { VehicleRemoteDataSource(get(), get()) }
    single<VehicleRepository> { VehicleRepositoryImpl(get(), get()) }
    viewModel { VehicleSearchViewModel(get()) }
    viewModel { AddVehicleViewModel(get()) }
    viewModel { AddMaintenanceViewModel(get()) }
}
