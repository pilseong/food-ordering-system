apply<LibraryConventionsPlugin>()

plugins {
    kotlin("plugin.jpa") version Versions.KOTLIN
}

dependencies {
    implementation(Dependencies.SPRING_BOOT_STARTER_DATA_JPA)
    implementation(Dependencies.POSTGRESQL)
}

description = "common-dataaccess"