package net.philipheur.food_ordering_system.order_service.domain.application_service.ports.output.repository

import net.philipheur.food_ordering_system.order_service.domain.core.entity.Order
import net.philipheur.food_ordering_system.order_service.domain.core.valueobject.TrackingId

interface OrderRepository {

    fun save(order: Order): Order
    fun findByTrackingId(trackingId: TrackingId): Order?
}