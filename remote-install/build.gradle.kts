import dk.rohdef.gourmet.convention.configureCommon
import dk.rohdef.gourmet.convention.kotest

plugins {
    kotlin("multiplatform")
    kotlin("plugin.serialization")
}

group = "dk.rohdef.gourmet.plugins.remote-install"
description = ""

configureCommon()
kotest()
kotlin {
    val kotestVersion = "5.5.4"
    val kotlinLoggingVersion = "3.0.4"
    val arrowKtVersion = "1.1.3"
    val arrowKtVersionKotest = "1.3.0"
    sourceSets {
        val nativeMain by getting {
            dependencies {
                implementation(project(":gourmet-api"))

                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")
            }
        }
        val nativeTest by getting {
            dependencies {
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.6.3-native-mt")
            }
        }
    }
}