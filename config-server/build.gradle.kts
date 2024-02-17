
plugins {
    id("net.pilseong.food_ordering_system.kotlin-application-conventions")
    id("org.springframework.boot") version Versions.SPRING_BOOT
}


dependencies {
    implementation(Dependencies.SPRING_AUTOCONFIGURE)
    implementation(Dependencies.SPRING_CLOUD_CONFIG_SERVER)
    implementation(Dependencies.SPRING_BOOT_STARTER_SECURITY)
    implementation(project(":common-utils"))
}

description = "config-server"