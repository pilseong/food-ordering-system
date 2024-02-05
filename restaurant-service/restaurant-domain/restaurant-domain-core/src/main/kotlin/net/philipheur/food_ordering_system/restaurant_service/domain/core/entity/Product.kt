package net.philipheur.food_ordering_system.restaurant_service.domain.core.entity

import net.philipheur.food_ordering_system.common.domain.entity.BaseEntity
import net.philipheur.food_ordering_system.common.domain.valueobject.Money
import net.philipheur.food_ordering_system.common.domain.valueobject.ProductId

class Product(
    productId: ProductId,
    var name: String? = null,
    var price: Money? = null,
    val quantity: Int? = null,
    var available: Boolean? = null
) : BaseEntity<ProductId>(productId) {
    fun updateWithConfirmedNamePriceAndAvailability(
        name: String,
        price: Money,
        available: Boolean
    ) {
        this.name = name
        this.price = price
        this.available = available
    }
}