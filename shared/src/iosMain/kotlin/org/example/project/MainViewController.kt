package org.example.project

import androidx.compose.ui.window.ComposeUIViewController
import org.example.project.database.createTrackerDatabase
import org.example.project.di.appModule
import org.example.project.di.databaseModule
import org.example.project.di.iosDataStoreModule
import org.example.project.di.repositoryModule
import org.example.project.di.useCaseModule
import org.example.project.di.viewModelModule
import org.koin.core.context.startKoin
import org.koin.dsl.module

fun MainViewController() = ComposeUIViewController(
    configure = {
        val iosDatabaseModule = module {
            single { createTrackerDatabase() }
        }
        startKoin {
            modules(
                iosDatabaseModule,
                iosDataStoreModule,
                databaseModule,
                repositoryModule,
                useCaseModule,
                viewModelModule,
                appModule
            )
        }
    }
) {
    App()
}
