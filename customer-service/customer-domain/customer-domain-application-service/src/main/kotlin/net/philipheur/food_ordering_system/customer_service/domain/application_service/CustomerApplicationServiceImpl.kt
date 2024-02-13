package net.philipheur.food_ordering_system.customer_service.domain.application_service

import net.philipheur.food_ordering_system.customer_service.domain.application_service.dto.create.CreateCustomerCommand
import net.philipheur.food_ordering_system.customer_service.domain.application_service.dto.create.CreateCustomerResponseDto
import net.philipheur.food_ordering_system.customer_service.domain.application_service.ports.input.service.CustomerApplicationService
import net.philipheur.food_ordering_system.customer_service.domain.application_service.ports.output.message.publisher.CustomerMessagePublisher
import org.springframework.stereotype.Service
import org.springframework.validation.annotation.Validated

@Validated
@Service
open class CustomerApplicationServiceImpl(
    private val customerCreateHelper: CustomerCreateHelper,
    private val publisher: CustomerMessagePublisher
) : CustomerApplicationService {
    override fun createCustomer(
        command: CreateCustomerCommand
    ): CreateCustomerResponseDto {

        val event = customerCreateHelper.createOrder(command)
        publisher.publish(event)

        return CreateCustomerResponseDto(
            customerId = event.customer.id!!.value,
            message = "Customer saved successfully"
        )
    }
}