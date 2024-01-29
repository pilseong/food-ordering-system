package net.philipheur.food_ordering_system.infrastructure.kafka.consumer

import org.apache.avro.specific.SpecificRecordBase

interface KafkaConsumer<MSG : SpecificRecordBase> {
    fun receive(
        models: List<MSG>,
        keys: List<String>,
        partitions: List<Int>,
        offsets: List<Long>
    )
}