package net.philipheur.food_ordering_system.restaurant_service.domain.service.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.bind.ConstructorBinding

@ConfigurationProperties(prefix = "restaurant-service")
data class RestaurantServiceConfigData @ConstructorBinding constructor(
    val restaurantApprovalRequestTopicName: String,
    val restaurantApprovalResponseTopicName: String,
)