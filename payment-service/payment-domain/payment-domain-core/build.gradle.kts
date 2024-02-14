apply<LibraryConventionsPlugin>()

dependencies {
    api(project(":common-domain"))
    implementation(project(":common-utils"))

}

description = "payment-domain-core"