import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.targets.js.dsl.KotlinJsTargetDsl
import org.jetbrains.kotlin.gradle.targets.js.ir.KotlinJsIrLink

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidMultiplatformLibrary)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.kotlinSerialization)
    alias(libs.plugins.ksp)
    alias(libs.plugins.room)
}

kotlin {
    androidLibrary {
        namespace = "org.example.project.shared"
        compileSdk = libs.versions.android.compileSdk.get().toInt()
        minSdk = libs.versions.android.minSdk.get().toInt()
    }

    listOf(
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "Shared"
            isStatic = true
        }
    }

    jvm()

    this.js {
        browser {
            webpackTask {
                mainOutputFileName = "webApp.js"
            }
            commonWebpackConfig {
                mode = org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpackConfig.Mode.DEVELOPMENT
            }
        }
        binaries.executable()
    }



    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        browser()
    }


    sourceSets {
        androidMain.dependencies {
            implementation(libs.compose.uiToolingPreview)
            implementation(libs.koin.android)
            implementation(libs.kotlinx.coroutines.android)
            implementation(libs.datastore.preferences)

            // Room
//            implementation(libs.room.runtime)
            implementation(libs.sqlite.bundled)
        }
        commonMain.dependencies {
            // Compose
            implementation(libs.compose.runtime)
            implementation(libs.compose.foundation)
            implementation(libs.compose.material3)
            implementation(libs.compose.ui)
            implementation(libs.compose.components.resources)
            implementation(libs.compose.uiToolingPreview)
            implementation(libs.compose.materialIconsExtended)

            // Lifecycle
            implementation(libs.androidx.lifecycle.viewmodelCompose)
            implementation(libs.androidx.lifecycle.runtimeCompose)

            // Navigation
            implementation(libs.navigation.compose)

            // Koin
            implementation(libs.koin.core)
            implementation(libs.koin.compose)
            implementation(libs.koin.composeViewModel)


            // DataStore
//            implementation(libs.datastore.preferences.core)
            implementation("androidx.datastore:datastore-preferences-core:1.3.0-alpha09")


            // KotlinX
            implementation(libs.kotlinx.datetime)
            implementation(libs.kotlinx.serialization.json)
            implementation(libs.kotlinx.coroutines.core)

            implementation(libs.room.runtime)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
         jsMain.dependencies {
            implementation(libs.wrappers.browser)
            implementation(libs.sqliteWeb)
            implementation(npm("sql.js", "1.13.0"))

        }

        webMain.dependencies {
            implementation(libs.sqliteWeb)
            implementation(npm("sql.js", "1.13.0"))

        }
        wasmJsMain.dependencies {
            implementation(libs.sqliteWeb)
            implementation(npm("sql.js", "1.13.0"))
         }
        iosMain.dependencies {

            implementation(libs.sqlite.bundled)
        }
        jvmMain.dependencies {
            implementation(libs.sqlite.bundled)
        }
    }

    compilerOptions {
        jvmToolchain(11)
    }
}

tasks.withType<KotlinJsIrLink>().configureEach {
    compilerOptions {
        sourceMap.set(false)
    }
}

room3 {
    schemaDirectory("$projectDir/schemas")
}
dependencies {
    androidRuntimeClasspath(libs.compose.uiTooling)
    // Room KSP for all targets
//    add("kspAndroid", libs.room.compiler)
//    add("kspIosArm64", libs.room.compiler)
//    add("kspIosSimulatorArm64", libs.room.compiler)
//    add("kspJvm", libs.room.compiler)
//    ksp("androidx.room3:room3-compiler:3.0.0")

    add("kspAndroid", libs.room.compiler)

    add("kspIosArm64", libs.room.compiler)
    add("kspIosSimulatorArm64", libs.room.compiler)

    add("kspJvm", libs.room.compiler)

    add("kspJs", libs.room.compiler)
    add("kspWasmJs", libs.room.compiler)
}
