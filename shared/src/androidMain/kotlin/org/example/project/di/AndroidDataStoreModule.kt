package org.example.project.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val androidDataStoreModule = module {
    single<DataStore<Preferences>> {
        androidContext().createDataStore()
    }
}

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "tracker_settings")
private fun Context.createDataStore(): DataStore<Preferences> = dataStore
