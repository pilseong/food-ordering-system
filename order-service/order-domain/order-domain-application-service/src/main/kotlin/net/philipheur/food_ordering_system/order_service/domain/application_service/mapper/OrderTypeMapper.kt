package net.philipheur.food_ordering_system.order_service.domain.application_service.mapper

import net.philipheur.food_ordering_system.common.domain.valueobject.CustomerId
import net.philipheur.food_ordering_system.common.domain.valueobject.Money
import net.philipheur.food_ordering_system.common.domain.valueobject.ProductId
import net.philipheur.food_ordering_system.common.domain.valueobject.RestaurantId
import net.philipheur.food_ordering_system.order_service.domain.application_service.dto.create.CreateOrderCommand
import net.philipheur.food_ordering_system.order_service.domain.core.entity.Order
import net.philipheur.food_ordering_system.order_service.domain.core.entity.OrderItem
import net.philipheur.food_ordering_system.order_service.domain.core.entity.Product
import net.philipheur.food_ordering_system.order_service.domain.core.entity.Restaurant
import net.philipheur.food_ordering_system.order_service.domain.core.valueobject.OrderAddress
import org.springframework.stereotype.Component
import java.util.*

// 타입 변환을 위한 클래스
// 웬만큼 복잡하지 않는 한에는 추가하지 않는다. 바로 생성하는 것이 가독성이 높다.
@Component
open class OrderTypeMapper {

    // 요청 정보에는 식당 id 와 OrderItemDto 의 id 필드만 채워져 있다.
    // 이것으로 식당 객체를 생성한다.
    fun createOrderCommandToRestaurant(
        command: CreateOrderCommand
    ) = Restaurant(
        restaurantId = RestaurantId(command.restaurantId),
        products = command.items
            .map { item -> Product(ProductId(item.productId)) }
            .toList(),
    )

    // OrderItem 의 orderId와 id는 null 로 설정된다.
    fun createOrderCommandToOrder(
        command: CreateOrderCommand
    ) = Order(
        restaurantId = RestaurantId(command.restaurantId),
        customerId = CustomerId(command.customerId),
        orderAddress = OrderAddress(
            id = UUID.randomUUID(),
            street = command.deliveryAddress.street,
            postalCode = command.deliveryAddress.postalCode,
            city = command.deliveryAddress.city
        ),
        items = command.items.map { orderItemDto ->
            OrderItem(
                product = Product(
                    productId = ProductId(orderItemDto.productId)
                ),
                quantity = orderItemDto.quantity,
                price = Money(orderItemDto.price),
                subTotal = Money(orderItemDto.subTotal)
            )
        },
        price = Money(command.price)
    )
}