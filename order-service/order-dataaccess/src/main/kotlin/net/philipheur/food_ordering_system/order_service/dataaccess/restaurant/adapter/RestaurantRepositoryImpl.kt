package net.philipheur.food_ordering_system.order_service.dataaccess.restaurant.adapter

import net.philipheur.food_ordering_system.common.dataaccess.restaurant.repository.RestaurantJpaRepository
import net.philipheur.food_ordering_system.common.domain.valueobject.Money
import net.philipheur.food_ordering_system.common.domain.valueobject.ProductId
import net.philipheur.food_ordering_system.common.domain.valueobject.RestaurantId
import net.philipheur.food_ordering_system.order_service.domain.application_service.ports.output.repository.RestaurantRepository
import net.philipheur.food_ordering_system.order_service.domain.core.entity.Product
import net.philipheur.food_ordering_system.order_service.domain.core.entity.Restaurant
import net.philipheur.food_ordering_system.order_service.domain.core.exception.OrderDomainException
import org.springframework.stereotype.Component

@Component
open class RestaurantRepositoryImpl(
    private val restaurantJpaRepository: RestaurantJpaRepository
) : RestaurantRepository {
    override fun fetchRestaurantInformation(restaurant: Restaurant): Restaurant? {

        val productIds = restaurant.products.map { it.id!!.value }

        val restaurantEntities =
            restaurantJpaRepository.findByRestaurantIdAndProductIdIn(
                restaurantId = restaurant.id!!.value,
                productIds = productIds
            )

        if (restaurantEntities.isEmpty()) {
            throw OrderDomainException("the restaurant with" +
                    " id ${restaurant.id!!.value} and " +
                    "products id with $productIds cannot be found")
        }

        return Restaurant(
            restaurantId = RestaurantId(restaurantEntities[0].restaurantId),
            products = restaurantEntities.map {
                Product(
                    productId = ProductId(it.productId),
                    name = it.productName,
                    price = Money(it.productPrice)
                )
            },
            active = restaurantEntities[0].restaurantActive
        )
    }
}