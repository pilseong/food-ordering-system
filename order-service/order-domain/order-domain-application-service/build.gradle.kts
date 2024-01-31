
plugins {
    id("net.pilseong.food_ordering_system.kotlin-library-conventions")
}

dependencies {
    implementation(project(":common-utils"))
    api(project(":order-domain-core"))
    api("org.springframework.boot:spring-boot-starter-validation:3.2.2")
    api("org.springframework:spring-tx:6.1.2")
    testImplementation("org.mockito.kotlin:mockito-kotlin:5.0.0")
    testImplementation("org.springframework.boot:spring-boot-starter-test:3.2.2")

}
description = "order-domain-application-service"