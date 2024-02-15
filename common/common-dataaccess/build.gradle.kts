apply<LibraryConventionsPlugin>()

plugins {
    kotlin("plugin.jpa") version Versions.KOTLIN
}

dependencies {
    api(Dependencies.SPRING_BOOT_STARTER_DATA_JPA)
    api(Dependencies.POSTGRESQL)
}

description = "common-dataaccess"