package net.philipheur.food_ordering_system.restaurant_service.domain.service

import net.philipheur.food_ordering_system.common.domain.valueobject.Money
import net.philipheur.food_ordering_system.common.domain.valueobject.OrderId
import net.philipheur.food_ordering_system.common.domain.valueobject.OrderStatus
import net.philipheur.food_ordering_system.common.domain.valueobject.RestaurantId
import net.philipheur.food_ordering_system.common.utils.logging.LoggerDelegator
import net.philipheur.food_ordering_system.restaurant_service.domain.core.RestaurantDomainService
import net.philipheur.food_ordering_system.restaurant_service.domain.core.entity.OrderDetail
import net.philipheur.food_ordering_system.restaurant_service.domain.core.entity.Product
import net.philipheur.food_ordering_system.restaurant_service.domain.core.entity.Restaurant
import net.philipheur.food_ordering_system.restaurant_service.domain.core.event.OrderApprovalEvent
import net.philipheur.food_ordering_system.restaurant_service.domain.core.exception.RestaurantNotFoundException
import net.philipheur.food_ordering_system.restaurant_service.domain.service.dto.RestaurantApprovalRequest
import net.philipheur.food_ordering_system.restaurant_service.domain.service.ports.output.publisher.OrderApprovedMessagePublisher
import net.philipheur.food_ordering_system.restaurant_service.domain.service.ports.output.publisher.OrderRejectedMessagePublisher
import net.philipheur.food_ordering_system.restaurant_service.domain.service.ports.output.repository.OrderApprovalRepository
import net.philipheur.food_ordering_system.restaurant_service.domain.service.ports.output.repository.RestaurantRepository
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
open class RestaurantApprovalRequestHelper(
    private val restaurantDomainService: RestaurantDomainService,
    private val restaurantRepository: RestaurantRepository,
    private val orderApprovalRepository: OrderApprovalRepository,
    private val orderApprovedMessagePublisher: OrderApprovedMessagePublisher,
    private val orderRejectedMessagePublisher: OrderRejectedMessagePublisher
) {

    private val log by LoggerDelegator()

    @Transactional
    open fun persistOrderApproval(
        restaurantApprovalRequest
        : RestaurantApprovalRequest
    ): OrderApprovalEvent {
        log.info(
            "Processing restaurant approval for " +
                    "order id: ${restaurantApprovalRequest.orderId} "
        )

        val failureMessages = mutableListOf<String>()

        // 식당 검색 하여 정보를 채운다
        val restaurant = findRestaurant(restaurantApprovalRequest)

        // 주문 정보를 검증한다.
        val orderApprovalEvent = restaurantDomainService.validateOrder(
            restaurant = restaurant,
            failureMessages = failureMessages,
            orderApprovedEventDomainEventPublisher = orderApprovedMessagePublisher,
            orderRejectedEventDomainEventPublisher = orderRejectedMessagePublisher
        )

        // 주문 처리 결과를 저장
        orderApprovalRepository.save(restaurant.orderApproval!!)

        return orderApprovalEvent!!
    }

    private fun findRestaurant(
        restaurantApprovalRequest:
        RestaurantApprovalRequest
    ): Restaurant {

        // 요청 객체 정보를 가지고 식당 객체를 생성
        val restaurant = Restaurant(
            restaurantId = RestaurantId(restaurantApprovalRequest.restaurantId),
            orderDetail = OrderDetail(
                orderId = OrderId(restaurantApprovalRequest.orderId),
                products = restaurantApprovalRequest.products.map {
                    Product(
                        productId = it.id!!,
                        quantity = it.quantity,
                    )
                },
                totalAmount = Money(restaurantApprovalRequest.price),
                orderStatus = OrderStatus.valueOf(
                    restaurantApprovalRequest.restaurantOrderStatus.name
                )
            )
        )

        // 식당 정보를 데이터베이스에서 검색
        val restaurantEntity = restaurantRepository
            .findRestaurantInformation(restaurant)

        if (restaurantEntity == null) {
            log.error("Restaurant id: ${restaurant.id} not found")
            throw RestaurantNotFoundException(
                "Restaurant with " +
                        "id ${restaurant.id} not found"
            )
        }

        // 읽어온 식당 정보를 식당 객체에 업데이트 한다.ㅇ
        restaurant.active = restaurantEntity.active
        restaurant.orderDetail.products.forEach { product ->
            restaurantEntity.orderDetail.products.forEach { entity ->
                if (product.id == entity.id) {
                    product.updateWithConfirmedNamePriceAndAvailability(
                        entity.name!!,
                        entity.price!!,
                        entity.available!!
                    )
                }
            }
        }

        return restaurant
    }
}