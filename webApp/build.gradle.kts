import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
}

kotlin {
    this.js {
        browser {
            webpackTask {
                mainOutputFileName = "app.js"
            }
        }
        binaries.executable()
    }

    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        browser()
        binaries.executable()
    }

    sourceSets {

        commonMain.dependencies {
            implementation(projects.shared)
            implementation(libs.compose.ui)
            implementation(libs.koin.core)
            implementation(libs.koin.compose)
            implementation(libs.sqliteWeb)
            implementation(npm("sql.js", "1.13.0"))
            implementation(
                npm("web-main", layout.projectDirectory.dir("worker").asFile)
            )

        }

    }
}