package net.philipheur.food_ordering_system.order_service.domain.application_service.outbox.model.approval

import com.fasterxml.jackson.annotation.JsonProperty

data class OrderApprovalEventProduct(
    @JsonProperty
    val id: String,
    @JsonProperty
    val quantity: Int
)