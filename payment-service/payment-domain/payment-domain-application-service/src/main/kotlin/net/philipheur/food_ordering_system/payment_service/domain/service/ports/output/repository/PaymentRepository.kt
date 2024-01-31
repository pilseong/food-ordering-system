package net.philipheur.food_ordering_system.payment_service.domain.service.ports.output.repository

import net.philipheur.food_ordering_system.payment_service.domain.core.entity.Payment
import java.util.*

interface PaymentRepository {
    fun save(payment: Payment): Payment

    fun findByOrderId(orderId: UUID): Payment?

}