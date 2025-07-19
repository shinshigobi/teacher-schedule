package com.example.teacherschedule.presentation.schedule

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.teacherschedule.domain.exception.AppException
import com.example.teacherschedule.domain.model.schedule.TimeSlot
import com.example.teacherschedule.domain.usecase.schedule.GetTimeSlotUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.ZoneId
import javax.inject.Inject

@HiltViewModel
class ScheduleViewModel @Inject constructor(
    private val getTimeSlotUseCase: GetTimeSlotUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<ScheduleUiState>(ScheduleUiState.Loading)
    val uiState = _uiState.asStateFlow()

    private var teacherId = ""
    private var allTimeSlotMap: Map<LocalDate, List<TimeSlot>> = emptyMap()

    fun init(teacherId: String, defaultDate: LocalDate = LocalDate.now()) {
        if (this.teacherId.isNotBlank()) return
        this.teacherId = teacherId
        loadSchedule(defaultDate, defaultDate)
    }

    fun updateSlotsByDate(date: LocalDate) {
        val currentState = _uiState.value as? ScheduleUiState.Success ?: return
        val newTimeSlots = allTimeSlotMap[date].orEmpty()

        _uiState.value = currentState.copy(
            selectedDate = date,
            timeSlotList = newTimeSlots,
            selectedTime = null, // 點選日期後清除已選的時間
            isBookingConfirmed = false
        )
    }

    fun loadPreviousWeek() {
        val currentState = _uiState.value as? ScheduleUiState.Success ?: return
        if (!canLoadPrevWeek(currentState.currentRangeStartDate)) return

        val prevWeekStart = currentState.currentRangeStartDate.minusWeeks(1)
        loadSchedule(prevWeekStart, prevWeekStart)
    }

    fun loadNextWeek() {
        val currentState = _uiState.value as? ScheduleUiState.Success ?: return
        val nextWeekStart = currentState.currentRangeStartDate.plusWeeks(1)
        loadSchedule(nextWeekStart, nextWeekStart)
    }

    fun selectTimeSlot(timeSlot: TimeSlot) {
        val currentState = _uiState.value as? ScheduleUiState.Success ?: return

        _uiState.value = currentState.copy(
            selectedTime = timeSlot
        )
    }

    private fun loadSchedule(rangeStartDate: LocalDate, selectedDate: LocalDate) {
        _uiState.value = ScheduleUiState.Loading

        viewModelScope.launch {
            val result = getTimeSlotUseCase.invoke(teacherId, rangeStartDate.toString())

            result.onSuccess { timeSlotList ->
                allTimeSlotMap = timeSlotList.groupBy {
                    it.startUtc.withZoneSameInstant(ZoneId.systemDefault()).toLocalDate()
                }
                val targetDate = if (selectedDate in allTimeSlotMap) {
                    selectedDate
                } else {
                    allTimeSlotMap.keys.first()
                }
                _uiState.value = ScheduleUiState.Success(
                    currentRangeStartDate = rangeStartDate,
                    timeSlotList = allTimeSlotMap[targetDate].orEmpty(),
                    dateList = allTimeSlotMap.keys.sorted(),
                    selectedDate = targetDate,
                    selectedTime = null,
                    isPrevEnabled = canLoadPrevWeek(rangeStartDate),
                    isBookingConfirmed = false
                )
            }.onFailure { throwable ->
                val error = throwable as? AppException ?: AppException.UnknownError(throwable)
                _uiState.value = ScheduleUiState.Error(error)
            }
        }
    }

    /**
     * 判斷整週的範圍是否完全過去，若完全過去則不可再往前。
     *
     * @param date 整週的開始日期。
     * @return 是否可往前載入。
     */
    private fun canLoadPrevWeek(date: LocalDate): Boolean {
        val prevWeekStart = date.minusWeeks(1)
        val prevWeekEnd = prevWeekStart.plusDays(6)
        return prevWeekEnd > LocalDate.now()
    }
}
