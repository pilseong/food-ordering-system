
plugins {
    id("net.pilseong.food_ordering_system.kotlin-library-conventions")
}

dependencies {
    api(project(":common-utils"))
    api(project(":common-domain"))
}

description = "order-domain-core"