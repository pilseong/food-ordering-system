
plugins {
    id("net.pilseong.food_ordering_system.kotlin-library-conventions")
}

dependencies {
    api(project(":order-domain-application-service"))
    api(project(":kafka-model"))
    api(project(":kafka-producer"))
}

description = "order-messaging"