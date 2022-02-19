@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    kotlin("multiplatform")
    alias(libs.plugins.kotlinx.serialization)
    id("io.kotest.multiplatform") version "5.1.0"
    kotlin("native.cocoapods")
}

version = "1.0"

kotlin {
    targets {
        ios()
        jvm()
    }

    cocoapods {
        ios.deploymentTarget = "13.5"

        summary = "CocoaPods test library"
        homepage = "https://github.com/JetBrains/kotlin"

        // pod("AFNetworking") {
        //     version = "~> 4.0.1"
        // }
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(libs.kotlinx.serialization.json)
                implementation(libs.kotlin.stdlib)
                implementation(libs.coroutines.core)

                implementation(project.dependencies.platform(libs.arrow.bom))
                implementation(libs.arrow.core)
                implementation(libs.arrow.fx.coroutines)
                implementation(libs.ktor.client.core)
                implementation(libs.ktor.client.logging)
                implementation(libs.ktor.client.serialization)
                implementation(libs.kotlinx.datetime)

                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.5.2-native-mt") {
                    version {
                        strictly("1.5.2-native-mt")
                    }
                }
            }
        }
        val commonTest by getting {
            dependencies {

                implementation("io.kotest:kotest-assertions-core:5.1.0")
                implementation("io.kotest:kotest-framework-engine:5.1.0")
                implementation("io.kotest:kotest-framework-datatest:5.1.0")
                implementation("org.jetbrains.kotlin:kotlin-test-common:1.6.0")
                implementation("org.jetbrains.kotlin:kotlin-test-annotations-common:1.6.0")
                // implementation(libs.kotest.assertions.arrow)

                // implementation(libs.kotest.assertions.core)
                // implementation(libs.kotest.assertions.arrow)
                // implementation(libs.kotest.property)
                // implementation(libs.kotest.framework.engine)
            }
        }
        val jvmMain by getting {
            dependencies {
                implementation(project.dependencies.platform(libs.aws.bom))
                implementation(libs.aws.cognitoidentityprovider)
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
