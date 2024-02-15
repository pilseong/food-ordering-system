import gradle.kotlin.dsl.accessors._8eee71391307bc564dab028e1f37d739.java
import org.gradle.api.Project


val Project.java: org.gradle.api.plugins.JavaPluginExtension get() =
    (this as org.gradle.api.plugins.ExtensionAware).extensions.getByName("java") as org.gradle.api.plugins.JavaPluginExtension
