
plugins {
    id("net.pilseong.food_ordering_system.kotlin-application-conventions")
}

dependencies {
    api("org.springframework.boot:spring-boot-starter:3.2.2")
}

application {
    // Define the main class for the application.
    mainClass.set("net.philipheur.food_ordering_system.customer_service.CustomerServiceApplicationKt")
}

description = "customer-service"