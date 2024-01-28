package net.philipheur.food_ordering_system.order_service.domain.core.entity

import net.philipheur.food_ordering_system.common.domain.entity.BaseEntity
import net.philipheur.food_ordering_system.common.domain.valueobject.Money
import net.philipheur.food_ordering_system.common.domain.valueobject.ProductId

class Product(
    productId: ProductId,
    var name: String? = null,
    var price: Money? = null
) : BaseEntity<ProductId>(productId) {
}