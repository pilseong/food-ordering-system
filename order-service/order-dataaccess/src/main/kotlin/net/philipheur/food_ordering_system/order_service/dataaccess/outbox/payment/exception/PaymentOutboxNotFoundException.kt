package net.philipheur.food_ordering_system.order_service.dataaccess.outbox.payment.exception

class PaymentOutboxNotFoundException(
    message: String
) : RuntimeException(message)