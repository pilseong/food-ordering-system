package net.philipheur.food_ordering_system.common.domain.event

interface DomainEvent<T> {
    fun fire()
}