package com.mmetzner.vehiclemaintenance.core.di

import com.mmetzner.vehiclemaintenance.feature.vehicle.data.VehicleRepositoryImpl
import com.mmetzner.vehiclemaintenance.feature.vehicle.data.remote.VehicleRemoteDataSource
import com.mmetzner.vehiclemaintenance.feature.vehicle.domain.repository.VehicleRepository
import com.mmetzner.vehiclemaintenance.feature.vehicle.presentation.addmaintenance.AddMaintenanceViewModel
import com.mmetzner.vehiclemaintenance.feature.vehicle.presentation.addvehicle.AddVehicleViewModel
import com.mmetzner.vehiclemaintenance.feature.vehicle.presentation.home.VehicleHomeViewModel
import com.mmetzner.vehiclemaintenance.feature.vehicle.presentation.maintenanceedit.MaintenanceEditViewModel
import com.mmetzner.vehiclemaintenance.feature.vehicle.presentation.search.VehicleSearchViewModel
import com.mmetzner.vehiclemaintenance.feature.vehicle.presentation.vehicledetails.VehicleDetailsViewModel
import com.mmetzner.vehiclemaintenance.feature.vehicle.presentation.vehicleedit.VehicleEditViewModel
import com.mmetzner.vehiclemaintenance.feature.vehicle.presentation.vehiclelist.VehicleListViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val vehicleModule = module {
    single { VehicleRemoteDataSource(get(), get()) }
    single<VehicleRepository> { VehicleRepositoryImpl(get(), get(), get(), get()) }
    viewModel { VehicleSearchViewModel(get()) }
    viewModel { VehicleListViewModel(get()) }
    viewModel { VehicleDetailsViewModel(get()) }
    viewModel { VehicleEditViewModel(get()) }
    viewModel { VehicleHomeViewModel(get()) }
    viewModel { AddVehicleViewModel(get()) }
    viewModel { AddMaintenanceViewModel(get()) }
    viewModel { MaintenanceEditViewModel(get()) }
}
