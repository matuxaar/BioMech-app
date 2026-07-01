package com.biomech.core.di

import com.biomech.core.ble.AndroidBleManager
import com.biomech.core.ble.BleManager
import com.biomech.core.ble.SmartBleManager
import com.biomech.core.buildconfig.BuildConfig
import com.biomech.core.common.PlatformContext
import com.biomech.core.connectivity.AndroidConnectivityObserver
import com.biomech.core.connectivity.ConnectivityObserver
import com.biomech.core.database.AppDatabase
import com.biomech.core.database.createRoomDatabase
import com.biomech.core.network.OfflineQueueManager
import com.biomech.core.network.RoomQueueStorage
import com.biomech.core.network.createHttpClient
import com.biomech.core.network.repository.AndroidEMGRepositoryImpl
import com.biomech.core.network.repository.AndroidDeviceRepositoryImpl
import com.biomech.core.network.repository.AndroidTrainingRepositoryImpl
import com.biomech.core.network.repository.AuthRepositoryImpl
import com.biomech.domain.repository.AuthRepository
import com.biomech.domain.repository.DeviceRepository
import com.biomech.domain.repository.EMGRepository
import com.biomech.domain.repository.TrainingRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext
import org.koin.core.module.Module
import org.koin.dsl.module

actual val databaseModule: Module = module {
    single { get<AppDatabase>().deviceDao() }
    single { get<AppDatabase>().emgSessionDao() }
    single { get<AppDatabase>().trainingJobDao() }
    single { get<AppDatabase>().offlineQueueDao() }
}

actual val repositoriesModule: Module = module {
    single<AuthRepository> { AuthRepositoryImpl(get(), get()) }
    single<DeviceRepository> { AndroidDeviceRepositoryImpl(get(), get(), get()) }
    single<TrainingRepository> { AndroidTrainingRepositoryImpl(get(), get(), get()) }
    single<EMGRepository> { AndroidEMGRepositoryImpl(get(), get(), get()) }
}

actual val platformModule: Module = module {
    single<BleManager> {
        SmartBleManager(realManager = AndroidBleManager(get()))
    }
    single<AppDatabase> { createRoomDatabase(get())!! }
    single<ConnectivityObserver> { AndroidConnectivityObserver(get()) }
    single { OfflineQueueManager(RoomQueueStorage(get()), get(), createHttpClient()) }
}

actual fun initKoin(context: PlatformContext) {
    com.biomech.core.network.ApiConfig.baseUrl = BuildConfig.baseUrl
    org.koin.core.context.startKoin {
        androidContext(context.androidContext)
        modules(
            module { single { context } },
            platformModule,
            storageModule,
            databaseModule,
            apisModule,
            repositoriesModule,
            useCasesModule,
            viewModelsModule,
        )
    }
}

private val appScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

fun onAppStart() {
    val koin = GlobalContext.get()
    koin.get<ConnectivityObserver>().start()
    koin.get<OfflineQueueManager>().start()
    appScope.launch {
        koin.get<AuthRepository>().restoreSession()
    }
}

fun onAppStop() {
    val koin = GlobalContext.getOrNull() ?: return
    koin.get<ConnectivityObserver>().stop()
    koin.get<OfflineQueueManager>().stop()
}
