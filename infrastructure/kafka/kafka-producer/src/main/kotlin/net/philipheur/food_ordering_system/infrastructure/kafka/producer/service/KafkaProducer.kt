package net.philipheur.food_ordering_system.infrastructure.kafka.producer.service

import org.apache.avro.specific.SpecificRecordBase
import org.springframework.kafka.support.SendResult
import java.io.Serializable
import java.util.function.BiConsumer


interface KafkaProducer<ID : Serializable, MSG : SpecificRecordBase> {
    fun send(topicName: String,
             key: ID,
             message: MSG,
             // callback 에서 결과 값이 result 가 올지 throwable 이 올지 모른다. nullable 로 설정)
             callback: BiConsumer<SendResult<ID, MSG>?, Throwable?>
    )
}
