package com.example.teacherschedule.domain.usecase.schedule

import com.example.teacherschedule.data.repository.schedule.ScheduleRepository
import com.example.teacherschedule.domain.model.schedule.TimeRange
import com.example.teacherschedule.domain.model.schedule.TimeSlot
import com.example.teacherschedule.domain.model.schedule.TimeSlotStatus
import java.time.ZoneId
import java.time.ZonedDateTime
import javax.inject.Inject

class GetTimeSlotUseCaseImpl @Inject constructor(
    private val repository: ScheduleRepository
) : GetTimeSlotUseCase {
    override suspend operator fun invoke(
        teacherId: String,
        startedAt: String,
        zoneId: ZoneId
    ): Result<List<TimeSlot>> {
        return repository.getSchedule(teacherId, startedAt).mapCatching { raw ->
            generateTimeSlot(
                availableRanges = raw.availableList,
                bookedRanges = raw.bookedList,
                zoneId = zoneId
            )
        }
    }

    private fun generateTimeSlot(
        availableRanges: List<TimeRange>,
        bookedRanges: List<TimeRange>,
        zoneId: ZoneId
    ): List<TimeSlot> {
        val map = mutableMapOf<ZonedDateTime, TimeSlotStatus>()

        availableRanges.forEach { range ->
            var current = range.startUtc
            while (current < range.endUtc) {
                map[current] = TimeSlotStatus.AVAILABLE
                current = current.plusMinutes(30)
            }
        }

        bookedRanges.forEach { range ->
            var current = range.startUtc
            while (current < range.endUtc) {
                map[current] = TimeSlotStatus.BOOKED
                current = current.plusMinutes(30)
            }
        }

        return map.toSortedMap().map { (startUtc, status) ->
            val localTime = startUtc.withZoneSameInstant(zoneId)
            TimeSlot(
                startUtc = startUtc,
                displayTime = localTime.format(java.time.format.DateTimeFormatter.ofPattern("HH:mm")),
                status = status
            )
        }
    }
}
