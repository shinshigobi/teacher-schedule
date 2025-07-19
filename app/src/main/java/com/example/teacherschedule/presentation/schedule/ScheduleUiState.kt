package com.example.teacherschedule.presentation.schedule

import com.example.teacherschedule.domain.exception.AppException
import com.example.teacherschedule.domain.model.schedule.TimeSlot
import java.time.LocalDate

sealed class ScheduleUiState {

    data object Loading : ScheduleUiState()

    /**
     * 表示行事曆載入成功時的狀態。
     *
     * 此狀態下畫面將顯示可供選擇的時間區段（time slots），並允許使用者切換不同的日期或進行預約。
     *
     * @property currentRangeStartDate 此次排程資料的起始日（對應 API 的 `startedAt` 參數）。
     * 此欄位不一定是週一，完全取決於 ViewModel 發送 API 的時點。
     *
     * @property timeSlotList 對應 `selectedDate` 的時間區段列表，
     * 每半小時為一格，會標記為「可預約」或「已被預約」。
     *
     * @property selectedDate 使用者目前選擇查看的特定日期。
     * 預設為資料範圍內的今天（或資料內最早的一天）。
     *
     * @property selectedTime 使用者目前點選的時間區段。
     * 若為 null，表示尚未選擇時段。
     *
     * @property isBookingConfirmed 是否進行預約，用來顯示 Dialog。
     */
    data class Success(
        val currentRangeStartDate: LocalDate,
        val timeSlotList: List<TimeSlot>,
        val selectedDate: LocalDate,
        val selectedTime: TimeSlot? = null,
        val isBookingConfirmed: Boolean = false
    ) : ScheduleUiState()

    data class Error(val error: AppException) : ScheduleUiState()
}
