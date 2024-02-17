import org.springframework.boot.gradle.tasks.bundling.BootBuildImage

plugins {
    id("net.pilseong.food_ordering_system.kotlin-application-conventions")
    id("org.springframework.boot") version Versions.SPRING_BOOT
}

dependencies {
    implementation(project(":common-controller"))
    implementation(project(":common-dataaccess"))
    implementation(project(":customer-controller"))
    implementation(project(":customer-dataaccess"))
    implementation(project(":customer-domain-application-service"))
    implementation(project(":customer-domain-core"))
    implementation(project(":customer-messaging"))
    implementation(project(":kafka-config-data"))

    implementation(Dependencies.SPRING_CLOUD_STARTER_CONFIG)
    implementation(Dependencies.SPRING_BOOT_STARTER)
}

application {
    // Define the main class for the application.
    mainClass.set("net.philipheur.food_ordering_system.customer_service.CustomerServiceApplicationKt")
}

tasks.named<BootBuildImage>("bootBuildImage") {
    imageName.set("${project.group}/customer-service:${project.version}")
}

description = "customer-app"