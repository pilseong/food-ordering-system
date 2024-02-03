package net.philipheur.food_ordering_system.order_service.domain.application_service.ports.input.service

import jakarta.validation.Valid
import net.philipheur.food_ordering_system.order_service.domain.application_service.dto.create.CreateOrderCommand
import net.philipheur.food_ordering_system.order_service.domain.application_service.dto.create.CreateOrderResponseDto
import net.philipheur.food_ordering_system.order_service.domain.application_service.dto.track.TrackOrderCommand
import net.philipheur.food_ordering_system.order_service.domain.application_service.dto.track.TrackOrderResponse

interface OrderApplicationService {
    fun createOrder(
        command: @Valid CreateOrderCommand
    ): CreateOrderResponseDto

    fun trackOrder(
        command: @Valid TrackOrderCommand
    ): TrackOrderResponse

}