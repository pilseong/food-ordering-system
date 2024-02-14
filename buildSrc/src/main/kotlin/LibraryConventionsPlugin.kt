
import org.gradle.api.Plugin
import org.gradle.api.Project

class LibraryConventionsPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        target.apply {
            plugin(CommonConventionsPlugin::class.java)
            plugin("java-library")
        }
    }
}