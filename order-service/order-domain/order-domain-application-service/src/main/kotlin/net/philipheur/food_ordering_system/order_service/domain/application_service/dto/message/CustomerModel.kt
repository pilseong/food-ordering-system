package net.philipheur.food_ordering_system.order_service.domain.application_service.dto.message

data class CustomerModel(
    val id: String,
    val username: String,
    val firstName: String,
    val lastName: String
)