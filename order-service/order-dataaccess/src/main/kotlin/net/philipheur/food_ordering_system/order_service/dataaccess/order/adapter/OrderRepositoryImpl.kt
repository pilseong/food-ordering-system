package net.philipheur.food_ordering_system.order_service.dataaccess.order.adapter

import net.philipheur.food_ordering_system.common.domain.valueobject.OrderId
import net.philipheur.food_ordering_system.order_service.dataaccess.order.mapper.OrderDataAccessMapper
import net.philipheur.food_ordering_system.order_service.dataaccess.order.repository.OrderJpaRepository
import net.philipheur.food_ordering_system.order_service.domain.application_service.ports.output.repository.OrderRepository
import net.philipheur.food_ordering_system.order_service.domain.core.entity.Order
import net.philipheur.food_ordering_system.order_service.domain.core.valueobject.TrackingId
import org.springframework.stereotype.Component

@Component
open class OrderRepositoryImpl(
    private val orderJpaRepository: OrderJpaRepository,
    private val mapper: OrderDataAccessMapper,
) : OrderRepository {
    override fun save(order: Order): Order {

        var orderEntity = mapper.orderToOrderEntity(order)
        orderEntity = orderJpaRepository.save(orderEntity)

        return mapper.orderEntityToOrder(orderEntity)
    }

    override fun findByTrackingId(trackingId: TrackingId) =
        orderJpaRepository
            .findByTrackingId(trackingId.value)
            ?.let { mapper.orderEntityToOrder(it) }

    override fun findByOrderId(orderId: OrderId): Order? {
        val optionalOrder = orderJpaRepository
            .findById(orderId.value)

        return if (optionalOrder.isPresent) {
            mapper.orderEntityToOrder(optionalOrder.get())
        } else null
    }


}