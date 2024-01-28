package net.philipheur.food_ordering_system.order_service.dataaccess.customer.repository

import net.philipheur.food_ordering_system.order_service.dataaccess.customer.entity.CustomerEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*


interface CustomerJpaRepository: JpaRepository<CustomerEntity, UUID>