package org.example.project.platform

import kotlin.js.JsString
import kotlin.js.toJsString

@OptIn(ExperimentalWasmJsInterop::class)
@JsFun("msg => console.log(msg)")
private external fun consoleLog(msg: JsString)

@OptIn(ExperimentalWasmJsInterop::class)
@JsFun("msg => console.info(msg)")
private external fun consoleInfo(msg: JsString)

@OptIn(ExperimentalWasmJsInterop::class)
@JsFun("msg => console.warn(msg)")
private external fun consoleWarn(msg: JsString)

@OptIn(ExperimentalWasmJsInterop::class)
@JsFun("msg => console.error(msg)")
private external fun consoleError(msg: JsString)

@OptIn(ExperimentalWasmJsInterop::class)
actual object Logger {

    actual fun d(tag: String, message: String) {
        consoleLog("[$tag] $message".toJsString())
    }

    actual fun i(tag: String, message: String) {
        consoleInfo("[$tag] $message".toJsString())
    }

    actual fun w(tag: String, message: String) {
        consoleWarn("[$tag] $message".toJsString())
    }

    actual fun e(tag: String, message: String) {
        consoleError("[$tag] $message".toJsString())
    }

    actual fun v(tag: String, message: String) {
        consoleLog("[$tag] $message".toJsString())
    }
}