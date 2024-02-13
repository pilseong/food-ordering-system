package net.philipheur.food_ordering_system.order_service.domain.application_service.ports.input.message

import net.philipheur.food_ordering_system.order_service.domain.application_service.dto.message.CustomerModel

interface CustomerMessageListener {
    fun customerCreated(customerModel: CustomerModel)
}