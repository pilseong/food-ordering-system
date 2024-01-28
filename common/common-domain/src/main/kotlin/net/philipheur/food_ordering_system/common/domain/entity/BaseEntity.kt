package net.philipheur.food_ordering_system.common.domain.entity

abstract class BaseEntity<ID>(
    var id: ID?
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is BaseEntity<*>) return false

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        return id?.hashCode() ?: 0
    }


}
