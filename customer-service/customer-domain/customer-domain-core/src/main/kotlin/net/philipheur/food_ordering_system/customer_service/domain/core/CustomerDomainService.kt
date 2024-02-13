package net.philipheur.food_ordering_system.customer_service.domain.core

import net.philipheur.food_ordering_system.customer_service.domain.core.entity.Customer
import net.philipheur.food_ordering_system.customer_service.domain.core.event.CustomerCreatedEvent

interface CustomerDomainService {
    fun validateAndInitiateCustomer(customer: Customer): CustomerCreatedEvent
}