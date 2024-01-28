package net.philipheur.food_ordering_system.infrastructure.kafka.config_data

import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.bind.ConstructorBinding
import org.springframework.context.annotation.Configuration


@Configuration
@ConfigurationProperties(prefix = "kafka-config")
open class KafkaConfigData(
    var bootstrapServers: String? = null,
    var schemaRegistryUrlKey: String? = null,
    var schemaRegistryUrl: String? = null,
    var numOfPartitions: Int? = null,
    var replicationFactor: Int? = null
)