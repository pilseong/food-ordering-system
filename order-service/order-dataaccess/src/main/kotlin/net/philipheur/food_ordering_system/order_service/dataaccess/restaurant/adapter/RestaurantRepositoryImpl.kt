package net.philipheur.food_ordering_system.order_service.dataaccess.restaurant.adapter

import net.philipheur.food_ordering_system.common.domain.valueobject.Money
import net.philipheur.food_ordering_system.common.domain.valueobject.ProductId
import net.philipheur.food_ordering_system.common.domain.valueobject.RestaurantId
import net.philipheur.food_ordering_system.order_service.dataaccess.restaurant.repository.RestaurantJpaRepository
import net.philipheur.food_ordering_system.order_service.domain.application_service.ports.output.repository.RestaurantRepository
import net.philipheur.food_ordering_system.order_service.domain.core.entity.Product
import net.philipheur.food_ordering_system.order_service.domain.core.entity.Restaurant
import org.springframework.stereotype.Component

@Component
open class RestaurantRepositoryImpl(
    private val restaurantJpaRepository: RestaurantJpaRepository
) : RestaurantRepository {
    override fun fetchRestaurantInformation(restaurant: Restaurant): Restaurant? {

        val productIds = restaurant.products.map { it.id!!.value }

        val restaurantEntity =
            restaurantJpaRepository.findByRestaurantIdAndProductIdIn(
                restaurantId = restaurant.id!!.value,
                productIds = productIds
            )

        return Restaurant(
            restaurantId = RestaurantId(restaurantEntity[0].restaurantId),
            products = restaurantEntity.map {
                Product(
                    productId = ProductId(it.productId),
                    name = it.restaurantName,
                    price = Money(it.productPrice)
                )
            },
            active = restaurantEntity[0].restaurantActive
        )
    }
}