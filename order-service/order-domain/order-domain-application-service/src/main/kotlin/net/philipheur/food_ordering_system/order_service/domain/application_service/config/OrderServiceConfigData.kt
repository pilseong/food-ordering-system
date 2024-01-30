package net.philipheur.food_ordering_system.order_service.domain.application_service.config

import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.bind.ConstructorBinding
import org.springframework.context.annotation.Configuration

@ConfigurationProperties(prefix = "order-service")
data class OrderServiceConfigData @ConstructorBinding constructor(
    var paymentRequestTopicName: String,
    var paymentResponseTopicName: String,
    var restaurantApprovalRequestTopicName: String,
    var restaurantApprovalResponseTopicName: String
)