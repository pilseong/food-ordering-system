package net.philipheur.food_ordering_system.order_service.domain.application_service.outbox.model.payment

import com.fasterxml.jackson.annotation.JsonProperty
import java.math.BigDecimal
import java.time.ZonedDateTime


data class OrderPaymentEventPayload(
    @JsonProperty
    val orderId: String,
    @JsonProperty
    val customerId: String,
    @JsonProperty
    val price: BigDecimal,
    @JsonProperty
    val createdAt: ZonedDateTime,
    @JsonProperty
    val paymentOrderStatus: String
)