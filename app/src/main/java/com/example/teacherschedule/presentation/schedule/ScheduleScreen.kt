package com.example.teacherschedule.presentation.schedule

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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

    LaunchedEffect(Unit) {
        viewModel.init(teacherId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("預約老師") },
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
                is ScheduleUiState.Loading -> {
                    CircularProgressIndicator(Modifier.align(Alignment.Center))
                }

                is ScheduleUiState.Error -> {
                    // TODO
                }

                is ScheduleUiState.Success -> {
                    val successState = (uiState as? ScheduleUiState.Success) ?: return@Box
                    Column {
                        TeacherInfoCard(name = teacherId)
                        WeekSelectorBar(
                            rangeText = successState.rangeText,
                            isPrevEnabled = successState.isPrevEnabled,
                            onPrev = {
                                // TODO
                            },
                            onNext = {
                                // TODO
                            }
                        )
                        DayOfWeekTab(
                            dateList = successState.dateList,
                            selectedDate = successState.selectedDate,
                            onDateSelected = { date ->
                                viewModel.updateSlotsByDate(date)
                            }
                        )
                        TimeSlotList(
                            slots = successState.timeSlotList,
                            selectedSlot = null, // TODO
                            onSelect = {
                                // TODO
                            }
                        )
                    }
                }
            }
        }
    }
}
