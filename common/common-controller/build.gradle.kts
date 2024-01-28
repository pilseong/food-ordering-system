
plugins {
    id("net.pilseong.food_ordering_system.kotlin-library-conventions")
}

dependencies {
    api("org.springframework.boot:spring-boot-starter-web:3.2.2")
    api("org.springframework.boot:spring-boot-starter-validation:3.2.2")
}

description = "common-controller"