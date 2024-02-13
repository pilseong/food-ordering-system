package net.philipheur.food_ordering_system.restaurant_service.domain.service

import net.philipheur.food_ordering_system.restaurant_service.domain.service.dto.RestaurantApprovalRequest
import net.philipheur.food_ordering_system.restaurant_service.domain.service.ports.input.message.listener.RestaurantApprovalRequestMessageListener
import org.springframework.stereotype.Service

@Service
class RestaurantApprovalRequestMessageListenerImpl(
    private val restaurantApprovalRequestHelper: RestaurantApprovalRequestHelper
) : RestaurantApprovalRequestMessageListener {
    override fun approveOrder(restaurantApprovalRequest: RestaurantApprovalRequest) {
        restaurantApprovalRequestHelper
            .persistOrderApproval(restaurantApprovalRequest)
    }
}