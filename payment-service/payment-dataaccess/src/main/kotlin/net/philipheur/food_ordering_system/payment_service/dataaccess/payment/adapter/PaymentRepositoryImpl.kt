package net.philipheur.food_ordering_system.payment_service.dataaccess.payment.adapter

import net.philipheur.food_ordering_system.common.domain.valueobject.CustomerId
import net.philipheur.food_ordering_system.common.domain.valueobject.Money
import net.philipheur.food_ordering_system.common.domain.valueobject.OrderId
import net.philipheur.food_ordering_system.common.domain.valueobject.PaymentId
import net.philipheur.food_ordering_system.payment_service.dataaccess.payment.entity.PaymentEntity
import net.philipheur.food_ordering_system.payment_service.dataaccess.payment.repository.PaymentJpaRepository
import net.philipheur.food_ordering_system.payment_service.domain.core.entity.Payment
import net.philipheur.food_ordering_system.payment_service.domain.service.ports.output.repository.PaymentRepository
import org.springframework.stereotype.Component
import java.util.*

@Component
open class PaymentRepositoryImpl(
    private val paymentJpaRepository: PaymentJpaRepository
) : PaymentRepository {
    override fun save(payment: Payment): Payment {
        val paymentEntity = paymentJpaRepository
            .save(
                PaymentEntity(
                    id = payment.id!!.value,
                    customerId = payment.customerId.value,
                    orderId = payment.orderId.value,
                    price = payment.price.amount,
                    status = payment.paymentStatus!!,
                    createdAt = payment.createdAt!!
                )
            )

        return Payment(
            paymentId = PaymentId(paymentEntity.id),
            customerId = CustomerId(paymentEntity.customerId),
            orderId = OrderId(paymentEntity.orderId),
            price = Money(paymentEntity.price),
            paymentStatus = paymentEntity.status,
            createdAt = paymentEntity.createdAt
        )
    }

    override fun findByOrderId(orderId: UUID): Payment? {
        return paymentJpaRepository.findByOrderId(orderId)
            ?.let { entity ->
                Payment(
                    paymentId = PaymentId(entity.id),
                    customerId = CustomerId(entity.customerId),
                    orderId = OrderId(entity.orderId),
                    price = Money(entity.price),
                    paymentStatus = entity.status,
                    createdAt = entity.createdAt
                )
            }
    }
}