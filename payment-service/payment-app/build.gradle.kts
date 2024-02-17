import org.springframework.boot.gradle.tasks.bundling.BootBuildImage

plugins {
    id("net.pilseong.food_ordering_system.kotlin-application-conventions")
    id("org.springframework.boot") version Versions.SPRING_BOOT
}


dependencies {
    implementation(project(":payment-domain-application-service"))
    implementation(project(":payment-domain-core"))
    implementation(project(":payment-dataaccess"))
    implementation(project(":payment-messaging"))

    implementation(Dependencies.SPRING_CLOUD_STARTER_CONFIG)
    implementation(Dependencies.SPRING_BOOT_STARTER)
}

application {
    // Define the main class for the application.
    mainClass.set("net.philipheur.food_ordering_system.payment_service.PaymentServiceApplicationKt")
}

tasks.named<BootBuildImage>("bootBuildImage") {
    imageName.set("${project.group}/payment-service:${project.version}")
}

description = "payment-app"