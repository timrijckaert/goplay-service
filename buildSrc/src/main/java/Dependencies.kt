object Versions {
    const val kotlinVersion: String = "1.6.10"
}

object Dependencies {
    const val kotlinXSerialization: String = "org.jetbrains.kotlinx:kotlinx-serialization-json:1.1.0"

    const val kotlinStdLib: String = "org.jetbrains.kotlin:kotlin-stdlib:${Versions.kotlinVersion}"

    const val awsBOM: String = "software.amazon.awssdk:bom:2.16.23"
    const val awsCognitoProvider: String = "software.amazon.awssdk:cognitoidentityprovider"

    // TODO Replace 🔫 with Ktor to make MPP compatible
    const val okHttp3: String = "com.squareup.okhttp3:okhttp:4.9.1"

    // TODO Use BOM
    private const val arrowVersion = "1.0.1"
    const val arrowCore: String = "io.arrow-kt:arrow-core:${arrowVersion}"
    const val arrowSyntax: String = "io.arrow-kt:arrow-syntax:${arrowVersion}"
    const val arrowMeta: String = "io.arrow-kt:arrow-meta:${arrowVersion}"
    const val arrowFx: String = "io.arrow-kt:arrow-fx:${arrowVersion}"
    const val arrowFxCoroutines: String = "io.arrow-kt:arrow-fx-coroutines:${arrowVersion}"

    // TODO Remove 🔫
    const val jsoup: String = "org.jsoup:jsoup:1.13.1"

    private const val coroutineVersion = "1.6.0"
    const val coroutinesCore: String = "org.jetbrains.kotlinx:kotlinx-coroutines-core:${coroutineVersion}"
}

object Plugins {
    const val kotlinGradle: String = "org.jetbrains.kotlin:kotlin-gradle-plugin:${Versions.kotlinVersion}"
    const val versions: String = "com.github.ben-manes:gradle-versions-plugin:0.38.0"
}

object Testing {
    private const val kotestVersion = "5.1.0"

    private const val kotestRunner = "io.kotest:kotest-runner-junit5:${kotestVersion}"
    private const val kotestAssertionsCore = "io.kotest:kotest-assertions-core:${kotestVersion}"
    private const val kotestAssertionsArrow = "io.kotest.extensions:kotest-assertions-arrow:1.2.2"
    private const val kotestProperty = "io.kotest:kotest-property:${kotestVersion}"

    const val mockk: String = "io.mockk:mockk:v1.10.2"
}
