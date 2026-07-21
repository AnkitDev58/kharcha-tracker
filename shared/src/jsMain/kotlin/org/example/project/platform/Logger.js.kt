package org.example.project.platform


actual object Logger {
    actual fun d(tag: String, message: String) {
       console.log("[$tag] $message")
    }

    actual fun i(tag: String, message: String) {
       console.info("[$tag] $message")
    }

    actual fun w(tag: String, message: String) {
       console.warn("[$tag] $message")
    }

    actual fun e(tag: String, message: String) {
       console.error("[$tag] $message")
    }

    actual fun v(tag: String, message: String) {
       console.log("[$tag] $message")
    }
}