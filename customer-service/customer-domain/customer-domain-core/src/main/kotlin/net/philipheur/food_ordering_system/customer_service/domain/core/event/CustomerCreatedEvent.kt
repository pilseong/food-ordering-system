package net.philipheur.food_ordering_system.customer_service.domain.core.event

import net.philipheur.food_ordering_system.common.domain.event.DomainEvent
import net.philipheur.food_ordering_system.customer_service.domain.core.entity.Customer
import java.time.ZonedDateTime

class CustomerCreatedEvent(
    val customer: Customer,
    val createdAt: ZonedDateTime,
) : DomainEvent<Customer>