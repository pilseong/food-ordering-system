package net.philipheur.food_ordering_system.common.domain.entity

abstract class AggregateRoot<ID>(
    id: ID?
) : BaseEntity<ID>(id)
