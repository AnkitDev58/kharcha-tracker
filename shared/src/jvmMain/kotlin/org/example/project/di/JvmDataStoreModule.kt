package org.example.project.di

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import okio.Path.Companion.toPath
import org.koin.dsl.module
import java.io.File

val jvmDataStoreModule = module {
    single<DataStore<Preferences>> { createJvmDataStore() }
}

private fun createJvmDataStore(): DataStore<Preferences> {
    val dbFile = File(System.getProperty("user.home"), "tracker_settings.preferences_pb")
    return PreferenceDataStoreFactory.createWithPath(produceFile = { dbFile.absolutePath.toPath() })
}
