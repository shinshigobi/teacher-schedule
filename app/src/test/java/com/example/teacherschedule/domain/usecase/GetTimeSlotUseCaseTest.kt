package com.example.teacherschedule.domain.usecase

import com.example.teacherschedule.data.repository.schedule.ScheduleRepository
import com.example.teacherschedule.domain.model.schedule.RawSchedule
import com.example.teacherschedule.domain.model.schedule.TimeRange
import com.example.teacherschedule.domain.model.schedule.TimeSlotStatus
import com.example.teacherschedule.domain.usecase.schedule.GetTimeSlotUseCase
import com.example.teacherschedule.domain.usecase.schedule.GetTimeSlotUseCaseImpl
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.time.ZoneId
import java.time.ZonedDateTime

class GetTimeSlotUseCaseTest {

    private lateinit var useCase: GetTimeSlotUseCase
    private lateinit var fakeRepository: FakeScheduleRepository

    /**
     * 提供一段 available 和 booked 的時間，時間區間為 07:00 ~ 09:30。
     */
    private val rawSchedule = RawSchedule(
        availableList = listOf(
            TimeRange(
                startUtc = ZonedDateTime.parse("2025-07-28T07:00:00Z"),
                endUtc = ZonedDateTime.parse("2025-07-28T08:00:00Z")
            ),
            TimeRange(
                startUtc = ZonedDateTime.parse("2025-07-28T09:00:00Z"),
                endUtc = ZonedDateTime.parse("2025-07-28T09:30:00Z")
            )
        ),
        bookedList = listOf(
            TimeRange(
                startUtc = ZonedDateTime.parse("2025-07-28T08:00:00Z"),
                endUtc = ZonedDateTime.parse("2025-07-28T09:00:00Z")
            )
        )
    )

    @Before
    fun setup() {
        fakeRepository = FakeScheduleRepository()
        useCase = GetTimeSlotUseCaseImpl(fakeRepository)
    }

    @Test
    fun should_return_success_when_repository_returns_valid_data() = runTest {
        fakeRepository.setData(rawSchedule)

        val result = useCase("levi-ackerman", "2025-07-28")

        assertTrue(result.isSuccess)
    }

    @Test
    fun should_generate_correct_number_of_slots_when_given_available_and_booked_ranges() = runTest {
        fakeRepository.setData(rawSchedule)

        val result = useCase("levi-ackerman", "2025-07-28")

        // Assert: 應生成 5 個 time slot
        val slots = result.getOrNull()
        assertEquals(5, slots?.size)
    }

    @Test
    fun should_mark_correct_slot_status_when_ranges_overlap() = runTest {
        fakeRepository.setData(rawSchedule)

        val result = useCase("levi-ackerman", "2025-07-28", ZoneId.of("Z"))

        // Assert: 應生成 [07:00]、[07:30]、[08:00]、[08:30]、[09:00] 五個 time slot
        val slots = result.getOrNull()
        assertEquals("07:00", slots?.get(0)?.displayTime)
        assertEquals(TimeSlotStatus.AVAILABLE, slots?.get(0)?.status)

        assertEquals("07:30", slots?.get(1)?.displayTime)
        assertEquals(TimeSlotStatus.AVAILABLE, slots?.get(1)?.status)

        assertEquals("08:00", slots?.get(2)?.displayTime)
        assertEquals(TimeSlotStatus.BOOKED, slots?.get(2)?.status)

        assertEquals("08:30", slots?.get(3)?.displayTime)
        assertEquals(TimeSlotStatus.BOOKED, slots?.get(3)?.status)

        assertEquals("09:00", slots?.get(4)?.displayTime)
        assertEquals(TimeSlotStatus.AVAILABLE, slots?.get(4)?.status)
    }

    @Test
    fun should_convert_time_to_local_displayTime_when_given_zoneId() = runTest {
        fakeRepository.setData(rawSchedule)

        val result = useCase("levi-ackerman", "2025-07-28", ZoneId.of("Asia/Taipei"))

        // Assert: 應生成 [07:00] +8 -> [15:00]
        val slots = result.getOrNull()
        assertEquals("15:00", slots?.get(0)?.displayTime)
    }

    @Test
    fun should_not_generate_slots_for_unavailable_gaps_between_available_ranges() = runTest {
        val rawSchedule = RawSchedule(
            availableList = listOf(
                TimeRange(
                    startUtc = ZonedDateTime.parse("2025-07-28T07:00:00Z"),
                    endUtc = ZonedDateTime.parse("2025-07-28T08:00:00Z")
                ),
                TimeRange(
                    startUtc = ZonedDateTime.parse("2025-07-28T09:00:00Z"),
                    endUtc = ZonedDateTime.parse("2025-07-28T09:30:00Z")
                )
            ),
            bookedList = listOf()
        )
        fakeRepository.setData(rawSchedule)

        val result = useCase("levi-ackerman", "2025-07-28")

        // Assert: 應生成 3 個 time slot
        val slots = result.getOrNull()
        assertEquals(3, slots?.size)
    }

    private class FakeScheduleRepository : ScheduleRepository {

        private var rawSchedule: RawSchedule? = null

        fun setData(data: RawSchedule) {
            rawSchedule = data
        }

        override suspend fun getSchedule(teacherId: String, startDate: String): Result<RawSchedule> {
            return rawSchedule?.let { Result.success(it) }
                ?: Result.failure(IllegalStateException("No data set"))
        }
    }
}
