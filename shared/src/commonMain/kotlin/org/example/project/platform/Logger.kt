package org.example.project.platform


expect object Logger {

    fun d(tag: String, message: String)

    fun i(tag: String, message: String)

    fun w(tag: String, message: String)

    fun e(tag: String, message: String)

    fun v(tag: String, message: String)
}


fun Any.logD(message: String) =
    Logger.d(this::class.simpleName ?: "Unknown", message)

fun Any.logI(message: String) =
    Logger.i(this::class.simpleName ?: "Unknown", message)

fun Any.logW(message: String) =
    Logger.w(this::class.simpleName ?: "Unknown", message)

fun Any.logE(message: String) =
    Logger.e(this::class.simpleName ?: "Unknown", message)

fun Any.logV(message: String) =
    Logger.v(this::class.simpleName ?: "Unknown", message)