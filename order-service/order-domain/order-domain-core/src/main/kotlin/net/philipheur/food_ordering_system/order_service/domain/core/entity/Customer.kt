package net.philipheur.food_ordering_system.order_service.domain.core.entity

import net.philipheur.food_ordering_system.common.domain.entity.AggregateRoot
import net.philipheur.food_ordering_system.common.domain.valueobject.CustomerId

class Customer(
    customerId: CustomerId,
    val username: String,
    val firstName: String,
    val lastName: String
) : AggregateRoot<CustomerId>(customerId)