package net.philipheur.food_ordering_syustem.infrastructure.outbox

interface OutboxScheduler {
    fun processOutboxMessage()
}