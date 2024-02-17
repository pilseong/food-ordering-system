package net.philipheur.food_ordering_system.infrastructure.kafka.consumer.config

import net.philipheur.food_ordering_system.infrastructure.kafka.config_data.KafkaConfigData
import net.philipheur.food_ordering_system.infrastructure.kafka.config_data.KafkaConsumerConfigData
import org.apache.avro.specific.SpecificRecordBase
import org.apache.kafka.clients.consumer.ConsumerConfig.*
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory
import org.springframework.kafka.config.KafkaListenerContainerFactory
import org.springframework.kafka.core.ConsumerFactory
import org.springframework.kafka.core.DefaultKafkaConsumerFactory
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer
import java.io.Serializable


@Configuration
open class KafkaConsumerConfig<KEY : Serializable, MSG : SpecificRecordBase>(
    private val configData: KafkaConfigData,
    private val consumerConfigData: KafkaConsumerConfigData,
) {

    @Bean
    open fun consumerConfigs(): Map<String, Any> {
        val props: Map<String, Any> =
            mapOf(
                BOOTSTRAP_SERVERS_CONFIG to configData.bootstrapServers,
                KEY_DESERIALIZER_CLASS_CONFIG to consumerConfigData.keyDeserializer,
                VALUE_DESERIALIZER_CLASS_CONFIG to consumerConfigData.valueDeserializer,
                AUTO_OFFSET_RESET_CONFIG to consumerConfigData.autoOffsetReset,
                configData.schemaRegistryUrlKey to configData.schemaRegistryUrl,
                consumerConfigData.specificAvroReaderKey to consumerConfigData.specificAvroReader,
                SESSION_TIMEOUT_MS_CONFIG to consumerConfigData.sessionTimeoutMs,
                HEARTBEAT_INTERVAL_MS_CONFIG to consumerConfigData.heartbeatIntervalMs,
                MAX_POLL_INTERVAL_MS_CONFIG to consumerConfigData.maxPollIntervalMs,
                MAX_PARTITION_FETCH_BYTES_CONFIG to
                        consumerConfigData.maxPartitionFetchBytesDefault *
                        consumerConfigData.maxPartitionFetchBytesBoostFactor,
                MAX_POLL_RECORDS_CONFIG to consumerConfigData.maxPollRecords,
            )
        return props
    }

    @Bean
    open fun consumerFactory(): ConsumerFactory<KEY, MSG> {
        return DefaultKafkaConsumerFactory(consumerConfigs())
    }

    @Bean
    open fun kafkaListenerContainerFactory():
            KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<KEY, MSG>> {

        val factory = ConcurrentKafkaListenerContainerFactory<KEY, MSG>()
        factory.consumerFactory = consumerFactory()
        factory.isBatchListener = consumerConfigData.batchListener
        factory.setConcurrency(consumerConfigData.concurrencyLevel)
        factory.setAutoStartup(consumerConfigData.autoStartup)
        factory.containerProperties.pollTimeout = consumerConfigData.pollTimeoutMs
        return factory
    }
}