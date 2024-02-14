
apply<LibraryConventionsPlugin>()

dependencies {
    api(project(":common-controller"))
    api(project(":customer-domain-application-service"))

    api(Dependencies.SPRING_BOOT_STARTER_WEB)
}

description = "customer-controller"