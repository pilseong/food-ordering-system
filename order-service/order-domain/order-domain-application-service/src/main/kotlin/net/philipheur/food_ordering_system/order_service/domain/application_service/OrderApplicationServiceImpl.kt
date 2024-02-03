package net.philipheur.food_ordering_system.order_service.domain.application_service

import net.philipheur.food_ordering_system.common.utils.logging.LoggerDelegator
import net.philipheur.food_ordering_system.order_service.domain.application_service.dto.create.CreateOrderCommand
import net.philipheur.food_ordering_system.order_service.domain.application_service.dto.create.CreateOrderResponseDto
import net.philipheur.food_ordering_system.order_service.domain.application_service.dto.track.TrackOrderCommand
import net.philipheur.food_ordering_system.order_service.domain.application_service.dto.track.TrackOrderResponse
import net.philipheur.food_ordering_system.order_service.domain.application_service.ports.input.service.OrderApplicationService
import org.springframework.stereotype.Service


@Service
class OrderApplicationServiceImpl(
    private val orderCreateHelper: OrderCreateHelper,
    private val trackOrderHelper: TrackOrderHelper,
) : OrderApplicationService {

    private val log by LoggerDelegator()

    override fun createOrder(
        command: CreateOrderCommand,
    ): CreateOrderResponseDto {
        return orderCreateHelper.createOrder(command)
    }

    override fun trackOrder(
        command: TrackOrderCommand
    ): TrackOrderResponse {

        val order = trackOrderHelper.trackOrder(command)

        return TrackOrderResponse(
            orderTrackingId = order.trackingId!!.value,
            orderStatus = order.orderStatus!!,
            message = "Order Created Successfully"
        )
    }
}