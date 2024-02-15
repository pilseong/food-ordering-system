import org.springframework.boot.gradle.tasks.bundling.BootBuildImage

plugins {
    id("net.pilseong.food_ordering_system.kotlin-application-conventions")
    id("org.springframework.boot") version Versions.SPRING_BOOT
}

dependencies {
    api(project(":common-controller"))
    api(project(":common-dataaccess"))
    api(project(":customer-controller"))
    api(project(":customer-dataaccess"))
    api(project(":customer-domain-application-service"))
    api(project(":customer-domain-core"))
    api(project(":customer-messaging"))
    api(project(":kafka-config-data"))

    api(Dependencies.SPRING_BOOT_STARTER)
}

application {
    // Define the main class for the application.
    mainClass.set("net.philipheur.food_ordering_system.customer_service.CustomerServiceApplicationKt")
}

tasks.named<BootBuildImage>("bootBuildImage") {
    imageName.set("${project.group}/customer-service:${project.version}")
}

description = "customer-app"