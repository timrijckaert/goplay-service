import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    alias(libs.plugins.kotlin.jvm) apply false
}

allprojects {
    apply {
        from("${rootDir}/credentials.gradle")
    }

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
            addAuthenticationTokensToSystemEnv(this)
            useJUnitPlatform()
        }
    }

//  Why can it not find KotlinProjectExtension or kotlin { }
//  configure<KotlinProjectExtension> { explicitApi() }
}

fun addAuthenticationTokensToSystemEnv(test: Test) {
    (project.extra.get("addAuthenticationTokensToSystemEnv") as org.codehaus.groovy.runtime.MethodClosure)(test)
}


