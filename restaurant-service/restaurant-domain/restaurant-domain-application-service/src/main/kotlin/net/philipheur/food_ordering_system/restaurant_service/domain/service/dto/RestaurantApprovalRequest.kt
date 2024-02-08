package net.philipheur.food_ordering_system.restaurant_service.domain.service.dto

import net.philipheur.food_ordering_system.common.domain.valueobject.RestaurantOrderStatus
import net.philipheur.food_ordering_system.restaurant_service.domain.core.entity.Product
import java.math.BigDecimal
import java.time.Instant
import java.util.*

data class RestaurantApprovalRequest(
    val id: UUID,
    val sagaId: UUID,
    val restaurantId: UUID,
    val orderId: UUID,
    val restaurantOrderStatus: RestaurantOrderStatus,
    val products: List<Product>,
    val price: BigDecimal,
    val createdAt: Instant,
)