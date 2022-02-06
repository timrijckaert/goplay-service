plugins {
    kotlin("jvm")
    id("org.jetbrains.kotlin.plugin.serialization") version "1.6.10"
}

dependencies {
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.kotlin.stdlib)
    implementation(libs.coroutines.core)

    implementation(platform(libs.aws.bom))
    implementation(libs.aws.cognitoidentityprovider)

    implementation(libs.okhttp3)

    implementation(platform(libs.arrow.bom))
    implementation(libs.arrow.core)
    implementation(libs.arrow.fx.coroutines)
    implementation(libs.jsoup)

    testImplementation(libs.kotest.runner)
    testImplementation(libs.kotest.assertions.core)
    testImplementation(libs.kotest.assertions.arrow)
    testImplementation(libs.kotest.property)
}
