package net.philipheur.food_ordering_system.order_service.dataaccess.customer.entity

import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.util.UUID

@Entity
@Table(name = "order_customer_m_view", schema = "customer")
class CustomerEntity(
    @Id
    var id: UUID
) {
}