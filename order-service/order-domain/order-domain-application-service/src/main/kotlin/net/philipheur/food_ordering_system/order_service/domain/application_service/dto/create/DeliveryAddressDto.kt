package net.philipheur.food_ordering_system.order_service.domain.application_service.dto.create

import jakarta.validation.constraints.Max
import jakarta.validation.constraints.NotNull
import lombok.AllArgsConstructor
import lombok.Builder
import lombok.NoArgsConstructor
import lombok.extern.jackson.Jacksonized

@NoArgsConstructor
@AllArgsConstructor
@Builder
class DeliveryAddressDto(
    @NotNull @Max(value = 50)
    val street: String,
    @NotNull @Max(value = 10)
    val postalCode: String,
    @NotNull @Max(value = 20)
    val city: String
)