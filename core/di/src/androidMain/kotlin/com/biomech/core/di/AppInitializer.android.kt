package com.biomech.core.di

import com.biomech.core.ble.AndroidBleManager
import com.biomech.core.ble.BleManager
import com.biomech.core.common.PlatformContext
import com.biomech.core.database.AppDatabase
import com.biomech.core.database.createRoomDatabase
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.dsl.module

actual val platformModule: Module = module {
    single<BleManager> { AndroidBleManager() }
    single<AppDatabase> { createRoomDatabase(get()) }
}

actual fun initKoin(context: PlatformContext) {
    org.koin.core.context.startKoin {
        androidContext(context.androidContext)
        modules(
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
