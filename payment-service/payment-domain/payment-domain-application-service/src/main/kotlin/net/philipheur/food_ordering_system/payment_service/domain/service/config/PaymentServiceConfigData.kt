package net.philipheur.food_ordering_system.payment_service.domain.service.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.bind.ConstructorBinding

@ConfigurationProperties(prefix = "payment-service")
data class PaymentServiceConfigData @ConstructorBinding constructor(
    val paymentRequestTopicName: String,
    val paymentResponseTopicName: String,
)