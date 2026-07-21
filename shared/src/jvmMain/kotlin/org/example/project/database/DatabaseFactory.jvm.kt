package org.example.project.database

import androidx.room3.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import java.io.File

fun createTrackerDatabase(): TrackerDatabase {
    val dbFile = File(System.getProperty("user.home"), DB_NAME)
    return Room.databaseBuilder<TrackerDatabase>(
        name = dbFile.absolutePath
    )
        .setDriver(BundledSQLiteDriver())
        .fallbackToDestructiveMigration(dropAllTables = false)
        .build()
}
