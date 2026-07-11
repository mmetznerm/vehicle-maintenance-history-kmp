package com.mmetzner.vehiclemaintenance.core.di

import com.mmetzner.vehiclemaintenance.core.database.AppDatabase
import com.mmetzner.vehiclemaintenance.core.database.createRoomDatabase
import org.koin.dsl.module

val databaseModule = module {
    single<AppDatabase> { createRoomDatabase(get()) }
    single { get<AppDatabase>().vehicleDao() }
    single { get<AppDatabase>().outboxDao() }
}


