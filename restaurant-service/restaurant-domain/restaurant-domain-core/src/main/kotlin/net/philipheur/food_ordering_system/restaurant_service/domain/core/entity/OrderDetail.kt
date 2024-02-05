package net.philipheur.food_ordering_system.restaurant_service.domain.core.entity

import net.philipheur.food_ordering_system.common.domain.entity.BaseEntity
import net.philipheur.food_ordering_system.common.domain.valueobject.Money
import net.philipheur.food_ordering_system.common.domain.valueobject.OrderId
import net.philipheur.food_ordering_system.common.domain.valueobject.OrderStatus

class OrderDetail(
    orderId: OrderId? = null,
    val orderStatus: OrderStatus? = null,
    val totalAmount: Money? = null,
    val products: List<Product>
) : BaseEntity<OrderId>(orderId) {

}