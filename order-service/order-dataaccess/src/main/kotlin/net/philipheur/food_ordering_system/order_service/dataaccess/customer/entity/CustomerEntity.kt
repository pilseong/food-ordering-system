package net.philipheur.food_ordering_system.order_service.dataaccess.customer.entity

import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.util.*

@Entity
@Table(name = "customers")
class CustomerEntity(
    @Id
    var id: UUID,

    var username: String,
    var firstName: String,
    var lastName: String
)