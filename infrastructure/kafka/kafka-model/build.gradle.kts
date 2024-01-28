import com.github.davidmc24.gradle.plugin.avro.GenerateAvroJavaTask
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile


plugins {
    id("net.pilseong.food_ordering_system.kotlin-library-conventions")
    id("com.github.davidmc24.gradle.plugin.avro") version "1.9.1"
}

dependencies {
    implementation("org.apache.avro:avro:1.11.3")
}

tasks.generateAvroJava {
    source("src/main/resources/avro")
    setOutputDir(file("src/main/java"))
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "17"
    }
}

description = "kafka-model"