package net.philipheur.food_ordering_system.common.domain.valueobject

import java.math.BigDecimal
import java.math.RoundingMode

class Money(
    val amount: BigDecimal
) {
    fun isGreaterThanZero(): Boolean =
        this.amount > BigDecimal.ZERO

    fun isGreaterThan(money: Money): Boolean =
        this.amount > money.amount

    fun add(money: Money): Money =
        Money(
            this.amount.add(money.amount)
                .setScale(2, RoundingMode.HALF_EVEN)
        )

    fun subtract(money: Money): Money =
        Money(
            this.amount.subtract(money.amount)
                .setScale(2, RoundingMode.HALF_EVEN)
        )

    fun multiply(multiplier: Int): Money =
        Money(
            this.amount.multiply(BigDecimal(multiplier))
                .setScale(2, RoundingMode.HALF_EVEN)
        )

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Money) return false

        if (amount != other.amount) return false

        return true
    }

    override fun hashCode(): Int {
        return amount.hashCode()
    }

    companion object {
        val ZERO = Money(BigDecimal.ZERO)
    }


}