package net.philipheur.food_ordering_system.infrastructure.saga

enum class SagaStatus {
    STARTED, FAILED, SUCCEEDED, PROCESSING, COMPENSATING, COMPENSATED
}