package net.philipheur.food_ordering_system.payment_service.domain.core.exception

class PaymentDomainException: RuntimeException {
    constructor(
        message: String?
    ): super(message)

    constructor(
        message: String?,
        cause: Throwable?
    ): super(message, cause)
}