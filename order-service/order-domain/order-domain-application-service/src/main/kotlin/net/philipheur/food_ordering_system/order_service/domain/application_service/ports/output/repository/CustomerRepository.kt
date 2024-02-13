package net.philipheur.food_ordering_system.order_service.domain.application_service.ports.output.repository

import net.philipheur.food_ordering_system.order_service.domain.core.entity.Customer
import java.util.*


interface CustomerRepository {
    fun findCustomer(customerId: UUID): Customer?

    fun save(customer: Customer): Customer
}