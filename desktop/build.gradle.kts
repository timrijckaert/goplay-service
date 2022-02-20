import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    id(libs.plugins.kotlin.jvm.pluginId)
    alias(libs.plugins.kotlinx.serialization)
    id("org.jetbrains.compose") version "1.0.1"
}

repositories {
    mavenCentral()
    google()
}

dependencies {
    // Note, if you develop a library, you should use compose.desktop.common.
    // compose.desktop.currentOs should be used in launcher-sourceSet
    // (in a separate module for demo project and in testMain).
    // With compose.desktop.common you will also lose @Preview functionality
    implementation(compose.desktop.currentOs)
    implementation(projects.goplay)
    implementation(platform(libs.arrow.bom))
    implementation(libs.arrow.core)
    implementation(libs.arrow.fx.coroutines)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.kotlin.stdlib)
    implementation(libs.coroutines.core)
}

compose.desktop {
    application {
        mainClass = "be.tapped.goplay.desktop.MainKt"

        nativeDistributions {
//            appResourcesRootDir.set(project.layout.projectDirectory.dir("xxx"))
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "GoPlayDesktop"
            packageVersion = "1.0.0"
        }
    }
}
