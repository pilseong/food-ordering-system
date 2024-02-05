package net.philipheur.food_ordering_system.restaurant_service.domain.service.ports.output.repository

import net.philipheur.food_ordering_system.restaurant_service.domain.core.entity.OrderApproval

interface OrderApprovalRepository {
    fun save(orderApproval: OrderApproval): OrderApproval
}