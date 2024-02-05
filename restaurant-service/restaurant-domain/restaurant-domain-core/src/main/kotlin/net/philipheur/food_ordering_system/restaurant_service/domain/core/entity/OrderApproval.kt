package net.philipheur.food_ordering_system.restaurant_service.domain.core.entity

import net.philipheur.food_ordering_system.common.domain.entity.BaseEntity
import net.philipheur.food_ordering_system.common.domain.valueobject.OrderApprovalStatus
import net.philipheur.food_ordering_system.common.domain.valueobject.OrderId
import net.philipheur.food_ordering_system.common.domain.valueobject.RestaurantId
import net.philipheur.food_ordering_system.restaurant_service.domain.core.valueobject.OrderApprovalId

class OrderApproval(
    orderApprovalId: OrderApprovalId,
    var restaurantId: RestaurantId,
    var orderId: OrderId,
    var orderApprovalStatus: OrderApprovalStatus
) : BaseEntity<OrderApprovalId>(orderApprovalId) {
}