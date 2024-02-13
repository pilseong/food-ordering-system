
plugins {
    id("net.pilseong.food_ordering_system.kotlin-application-conventions")
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

    api("org.springframework.boot:spring-boot-starter:3.2.2")
}

application {
    // Define the main class for the application.
    mainClass.set("net.philipheur.food_ordering_system.customer_service.CustomerServiceApplicationKt")
}

description = "customer-app"