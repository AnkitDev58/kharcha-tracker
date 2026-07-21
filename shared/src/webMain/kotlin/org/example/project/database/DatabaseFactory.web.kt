package org.example.project.database

import androidx.room3.Room
import androidx.sqlite.SQLiteDriver
import androidx.sqlite.driver.web.WebWorkerSQLiteDriver
import kotlin.js.js
import org.w3c.dom.Worker

fun createTrackerDatabase(): TrackerDatabase {
    val driver = createSqlJsWorker()
    return Room.inMemoryDatabaseBuilder<TrackerDatabase>()
        .setDriver(driver)
        .fallbackToDestructiveMigration(dropAllTables = false)
        .build()
}

//fun createSqlJsWorker(): SQLiteDriver =
//    WebWorkerSQLiteDriver(
//        Worker(js("""new URL("sql-js-worker/worker.js", import.meta.url)"""))
//    )

/*

fun createSqlJsWorker() =
    WebWorkerSQLiteDriver(
        Worker(js("""new URL("sqlite-web-worker/worker.js", import.meta.url)"""))
    )*/



fun createSqlJsWorker() =
    WebWorkerSQLiteDriver(Worker(js("""new URL("web-main/worker.js", import.meta.url)""")))