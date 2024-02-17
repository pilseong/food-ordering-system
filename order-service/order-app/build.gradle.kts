import org.springframework.boot.gradle.tasks.bundling.BootBuildImage

plugins {
    id("net.pilseong.food_ordering_system.kotlin-application-conventions")
    id("org.springframework.boot") version Versions.SPRING_BOOT
}
dependencies {
    implementation(project(":common-controller"))
    implementation(project(":common-dataaccess"))
    implementation(project(":order-controller"))
    implementation(project(":order-dataaccess"))
    implementation(project(":order-domain-application-service"))
    implementation(project(":order-domain-core"))
    implementation(project(":order-messaging"))
    implementation(project(":kafka-config-data"))

    implementation(Dependencies.SPRING_CLOUD_STARTER_CONFIG)
    implementation(Dependencies.SPRING_BOOT_STARTER_TEST)
    implementation(Dependencies.SPRING_BOOT_STARTER)
}


application {
    // Define the main class for the application.
    mainClass.set("net.philipheur.food_ordering_system.order_service.OrderServiceApplicationKt")
}

tasks.named<BootBuildImage>("bootBuildImage") {
    imageName.set("${project.group}/order-service:${project.version}")
}

description = "order-app"