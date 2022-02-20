import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

@Suppress("DSL_SCOPE_VIOLATION")
plugins {
  id(libs.plugins.kotlin.jvm.pluginId)
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

      listOf("goplay").forEach { brand ->
        val username by variable("${brand}.username")
        val password by variable("${brand}.password")

        environment("${brand}.username", username)
        environment("${brand}.password", password)
      }
    }
  }
}
