
plugins {
    id("net.pilseong.food_ordering_system.kotlin-library-conventions")
}

dependencies {
    api(project(":kafka-config-data"))
    api("org.springframework.kafka:spring-kafka:3.1.1")
    api("io.confluent:kafka-avro-serializer:7.5.1")
}

description = "kafka-consumer"
