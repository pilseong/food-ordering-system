
apply<LibraryConventionsPlugin>()

dependencies {
    api(project(":common-controller"))
    api(project(":order-domain-application-service"))

    api(Dependencies.SPRING_BOOT_STARTER_WEB)
}

description = "order-controller"