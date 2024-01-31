package net.philipheur.food_ordering_system.payment_service.domain.service

import net.philipheur.food_ordering_system.common.domain.event.DomainEvent
import net.philipheur.food_ordering_system.common.domain.event.publisher.DomainEventPublisher
import net.philipheur.food_ordering_system.common.domain.valueobject.CustomerId
import net.philipheur.food_ordering_system.common.domain.valueobject.Money
import net.philipheur.food_ordering_system.common.domain.valueobject.OrderId
import net.philipheur.food_ordering_system.common.utils.logging.LoggerDelegator
import net.philipheur.food_ordering_system.payment_service.domain.core.PaymentDomainService
import net.philipheur.food_ordering_system.payment_service.domain.core.entity.CreditEntry
import net.philipheur.food_ordering_system.payment_service.domain.core.entity.CreditHistory
import net.philipheur.food_ordering_system.payment_service.domain.core.entity.Payment
import net.philipheur.food_ordering_system.payment_service.domain.core.event.PaymentEvent
import net.philipheur.food_ordering_system.payment_service.domain.service.dto.request.PaymentRequest
import net.philipheur.food_ordering_system.payment_service.domain.service.exception.PaymentApplicationServiceException
import net.philipheur.food_ordering_system.payment_service.domain.service.ports.output.message.publisher.PaymentCancelledMessagePublisher
import net.philipheur.food_ordering_system.payment_service.domain.service.ports.output.message.publisher.PaymentCompletedMessagePublisher
import net.philipheur.food_ordering_system.payment_service.domain.service.ports.output.message.publisher.PaymentFailedMessagePublisher
import net.philipheur.food_ordering_system.payment_service.domain.service.ports.output.repository.CreditEntityRepository
import net.philipheur.food_ordering_system.payment_service.domain.service.ports.output.repository.CreditHistoryRepository
import net.philipheur.food_ordering_system.payment_service.domain.service.ports.output.repository.PaymentRepository
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Suppress("UNCHECKED_CAST")
@Component
open class PaymentRequestHelper(
    private val paymentDomainService: PaymentDomainService,
    private val paymentRepository: PaymentRepository,
    private val creditEntityRepository: CreditEntityRepository,
    private val creditHistoryRepository: CreditHistoryRepository,
    private val paymentCompletedMessagePublisher: PaymentCompletedMessagePublisher,
    private val paymentCancelledMessagePublisher: PaymentCancelledMessagePublisher,
    private val paymentFailedMessagePublisher: PaymentFailedMessagePublisher,
) {
    private val log by LoggerDelegator()

    @Transactional
    open fun persisPayment(
        paymentRequest: PaymentRequest
    ): PaymentEvent {

        log.info(
            "Received payment complete event " +
                    "for order id: ${paymentRequest.orderId}"
        )

        // 거래 객체 생성
        val payment = Payment(
            orderId = OrderId(
                UUID.fromString(
                    paymentRequest.orderId.toString()
                )
            ),
            customerId = CustomerId(
                UUID.fromString(
                    paymentRequest.customerId.toString()
                )
            ),
            price = Money(paymentRequest.price)
        )

        // 거래 객체 처리
        return processPayment(
            payment = payment,
            paymentMessagePublisher = paymentCompletedMessagePublisher
                    as DomainEventPublisher<DomainEvent<Payment>>,
            paymentFailedMessagePublisher = paymentFailedMessagePublisher,
            executeDomainLogic = paymentDomainService::validateAndInitiatePayment
        )
    }

    @Transactional
    open fun persistCancelPayment(
        paymentRequest: PaymentRequest
    ): PaymentEvent {

        log.info(
            "Received payment cancellation event " +
                    "for order id: ${paymentRequest.orderId}"
        )

        // 거래 객체 조회
        val payment = paymentRepository
            .findByOrderId(
                UUID.fromString(
                    paymentRequest.orderId.toString()
                )
            )
            ?: run {
                log.error(
                    "Payment with order id: ${paymentRequest.orderId} " +
                            "could not be found"
                )
                throw PaymentApplicationServiceException(
                    "Payment with order id: ${paymentRequest.orderId} " +
                            "could not be found"
                )
            }

        // 거래 객체 처리
        return processPayment(
            payment = payment,
            paymentMessagePublisher = paymentCancelledMessagePublisher
                    as DomainEventPublisher<DomainEvent<Payment>>,
            paymentFailedMessagePublisher = paymentFailedMessagePublisher,
            executeDomainLogic = paymentDomainService::validateAndCancelPayment
        )
    }


    /*
    * private functions ---------------------------------------------------------------------------------
    * */

    private fun processPayment(
        payment: Payment,
        paymentMessagePublisher: DomainEventPublisher<DomainEvent<Payment>>,
        paymentFailedMessagePublisher: PaymentFailedMessagePublisher,
        executeDomainLogic: (
            Payment,
            CreditEntry,
            MutableList<CreditHistory>,
            MutableList<String>,
            DomainEventPublisher<DomainEvent<Payment>>,
            PaymentFailedMessagePublisher
        ) -> PaymentEvent
    ): PaymentEvent {
        // 계좌 상태 가져오기
        val creditEntry = getCreditEntry(payment.customerId)

        // 계좌 이력 가져오기
        val creditHistories = getCreditHistories(payment.customerId)

        // 거래와 계좌 검증 및 처리
        val failureMessages = mutableListOf<String>()
        val paymentEvent = executeDomainLogic(
            payment,
            creditEntry,
            creditHistories,
            failureMessages,
            paymentMessagePublisher,
            paymentFailedMessagePublisher
        )

        // 결과 저장
        persistObjectToDB(payment, paymentEvent, creditEntry, creditHistories)
        return paymentEvent
    }


    private fun persistObjectToDB(
        payment: Payment,
        paymentEvent: PaymentEvent,
        creditEntry: CreditEntry,
        creditHistories: MutableList<CreditHistory>
    ) {
        // 거래 저장
        paymentRepository.save(payment)

        // 에러가 없을 때만 계좌 정보와 이력을 저장한다.
        if (paymentEvent.failureMessages.isEmpty()) {
            creditEntityRepository.save(creditEntry)
            creditHistoryRepository.save(creditHistories[creditHistories.size - 1])
        }
    }


    // 수정 가능한 list 를 반환
    private fun getCreditHistories(customerId: CustomerId)
            : MutableList<CreditHistory> {

        return creditHistoryRepository
            .findByCustomerId(customerId)
            .ifEmpty {
                throw PaymentApplicationServiceException(
                    "Could not find credit histories for customer: $customerId"
                )
            }
    }

    private fun getCreditEntry(customerId: CustomerId)
            : CreditEntry {

        return creditEntityRepository
            .findByCustomerId(customerId)
            ?: run {
                throw PaymentApplicationServiceException(
                    "Could not find credit entry for customer: $customerId"
                )
            }
    }
}