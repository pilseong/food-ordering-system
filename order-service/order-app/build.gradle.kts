plugins {
    id("net.pilseong.food_ordering_system.kotlin-application-conventions")
}


dependencies {
    api(project(":common-controller"))
    api(project(":order-controller"))
    api(project(":order-dataaccess"))
    api(project(":order-domain-application-service"))
    api(project(":order-domain-core"))
    api(project(":order-messaging"))
    api(project(":kafka-config-data"))
    api("org.springframework.boot:spring-boot-starter:3.2.2")
}

application {
    // Define the main class for the application.
    mainClass.set("net.philipheur.food_ordering_system.order_service.OrderServiceApplicationKt")
}

description = "order-app"