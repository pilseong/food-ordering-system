package net.philipheur.food_ordering_system.common.domain.valueobject

import net.philipheur.food_ordering_system.common.domain.valueobject.BaseId
import java.util.UUID

class OrderId(id: UUID): BaseId<UUID>(id)