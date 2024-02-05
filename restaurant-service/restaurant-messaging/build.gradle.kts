plugins {
    id("net.pilseong.food_ordering_system.kotlin-library-conventions")
}

dependencies {
    implementation(project(":common-utils"))
    api(project(":restaurant-domain-application-service"))
    api(project(":kafka-model"))
    api(project(":kafka-producer"))
    api(project(":kafka-consumer"))
}

description = "restaurant-messaging"