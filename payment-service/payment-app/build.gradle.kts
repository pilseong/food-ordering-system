plugins {
    id("net.pilseong.food_ordering_system.kotlin-application-conventions")
}


dependencies {
    api(project(":payment-domain-application-service"))
    api(project(":payment-domain-core"))
    api(project(":payment-dataaccess"))
    api(project(":payment-messaging"))
    api("org.springframework.boot:spring-boot-starter:3.2.2")
}

application {
    // Define the main class for the application.
    mainClass.set("net.philipheur.food_ordering_system.payment_service.PaymentServiceApplicationKt")
}

description = "payment-app"