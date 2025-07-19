package com.example.teacherschedule.data.remote.schedule

import com.example.teacherschedule.data.model.schedule.ScheduleResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ScheduleApi {
    @GET("v1/guest/teachers/{teacherId}/schedule")
    suspend fun getSchedule(
        @Path("teacherId") teacherId: String,
        @Query("started_at") startedAt: String
    ): Response<ScheduleResponse>
}
