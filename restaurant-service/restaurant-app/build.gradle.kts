import Dependencies.SPRING_BOOT_STARTER

plugins {
    id("net.pilseong.food_ordering_system.kotlin-application-conventions")
    id("org.springframework.boot") version Versions.SPRING_BOOT
}


dependencies {
    api(project(":restaurant-domain-application-service"))
    api(project(":restaurant-domain-core"))
    api(project(":restaurant-dataaccess"))
    api(project(":restaurant-messaging"))

    api(SPRING_BOOT_STARTER)
}

application {
    // Define the main class for the application.
    mainClass.set("net.philipheur.food_ordering_system.restaurant_service.RestaurantServiceApplicationKt")
}

description = "restaurant-app"