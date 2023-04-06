import dk.rohdef.gourmet.convention.configureCommon
import dk.rohdef.gourmet.convention.kotest

plugins {
    kotlin("multiplatform")
    kotlin("plugin.serialization")
}

group = "dk.rohdef.gourmet.plugins.debug"
description = ""

configureCommon()
kotest()
kotlin {
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(project(":gourmet-api"))

                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")
            }
        }
        val commonTest by getting {
            dependencies {
                kotest()

                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.6.3-native-mt")
            }
        }
    }
}