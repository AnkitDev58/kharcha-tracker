package org.example.project.database

import android.content.Context
import androidx.room3.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver

fun createTrackerDatabase(context: Context): TrackerDatabase {
    val dbFile = context.getDatabasePath(DB_NAME)
    return Room.databaseBuilder<TrackerDatabase>(
        context = context,
        name = dbFile.absolutePath
    )
        .setDriver(BundledSQLiteDriver())
        .fallbackToDestructiveMigration(dropAllTables = false)
        .build()
}
