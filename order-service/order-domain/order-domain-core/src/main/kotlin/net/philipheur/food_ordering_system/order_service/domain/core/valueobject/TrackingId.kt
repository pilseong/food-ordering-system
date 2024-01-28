package net.philipheur.food_ordering_system.order_service.domain.core.valueobject

import net.philipheur.food_ordering_system.common.domain.valueobject.BaseId
import java.util.UUID

class TrackingId(id: UUID): BaseId<UUID>(id) {
}