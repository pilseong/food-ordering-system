
plugins {
    id("net.pilseong.food_ordering_system.kotlin-library-conventions")
}

dependencies {
    api(project(":common-controller"))
    api(project(":customer-domain-application-service"))
    api("org.springframework.boot:spring-boot-starter-web:3.2.2")
}

description = "customer-controller"