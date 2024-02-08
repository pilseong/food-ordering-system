package net.philipheur.food_ordering_system.payment_service.domain.service

import net.philipheur.food_ordering_system.common.domain.event.DomainEvent
import net.philipheur.food_ordering_system.common.domain.event.publisher.DomainEventPublisher
import net.philipheur.food_ordering_system.common.domain.valueobject.CustomerId
import net.philipheur.food_ordering_system.common.domain.valueobject.DomainConstant.Companion.UTC
import net.philipheur.food_ordering_system.common.domain.valueobject.Money
import net.philipheur.food_ordering_system.common.domain.valueobject.OrderId
import net.philipheur.food_ordering_system.common.domain.valueobject.PaymentStatus
import net.philipheur.food_ordering_system.common.utils.logging.LoggerDelegator
import net.philipheur.food_ordering_system.infrastructure.outbox.OutboxStatus
import net.philipheur.food_ordering_system.infrastructure.saga.order.SagaConstants
import net.philipheur.food_ordering_system.payment_service.domain.core.PaymentDomainService
import net.philipheur.food_ordering_system.payment_service.domain.core.entity.CreditEntry
import net.philipheur.food_ordering_system.payment_service.domain.core.entity.CreditHistory
import net.philipheur.food_ordering_system.payment_service.domain.core.entity.Payment
import net.philipheur.food_ordering_system.payment_service.domain.core.event.PaymentEvent
import net.philipheur.food_ordering_system.payment_service.domain.service.dto.request.PaymentRequest
import net.philipheur.food_ordering_system.payment_service.domain.service.exception.PaymentApplicationServiceException
import net.philipheur.food_ordering_system.payment_service.domain.service.outbox.model.OrderOutboxMessage
import net.philipheur.food_ordering_system.payment_service.domain.service.outbox.model.PaymentEventPayload
import net.philipheur.food_ordering_system.payment_service.domain.service.outbox.scheduler.OrderOutboxHelper
import net.philipheur.food_ordering_system.payment_service.domain.service.ports.output.message.publisher.PaymentCancelledMessagePublisher
import net.philipheur.food_ordering_system.payment_service.domain.service.ports.output.message.publisher.PaymentCompletedMessagePublisher
import net.philipheur.food_ordering_system.payment_service.domain.service.ports.output.message.publisher.PaymentFailedMessagePublisher
import net.philipheur.food_ordering_system.payment_service.domain.service.ports.output.repository.CreditEntityRepository
import net.philipheur.food_ordering_system.payment_service.domain.service.ports.output.repository.CreditHistoryRepository
import net.philipheur.food_ordering_system.payment_service.domain.service.ports.output.repository.PaymentRepository
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.*

@Suppress("UNCHECKED_CAST")
@Component
open class PaymentRequestHelper(
    private val paymentDomainService: PaymentDomainService,
    private val orderOutboxHelper: OrderOutboxHelper,
    private val paymentRepository: PaymentRepository,
    private val creditEntityRepository: CreditEntityRepository,
    private val creditHistoryRepository: CreditHistoryRepository,
    private val paymentCompletedMessagePublisher: PaymentCompletedMessagePublisher,
    private val paymentCancelledMessagePublisher: PaymentCancelledMessagePublisher,
    private val paymentFailedMessagePublisher: PaymentFailedMessagePublisher,
) {
    private val log by LoggerDelegator()

    @Transactional
    open fun persistPayment(
        paymentRequest: PaymentRequest
    ) {
        outboxDecorator(
            paymentRequest = paymentRequest,
            paymentStatus = PaymentStatus.COMPLETED,
            this::processCompletePayment
        )
    }

    @Transactional
    open fun persistCancelPayment(
        paymentRequest: PaymentRequest
    ) {
        outboxDecorator(
            paymentRequest = paymentRequest,
            paymentStatus = PaymentStatus.CANCELLED,
            this::processCancelPayment
        )
    }


    /*
    * private functions ---------------------------------------------------------------------------------
    * */

    private fun processCompletePayment(paymentRequest: PaymentRequest): PaymentEvent {
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

    private fun processCancelPayment(paymentRequest: PaymentRequest): PaymentEvent {
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


    private fun outboxDecorator(
        paymentRequest: PaymentRequest,
        paymentStatus: PaymentStatus,
        execution: (PaymentRequest) -> PaymentEvent
    ) {

        // outbox 메시지가 저장되어 있으면 이미 처리가 된 요청이다.
        if (checkIfAlreadySaved(
                paymentRequest,
                paymentStatus
            )
        ) {
            log.info(
                "An outbox message with saga id: " +
                        "${paymentRequest.sagaId} is already saved to database!"
            )
            return
        }

        // 비즈니스 로직 실행
        val event = execution(paymentRequest)

        // outbox 메시지를 저장한다.
        saveNewOrderOutboxMessage(
            event,
            paymentRequest,
        )
    }


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


    // 새로운 order outbox message 를 저장한다.
    private fun saveNewOrderOutboxMessage(event: PaymentEvent, paymentRequest: PaymentRequest) {
        // order outbox 메시지 생성
        val payload = PaymentEventPayload(
            paymentId = event.payment.id!!.value.toString(),
            customerId = event.payment.customerId.value.toString(),
            orderId = event.payment.orderId.value.toString(),
            price = event.payment.price.amount,
            createdAt = event.createAt,
            paymentStatus = event.payment.paymentStatus!!.name,
            failureMessages = event.failureMessages,
        )

        // order outbox message 저장
        orderOutboxHelper.saveOutboxMessage(
            OrderOutboxMessage(
                id = UUID.randomUUID(),
                sagaId = paymentRequest.sagaId,
                createdAt = payload.createdAt,
                processedAt = ZonedDateTime.now(ZoneId.of(UTC)),
                type = SagaConstants.ORDER_SAGA_NAME,
                payload = orderOutboxHelper.createPayload(payload),
                paymentStatus = event.payment.paymentStatus!!,
                outboxStatus = OutboxStatus.STARTED
            )
        )
    }

    private fun checkIfAlreadySaved(
        paymentRequest: PaymentRequest,
        paymentStatus: PaymentStatus
    ): Boolean {

        log.info("checkIfAlreadySaved with data $paymentRequest and $paymentStatus")

        // 같은 상태에 메시지가 있는지 확인
        // 이미 메시지가 저장되어 있으면 중복 메시지이므로 무시한다. 저장된 메시는 스케줄러에서 발송
        // 중복이면 true
        return orderOutboxHelper.getPaymentOutboxMessageBySagaIdAndPaymentStatusAndOutboxStatus(
            sagaId = paymentRequest.sagaId,
            paymentStatus = paymentStatus,
            outboxStatus = OutboxStatus.STARTED,
        ) != null
    }
}