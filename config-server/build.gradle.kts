
plugins {
    id("net.pilseong.food_ordering_system.kotlin-application-conventions")
    id("org.springframework.boot") version Versions.SPRING_BOOT
}


dependencies {
    api(Dependencies.SPRING_AUTOCONFIGURE)
    api(Dependencies.SPRING_CLOUD_CONFIG_SERVER)
    api(Dependencies.SPRING_BOOT_STARTER_SECURITY)
    api(project(":common-utils"))
}

description = "config-server"