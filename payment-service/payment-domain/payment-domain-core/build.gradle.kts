plugins {
    id("net.pilseong.food_ordering_system.kotlin-library-conventions")
}

dependencies {
    api(project(":common-domain"))
    implementation(project(":common-utils"))

}

description = "payment-domain-core"