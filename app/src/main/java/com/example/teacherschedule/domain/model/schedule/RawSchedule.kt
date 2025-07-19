package com.example.teacherschedule.domain.model.schedule

import java.time.ZonedDateTime

/**
 * 表示教師的原始排程資料。
 *
 * 由兩部分組成：
 * - available：可被預約的時間區段列表
 * - booked：已被預約（不可再選擇）的時間區段列表
 *
 * 後續會經過轉換成半小時單位的時段（[TimeSlot]）以供 UI 顯示。
 *
 * @property availableList 可被預約的時間區段。
 * @property bookedList 已被預約的時間區段。
 */
data class RawSchedule(
    val availableList: List<TimeRange>,
    val bookedList: List<TimeRange>
)

/**
 * 表示一個連續的時間區段，包含起始與結束時間。
 *
 * @property startUtc 區段起始時間（使用 UTC 的 ZonedDateTime）。
 * @property endUtc 區段結束時間（使用 UTC 的 ZonedDateTime）。
 */
data class TimeRange(
    val startUtc: ZonedDateTime,
    val endUtc: ZonedDateTime
)
