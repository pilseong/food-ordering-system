package net.philipheur.food_ordering_system.payment_service.dataaccess.outbox.order.exception

class OrderOutboxNotFoundException(
    message: String
) : RuntimeException(message)
