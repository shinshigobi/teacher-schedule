package com.example.teacherschedule.data.repository.schedule

import com.example.teacherschedule.domain.model.schedule.RawSchedule

interface ScheduleRepository {
    suspend fun getSchedule(teacherId: String, startedAt: String): Result<RawSchedule>
}
