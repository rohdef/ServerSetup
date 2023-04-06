package dk.rohdef.gourmet.convention

import org.gradle.api.GradleException
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.getValue
import org.gradle.kotlin.dsl.getting
import org.gradle.kotlin.dsl.invoke
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTargetWithHostTests

fun Project.configureCommon(configureTarget: KotlinNativeTargetWithHostTests.()->Unit = {}) {
    nativeTarget(configureTarget)

    val kotlinLoggingVersion = "3.0.4"
    val arrowKtVersion = "1.1.3"

    kotlin {
        sourceSets {
            val commonMain by getting {
                dependencies {
                    implementation("io.arrow-kt:arrow-core:$arrowKtVersion")

                    implementation("io.github.microutils:kotlin-logging:$kotlinLoggingVersion")
                    implementation("io.github.microutils:kotlin-logging-linuxx64:$kotlinLoggingVersion")
                }
            }

            val commonTest by getting {
                dependencies {
                    implementation(kotlin("test"))
                }
            }
        }
    }
}

fun Project.nativeTarget(configureTarget: KotlinNativeTargetWithHostTests.()->Unit = {}) {
    apply(plugin = "kotlin-multiplatform")

    kotlin {
        val hostOs = System.getProperty("os.name")
        val isMingwX64 = hostOs.startsWith("Windows")
        val nativeTarget = when {
            hostOs == "Mac OS X" -> macosX64("native")
            hostOs == "Linux" -> linuxX64("native")
            isMingwX64 -> mingwX64("native")
            else -> throw GradleException("Host OS is not supported in Kotlin/Native.")
        }

        nativeTarget.apply(configureTarget)
    }
}