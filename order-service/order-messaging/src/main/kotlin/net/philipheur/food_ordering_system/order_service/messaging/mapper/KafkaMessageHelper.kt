package net.philipheur.food_ordering_system.order_service.messaging.mapper

import com.fasterxml.jackson.databind.ObjectMapper
import net.philipheur.food_ordering_system.common.utils.logging.LoggerDelegator
import net.philipheur.food_ordering_system.order_service.domain.core.exception.OrderDomainException
import org.springframework.stereotype.Component

@Component
class KafkaMessageHelper(
    private val objectMapper: ObjectMapper
) {
    private val log by LoggerDelegator()
    fun <T> getOrderEventPayload(
        payload: String,
        outputType: Class<T>
    ): T {
        try {
            return objectMapper
                .readValue(payload, outputType)
        } catch (e: Exception) {
            log.error("Could not read OrderPaymentEventPayload object", e)
            throw OrderDomainException(
                "Could not read OrderPaymentEventPayload object", e
            )
        }
    }
}