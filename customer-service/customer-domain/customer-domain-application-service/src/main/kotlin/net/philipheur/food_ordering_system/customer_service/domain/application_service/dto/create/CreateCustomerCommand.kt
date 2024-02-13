package net.philipheur.food_ordering_system.customer_service.domain.application_service.dto.create

import jakarta.validation.constraints.NotNull
import java.util.*

data class CreateCustomerCommand(
    @NotNull
    val customerId: UUID,
    @NotNull
    val username: String,
    @NotNull
    val firstName: String,
    @NotNull
    val lastName: String,
)