package org.example.project.di

import org.example.project.core.data.SeedDataManager
import org.koin.dsl.module

val appModule = module {
    factory { SeedDataManager(get(), get()) }
}
