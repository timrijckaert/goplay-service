object Versions {
    const val kotlinVersion = "1.4.31"
}

object Dependencies {
    const val kotlinXSerialization = "org.jetbrains.kotlinx:kotlinx-serialization-json:1.1.0"

    const val kotlinStdLib = "org.jetbrains.kotlin:kotlin-stdlib:${Versions.kotlinVersion}"

    const val awsBOM = "software.amazon.awssdk:bom:2.16.23"
    const val awsCognitoProvider = "software.amazon.awssdk:cognitoidentityprovider"

    const val okHttp3 = "com.squareup.okhttp3:okhttp:4.9.1"

    private const val arrowVersion = "1.0.0-SNAPSHOT"
    const val arrowCore = "io.arrow-kt:arrow-core:${arrowVersion}"
    const val arrowSyntax = "io.arrow-kt:arrow-syntax:${arrowVersion}"
    const val arrowMeta = "io.arrow-kt:arrow-meta:${arrowVersion}"
    const val arrowFx = "io.arrow-kt:arrow-fx:${arrowVersion}"
    const val arrowFxCoroutines = "io.arrow-kt:arrow-fx-coroutines:${arrowVersion}"

    const val jsoup = "org.jsoup:jsoup:1.13.1"

    private const val coroutineVersion = "1.4.3"
    const val coroutinesCore = "org.jetbrains.kotlinx:kotlinx-coroutines-core:${coroutineVersion}"
}

object Plugins {
    const val kotlinGradle = "org.jetbrains.kotlin:kotlin-gradle-plugin:${Versions.kotlinVersion}"
    const val versions = "com.github.ben-manes:gradle-versions-plugin:0.38.0"
}

object Testing {
    private const val kotestVersion = "4.4.3"

    private const val kotestRunner = "io.kotest:kotest-runner-junit5:${kotestVersion}"
    private const val kotestAssertionsCore = "io.kotest:kotest-assertions-core:${kotestVersion}"
    private const val kotestAssertionsArrow = "io.kotest:kotest-assertions-arrow:${kotestVersion}"
    private const val kotestProperty = "io.kotest:kotest-property:${kotestVersion}"

    const val mockk = "io.mockk:mockk:v1.10.2"
}
