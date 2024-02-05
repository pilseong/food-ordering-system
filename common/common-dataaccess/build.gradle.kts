plugins {
    id("net.pilseong.food_ordering_system.kotlin-library-conventions")
    kotlin("plugin.jpa") version "1.9.22"
}

dependencies {
    api("org.springframework.boot:spring-boot-starter-data-jpa:3.2.2")
    api("org.postgresql:postgresql:42.6.0")
}

description = "common-dataaccess"