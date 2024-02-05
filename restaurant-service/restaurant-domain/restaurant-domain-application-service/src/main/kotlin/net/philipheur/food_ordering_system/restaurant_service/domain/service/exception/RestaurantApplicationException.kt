package net.philipheur.food_ordering_system.restaurant_service.domain.service.exception

import net.philipheur.food_ordering_system.common.domain.exception.DomainException

class RestaurantApplicationException
    : DomainException {
    constructor(
        message: String?
    ) : super(message)

    constructor(
        message: String?, cause: Throwable?
    ) : super(message, cause)
}