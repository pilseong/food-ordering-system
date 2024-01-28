package net.philipheur.food_ordering_system.order_service.domain.application_service.ports.output.repository

import net.philipheur.food_ordering_system.order_service.domain.core.entity.Restaurant
import java.util.*

interface RestaurantRepository {
    // 식당 id, 제품 id 만 있는 식당 객체에 해당 정보를 채워주는 함수
    fun fetchRestaurantInformation(restaurant: Restaurant): Restaurant?
}