package net.philipheur.food_ordering_system.payment_service.domain.service.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration


@Configuration
@ConfigurationProperties(prefix = "payment-service")
open class PaymentServiceConfigData(
    var paymentRequestTopicName: String? = null,
    var paymentResponseTopicName: String? = null,
)