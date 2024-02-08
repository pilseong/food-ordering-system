package net.philipheur.food_ordering_system.order_service.domain.core.exception

import net.philipheur.food_ordering_system.common.domain.exception.DomainException

class OrderDomainException : DomainException {
    constructor(
        message: String
    ) : super(message)

    constructor(
        message: String, cause: Throwable
    ) : super(message, cause)
}
