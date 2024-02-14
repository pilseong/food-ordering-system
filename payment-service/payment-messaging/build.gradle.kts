
apply<LibraryConventionsPlugin>()

dependencies {
    implementation(project(":common-utils"))
    api(project(":payment-domain-application-service"))
    api(project(":kafka-model"))
    api(project(":kafka-producer"))
    api(project(":kafka-consumer"))
}

description = "payment-messaging"