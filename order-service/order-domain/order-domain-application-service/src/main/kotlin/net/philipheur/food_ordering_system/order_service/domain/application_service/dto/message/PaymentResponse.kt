package net.philipheur.food_ordering_system.order_service.domain.application_service.dto.message

import net.philipheur.food_ordering_system.common.domain.valueobject.PaymentStatus
import java.math.BigDecimal
import java.time.Instant
import java.util.*


// payment 서비스로 부터 수신하는 결재 정보
data class PaymentResponse(
    val id: UUID,
    val sagaId: UUID,
    val orderId: UUID,
    val paymentId: UUID,
    val customerId: UUID,
    val price: BigDecimal,
    val createdAt: Instant,
    val paymentStatus: PaymentStatus,
    val failureMessages: List<String>,
)