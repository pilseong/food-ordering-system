package net.philipheur.food_ordering_system.common.domain.event.publisher

import net.philipheur.food_ordering_system.common.domain.event.DomainEvent


interface DomainEventPublisher<in T> {
    fun publish(domainEvent: T)
}
