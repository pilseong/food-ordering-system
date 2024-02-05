package net.philipheur.food_ordering_system.restaurant_service.dataaccess.adapter

import net.philipheur.food_ordering_system.common.domain.valueobject.OrderId
import net.philipheur.food_ordering_system.common.domain.valueobject.RestaurantId
import net.philipheur.food_ordering_system.restaurant_service.dataaccess.entity.OrderApprovalEntity
import net.philipheur.food_ordering_system.restaurant_service.dataaccess.repository.OrderApprovalJpaRepository
import net.philipheur.food_ordering_system.restaurant_service.domain.core.entity.OrderApproval
import net.philipheur.food_ordering_system.restaurant_service.domain.core.valueobject.OrderApprovalId
import net.philipheur.food_ordering_system.restaurant_service.domain.service.ports.output.repository.OrderApprovalRepository
import org.springframework.stereotype.Component

@Component
class OrderApprovalRepositoryImpl(
    private val orderApprovalJpaRepository: OrderApprovalJpaRepository,
) : OrderApprovalRepository {
    override fun save(orderApproval: OrderApproval): OrderApproval {
        val entity = orderApprovalJpaRepository.save(
            OrderApprovalEntity(
                id = orderApproval.id!!.value,
                restaurantId = orderApproval.restaurantId.value,
                orderId = orderApproval.orderId.value,
                status = orderApproval.orderApprovalStatus
            )
        )

        return OrderApproval(
            orderApprovalId = OrderApprovalId(entity.id),
            orderId = OrderId(entity.orderId),
            restaurantId = RestaurantId(entity.restaurantId),
            orderApprovalStatus = entity.status
        )
    }
}