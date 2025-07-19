package com.example.teacherschedule.presentation.schedule

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.teacherschedule.R
import com.example.teacherschedule.domain.exception.AppException
import com.example.teacherschedule.domain.model.schedule.TimeSlot
import com.example.teacherschedule.presentation.common.component.ApiErrorContent
import com.example.teacherschedule.presentation.common.component.Collapse
import com.example.teacherschedule.presentation.common.component.NetworkErrorContent
import com.example.teacherschedule.presentation.common.component.UnknownErrorContent
import com.example.teacherschedule.presentation.schedule.component.DayOfWeekTab
import com.example.teacherschedule.presentation.schedule.component.TeacherInfoCard
import com.example.teacherschedule.presentation.schedule.component.TimeSlotList
import com.example.teacherschedule.presentation.schedule.component.WeekSelectorBar
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleScreen(
    teacherId: String,
    onBackClick: () -> Unit
) {
    val viewModel: ScheduleViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsState()
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    LaunchedEffect(Unit) {
        viewModel.init(teacherId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.booking_time)) },
                scrollBehavior = scrollBehavior,
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_arrow_back),
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            when (uiState) {
                is ScheduleUiState.Error -> {
                    ScheduleErrorContent(uiState) {
                        viewModel.retry()
                    }
                }

                is ScheduleUiState.Success, is ScheduleUiState.Loading -> {
                    val successState = when (val state = uiState) {
                        is ScheduleUiState.Success -> state
                        is ScheduleUiState.Loading -> state.lastState
                        else -> null
                    }
                    Box(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        if (successState != null) {
                            ScheduleContent(
                                successState = successState,
                                scrollBehavior = scrollBehavior,
                                teacherId = teacherId,
                                onPrev = { viewModel.loadPreviousWeek() },
                                onNext = { viewModel.loadNextWeek() },
                                onDateSelected = { viewModel.updateSlotsByDate(it) },
                                onTimeSlotSelected = { viewModel.selectTimeSlot(it) }
                            )
                        }
                        if (uiState is ScheduleUiState.Loading) {
                            Box(
                                modifier = Modifier.fillMaxSize()
                            ) {
                                CircularProgressIndicator(
                                    modifier = Modifier.align(Alignment.Center)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleContent(
    successState: ScheduleUiState.Success,
    scrollBehavior: TopAppBarScrollBehavior,
    teacherId: String,
    onPrev: () -> Unit,
    onNext: () -> Unit,
    onDateSelected: (LocalDate) -> Unit,
    onTimeSlotSelected: (TimeSlot) -> Unit
) {
    Collapse(
        collapseContent = {
            TeacherInfoCard(name = teacherId)
        },
        bodyContent = {
            LazyColumn(
                modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
            ) {
                stickyHeader {
                    Column {
                        WeekSelectorBar(
                            rangeText = successState.rangeText,
                            isPrevEnabled = successState.isPrevEnabled,
                            onPrev = onPrev,
                            onNext = onNext
                        )
                        DayOfWeekTab(
                            dateList = successState.dateList,
                            selectedDate = successState.selectedDate,
                            onDateSelected = { onDateSelected(it) }
                        )
                    }
                }
                item {
                    AnimatedContent(
                        targetState = successState.timeSlotList,
                        label = "TimeSlotListTransition"
                    ) { slotList ->
                        TimeSlotList(
                            slots = slotList,
                            selectedSlot = successState.selectedTime,
                            onSelect = { onTimeSlotSelected(it) }
                        )
                    }
                }
            }
        }
    )
}

@Composable
fun ScheduleErrorContent(uiState: ScheduleUiState, action: () -> Unit) {
    val error = (uiState as ScheduleUiState.Error).error
    when (error) {
        is AppException.ApiError,
        is AppException.HttpError -> {
            ApiErrorContent(action)
        }

        is AppException.NetworkError -> {
            NetworkErrorContent(action)
        }

        is AppException.UnknownError -> {
            UnknownErrorContent(action)
        }
    }
}
