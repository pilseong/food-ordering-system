package net.philipheur.food_ordering_system.infrastructure.kafka.producer.service

import jakarta.annotation.PreDestroy
import net.philipheur.food_ordering_system.infrastructure.kafka.producer.exception.KafkaProducerException
import org.apache.avro.specific.SpecificRecordBase
import org.slf4j.LoggerFactory
import org.springframework.kafka.KafkaException
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.support.SendResult
import org.springframework.stereotype.Component
import java.io.Serializable
import java.util.function.BiConsumer

inline fun <reified T> T.logger() = LoggerFactory.getLogger(T::class.java)!!

@Component
class KafkaProducerImpl<ID : Serializable, MSG : SpecificRecordBase>(
    private val kafkaTemplate: KafkaTemplate<ID, MSG>
) : KafkaProducer<ID, MSG> {

    private val log = logger()

    override fun send(
        topicName: String,
        key: ID,
        message: MSG,
        callback: BiConsumer<SendResult<ID, MSG>?, Throwable?>
    ) {
        log.info("Sending message={} to topic={}", message, topicName)

        try {
            // 메시지 발신
            val kafkaResultFuture = kafkaTemplate.send(
                topicName, key, message
            )

            // 나중에 호출될 콜백을 등록한다.
            kafkaResultFuture
                .whenComplete(callback)

        } catch (e: KafkaException) {

            log.error(
                "Error on kafka producer with " +
                        "key: $key, message: $message and exception: ${e.message}"
            )

            throw KafkaProducerException(
                "Error on kafka producer with key: $key and message: $message"
            )
        }
    }

    @PreDestroy
    fun close() {
        log.info("Closing kafka producer!")
        kafkaTemplate.destroy()
    }
}
