package com.example.teacherschedule.data.model.schedule

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * 表示教師可預約與已被預約的時間資料。
 *
 * @property availableTimeList 可被預約的時間區段列表。
 * @property bookedTimeList 已被預約的時間區段列表。
 */
@JsonClass(generateAdapter = true)
data class ScheduleResponse(
    @Json(name = "available")
    val availableTimeList: List<TimeRangeResponse>,

    @Json(name = "booked")
    val bookedTimeList: List<TimeRangeResponse>,
)

/**
 * 表示單一時間區段的資料結構（起始與結束時間）。
 *
 * @property startTime 區段起始時間（ISO 8601 格式，例如："2025-07-27T04:00:00Z"）。
 * @property endTime 區段結束時間（ISO 8601 格式，例如："2025-07-27T06:30:00Z"）。
 */
@JsonClass(generateAdapter = true)
data class TimeRangeResponse(
    @Json(name = "start")
    val startTime: String,

    @Json(name = "end")
    val endTime: String
)
