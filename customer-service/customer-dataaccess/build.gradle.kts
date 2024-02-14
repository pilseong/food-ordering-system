import Dependencies.POSTGRESQL
import Dependencies.SPRING_BOOT_STARTER_DATA_JPA
import Dependencies.SPRING_TX

apply<LibraryConventionsPlugin>()

plugins {
    kotlin("plugin.jpa") version Versions.KOTLIN
}

dependencies {
    api(project(":saga"))
    api(project(":outbox"))
    api(project(":customer-domain-application-service"))
    api(project(":common-dataaccess"))

    api(SPRING_TX)
    api(SPRING_BOOT_STARTER_DATA_JPA)
    api(POSTGRESQL)
}

description = "customer-dataaccess"