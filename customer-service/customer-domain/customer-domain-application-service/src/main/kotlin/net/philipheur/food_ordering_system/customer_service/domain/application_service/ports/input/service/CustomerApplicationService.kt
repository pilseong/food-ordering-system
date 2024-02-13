package net.philipheur.food_ordering_system.customer_service.domain.application_service.ports.input.service

import jakarta.validation.Valid
import net.philipheur.food_ordering_system.customer_service.domain.application_service.dto.create.CreateCustomerCommand
import net.philipheur.food_ordering_system.customer_service.domain.application_service.dto.create.CreateCustomerResponseDto

interface CustomerApplicationService {
    fun createCustomer(
        @Valid command: CreateCustomerCommand
    ): CreateCustomerResponseDto
}