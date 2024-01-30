package net.philipheur.food_ordering_system.infrastructure.kafka.config_data

import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.bind.ConstructorBinding
import org.springframework.context.annotation.Configuration


@ConfigurationProperties(prefix = "kafka-config")
data class KafkaConfigData
@ConstructorBinding constructor(
    var bootstrapServers: String,
    var schemaRegistryUrlKey: String,
    var schemaRegistryUrl: String,
    var numOfPartitions: Int,
    var replicationFactor: Int
)