package net.philipheur.food_ordering_system.customer_service.dataaccess.adapter

import net.philipheur.food_ordering_system.common.domain.valueobject.CustomerId
import net.philipheur.food_ordering_system.customer_service.dataaccess.entity.CustomerEntity
import net.philipheur.food_ordering_system.customer_service.dataaccess.repository.CustomerJpaRepository
import net.philipheur.food_ordering_system.customer_service.domain.application_service.ports.output.repository.CustomerRepository
import net.philipheur.food_ordering_system.customer_service.domain.core.entity.Customer
import org.springframework.stereotype.Component

@Component
class CustomerRepositoryImpl(
    private val customerJpaRepository: CustomerJpaRepository
): CustomerRepository {
    override fun save(customer: Customer): Customer {
        val customerEntity = customerJpaRepository.save(CustomerEntity(
            id = customer.id!!.value,
            username = customer.username,
            firstName = customer.firstName,
            lastName = customer.lastName
        ))

        return Customer(
            customerId = CustomerId(customerEntity.id),
            username = customerEntity.username,
            firstName = customerEntity.firstName,
            lastName = customerEntity.lastName
        )
    }
}