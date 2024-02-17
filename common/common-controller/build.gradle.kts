
apply<LibraryConventionsPlugin>()

dependencies {
    implementation(project(":common-utils"))

    implementation(Dependencies.SPRING_BOOT_STARTER_WEB)
    implementation(Dependencies.SPRING_BOOT_STARTER_VALIDATION)
}

description = "common-controller"