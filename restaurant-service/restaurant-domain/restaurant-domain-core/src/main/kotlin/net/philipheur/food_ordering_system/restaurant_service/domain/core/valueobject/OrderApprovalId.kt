package net.philipheur.food_ordering_system.restaurant_service.domain.core.valueobject

import net.philipheur.food_ordering_system.common.domain.valueobject.BaseId
import java.util.*

class OrderApprovalId(
    value: UUID
) : BaseId<UUID>(value) {
}