package org.example.project.platform


actual object Logger {

    actual fun d(tag: String, message: String) {
        println("🐞 [$tag] $message")
    }

    actual fun i(tag: String, message: String) {
        println("ℹ️ [$tag] $message")
    }

    actual fun w(tag: String, message: String) {
        println("⚠️ [$tag] $message")
    }

    actual fun e(tag: String, message: String) {
        println("❌ [$tag] $message")
    }

    actual fun v(tag: String, message: String) {
        println("👀 [$tag] $message")
    }
}