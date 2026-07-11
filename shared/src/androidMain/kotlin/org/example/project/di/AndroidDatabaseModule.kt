package org.example.project.di

import org.example.project.database.TrackerDatabase
import org.example.project.database.createTrackerDatabase
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val androidDatabaseModule = module {
    single<TrackerDatabase> { createTrackerDatabase(androidContext()) }
}
