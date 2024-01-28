package net.philipheur.food_ordering_system.order_service.dataaccess.order.entity

import jakarta.persistence.CascadeType
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.OneToOne
import jakarta.persistence.Table
import lombok.NoArgsConstructor
import java.util.UUID

@Entity
@Table(name ="order_address")
class OrderAddressEntity(
    @Id
    var id: UUID,

    var street: String,
    var postalCode: String,
    var city: String,

    @OneToOne(cascade = [CascadeType.ALL])
    @JoinColumn(name = "order_id")
    var order: OrderEntity? = null,



) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as OrderAddressEntity

        return id == other.id
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }
}