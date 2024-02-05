package net.philipheur.food_ordering_system.restaurant_service.domain.core.event

import net.philipheur.food_ordering_system.common.domain.event.DomainEvent
import net.philipheur.food_ordering_system.common.domain.valueobject.RestaurantId
import net.philipheur.food_ordering_system.restaurant_service.domain.core.entity.OrderApproval
import java.time.ZonedDateTime

abstract class OrderApprovalEvent(
    val orderApproval: OrderApproval,
    val restaurantId: RestaurantId,
    val failureMessages: MutableList<String>,
    val createdAt: ZonedDateTime
) : DomainEvent<OrderApproval> {
    open fun fire() {}
}