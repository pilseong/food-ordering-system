package net.philipheur.food_ordering_system.payment_service.domain.service.dto.request

import net.philipheur.food_ordering_system.common.domain.valueobject.PaymentOrderStatus
import java.math.BigDecimal
import java.time.Instant
import java.util.*

data class PaymentRequest(
    val id: UUID,
    val sagaId: UUID,
    val orderId: UUID,
    val customerId: UUID,
    val price: BigDecimal,
    val createdAt: Instant,
    var paymentOrderStatus: PaymentOrderStatus? = null
)