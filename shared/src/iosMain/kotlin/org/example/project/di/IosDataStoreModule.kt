package org.example.project.di

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import kotlinx.cinterop.ExperimentalForeignApi
import okio.Path.Companion.toPath
import org.koin.dsl.module
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSUserDomainMask

val iosDataStoreModule = module {
    single<DataStore<Preferences>> { createIosDataStore() }
}

@OptIn(ExperimentalForeignApi::class)
private fun createIosDataStore(): DataStore<Preferences> {
    val documentDirectory = NSFileManager.defaultManager.URLForDirectory(
        directory = NSDocumentDirectory,
        inDomain = NSUserDomainMask,
        appropriateForURL = null,
        create = false,
        error = null
    )
    val path = requireNotNull(documentDirectory?.path) + "/tracker_settings.preferences_pb"
    return PreferenceDataStoreFactory.createWithPath(produceFile = { path.toPath() })
}
