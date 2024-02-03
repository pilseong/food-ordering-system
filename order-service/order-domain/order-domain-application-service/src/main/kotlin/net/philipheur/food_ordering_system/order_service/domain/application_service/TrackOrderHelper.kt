package net.philipheur.food_ordering_system.order_service.domain.application_service

import net.philipheur.food_ordering_system.common.utils.logging.LoggerDelegator
import net.philipheur.food_ordering_system.order_service.domain.application_service.dto.track.TrackOrderCommand
import net.philipheur.food_ordering_system.order_service.domain.application_service.ports.output.repository.OrderRepository
import net.philipheur.food_ordering_system.order_service.domain.core.entity.Order
import net.philipheur.food_ordering_system.order_service.domain.core.exception.OrderNotFoundException
import net.philipheur.food_ordering_system.order_service.domain.core.valueobject.TrackingId

import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional


@Component
open class TrackOrderHelper(
    private val orderRepository: OrderRepository
) {
    private val log by LoggerDelegator()

    @Transactional
    fun trackOrder(command: TrackOrderCommand): Order   {
        val targetOrder = orderRepository.findByTrackingId(
            TrackingId(command.orderTrackingId)
        )
        if (targetOrder == null) {
            log.warn(
                "Could not find order with " +
                        "tracking id: ${command.orderTrackingId}"
            )
            throw OrderNotFoundException(
                "Could not find order with " +
                        "tracing id: ${command.orderTrackingId}"
            )
        }
        return targetOrder
    }
}