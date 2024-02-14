
import org.gradle.api.Action
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaApplication

class ApplicationConventionsPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        target.apply {
            plugin(CommonConventionsPlugin::class.java)
            plugin("application")
        }
        target.description

        if (target.name == "order-app") applyWith(
            target
        )
    }

    private fun applyWith(target:Project) {
//        println(target.property("mainClassName"))
        target.application {
            mainClass.set(target.property("mainClassName") as String)
        }
    }

    fun Project.application(configure: Action<JavaApplication>) {
        application {
            mainClass.set(configure.toString())
        }
    }
}