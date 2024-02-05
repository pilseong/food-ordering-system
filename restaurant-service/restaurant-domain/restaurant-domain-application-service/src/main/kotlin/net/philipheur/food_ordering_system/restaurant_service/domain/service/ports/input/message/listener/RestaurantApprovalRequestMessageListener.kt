package net.philipheur.food_ordering_system.restaurant_service.domain.service.ports.input.message.listener

import net.philipheur.food_ordering_system.restaurant_service.domain.service.dto.RestaurantApprovalRequest

interface RestaurantApprovalRequestMessageListener {

    fun approveOrder(restaurantApprovalRequest: RestaurantApprovalRequest)

}