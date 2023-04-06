package dk.rohdef.gourmet.convention

import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.getValue
import org.gradle.kotlin.dsl.getting
import org.gradle.kotlin.dsl.invoke
import org.jetbrains.kotlin.gradle.plugin.KotlinDependencyHandler

fun Project.kotest() {
    apply(plugin = "io.kotest.multiplatform")

    kotlin {
        sourceSets {
            val commonTest by getting {
                dependencies {
                    kotest()
                }
            }
        }
    }
}

fun KotlinDependencyHandler.kotest() {
    val kotestVersion = "5.5.5"
    val arrowKtVersionKotest = "1.3.0"

    implementation("io.kotest:kotest-assertions-core:$kotestVersion")
    implementation("io.kotest:kotest-framework-datatest:$kotestVersion")
    implementation("io.kotest:kotest-framework-engine:$kotestVersion")
    implementation("io.kotest.extensions:kotest-assertions-arrow:$arrowKtVersionKotest")
}