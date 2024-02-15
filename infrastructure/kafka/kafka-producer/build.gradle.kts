
apply<LibraryConventionsPlugin>()

dependencies {
    api(project(":common-utils"))
    api(project(":kafka-config-data"))
    api(Dependencies.SPRING_KAFKA)
    api(Dependencies.KAFKA_AVRO_SERIALIZER)
}

description = "kafka-producer"
