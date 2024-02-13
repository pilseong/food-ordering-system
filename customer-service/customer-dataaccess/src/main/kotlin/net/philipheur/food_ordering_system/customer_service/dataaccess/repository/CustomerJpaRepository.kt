package net.philipheur.food_ordering_system.customer_service.dataaccess.repository

import net.philipheur.food_ordering_system.customer_service.dataaccess.entity.CustomerEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface CustomerJpaRepository: JpaRepository<CustomerEntity, UUID> {
}