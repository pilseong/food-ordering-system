package net.philipheur.food_ordering_system.restaurant_service.domain.core.entity

import net.philipheur.food_ordering_system.common.domain.entity.AggregateRoot
import net.philipheur.food_ordering_system.common.domain.valueobject.OrderApprovalStatus
import net.philipheur.food_ordering_system.common.domain.valueobject.OrderStatus
import net.philipheur.food_ordering_system.common.domain.valueobject.RestaurantId
import net.philipheur.food_ordering_system.restaurant_service.domain.core.valueobject.OrderApprovalId
import java.util.*

class Restaurant(
    val restaurantId: RestaurantId,
    var orderApproval: OrderApproval? = null,
    var active: Boolean? = null,
    val orderDetail: OrderDetail
) : AggregateRoot<RestaurantId>(restaurantId) {

    fun validateOrder(failureMessages: MutableList<String>) {

        // 주문이 결제되어 있는지 확인
        if (orderDetail.orderStatus != OrderStatus.PAID) {
            failureMessages.add(
                "Payment is not completed " +
                        "for order id: ${orderDetail.id}"
            )
        }

        // 식당이 메뉴가 주문할 수 있는지 확인한다.
        val totalAmount = orderDetail.products.map {
            if (!it.available!!) {
                failureMessages.add(
                    "Product with id: ${it.id} is not available"
                )
            }
            it.price!!.multiply(it.quantity!!)
        }.reduce { sum, value ->
            sum.add(value)
        }

        // 각 메뉴의 합의 전체 합과 같은지 검증
        if (totalAmount != orderDetail.totalAmount) {
            failureMessages.add(
                "Price total is not correct for " +
                        "oder id: ${orderDetail.id}"
            )
        }
    }

    fun createOrderApproval(
        orderApprovalStatus: OrderApprovalStatus
    ) {
        orderApproval = OrderApproval(
            orderApprovalId = OrderApprovalId(UUID.randomUUID()),
            restaurantId = restaurantId,
            orderId = orderDetail.id!!,
            orderApprovalStatus = orderApprovalStatus
        )
    }
}