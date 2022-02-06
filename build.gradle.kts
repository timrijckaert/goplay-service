import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    alias(libs.plugins.kotlin.jvm) apply false
}

allprojects {
    repositories {
        mavenCentral()
        google()
    }

    tasks {
        withType<KotlinCompile>().configureEach {
            kotlinOptions {
                jvmTarget = JavaVersion.VERSION_11.toString()
                freeCompilerArgs = freeCompilerArgs +
                        "-Xexplicit-api=strict" +
                        "-opt-in=kotlinx.serialization.ExperimentalSerializationApi"
            }
            sourceCompatibility = JavaVersion.VERSION_11.toString()
            targetCompatibility = JavaVersion.VERSION_11.toString()
        }

        withType<Test>().configureEach {
            useJUnitPlatform()
        }
    }

//  Why can it not find KotlinProjectExtension or kotlin { }
//  configure<KotlinProjectExtension> { explicitApi() }
}
