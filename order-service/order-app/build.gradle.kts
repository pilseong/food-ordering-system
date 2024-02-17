import org.springframework.boot.gradle.tasks.bundling.BootBuildImage

plugins {
    id("net.pilseong.food_ordering_system.kotlin-application-conventions")
    id("org.springframework.boot") version Versions.SPRING_BOOT
}
dependencies {
    api(project(":common-controller"))
    api(project(":common-dataaccess"))
    api(project(":order-controller"))
    api(project(":order-dataaccess"))
    api(project(":order-domain-application-service"))
    api(project(":order-domain-core"))
    api(project(":order-messaging"))
    api(project(":kafka-config-data"))

    api(Dependencies.SPRING_CLOUD_STARTER_CONFIG)
    api(Dependencies.SPRING_BOOT_STARTER_TEST)
    api(Dependencies.SPRING_BOOT_STARTER)
}


application {
    // Define the main class for the application.
    mainClass.set("net.philipheur.food_ordering_system.order_service.OrderServiceApplicationKt")
}

tasks.named<BootBuildImage>("bootBuildImage") {
    imageName.set("${project.group}/order-service:${project.version}")
}

description = "order-app"