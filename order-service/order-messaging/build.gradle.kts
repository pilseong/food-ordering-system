
apply<LibraryConventionsPlugin>()

dependencies {
    implementation(project(":common-utils"))
    implementation(project(":outbox"))
    api(project(":order-domain-application-service"))
    api(project(":kafka-model"))
    api(project(":kafka-producer"))
    api(project(":kafka-consumer"))
}

description = "order-messaging"