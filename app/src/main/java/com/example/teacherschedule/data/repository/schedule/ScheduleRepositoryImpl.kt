package com.example.teacherschedule.data.repository.schedule

import com.example.teacherschedule.data.remote.schedule.ScheduleApi
import com.example.teacherschedule.domain.exception.AppException
import com.example.teacherschedule.domain.model.schedule.RawSchedule
import com.example.teacherschedule.domain.model.schedule.TimeRange
import com.squareup.moshi.JsonDataException
import java.io.IOException
import java.time.ZonedDateTime
import javax.inject.Inject

class ScheduleRepositoryImpl @Inject constructor(
    private val api: ScheduleApi
) : ScheduleRepository {
    override suspend fun getSchedule(teacherId: String, startedAt: String): Result<RawSchedule> {
        return try {
            val response = api.getSchedule(teacherId, startedAt)
            if (response.isSuccessful) {
                val body = response.body()!!
                val available = body.availableTimeList.map {
                    TimeRange(
                        startUtc = ZonedDateTime.parse(it.startTime),
                        endUtc = ZonedDateTime.parse(it.endTime)
                    )
                }
                val booked = body.bookedTimeList.map {
                    TimeRange(
                        startUtc = ZonedDateTime.parse(it.startTime),
                        endUtc = ZonedDateTime.parse(it.endTime)
                    )
                }
                Result.success(
                    RawSchedule(availableList = available, bookedList = booked)
                )
            } else {
                Result.failure(
                    AppException.HttpError(
                        code = response.code(),
                        errorBody = response.errorBody()?.string()
                    )
                )
            }
        } catch (e: JsonDataException) {
            Result.failure(AppException.ApiError("Invalid JSON format"))
        } catch (e: IOException) {
            Result.failure(AppException.NetworkError)
        } catch (e: Exception) {
            Result.failure(AppException.UnknownError(e))
        }
    }
}
