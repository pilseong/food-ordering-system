package net.philipheur.food_ordering_system.order_service.domain.application_service

import net.philipheur.food_ordering_system.order_service.domain.application_service.ports.output.message.publisher.payment.OrderCancelledPaymentRequestMessagePublisher
import net.philipheur.food_ordering_system.order_service.domain.application_service.ports.output.message.publisher.payment.OrderCreatedPaymentRequestMessagePublisher
import net.philipheur.food_ordering_system.order_service.domain.application_service.ports.output.message.publisher.payment.OrderPaidRestaurantRequestMessagePublisher
import net.philipheur.food_ordering_system.order_service.domain.application_service.ports.output.message.publisher.payment.PaymentRequestMessagePublisher
import net.philipheur.food_ordering_system.order_service.domain.application_service.ports.output.message.publisher.restaurantapproval.RestaurantApprovalRequestMessagePublisher
import net.philipheur.food_ordering_system.order_service.domain.application_service.ports.output.repository.*
import net.philipheur.food_ordering_system.order_service.domain.core.OrderDomainService
import net.philipheur.food_ordering_system.order_service.domain.core.OrderDomainServiceImpl
import org.mockito.Mockito
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.Bean


@SpringBootApplication(
    scanBasePackages = ["net.philipheur.food_ordering_system.order_service.domain"]
)
open class OrderTestConfiguration {

    // 외부 의존성 - 코어 로직이 아닌 부분들은 외부에서 받아야 하기 때문에 Mock 으로 생성

    // messaging publishers
    @Bean
    open fun orderCreatedPaymentRequestMessagePublisher(
    ): OrderCreatedPaymentRequestMessagePublisher {
        return Mockito.mock(OrderCreatedPaymentRequestMessagePublisher::class.java)
    }

    @Bean
    open fun orderPaidRestaurantRequestMessagePublisher(
    ): OrderPaidRestaurantRequestMessagePublisher {
        return Mockito.mock(OrderPaidRestaurantRequestMessagePublisher::class.java)
    }

    @Bean
    open fun orderCancelledPaymentRequestMessagePublisher(
    ): OrderCancelledPaymentRequestMessagePublisher {
        return Mockito.mock(OrderCancelledPaymentRequestMessagePublisher::class.java)
    }

    @Bean
    open fun PaymentRequestMessagePublisher(
    ): PaymentRequestMessagePublisher {
        return Mockito.mock(PaymentRequestMessagePublisher::class.java)
    }

    @Bean
    open fun restaurantApprovalRequestMessagePublisher()
            : RestaurantApprovalRequestMessagePublisher {
        return Mockito.mock(RestaurantApprovalRequestMessagePublisher::class.java)
    }

    // data access layer adapters
    @Bean
    open fun orderRepository(): OrderRepository {
        return Mockito.mock(OrderRepository::class.java)
    }

    @Bean
    open fun customerRepository(): CustomerRepository {
        return Mockito.mock(CustomerRepository::class.java)
    }

    @Bean
    open fun restaurantRepository(): RestaurantRepository {
        return Mockito.mock(RestaurantRepository::class.java)
    }

    @Bean
    open fun paymentOutboxRepository(): PaymentOutboxRepository {
        return Mockito.mock(PaymentOutboxRepository::class.java)
    }

    @Bean
    open fun approvalOutboxRepository(): ApprovalOutboxRepository {
        return Mockito.mock(ApprovalOutboxRepository::class.java)
    }

    @Bean
    open fun orderDomainService(): OrderDomainService {
        return OrderDomainServiceImpl()
    }

}