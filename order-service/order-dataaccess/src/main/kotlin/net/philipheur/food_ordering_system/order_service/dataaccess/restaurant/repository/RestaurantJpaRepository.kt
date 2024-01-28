package net.philipheur.food_ordering_system.order_service.dataaccess.restaurant.repository

import net.philipheur.food_ordering_system.order_service.dataaccess.restaurant.entity.RestaurantEntity
import net.philipheur.food_ordering_system.order_service.dataaccess.restaurant.entity.RestaurantEntityId
import net.philipheur.food_ordering_system.order_service.domain.core.entity.Restaurant
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface RestaurantJpaRepository :
    JpaRepository<RestaurantEntity, RestaurantEntityId> {

    fun findByRestaurantIdAndProductIdIn(
        restaurantId: UUID,
        productIds: List<UUID>
    ): List<RestaurantEntity>
}