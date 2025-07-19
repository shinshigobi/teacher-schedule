package com.example.teacherschedule.domain.usecase.schedule

import com.example.teacherschedule.domain.model.schedule.TimeSlot
import java.time.ZoneId

interface GetTimeSlotUseCase {
    suspend operator fun invoke(
        teacherId: String,
        startedAt: String,
        zoneId: ZoneId = ZoneId.systemDefault()
    ): Result<List<TimeSlot>>
}
