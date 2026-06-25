package com.biomech.core.di

import com.biomech.core.ble.BleManager
import com.biomech.core.ble.IosBleManager
import com.biomech.core.common.PlatformContext
import com.biomech.core.database.AppDatabase
import com.biomech.core.database.createRoomDatabase
import org.koin.core.module.Module
import org.koin.dsl.module

actual val platformModule: Module = module {
    single<BleManager> { IosBleManager() }
    single<AppDatabase> { createRoomDatabase(get()) }
}

actual fun initKoin(context: PlatformContext) {
    org.koin.core.context.startKoin {
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
