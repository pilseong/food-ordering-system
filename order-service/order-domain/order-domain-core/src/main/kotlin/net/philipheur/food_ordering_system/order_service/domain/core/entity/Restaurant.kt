package net.philipheur.food_ordering_system.order_service.domain.core.entity

import net.philipheur.food_ordering_system.common.domain.entity.AggregateRoot
import net.philipheur.food_ordering_system.common.domain.valueobject.RestaurantId
class Restaurant(
    restaurantId: RestaurantId, // id에 null 을 허용하지 않는다.
    val products: List<Product>,
    var active: Boolean? = null
): AggregateRoot<RestaurantId>(restaurantId) {
}