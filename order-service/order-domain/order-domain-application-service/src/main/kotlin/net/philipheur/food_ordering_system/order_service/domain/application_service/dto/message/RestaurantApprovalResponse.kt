package net.philipheur.food_ordering_system.order_service.domain.application_service.dto.message

import net.philipheur.food_ordering_system.common.domain.valueobject.OrderApprovalStatus
import java.time.Instant
import java.util.*

// 식당에서 부터 수신 받는 주문 수락 정보
data class RestaurantApprovalResponse(
    val id: UUID,
    val sagaId: UUID,
    val orderId: UUID,
    val restaurantId: UUID,
    val createdAt: Instant,
    val orderApprovalStatus: OrderApprovalStatus,
    val failureMessages: List<String>,
)