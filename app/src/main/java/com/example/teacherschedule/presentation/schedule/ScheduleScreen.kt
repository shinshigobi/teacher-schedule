package com.example.teacherschedule.presentation.schedule

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Box
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.teacherschedule.R
import com.example.teacherschedule.presentation.schedule.component.DayOfWeekTab
import com.example.teacherschedule.presentation.schedule.component.TeacherInfoCard
import com.example.teacherschedule.presentation.schedule.component.TimeSlotList
import com.example.teacherschedule.presentation.schedule.component.WeekSelectorBar

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
                title = { Text("預約老師") },
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
                    // TODO
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
                            LazyColumn(
                                modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
                            ) {
                                item {
                                    TeacherInfoCard(name = teacherId)
                                }
                                item {
                                    WeekSelectorBar(
                                        rangeText = successState.rangeText,
                                        isPrevEnabled = successState.isPrevEnabled,
                                        onPrev = { viewModel.loadPreviousWeek() },
                                        onNext = { viewModel.loadNextWeek() }
                                    )
                                }
                                item {
                                    DayOfWeekTab(
                                        dateList = successState.dateList,
                                        selectedDate = successState.selectedDate,
                                        onDateSelected = { date ->
                                            viewModel.updateSlotsByDate(date)
                                        }
                                    )
                                }
                                item {
                                    AnimatedContent(
                                        targetState = successState.timeSlotList,
                                        label = "TimeSlotListTransition"
                                    ) { slotList ->
                                        TimeSlotList(
                                            slots = slotList,
                                            selectedSlot = successState.selectedTime,
                                            onSelect = { viewModel.selectTimeSlot(it) }
                                        )
                                    }
                                }
                            }
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
