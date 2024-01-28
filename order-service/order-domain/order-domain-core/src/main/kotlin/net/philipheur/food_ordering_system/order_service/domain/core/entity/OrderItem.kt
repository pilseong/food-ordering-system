package net.philipheur.food_ordering_system.order_service.domain.core.entity

import net.philipheur.food_ordering_system.common.domain.entity.BaseEntity
import net.philipheur.food_ordering_system.common.domain.valueobject.Money
import net.philipheur.food_ordering_system.common.domain.valueobject.OrderId
import net.philipheur.food_ordering_system.order_service.domain.core.valueobject.OrderItemId

// order id와 order item id는 객체가 생성된 이후
// 나중에 설정된다.
class OrderItem(
    orderItemId: OrderItemId? = null,
    var orderId: OrderId? = null,
    val product: Product,
    val quantity: Int,
    val price: Money,
    val subTotal: Money
) : BaseEntity<OrderItemId>(orderItemId) {

    // 가격이 0보다 큰지, 하나의 주문 item 이 해당 제품의 가격과 같은지,
    // 단위 가격 * 수량이 subTotal 과 값이 동일한지.
    fun isPriceValid() = price.isGreaterThanZero() &&
            price == product.price &&
            price.multiply(quantity) == subTotal

    // 객체의 id 와 주문 id를 할당한다.
    fun initializeOrderItem(
        orderId: OrderId,
        orderItemId: OrderItemId
    ) {
        this.orderId = orderId
        super.id = orderItemId
    }
}