object Versions {
    const val compileSdkVersion = 30
    const val minSdkVersion = 21
    const val targetSdkVersion = 30
    const val kotlinVersion = "1.4.10"
}

object Dependencies {
    const val kotlinXSerialization = "org.jetbrains.kotlinx:kotlinx-serialization-json:1.0.1"

    const val kotlinStdLib = "org.jetbrains.kotlin:kotlin-stdlib:${Versions.kotlinVersion}"

    const val awsBOM = "software.amazon.awssdk:bom:2.15.23"
    const val awsCognitoProvider = "software.amazon.awssdk:cognitoidentityprovider"

    const val okHttp3 = "com.squareup.okhttp3:okhttp:4.9.0"
    const val okHttpCurlInterceptor = "com.github.mrmike:ok2curl:0.6.0"

    private const val arrowVersion = "0.11.0"
    const val arrowCore = "io.arrow-kt:arrow-core:${arrowVersion}"
    const val arrowSyntax = "io.arrow-kt:arrow-syntax:${arrowVersion}"
    const val arrowMeta = "io.arrow-kt:arrow-meta:${arrowVersion}"

}

object Plugins {
    const val androidGradle = "com.android.tools.build:gradle:4.1.0"
    const val kotlinGradle = "org.jetbrains.kotlin:kotlin-gradle-plugin:${Versions.kotlinVersion}"
}

object Testing {
    private const val kotestVersion = "4.3.1"
    const val kotestRunner4 = "io.kotest:kotest-runner-junit4:${kotestVersion}"
    const val kotestApiJvm = "io.kotest:kotest-framework-api-jvm:${kotestVersion}"
    const val kotestCoreAssertions = "io.kotest:kotest-assertions-core:${kotestVersion}"
    const val kotestPropertyTesting = "io.kotest:kotest-property:${kotestVersion}"

    const val mockk = "io.mockk:mockk:v1.10.2"
}
