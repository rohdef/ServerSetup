plugins {
    val kotlinVersion = "1.7.21"
    kotlin("multiplatform") version kotlinVersion
    kotlin("plugin.serialization") version kotlinVersion

    id("io.kotest.multiplatform") version "5.5.4"
}

group = "dk.rohdef.gourmet.recipe-runner"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

kotlin {
    val hostOs = System.getProperty("os.name")
    val isMingwX64 = hostOs.startsWith("Windows")
    val nativeTarget = when {
        hostOs == "Mac OS X" -> macosX64("native")
        hostOs == "Linux" -> linuxX64("native")
        isMingwX64 -> mingwX64("native")
        else -> throw GradleException("Host OS is not supported in Kotlin/Native.")
    }

    nativeTarget.apply {
        binaries {
            executable {
                entryPoint = "main"
            }
        }
    }

    val kotestVersion = "5.5.4"
    val kotlinLoggingVersion = "3.0.4"
    val arrowKtVersion = "1.1.3"
    val arrowKtVersionKotest = "1.3.0"
    sourceSets {
        commonMain {
            dependencies {
                implementation(project(":rfpath"))

                implementation(project(":gourmet-api"))

                implementation(project(":debug"))
                implementation(project(":reboot"))
                implementation(project(":remote-install"))
                implementation(project(":run-recipe"))
                implementation(project(":update-environment"))

                implementation("io.arrow-kt:arrow-core:$arrowKtVersion")
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")

                implementation("io.github.microutils:kotlin-logging:$kotlinLoggingVersion")
                implementation("io.github.microutils:kotlin-logging-linuxx64:$kotlinLoggingVersion")

                implementation("org.jetbrains.kotlinx:kotlinx-cli:0.3.5")
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.4.0-RC")

                implementation("io.insert-koin:koin-core:3.2.2")
            }
        }
        val commonTest by getting

        val nativeMain by getting
        val nativeTest by getting {
            dependencies {
                implementation(kotlin("test"))

                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.6.3-native-mt")
                implementation("io.kotest:kotest-assertions-core:$kotestVersion")
                implementation("io.kotest:kotest-framework-datatest:$kotestVersion")
                implementation("io.kotest:kotest-framework-engine:$kotestVersion")
                implementation("io.kotest.extensions:kotest-assertions-arrow:$arrowKtVersionKotest")
            }
        }
    }
}