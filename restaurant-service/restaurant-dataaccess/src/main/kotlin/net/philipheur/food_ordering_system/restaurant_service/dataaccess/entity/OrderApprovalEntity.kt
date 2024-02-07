package net.philipheur.food_ordering_system.restaurant_service.dataaccess.entity

import jakarta.persistence.*
import net.philipheur.food_ordering_system.common.domain.valueobject.OrderApprovalStatus

import java.util.*

@Entity
@Table(name = "order_approval", schema = "restaurant")
class OrderApprovalEntity(
    @Id var id: UUID,
    var restaurantId: UUID,
    var orderId: UUID,
    @Enumerated(EnumType.STRING)
    var status: OrderApprovalStatus
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as OrderApprovalEntity

        return id == other.id
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }
}