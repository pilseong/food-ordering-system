package net.philipheur.food_ordering_system.order_service.dataaccess.restaurant.entity

import java.io.Serializable
import java.util.UUID

data class RestaurantEntityId(
    var restaurantId: UUID? = null,
    var productId: UUID? = null
): Serializable