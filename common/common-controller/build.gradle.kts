
apply<LibraryConventionsPlugin>()

dependencies {
    api(project(":common-utils"))

    api(Dependencies.SPRING_BOOT_STARTER_WEB)
    api(Dependencies.SPRING_BOOT_STARTER_VALIDATION)
}

description = "common-controller"