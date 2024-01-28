package net.philipheur.food_ordering_system.order_service.domain.application_service.dto.create

import jakarta.validation.constraints.NotNull
import net.philipheur.food_ordering_system.common.domain.valueobject.OrderStatus
import java.util.*

data class CreateOrderResponseDto(
    @NotNull val orderTrackingId: UUID,
    @NotNull val orderStatus: OrderStatus,
    @NotNull val message: String
)