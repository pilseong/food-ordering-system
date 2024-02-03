package net.philipheur.food_ordering_system.order_service.domain.application_service

import net.philipheur.food_ordering_system.common.domain.valueobject.OrderStatus
import net.philipheur.food_ordering_system.common.domain.valueobject.PaymentStatus
import net.philipheur.food_ordering_system.common.utils.logging.LoggerDelegator
import net.philipheur.food_ordering_system.infrastructure.saga.SagaStatus
import org.springframework.stereotype.Component


@Component
class OrderSagaHelper {

    private val log by LoggerDelegator()

    fun orderStatusToSagaStatus(orderStatus: OrderStatus?): SagaStatus {
        val sagaStatus = when (orderStatus) {
            OrderStatus.PAID -> SagaStatus.PROCESSING
            OrderStatus.APPROVED -> SagaStatus.SUCCEEDED
            OrderStatus.CANCELLING -> SagaStatus.COMPENSATING
            OrderStatus.CANCELLED -> SagaStatus.COMPENSATED
            OrderStatus.PENDING -> SagaStatus.STARTED
            null -> SagaStatus.STARTED
        }
        return sagaStatus
    }

    fun getCurrentSagaStatus(paymentStatus: PaymentStatus): Array<SagaStatus> {
        return when (paymentStatus) {
            PaymentStatus.COMPLETED -> arrayOf(SagaStatus.STARTED)
            PaymentStatus.CANCELLED -> arrayOf(SagaStatus.PROCESSING)
            PaymentStatus.FAILED -> arrayOf(SagaStatus.STARTED, SagaStatus.PROCESSING)
        }
    }
}