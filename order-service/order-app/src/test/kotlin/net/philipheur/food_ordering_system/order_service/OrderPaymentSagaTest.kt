package net.philipheur.food_ordering_system.order_service

import net.philipheur.food_ordering_system.common.domain.valueobject.PaymentStatus
import net.philipheur.food_ordering_system.common.utils.logging.LoggerDelegator
import net.philipheur.food_ordering_system.infrastructure.saga.SagaStatus
import net.philipheur.food_ordering_system.infrastructure.saga.order.SagaConstants.Companion.ORDER_SAGA_NAME
import net.philipheur.food_ordering_system.order_service.dataaccess.outbox.payment.repository.PaymentOutboxJpaRepository
import net.philipheur.food_ordering_system.order_service.domain.application_service.OrderPaymentSaga
import net.philipheur.food_ordering_system.order_service.domain.application_service.dto.message.PaymentResponse
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.dao.OptimisticLockingFailureException
import org.springframework.test.context.jdbc.Sql
import java.math.BigDecimal
import java.time.Instant
import java.util.*
import java.util.concurrent.CountDownLatch
import kotlin.test.assertNotNull

@SpringBootTest(classes = [OrderServiceApplication::class])
@Sql(value = ["classpath:sql/OrderPaymentSagaTestSetUp.sql"])
@Sql(
    value = ["classpath:sql/OrderPaymentSagaTestCleanUp.sql"],
    executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD
)
class OrderPaymentSagaTest() {

    val log by LoggerDelegator()

    @Autowired
    private lateinit var orderPaymentSaga: OrderPaymentSaga

    @Autowired
    private lateinit var paymentOutboxJpaRepository: PaymentOutboxJpaRepository

    private val SAGA_ID: UUID = UUID.fromString("15a497c1-0f4b-4eff-b9f4-c402c8c07afa")
    private val ORDER_ID: UUID = UUID.fromString("d215b5f8-0249-4dc5-89a3-51fd148cfb17")
    private val CUSTOMER_ID: UUID = UUID.fromString("d215b5f8-0249-4dc5-89a3-51fd148cfb41")
    private val PAYMENT_ID: UUID = UUID.randomUUID()
    private val PRICE = BigDecimal("100")


    @Test
    fun testDoublePaymentWithThreads() {
        val threads = mutableListOf<Thread>()

        for (i in 0..<10) {
            threads.add(Thread {
                try {
                    orderPaymentSaga.process(getPaymentResponse())
                } catch (e: OptimisticLockingFailureException) {
                    log.error("OptimisticLockingFailureException occurred for thread $i")
                }
            })
        }

        for (i in 0..<10) {
            threads[i].start()
        }

        for (i in 0..<10) {
            threads[i].join()
        }

        val paymentOutboxEntity = paymentOutboxJpaRepository
            .findByTypeAndSagaIdAndSagaStatusIn(
                ORDER_SAGA_NAME,
                SAGA_ID,
                listOf(SagaStatus.PROCESSING)
            )

        assertNotNull(paymentOutboxEntity)
    }

    @Test
    fun testDoublePaymentWithThreadsAndLatch() {

        val latch = CountDownLatch(10)

        val threads = mutableListOf<Thread>()

        for (i in 0..<10) {
            threads.add(Thread {
                try {
                    orderPaymentSaga.process(getPaymentResponse())
                } catch (e: OptimisticLockingFailureException) {
                    log.error("OptimisticLockingFailureException occurred for thread $i")
                } finally {
                    latch.countDown()
                }
            })
        }

        for (i in 0..<10) {
            threads[i].start()
        }

        latch.await()
//        for (i in 0..<10) {
//            threads[i].join()
//        }

        val paymentOutboxEntity = paymentOutboxJpaRepository
            .findByTypeAndSagaIdAndSagaStatusIn(
                ORDER_SAGA_NAME,
                SAGA_ID,
                listOf(SagaStatus.PROCESSING)
            )

        assertNotNull(paymentOutboxEntity)
    }

    @Test
    fun testDoublePayment() {
        orderPaymentSaga.process(getPaymentResponse())
        orderPaymentSaga.process(getPaymentResponse())
    }

    private fun getPaymentResponse() = PaymentResponse(
        id = UUID.randomUUID(),
        sagaId = SAGA_ID,
        paymentStatus = PaymentStatus.COMPLETED,
        paymentId = PAYMENT_ID,
        orderId = ORDER_ID,
        customerId = CUSTOMER_ID,
        price = PRICE,
        createdAt = Instant.now(),
        failureMessages = emptyList()
    )
}