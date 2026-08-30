package com.mmetzner.vehiclemaintenance.core.di

import com.mmetzner.vehiclemaintenance.feature.vehicle.data.VehicleRepositoryImpl
import com.mmetzner.vehiclemaintenance.feature.vehicle.data.remote.VehicleRemoteDataSource
import com.mmetzner.vehiclemaintenance.feature.vehicle.domain.repository.MaintenanceRepository
import com.mmetzner.vehiclemaintenance.feature.vehicle.domain.repository.SyncRepository
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
    single { VehicleRepositoryImpl(get(), get(), get(), get()) }
    single<VehicleRepository> { get<VehicleRepositoryImpl>() }
    single<MaintenanceRepository> { get<VehicleRepositoryImpl>() }
    single<SyncRepository> { get<VehicleRepositoryImpl>() }
    viewModel { VehicleSearchViewModel(get()) }
    viewModel { VehicleListViewModel(get(), get()) }
    viewModel { VehicleDetailsViewModel(get()) }
    viewModel { VehicleEditViewModel(get()) }
    viewModel { VehicleHomeViewModel(get(), get()) }
    viewModel { AddVehicleViewModel(get()) }
    viewModel { AddMaintenanceViewModel(get()) }
    viewModel { MaintenanceEditViewModel(get(), get()) }
}
