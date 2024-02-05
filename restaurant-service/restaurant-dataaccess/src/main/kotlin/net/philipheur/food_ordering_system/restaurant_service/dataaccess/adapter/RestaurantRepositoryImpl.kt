package net.philipheur.food_ordering_system.restaurant_service.dataaccess.adapter

import net.philipheur.food_ordering_system.common.dataaccess.restaurant.exception.RestaurantDataAccessException
import net.philipheur.food_ordering_system.common.dataaccess.restaurant.repository.RestaurantJpaRepository
import net.philipheur.food_ordering_system.common.domain.valueobject.Money
import net.philipheur.food_ordering_system.common.domain.valueobject.ProductId
import net.philipheur.food_ordering_system.common.domain.valueobject.RestaurantId
import net.philipheur.food_ordering_system.restaurant_service.domain.core.entity.OrderDetail
import net.philipheur.food_ordering_system.restaurant_service.domain.core.entity.Product
import net.philipheur.food_ordering_system.restaurant_service.domain.core.entity.Restaurant
import net.philipheur.food_ordering_system.restaurant_service.domain.service.ports.output.repository.RestaurantRepository
import org.springframework.stereotype.Component

@Component
class RestaurantRepositoryImpl(
    private val restaurantJpaRepository: RestaurantJpaRepository,
) : RestaurantRepository {
    override fun findRestaurantInformation(
        restaurant: Restaurant
    ): Restaurant {
        val restaurantProductsIds = restaurant.orderDetail.products.map {
            it.id!!.value
        }

        val restaurantEntities = restaurantJpaRepository.findByRestaurantIdAndProductIdIn(
            restaurantId = restaurant.id!!.value,
            productIds = restaurantProductsIds
        )

        if (restaurantEntities.isEmpty()) {
            throw RestaurantDataAccessException(
                "No restaurants found"
            )
        }

        return Restaurant(
            restaurantId = RestaurantId(restaurantEntities[0].restaurantId),
            orderDetail = OrderDetail(
                products = restaurantEntities.map {
                    Product(
                        productId = ProductId(it.productId),
                        name = it.productName,
                        price = Money(it.productPrice),
                        available = it.productAvailable,
                    )
                }
            ),
            active = restaurantEntities[0].restaurantActive
        )
    }
}