package net.philipheur.food_ordering_system.order_service.domain.application_service.dto.track

import jakarta.validation.constraints.NotNull
import java.util.UUID

data class TrackOrderCommand(
    @NotNull val orderTrackingId: UUID
)