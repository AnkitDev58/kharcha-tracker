package org.example.project

import androidx.compose.ui.window.ComposeUIViewController
import org.example.project.di.*
import org.example.project.database.createTrackerDatabase
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
