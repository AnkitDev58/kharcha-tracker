package org.example.project

actual fun getPlatform(): Platform {
    return WebPlatform()
}

class WebPlatform() : Platform {
    override val name: String
        get() = "web"

}