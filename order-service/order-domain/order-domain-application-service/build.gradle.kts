
apply<LibraryConventionsPlugin>()

dependencies {
    api(project(":common-utils"))
    api(project(":saga"))
    api(project(":outbox"))
    api(project(":order-domain-core"))

    api(Dependencies.SPRING_BOOT_STARTER_VALIDATION)
    api(Dependencies.SPRING_TX)
    implementation(Dependencies.SPRING_BOOT_STARTER_JSON)
    testImplementation(Dependencies.MOKITO_KOTLIN)
    testImplementation(Dependencies.SPRING_BOOT_STARTER_TEST)

}
description = "order-domain-application-service"