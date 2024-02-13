package net.philipheur.food_ordering_system.customer_service.domain.application_service.ports.output.repository

import net.philipheur.food_ordering_system.customer_service.domain.core.entity.Customer

interface CustomerRepository {
    fun save(customer: Customer): Customer
}