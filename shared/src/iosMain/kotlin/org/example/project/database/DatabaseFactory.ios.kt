package org.example.project.database

import androidx.room.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import platform.Foundation.NSHomeDirectory

fun createTrackerDatabase(): TrackerDatabase {
    val dbFile = "${NSHomeDirectory()}/Documents/$DB_NAME"
    return Room.databaseBuilder<TrackerDatabase>(
        name = dbFile
    )
        .setDriver(BundledSQLiteDriver())
        .fallbackToDestructiveMigration(dropAllTables = false)
        .build()
}
