package net.philipheur.food_ordering_system.order_service.dataaccess.order.mapper

import net.philipheur.food_ordering_system.common.domain.valueobject.*
import net.philipheur.food_ordering_system.order_service.dataaccess.order.entity.OrderAddressEntity
import net.philipheur.food_ordering_system.order_service.dataaccess.order.entity.OrderEntity
import net.philipheur.food_ordering_system.order_service.dataaccess.order.entity.OrderItemEntity
import net.philipheur.food_ordering_system.order_service.domain.core.entity.Order
import net.philipheur.food_ordering_system.order_service.domain.core.entity.OrderItem
import net.philipheur.food_ordering_system.order_service.domain.core.entity.Product
import net.philipheur.food_ordering_system.order_service.domain.core.valueobject.OrderAddress
import net.philipheur.food_ordering_system.order_service.domain.core.valueobject.OrderItemId
import net.philipheur.food_ordering_system.order_service.domain.core.valueobject.TrackingId
import org.springframework.stereotype.Component

@Component
class OrderDataAccessMapper {

    fun orderEntityToOrder(orderEntity: OrderEntity) =
        Order(
            orderId = OrderId(orderEntity.id),
            restaurantId = RestaurantId(orderEntity.restaurantId),
            customerId = CustomerId(orderEntity.customerId),
            orderAddress = OrderAddress(
                id = orderEntity.address.id,
                street = orderEntity.address.street,
                postalCode = orderEntity.address.postalCode,
                city = orderEntity.address.city
            ),
            items = orderEntity.items.map {
                OrderItem(
                    orderId = OrderId(it.order!!.id),
                    orderItemId = OrderItemId(it.id),
                    product = Product(
                        productId = ProductId(it.productId),
                    ),
                    quantity = it.quantity,
                    subTotal = Money(it.subTotal),
                    price = Money(it.price)
                )
            },
            price = Money(orderEntity.price),
            trackingId = TrackingId(orderEntity.trackingId),
            orderStatus = orderEntity.orderStatus,
            failureMessage = orderEntity.failureMessages
                .split(",").toMutableList()
        )

    fun orderToOrderEntity(order: Order): OrderEntity {
        val orderEntity = OrderEntity(
            id = order.id!!.value,
            customerId = order.customerId.value,
            restaurantId = order.restaurantId.value,
            trackingId = order.trackingId!!.value,
            price = order.price.amount,
            orderStatus = order.orderStatus!!,
            failureMessages = order.failureMessage?.joinToString() ?: "",
            address = OrderAddressEntity(
                id = order.orderAddress.id,
                street = order.orderAddress.street,
                postalCode = order.orderAddress.postalCode,
                city = order.orderAddress.city
            ),
            items = order.items.map {
                OrderItemEntity(
                    id = it.id!!.value,
                    productId = it.product.id!!.value,
                    price = it.price.amount,
                    quantity = it.quantity,
                    subTotal = it.subTotal.amount
                )
            }
        )
        orderEntity.address.order = orderEntity
        orderEntity.items.forEach {
            it.order = orderEntity
        }

        return orderEntity
    }

}