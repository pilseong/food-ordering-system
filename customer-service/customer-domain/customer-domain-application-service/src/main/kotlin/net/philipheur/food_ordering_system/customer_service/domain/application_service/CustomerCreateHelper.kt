package net.philipheur.food_ordering_system.customer_service.domain.application_service

import net.philipheur.food_ordering_system.common.domain.valueobject.CustomerId
import net.philipheur.food_ordering_system.common.utils.logging.LoggerDelegator
import net.philipheur.food_ordering_system.customer_service.domain.application_service.dto.create.CreateCustomerCommand
import net.philipheur.food_ordering_system.customer_service.domain.application_service.ports.output.repository.CustomerRepository
import net.philipheur.food_ordering_system.customer_service.domain.core.CustomerDomainService
import net.philipheur.food_ordering_system.customer_service.domain.core.entity.Customer
import net.philipheur.food_ordering_system.customer_service.domain.core.event.CustomerCreatedEvent
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional


@Component
open class CustomerCreateHelper(
    private val customerRepository: CustomerRepository,
    private val customerDomainService: CustomerDomainService,
) {
    private val log by LoggerDelegator()
    @Transactional
    open fun createOrder(
        command: CreateCustomerCommand
    ): CustomerCreatedEvent {

        // 고객 객체 생성
        val event = customerDomainService.validateAndInitiateCustomer(
            Customer(
                customerId = CustomerId(command.customerId),
                username = command.username,
                firstName = command.firstName,
                lastName = command.lastName
            )
        )

        customerRepository.save(event.customer)

        log.info(
            "Returning CreateOrderResponseDto with " +
                    "order id: ${event.customer.id!!.value}"
        )

        return event
    }
}