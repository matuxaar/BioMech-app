package com.biomech.core.di

import com.biomech.core.common.PlatformContext
import com.biomech.core.database.AppDatabase
import com.biomech.core.network.api.EMGApi
import com.biomech.core.network.repository.EMGRepositoryImpl
import com.biomech.core.network.repository.AuthRepositoryImpl
import com.biomech.core.network.repository.DeviceRepositoryImpl
import com.biomech.core.network.repository.TrainingRepositoryImpl
import com.biomech.core.network.api.AuthApi
import com.biomech.core.network.api.DeviceApi
import com.biomech.core.network.api.TrainingApi
import com.biomech.core.storage.KeyValueStorage
import com.biomech.core.storage.createKeyValueStorage
import com.biomech.domain.repository.AuthRepository
import com.biomech.domain.repository.DeviceRepository
import com.biomech.domain.repository.EMGRepository
import com.biomech.domain.repository.TrainingRepository
import com.biomech.domain.usecase.LoginUseCase
import com.biomech.domain.usecase.StartEMGSessionUseCase
import com.biomech.feature.auth.LoginViewModel
import com.biomech.feature.home.HomeViewModel
import com.biomech.feature.profile.ProfileViewModel
import com.biomech.feature.settings.SettingsViewModel
import com.biomech.feature.devices.DevicesViewModel
import com.biomech.feature.training.TrainingViewModel
import org.koin.core.module.Module
import org.koin.dsl.module

val viewModelsModule: Module = module {
    factory { LoginViewModel(get()) }
    factory { HomeViewModel(get()) }
    factory { ProfileViewModel(get()) }
    factory { SettingsViewModel(get()) }
    factory { DevicesViewModel(get()) }
    factory { TrainingViewModel(get(), get()) }
}

val useCasesModule: Module = module {
    factory { LoginUseCase(get()) }
    factory { StartEMGSessionUseCase(get()) }
}

val storageModule: Module = module {
    single<KeyValueStorage> { createKeyValueStorage(get()) }
}

val databaseModule: Module = module {
    single { get<AppDatabase>().deviceDao() }
    single { get<AppDatabase>().emgSessionDao() }
    single { get<AppDatabase>().trainingJobDao() }
}

val repositoriesModule: Module = module {
    single<AuthRepository> { AuthRepositoryImpl(get(), get()) }
    single<DeviceRepository> { DeviceRepositoryImpl(get(), get()) }
    single<TrainingRepository> { TrainingRepositoryImpl(get(), get()) }
    single<EMGRepository> { EMGRepositoryImpl(get(), get()) }
}

val apisModule: Module = module {
    single { AuthApi() }
    single { DeviceApi() }
    single { TrainingApi() }
    single { EMGApi() }
}

expect val platformModule: Module

expect fun initKoin(context: PlatformContext)
