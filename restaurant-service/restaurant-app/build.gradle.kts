
plugins {
    id("net.pilseong.food_ordering_system.kotlin-application-conventions")
}


dependencies {
    api(project(":restaurant-domain-application-service"))
    api(project(":restaurant-domain-core"))
    api(project(":restaurant-dataaccess"))
    api(project(":restaurant-messaging"))
    api("org.springframework.boot:spring-boot-starter:3.2.2")
}

application {
    // Define the main class for the application.
    mainClass.set("net.philipheur.food_ordering_system.restaurant_service.RestaurantServiceApplicationKt")
}

description = "restaurant-app"