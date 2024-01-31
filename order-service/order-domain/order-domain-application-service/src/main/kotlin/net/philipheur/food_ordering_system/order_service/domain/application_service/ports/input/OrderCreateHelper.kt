package net.philipheur.food_ordering_system.order_service.domain.application_service.ports.input

import net.philipheur.food_ordering_system.common.utils.logging.LoggerDelegator
import net.philipheur.food_ordering_system.order_service.domain.application_service.dto.create.CreateOrderCommand
import net.philipheur.food_ordering_system.order_service.domain.application_service.mapper.OrderTypeMapper
import net.philipheur.food_ordering_system.order_service.domain.application_service.ports.output.repository.CustomerRepository
import net.philipheur.food_ordering_system.order_service.domain.application_service.ports.output.repository.OrderRepository
import net.philipheur.food_ordering_system.order_service.domain.application_service.ports.output.repository.RestaurantRepository
import net.philipheur.food_ordering_system.order_service.domain.core.OrderDomainService
import net.philipheur.food_ordering_system.order_service.domain.core.entity.Customer
import net.philipheur.food_ordering_system.order_service.domain.core.entity.Order
import net.philipheur.food_ordering_system.order_service.domain.core.entity.Restaurant
import net.philipheur.food_ordering_system.order_service.domain.core.event.OrderCreatedEvent
import net.philipheur.food_ordering_system.order_service.domain.core.exception.OrderDomainException
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.util.*


@Component
open class OrderCreateHelper(
    private val orderTypeMapper: OrderTypeMapper,
    private val customerRepository: CustomerRepository,
    private val restaurantRepository: RestaurantRepository,
    private val orderRepository: OrderRepository,
    private val orderDomainService: OrderDomainService,
) {

    private val log by LoggerDelegator()

    // 주문을 저장하고 이벤트 객체에 넣어서 반환

    @Transactional
    open fun persistOrder(
        createOrderCommand: CreateOrderCommand
    ): OrderCreatedEvent {
        // 고객 정보 확인
        checkCustomerExistsInDB(
            createOrderCommand.customerId
        )

        // 식당 객체를 생성하고 정보를 채운다.
        val restaurant: Restaurant =
            fillRestaurantInformation(createOrderCommand)

        // 주문 개체 생성
        val order = orderTypeMapper
            .crateOrderCommandToOrder(createOrderCommand)

        // 주문 객체를 초기화 한 후 주문 생성 event 생성
        val orderCreatedEvent: OrderCreatedEvent = orderDomainService
            .validateAndInitializeOrder(order, restaurant)

        // 주문 저장
        saveOrder(order)

        log.info("Order is created with id: " +
                "${orderCreatedEvent.order.id!!.value}")

        return orderCreatedEvent
    }

    private fun saveOrder(order: Order) {
        val orderResult = orderRepository.save(order)
//        if (orderResult == null) {
//            log.error("Could not save order")
//            throw OrderDomainException("Could not save order")
//        }

        log.info("Order is saved with id: " +
                "${orderResult.id!!.value}")
    }

    // 식당 id와 제품 id로 식당객체를 생성하고,
    // 데이터 베이스에서 해당 식당이 존재하는지 확인 후 있으면 식당 정보를 반환한다.
    private fun fillRestaurantInformation(
        createOrderCommand: CreateOrderCommand
    ): Restaurant {
        val restaurant = orderTypeMapper.createOrderCommandToRestaurant(createOrderCommand)
        val result = restaurantRepository.fetchRestaurantInformation(restaurant)
        if (result == null) {
            log.warn(
                "Could not find restaurant with restaurant id: " +
                        "${createOrderCommand.restaurantId}"
            )

            throw OrderDomainException(
                "Could not find restaurant with restaurant id: " +
                        "${createOrderCommand.restaurantId}"
            )
        }
        return result
    }

    // DB 애서 고객이 존재하는지 확인
    private fun checkCustomerExistsInDB(customerId: UUID) {
        val customer: Customer? = customerRepository.findCustomer(customerId)
        if (customer == null) {
            log.warn("Could not find customer with customer id: $customerId")
            throw OrderDomainException("Could not find customer with " +
                    "customer id: $customerId")
        }
    }
}