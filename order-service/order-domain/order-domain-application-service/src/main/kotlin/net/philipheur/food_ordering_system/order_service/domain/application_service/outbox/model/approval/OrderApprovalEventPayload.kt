package net.philipheur.food_ordering_system.order_service.domain.application_service.outbox.model.approval

import com.fasterxml.jackson.annotation.JsonProperty
import java.math.BigDecimal
import java.time.ZonedDateTime

data class OrderApprovalEventPayload(
    @JsonProperty
    val orderId: String,
    @JsonProperty
    val restaurantId: String,
    @JsonProperty
    val price: BigDecimal,
    @JsonProperty
    val createdAt: ZonedDateTime,
    @JsonProperty
    val restaurantOrderStatus: String,
    @JsonProperty
    val products: List<OrderApprovalEventProduct>
)