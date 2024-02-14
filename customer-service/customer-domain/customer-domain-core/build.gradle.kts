
apply<LibraryConventionsPlugin>()

dependencies {
    api(project(":common-utils"))
    api(project(":common-domain"))
}

description = "customer-domain-core"