package org.example.project.di

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import okio.Path.Companion.toPath
import org.koin.dsl.module

val webDataStoreModule = module {
    single<DataStore<Preferences>> { createWebDataStore() }
}

private fun createWebDataStore(): DataStore<Preferences> {
    return PreferenceDataStoreFactory.createWithPath(
        produceFile = { "tracker_settings.preferences_pb".toPath() }
    )
}
