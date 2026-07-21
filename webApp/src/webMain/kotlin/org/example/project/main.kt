package org.example.project

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import org.example.project.di.*
import org.koin.core.context.startKoin

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    startKoin {
        modules(
            webDatabaseModule,
            webDataStoreModule,
            databaseModule,
            repositoryModule,
            useCaseModule,
            viewModelModule,
            appModule
        )
    }

    ComposeViewport(viewportContainerId = "app", configure = {

    }) {
        App()
    }
}
