package net.philipheur.food_ordering_system.customer_service.domain.application_service.ports.output.message.publisher

import net.philipheur.food_ordering_system.customer_service.domain.core.event.CustomerCreatedEvent

interface CustomerMessagePublisher {
    fun publish(customerCreatedEvent: CustomerCreatedEvent)
}