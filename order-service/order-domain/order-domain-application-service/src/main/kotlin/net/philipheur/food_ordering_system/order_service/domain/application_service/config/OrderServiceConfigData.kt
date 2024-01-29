package net.philipheur.food_ordering_system.order_service.domain.application_service.config

import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties(prefix = "order-service")
open class OrderServiceConfigData(
    var paymentRequestTopicName: String? = null,
    var paymentResponseTopicName: String? = null,
    var restaurantApprovalRequestTopicName: String? = null,
    var restaurantApprovalResponseTopicName: String? = null
)