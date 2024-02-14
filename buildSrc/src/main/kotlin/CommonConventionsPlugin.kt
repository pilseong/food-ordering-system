import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.repositories
import java.net.URI

class CommonConventionsPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        applyPlugin(project)

        applyRepositories(project)

        applyDependencies(project)
    }

    private fun applyPlugin(project: Project) {
        project.apply {
            plugin("org.jetbrains.kotlin.jvm")
        }
    }

    private fun applyRepositories(project: Project) {
        project.repositories {
            mavenCentral()
            gradlePluginPortal()
            maven {
                url = URI("https://packages.confluent.io/maven")
            }
        }
    }
}

private fun applyDependencies(project: Project) {
    project.dependencies {
        implementation(Dependencies.COMMON_TEXT)

        implementation(Dependencies.JACKSON_MODULE_KOTLIN)
        api(Dependencies.SPRING_BOOT_STARTER_LOGGING)
        runtimeOnly(Dependencies.KOTLIN_REFLECT)

        // Use JUnit Jupiter for testing.
        testImplementation(Dependencies.JUNIT_JUPITER)
        testImplementation(Dependencies.KOTLIN_TEST)
        testRuntimeOnly(Dependencies.JUNIT_PLATFORM_LAUNCHER)
        compileOnly(Dependencies.LOMBOK)
    }
}