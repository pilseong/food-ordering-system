package net.philipheur.food_ordering_system.customer_service.domain.core.exception

import net.philipheur.food_ordering_system.common.domain.exception.DomainException

class CustomerDomainException : DomainException {
    constructor(
        message: String
    ) : super(message)

    constructor(
        message: String, cause: Throwable
    ) : super(message, cause)
}
