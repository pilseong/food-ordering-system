package net.philipheur.food_ordering_system.restaurant_service.dataaccess.repository

import net.philipheur.food_ordering_system.restaurant_service.dataaccess.entity.OrderApprovalEntity
import org.hibernate.validator.constraints.UUID
import org.springframework.data.jpa.repository.JpaRepository

interface OrderApprovalJpaRepository: JpaRepository<OrderApprovalEntity, UUID> {

}