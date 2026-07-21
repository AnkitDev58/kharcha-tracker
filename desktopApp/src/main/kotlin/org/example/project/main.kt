package org.example.project

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import org.example.project.di.*
import org.koin.core.context.startKoin

fun main() {
    startKoin {
        modules(
            jvmDatabaseModule,
            jvmDataStoreModule,
            databaseModule,
            repositoryModule,
            useCaseModule,
            viewModelModule,
            appModule,
        )
    }

    application {
        Window(
            onCloseRequest = ::exitApplication,
            title = "Kharcha Tracker",
        ) {
            App()
        }
    }
}
