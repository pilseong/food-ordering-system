
apply<LibraryConventionsPlugin>()

dependencies {
    api(project(":kafka-config-data"))
    api("org.springframework.kafka:spring-kafka:3.1.1")
    api("io.confluent:kafka-avro-serializer:7.5.1")
}

description = "kafka-producer"
