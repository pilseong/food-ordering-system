package net.philipheur.food_ordering_system.order_service.domain.application_service.dto.create

import jakarta.validation.constraints.NotNull
import lombok.AllArgsConstructor
import lombok.Builder
import lombok.NoArgsConstructor
import lombok.extern.jackson.Jacksonized
import java.math.BigDecimal
import java.util.*

@NoArgsConstructor
@AllArgsConstructor
@Builder
class OrderItemDto(
    @NotNull
    val productId: UUID,
    @NotNull
    val quantity: Int,
    @NotNull
    val price: BigDecimal,
    @NotNull
    val subTotal: BigDecimal
)