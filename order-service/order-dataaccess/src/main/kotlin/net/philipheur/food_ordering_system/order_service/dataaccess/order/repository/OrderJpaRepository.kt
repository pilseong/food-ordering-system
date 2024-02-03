package net.philipheur.food_ordering_system.order_service.dataaccess.order.repository

import net.philipheur.food_ordering_system.order_service.dataaccess.order.entity.OrderEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface OrderJpaRepository: JpaRepository<OrderEntity, UUID> {
    fun findByTrackingId(trackingId: UUID): OrderEntity?
}