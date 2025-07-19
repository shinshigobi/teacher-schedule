package com.example.teacherschedule.domain.model.schedule

import java.time.ZonedDateTime

/**
 * 時段狀態。
 *
 * @property AVAILABLE 可被預約。
 * @property BOOKED 已被預約。
 */
enum class TimeSlotStatus {
    AVAILABLE, BOOKED
}

/**
 * 時段。
 *
 * @param startUtc 開始時間（UTC）。
 * @param displayTime 顯示用時間，格式為："HH:mm"。
 * @param status 時段的狀態。
 */
data class TimeSlot(
    val startUtc: ZonedDateTime,
    val displayTime: String,
    val status: TimeSlotStatus
)
