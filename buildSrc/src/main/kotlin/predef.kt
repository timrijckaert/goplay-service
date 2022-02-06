import org.gradle.api.provider.Provider;
import org.gradle.plugin.use.PluginDependency

@Suppress("UnstableApiUsage")
val Provider<PluginDependency>.pluginId: String
    get() = get().pluginId
