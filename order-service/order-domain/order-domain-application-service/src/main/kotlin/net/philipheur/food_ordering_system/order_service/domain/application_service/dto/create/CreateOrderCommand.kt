package net.philipheur.food_ordering_system.order_service.domain.application_service.dto.create

import jakarta.validation.constraints.NotNull
import java.math.BigDecimal
import java.util.*


data class CreateOrderCommand(
    @NotNull
    val customerId: UUID,
    @NotNull
    val restaurantId: UUID,
    @NotNull
    val price: BigDecimal,
    @NotNull
    val items: List<OrderItemDto>,
    @NotNull
    val deliveryAddress: DeliveryAddressDto
)