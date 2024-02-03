package net.philipheur.food_ordering_system.infrastructure.saga

interface SagaStep<DATA> {
    fun process(data: DATA)
    fun rollback(data: DATA)
}