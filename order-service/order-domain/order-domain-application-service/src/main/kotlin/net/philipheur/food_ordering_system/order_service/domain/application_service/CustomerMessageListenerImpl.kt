package net.philipheur.food_ordering_system.order_service.domain.application_service

import net.philipheur.food_ordering_system.common.domain.valueobject.CustomerId
import net.philipheur.food_ordering_system.common.utils.logging.LoggerDelegator
import net.philipheur.food_ordering_system.order_service.domain.application_service.dto.message.CustomerModel
import net.philipheur.food_ordering_system.order_service.domain.application_service.ports.input.message.CustomerMessageListener
import net.philipheur.food_ordering_system.order_service.domain.application_service.ports.output.repository.CustomerRepository
import net.philipheur.food_ordering_system.order_service.domain.core.entity.Customer
import org.springframework.stereotype.Service
import java.util.*

@Service
class CustomerMessageListenerImpl(
    private val customerRepository: CustomerRepository
) : CustomerMessageListener {

    private val log by LoggerDelegator()

    override fun customerCreated(customerModel: CustomerModel) {
        customerRepository.save(
            Customer(
                customerId = CustomerId(UUID.fromString(customerModel.id)),
                username = customerModel.username,
                firstName = customerModel.firstName,
                lastName = customerModel.lastName
            )
        )

        log.info("Customer is created in order database with id: ${customerModel.id}")
    }
}