package net.philipheur.food_ordering_system.payment_service.domain.service.outbox.model

import com.fasterxml.jackson.annotation.JsonProperty
import java.time.ZonedDateTime


data class OrderApprovalEventPayload(
    @JsonProperty
    val restaurantId: String,
    @JsonProperty
    val orderId: String,
    @JsonProperty
    val createdAt: ZonedDateTime,
    @JsonProperty
    val orderApprovalStatus: String,
    @JsonProperty
    val failureMessages: MutableList<String>
)