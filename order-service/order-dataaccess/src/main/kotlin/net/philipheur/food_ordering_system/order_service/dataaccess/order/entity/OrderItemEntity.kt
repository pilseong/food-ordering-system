package net.philipheur.food_ordering_system.order_service.dataaccess.order.entity

import jakarta.persistence.CascadeType
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.IdClass
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import java.math.BigDecimal
import java.util.UUID

@IdClass(OrderItemEntityId::class)
@Entity
@Table(name = "order_items")
class OrderItemEntity(
    @Id
    var id: Long,
    @Id
    @ManyToOne(cascade = [CascadeType.ALL])
    @JoinColumn(name = "order_id")
    var order: OrderEntity? = null,

    var productId: UUID,
    var price: BigDecimal,
    var quantity: Int,
    var subTotal: BigDecimal
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as OrderItemEntity

        if (id != other.id) return false
        if (order != other.order) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + order.hashCode()
        return result
    }
}