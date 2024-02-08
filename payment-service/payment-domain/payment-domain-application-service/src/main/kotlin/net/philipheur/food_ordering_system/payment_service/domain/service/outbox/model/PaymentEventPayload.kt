package net.philipheur.food_ordering_system.payment_service.domain.service.outbox.model

import com.fasterxml.jackson.annotation.JsonProperty
import java.math.BigDecimal
import java.time.ZonedDateTime


data class PaymentEventPayload(
    @JsonProperty
    val paymentId: String,
    @JsonProperty
    val customerId: String,
    @JsonProperty
    val orderId: String,
    @JsonProperty
    val price: BigDecimal,
    @JsonProperty
    val createdAt: ZonedDateTime,
    @JsonProperty
    val paymentStatus: String,
    @JsonProperty
    val failureMessages: MutableList<String>
)