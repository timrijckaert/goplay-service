@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    kotlin("multiplatform")
    alias(libs.plugins.kotlinx.serialization)
}

kotlin {
    targets {
        ios()
        jvm()
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(libs.kotlinx.serialization.json)
                implementation(libs.kotlin.stdlib)
                implementation(libs.coroutines.core)

                implementation(project.dependencies.platform(libs.aws.bom))
                implementation(libs.aws.cognitoidentityprovider)

                implementation(project.dependencies.platform(libs.arrow.bom))
                implementation(libs.arrow.core)
                implementation(libs.arrow.fx.coroutines)
                implementation(libs.ktor.client.core)
                implementation(libs.ktor.client.logging)
                implementation(libs.ktor.client.serialization)
                implementation(libs.kotlinx.datetime)
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(libs.kotest.runner)
                implementation(libs.kotest.assertions.core)
                implementation(libs.kotest.assertions.arrow)
                implementation(libs.kotest.property)
            }
        }
        val jvmMain by getting {
            dependencies {
                implementation(libs.ktor.client.apache)
            }
        }
        val iosMain by getting {
            dependencies {
                implementation(libs.ktor.client.ios)
            }
        }
    }
}

tasks {
    withType<Test> {
        useJUnitPlatform()
    }
}
