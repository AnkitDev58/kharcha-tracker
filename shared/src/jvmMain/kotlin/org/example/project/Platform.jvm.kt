package org.example.project

actual fun getPlatform(): Platform {
    return DesktopPlatform()
}

class DesktopPlatform : Platform {
    override val name: String = "Desktop "
}