import Dependencies.POSTGRESQL
import Dependencies.SPRING_BOOT_STARTER_DATA_JPA
import Dependencies.SPRING_TX

apply<LibraryConventionsPlugin>()

plugins {
    kotlin("plugin.jpa") version Versions.KOTLIN
}

dependencies {
    api(project(":common-dataaccess"))
    api(project(":restaurant-domain-application-service"))

    api(SPRING_TX)
    api(SPRING_BOOT_STARTER_DATA_JPA)
    api(POSTGRESQL)
}

description = "restaurant-dataaccess"