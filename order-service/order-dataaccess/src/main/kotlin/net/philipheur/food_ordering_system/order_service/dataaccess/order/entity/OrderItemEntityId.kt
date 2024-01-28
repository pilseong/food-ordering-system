package net.philipheur.food_ordering_system.order_service.dataaccess.order.entity

import java.io.Serializable

data class OrderItemEntityId(
    var id: Long = 0L,
    var order: OrderEntity? = null
): Serializable