package org.example.project

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import org.example.project.di.*
import org.koin.core.context.startKoin
import kotlin.js.js

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    registerServiceWorker()
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


fun registerServiceWorker() {
    js(
        """
        if ('serviceWorker' in navigator) {
            navigator.serviceWorker.register('/sw.js');
        }
        """
    )
}
