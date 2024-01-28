package net.philipheur.food_ordering_system.order_service.dataaccess.order.entity

import jakarta.persistence.*
import net.philipheur.food_ordering_system.common.domain.valueobject.OrderStatus
import java.math.BigDecimal
import java.util.UUID

@Entity
@Table(name = "orders")
class OrderEntity(
    @Id
    var id: UUID,

    var customerId: UUID,
    var restaurantId: UUID,
    var trackingId: UUID,
    var price: BigDecimal,

    @Enumerated(EnumType.STRING)
    var orderStatus: OrderStatus,
    var failureMessages: String,

    @OneToOne(mappedBy = "order", cascade = [CascadeType.ALL])
    var address: OrderAddressEntity,

    @OneToMany(mappedBy = "order", cascade = [CascadeType.ALL])
    var items: List<OrderItemEntity>
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as OrderEntity

        return id == other.id
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }
}
