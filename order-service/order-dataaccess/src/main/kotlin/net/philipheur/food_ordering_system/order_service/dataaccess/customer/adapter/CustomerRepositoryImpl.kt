package net.philipheur.food_ordering_system.order_service.dataaccess.customer.adapter

import net.philipheur.food_ordering_system.common.domain.valueobject.CustomerId
import net.philipheur.food_ordering_system.order_service.dataaccess.customer.repository.CustomerJpaRepository
import net.philipheur.food_ordering_system.order_service.domain.application_service.ports.output.repository.CustomerRepository
import net.philipheur.food_ordering_system.order_service.domain.core.entity.Customer
import org.springframework.stereotype.Component
import org.springframework.stereotype.Repository
import java.util.*

@Component
open class CustomerRepositoryImpl(
    private val customerJpaRepository: CustomerJpaRepository
) : CustomerRepository {

    override fun findCustomer(customerId: UUID): Customer? {
        val result = customerJpaRepository.findById(customerId)

        return result.map {
            Customer(
                customerId = CustomerId(it.id)
            )
        }.orElse(null)
    }
}