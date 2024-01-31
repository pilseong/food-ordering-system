package net.philipheur.food_ordering_system.payment_service.domain.core

import net.philipheur.food_ordering_system.common.domain.event.publisher.DomainEventPublisher
import net.philipheur.food_ordering_system.common.domain.valueobject.CreditHistoryId
import net.philipheur.food_ordering_system.common.domain.valueobject.DomainConstant.Companion.UTC
import net.philipheur.food_ordering_system.common.domain.valueobject.PaymentStatus
import net.philipheur.food_ordering_system.common.utils.logging.LoggerDelegator
import net.philipheur.food_ordering_system.payment_service.domain.core.entity.CreditEntry
import net.philipheur.food_ordering_system.payment_service.domain.core.entity.CreditHistory
import net.philipheur.food_ordering_system.payment_service.domain.core.entity.Payment
import net.philipheur.food_ordering_system.payment_service.domain.core.event.PaymentCancelledEvent
import net.philipheur.food_ordering_system.payment_service.domain.core.event.PaymentCompletedEvent
import net.philipheur.food_ordering_system.payment_service.domain.core.event.PaymentEvent
import net.philipheur.food_ordering_system.payment_service.domain.core.event.PaymentFailedEvent
import net.philipheur.food_ordering_system.payment_service.domain.core.valueobject.TransactionType
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.*

class PaymentDomainServiceImpl : PaymentDomainService {
    private val log by LoggerDelegator()
    override fun validateAndInitiatePayment(
        payment: Payment,
        creditEntry: CreditEntry,
        creditHistories: MutableList<CreditHistory>,
        failureMessages: MutableList<String>,
        paymentCompletedMessagePublisher: DomainEventPublisher<PaymentCompletedEvent>,
        paymentFailedMessagePublisher: DomainEventPublisher<PaymentFailedEvent>
    ): PaymentEvent {
        // 가격이 0보디 큰지 확인
        payment.validatePayment(failureMessages)

        // 지불의 ID와 시간을 입력
        payment.initializePayment()

        // 충분한 계좌 잔고가 있는지 확인
        isEnoughCreditEntry(payment, creditEntry, failureMessages)

        // 계좌에서 지불을 차감
        subtractCreditEntry(payment, creditEntry)

        // 차감 이력을 저장
        updateCreditHistory(payment, creditHistories, TransactionType.DEBIT)

        // 입출금 이력과 현재 잔액이 정확한지 확인
        validateCreditHistory(creditEntry, creditHistories, failureMessages)

        if (failureMessages.isEmpty()) {
            log.info("Payment is initiated successfully for order id: ${payment.orderId.value}")
            payment.updateState(PaymentStatus.COMPLETED)
            return PaymentCompletedEvent(
                payment = payment,
                createdAt = ZonedDateTime.now(ZoneId.of(UTC)),
                failureMessages = failureMessages,
                paymentCompletedEventPublisher = paymentCompletedMessagePublisher
            )
        } else {
            log.info("Payment initiation has failed for order id: ${payment.orderId.value}")
            payment.updateState(PaymentStatus.FAILED)

            return PaymentFailedEvent(
                payment = payment,
                createdAt = ZonedDateTime.now(ZoneId.of(UTC)),
                failureMessages = failureMessages,
                paymentFailedEventPublisher = paymentFailedMessagePublisher

            )
        }
    }

    override fun validateAndCancelPayment(
        payment: Payment,
        creditEntry: CreditEntry,
        creditHistories: MutableList<CreditHistory>,
        failureMessages: MutableList<String>,
        paymentCancelledMessagePublisher: DomainEventPublisher<PaymentCancelledEvent>,
        paymentFailedMessagePublisher: DomainEventPublisher<PaymentFailedEvent>
    ): PaymentEvent {

        payment.validatePayment(failureMessages)

        creditEntry.addCreditAmount(payment.price)

        updateCreditHistory(payment, creditHistories, TransactionType.CREDIT)

        if (failureMessages.isEmpty()) {
            log.info("Payment is cancelled for order id: ${payment.orderId.value}")
            payment.updateState(PaymentStatus.CANCELLED)
            return PaymentCancelledEvent(
                payment = payment,
                createdAt = ZonedDateTime.now(ZoneId.of(UTC)),
                paymentCancelledPublisher = paymentCancelledMessagePublisher
            )
        } else {
            log.info("Payment cancellation has failed for order id: ${payment.orderId.value}")
            payment.updateState(PaymentStatus.FAILED)
            return PaymentFailedEvent(
                payment = payment,
                createdAt = ZonedDateTime.now(ZoneId.of(UTC)),
                failureMessages = failureMessages,
                paymentFailedEventPublisher = paymentFailedMessagePublisher
            )
        }
    }

    // 계좌 입금과 출금이력을 조회하여 전체 출금이 입금보다 많은지 확인
    // 총 입금에서 총 출금을 뺀 금액이 현재 잔액이 맞는지를 검증
    private fun validateCreditHistory(
        creditEntry: CreditEntry,
        creditHistories: MutableList<CreditHistory>,
        failureMessages: MutableList<String>
    ) {

        val totalCreditHistory = creditHistories
            .filter { it.transactionType == TransactionType.CREDIT }
            .map { it.amount }
            .reduce { total, money -> total.add(money) }

        val totalDebitHistory = creditHistories
            .filter { it.transactionType == TransactionType.DEBIT }
            .map { it.amount }
            .reduce { total, money -> total.add(money) }

        if (totalDebitHistory.isGreaterThan(totalCreditHistory)) {
            log.error(
                "Customer with id: ${creditEntry.customerId.value} does not have " +
                        "enough credit according to credit history"
            )
            failureMessages.add(
                "Customer with id: ${creditEntry.customerId.value} does not have " +
                        "enough credit according to credit history"
            )
        }

        if (creditEntry.totalCreditAmount != totalCreditHistory.subtract(totalDebitHistory)) {
            log.error(
                "Credit history total is not equal to current credit for " +
                        "customer id: ${creditEntry.customerId.value}"
            )
            failureMessages.add(
                "Credit history total is not equal to current credit for " +
                        "customer id: ${creditEntry.customerId.value}"
            )
        }
    }

    private fun updateCreditHistory(
        payment: Payment,
        creditHistories: MutableList<CreditHistory>,
        transactionType: TransactionType
    ) {

        creditHistories.add(
            CreditHistory(
                creditHistoryId = CreditHistoryId(UUID.randomUUID()),
                customerId = payment.customerId,
                amount = payment.price,
                transactionType = transactionType
            )
        )
    }

    private fun subtractCreditEntry(
        payment: Payment,
        creditEntry: CreditEntry
    ) {
        creditEntry.subtractCreditAmount(payment.price)
    }

    private fun isEnoughCreditEntry(
        payment: Payment,
        creditEntry: CreditEntry,
        failureMessages: MutableList<String>
    ) {
        if (payment.price.isGreaterThan(creditEntry.totalCreditAmount)) {
            log.error(
                "Customer with id: ${payment.customerId.value} " +
                        "does not have enough credit for payment"
            )

            failureMessages.add(
                "Customer with id: ${payment.customerId.value} " +
                        "does not have enough credit for payment"
            )
        }
    }


}