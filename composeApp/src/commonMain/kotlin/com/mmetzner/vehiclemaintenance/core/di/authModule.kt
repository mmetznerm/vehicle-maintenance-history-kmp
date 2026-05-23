package com.mmetzner.vehiclemaintenance.core.di

import com.mmetzner.vehiclemaintenance.core.auth.AuthTokenStore
import com.mmetzner.vehiclemaintenance.core.auth.InMemoryAuthTokenStore
import com.mmetzner.vehiclemaintenance.feature.auth.data.AuthRepositoryImpl
import com.mmetzner.vehiclemaintenance.feature.auth.data.remote.AuthRemoteDataSource
import com.mmetzner.vehiclemaintenance.feature.auth.domain.repository.AuthRepository
import com.mmetzner.vehiclemaintenance.feature.auth.presentation.login.LoginViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val authModule = module {
    single<AuthTokenStore> { InMemoryAuthTokenStore() }
    single { AuthRemoteDataSource(get(), get()) }
    single<AuthRepository> { AuthRepositoryImpl(get(), get()) }
    viewModel { LoginViewModel(get()) }
}
