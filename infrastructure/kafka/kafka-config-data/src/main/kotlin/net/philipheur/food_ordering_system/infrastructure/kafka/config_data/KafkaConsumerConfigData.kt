package net.philipheur.food_ordering_system.infrastructure.kafka.config_data

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.bind.ConstructorBinding

@ConfigurationProperties(prefix = "kafka-consumer-config")
data class KafkaConsumerConfigData
@ConstructorBinding constructor(
    val keyDeserializer: String,
    val valueDeserializer: String,
    val autoOffsetReset: String,
    val specificAvroReaderKey: String,
    val specificAvroReader: String,
    val batchListener: Boolean,
    val autoStartup: Boolean,
    val concurrencyLevel: Int,
    val sessionTimeoutMs: Int,
    val heartbeatIntervalMs: Int,
    val maxPollIntervalMs: Int,
    val pollTimeoutMs: Long,
    val maxPollRecords: Int,
    val maxPartitionFetchBytesDefault: Int,
    val maxPartitionFetchBytesBoostFactor: Int
)