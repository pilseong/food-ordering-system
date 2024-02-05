package net.philipheur.food_ordering_system.common.dataaccess.restaurant.repository

import net.philipheur.food_ordering_system.common.dataaccess.restaurant.entity.RestaurantEntity
import net.philipheur.food_ordering_system.common.dataaccess.restaurant.entity.RestaurantEntityId
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface RestaurantJpaRepository :
    JpaRepository<RestaurantEntity, RestaurantEntityId> {

    fun findByRestaurantIdAndProductIdIn(
        restaurantId: UUID,
        productIds: List<UUID>
    ): List<RestaurantEntity>
}