package net.philipheur.food_ordering_system.payment_service.dataaccess.payment.repository

import net.philipheur.food_ordering_system.payment_service.dataaccess.payment.entity.PaymentEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface PaymentJpaRepository : JpaRepository<PaymentEntity, UUID> {
    fun findByOrderId(orderId: UUID): PaymentEntity?
}