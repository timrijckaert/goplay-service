import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    id(libs.plugins.kotlin.jvm.pluginId) apply false
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

        val username by variable("username")
        val password by variable("password")

        withType<Test>().configureEach {
            useJUnitPlatform()

            environment("goplay.username", username)
            environment("goplay.password", password)
        }
    }
}
