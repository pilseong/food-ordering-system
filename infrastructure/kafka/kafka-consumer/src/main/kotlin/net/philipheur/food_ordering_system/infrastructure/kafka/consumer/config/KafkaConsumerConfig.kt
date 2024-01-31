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
open class KafkaConsumerConfig<ID : Serializable, MSG : SpecificRecordBase>(
    private val kafkaConfigData: KafkaConfigData,
    private val kafkaConsumerConfigData: KafkaConsumerConfigData,
) {

    @Bean
    open fun consumerConfigs(): Map<String, Any> {
        val props: Map<String, Any> =
            mapOf(
                BOOTSTRAP_SERVERS_CONFIG to kafkaConfigData.bootstrapServers,
                KEY_DESERIALIZER_CLASS_CONFIG to kafkaConsumerConfigData.keyDeserializer,
                VALUE_DESERIALIZER_CLASS_CONFIG to kafkaConsumerConfigData.valueDeserializer,
                AUTO_OFFSET_RESET_CONFIG to kafkaConsumerConfigData.autoOffsetReset,
                kafkaConfigData.schemaRegistryUrlKey to kafkaConfigData.schemaRegistryUrl,
                kafkaConsumerConfigData.specificAvroReaderKey to kafkaConsumerConfigData.specificAvroReader,
                SESSION_TIMEOUT_MS_CONFIG to kafkaConsumerConfigData.sessionTimeoutMs,
                HEARTBEAT_INTERVAL_MS_CONFIG to kafkaConsumerConfigData.heartbeatIntervalMs,
                MAX_POLL_INTERVAL_MS_CONFIG to kafkaConsumerConfigData.maxPollIntervalMs,
                MAX_PARTITION_FETCH_BYTES_CONFIG to
                        kafkaConsumerConfigData.maxPartitionFetchBytesDefault *
                        kafkaConsumerConfigData.maxPartitionFetchBytesBoostFactor,
                MAX_POLL_RECORDS_CONFIG to kafkaConsumerConfigData.maxPollRecords,
            )
        return props
    }

    @Bean
    open fun consumerFactory(): ConsumerFactory<ID, MSG> {
        return DefaultKafkaConsumerFactory(consumerConfigs())
    }

    @Bean
    open fun kafkaListenerContainerFactory():
            KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<ID, MSG>> {

        val factory = ConcurrentKafkaListenerContainerFactory<ID, MSG>()
        factory.consumerFactory = consumerFactory()
        factory.isBatchListener = kafkaConsumerConfigData.batchListener
        factory.setConcurrency(kafkaConsumerConfigData.concurrencyLevel)
        factory.setAutoStartup(kafkaConsumerConfigData.autoStartup)
        factory.containerProperties.pollTimeout = kafkaConsumerConfigData.pollTimeoutMs
        return factory
    }
}