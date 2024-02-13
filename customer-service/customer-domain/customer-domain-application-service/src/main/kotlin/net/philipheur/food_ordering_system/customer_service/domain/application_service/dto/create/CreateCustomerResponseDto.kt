package net.philipheur.food_ordering_system.customer_service.domain.application_service.dto.create

import jakarta.validation.constraints.NotNull
import java.util.*

data class CreateCustomerResponseDto(
    @NotNull val customerId: UUID,
    @NotNull val message: String
)