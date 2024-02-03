package net.philipheur.food_ordering_system.common.domain.event

class EmptyEvent : DomainEvent<Void> {
    companion object {
        val INSTANCE = EmptyEvent()
    }
}