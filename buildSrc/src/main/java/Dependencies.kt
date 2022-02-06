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
    const val arrowBom: String = "io.arrow-kt:arrow-stack:$arrowVersion"
    const val arrowCore: String = "io.arrow-kt:arrow-core"
    const val arrowSyntax: String = "io.arrow-kt:arrow-syntax"
    const val arrowMeta: String = "io.arrow-kt:arrow-meta"
    const val arrowFx: String = "io.arrow-kt:arrow-fx"
    const val arrowFxCoroutines: String = "io.arrow-kt:arrow-fx-coroutines"

    // TODO Remove 🔫
    const val jsoup: String = "org.jsoup:jsoup:1.13.1"

    private const val coroutineVersion = "1.6.0"
    const val coroutinesCore: String = "org.jetbrains.kotlinx:kotlinx-coroutines-core:${coroutineVersion}"
}

object Plugins {
    const val kotlinGradle: String = "org.jetbrains.kotlin:kotlin-gradle-plugin:${Versions.kotlinVersion}"
}

object Testing {
    private const val kotestVersion = "5.1.0"

    const val kotestRunner: String = "io.kotest:kotest-runner-junit5:${kotestVersion}"
    const val kotestAssertionsCore: String = "io.kotest:kotest-assertions-core:${kotestVersion}"
    const val kotestAssertionsArrow: String = "io.kotest.extensions:kotest-assertions-arrow:1.2.2"
    const val kotestProperty: String = "io.kotest:kotest-property:${kotestVersion}"

    const val mockk: String = "io.mockk:mockk:v1.10.2"
}
