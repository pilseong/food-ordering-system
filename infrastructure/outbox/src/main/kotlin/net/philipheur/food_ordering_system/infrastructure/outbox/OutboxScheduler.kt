package net.philipheur.food_ordering_system.infrastructure.outbox

interface OutboxScheduler {
    fun processOutboxMessage()
}