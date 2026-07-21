package org.example.project.di

import org.example.project.database.TrackerDatabase
import org.example.project.database.createTrackerDatabase
import org.koin.dsl.module

val webDatabaseModule = module {
    single<TrackerDatabase> { createTrackerDatabase() }
}
