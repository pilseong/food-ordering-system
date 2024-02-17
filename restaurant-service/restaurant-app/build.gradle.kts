
import Dependencies.SPRING_BOOT_STARTER
import Dependencies.SPRING_CLOUD_STARTER_CONFIG
import org.springframework.boot.gradle.tasks.bundling.BootBuildImage

plugins {
    id("net.pilseong.food_ordering_system.kotlin-application-conventions")
    id("org.springframework.boot") version Versions.SPRING_BOOT
}


dependencies {
    api(project(":restaurant-domain-application-service"))
    api(project(":restaurant-domain-core"))
    api(project(":restaurant-dataaccess"))
    api(project(":restaurant-messaging"))

    api(SPRING_CLOUD_STARTER_CONFIG)
    api(SPRING_BOOT_STARTER)
}

application {
    // Define the main class for the application.
    mainClass.set("net.philipheur.food_ordering_system.restaurant_service.RestaurantServiceApplicationKt")
}

tasks.named<BootBuildImage>("bootBuildImage") {
    imageName.set("${project.group}/restaurant-service:${project.version}")
}

description = "restaurant-app"