package org.example.project

import android.app.Application
import org.example.project.di.androidDatabaseModule
import org.example.project.di.androidDataStoreModule
import org.example.project.di.appModule
import org.example.project.di.databaseModule
import org.example.project.di.repositoryModule
import org.example.project.di.useCaseModule
import org.example.project.di.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class TrackerApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger()
            androidContext(this@TrackerApplication)
            modules(
                androidDatabaseModule,
                androidDataStoreModule,
                databaseModule,
                repositoryModule,
                useCaseModule,
                viewModelModule,
                appModule
            )
        }
    }
}
