package net.philipheur.food_ordering_system.order_service.domain.application_service.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.bind.ConstructorBinding

@ConfigurationProperties(prefix = "order-service")
data class OrderServiceConfigData @ConstructorBinding constructor(
    val paymentRequestTopicName: String,
    val paymentResponseTopicName: String,
    val restaurantApprovalRequestTopicName: String,
    val restaurantApprovalResponseTopicName: String
)