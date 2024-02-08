package net.philipheur.food_ordering_system.payment_service.messaging.mapper

import com.fasterxml.jackson.databind.ObjectMapper
import net.philipheur.food_ordering_system.common.utils.logging.LoggerDelegator
import net.philipheur.food_ordering_system.payment_service.domain.core.exception.PaymentDomainException
import org.springframework.stereotype.Component

@Component
class KafkaMessageHelper(
    private val objectMapper: ObjectMapper
) {
    private val log by LoggerDelegator()
    fun <T> getPaymentEventPayload(
        payload: String,
        outputType: Class<T>
    ): T {
        try {
            return objectMapper
                .readValue(payload, outputType)
        } catch (e: Exception) {
            log.error("Could not read PaymentEventPayload object", e)
            throw PaymentDomainException(
                "Could not read PaymentEventPayload object", e
            )
        }
    }
}