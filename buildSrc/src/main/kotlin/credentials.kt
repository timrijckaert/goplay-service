import org.gradle.api.Project
import java.io.File
import java.io.FileInputStream
import java.util.Properties
import kotlin.reflect.KProperty

fun Project.variable(name: String): Variable = Variable(this, name)

class Variable(private val project: Project, private val name: String) {
    private val localProperties by lazy {
        Properties().apply {
            project.rootProject.file("local.properties")
                .takeIf(File::exists)
                ?.let(::FileInputStream)
                ?.let(this::load)
        }
    }

    operator fun getValue(nothing: Nothing?, property: KProperty<*>): String =
        requireNotNull(
            project.properties[name]?.toString() ?:
            localProperties.getProperty(name, null) ?:
            System.getenv(name)
        ) { "Property $name as parameter, project property, local.properties or system variable" }
}
