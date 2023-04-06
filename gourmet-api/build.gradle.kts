import dk.rohdef.gourmet.convention.configureCommon
import dk.rohdef.gourmet.convention.kotest

plugins {
    kotlin("multiplatform")
    kotlin("plugin.serialization")
}

group = "dk.rohdef.gourmet-api"
description = ""

configureCommon()
kotest()
kotlin {
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")

                implementation("org.jetbrains.kotlinx:kotlinx-cli:0.3.5")
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.4.0-RC")
            }
        }
        val commonTest by getting {
            dependencies {
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.6.3-native-mt")
            }
        }

        val nativeMain by getting {
            dependencies {
                implementation("dk.rohdef.rfpath:rfpath-core:1.0-SNAPSHOT")
            }
        }
        val nativeTest by getting
    }
}