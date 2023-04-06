import dk.rohdef.gourmet.convention.configureCommon
import dk.rohdef.gourmet.convention.kotest
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTargetWithHostTests

plugins {
    kotlin("multiplatform")
    kotlin("plugin.serialization")
}

group = "dk.rohdef.gourmet.recipe-runner"

kotlin.targets.withType<org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget> {
    binaries.all {
        freeCompilerArgs += "-Xdisable-phases=EscapeAnalysis"
    }
}

configureCommon {
    binaries {
        executable {
            entryPoint = "dk.rohdef.gourmet.main"
        }
    }
}
kotest()
kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation("dk.rohdef.rfpath:rfpath-core:1.0-SNAPSHOT")

                implementation(project(":gourmet-api"))

                implementation(project(":debug"))
                implementation(project(":reboot"))
                implementation(project(":remote-install"))
                implementation(project(":run-recipe"))
                implementation(project(":update-environment"))

                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")

                implementation("org.jetbrains.kotlinx:kotlinx-cli:0.3.5")
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.4.0-RC")

                implementation("io.insert-koin:koin-core:3.2.2")
            }
        }
        val commonTest by getting

        val nativeMain by getting
        val nativeTest by getting {
            dependencies {
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.6.3-native-mt")
            }
        }
    }
}