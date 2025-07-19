package com.example.teacherschedule.presentation.schedule.component

import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun DayOfWeekTab(
    dateList: List<LocalDate>,
    selectedDate: LocalDate,
    onDateSelected: (LocalDate) -> Unit,
    modifier: Modifier = Modifier
) {
    val selectedIndex = dateList.indexOfFirst { it == selectedDate }.coerceAtLeast(0)

    ScrollableTabRow(
        selectedTabIndex = selectedIndex,
        modifier = modifier,
        edgePadding = 16.dp,
        indicator = { tabPositions ->
            TabRowDefaults.SecondaryIndicator(
                Modifier.tabIndicatorOffset(tabPositions[selectedIndex])
            )
        }
    ) {
        dateList.forEach { date ->
            val isSelected = date == selectedDate
            val dayOfWeek = date.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.ENGLISH)
            val dateString = date.format(DateTimeFormatter.ofPattern("MM/dd"))

            Tab(
                selected = isSelected,
                onClick = { onDateSelected(date) },
                text = {
                    Text(
                        text = "$dayOfWeek\n$dateString",
                        textAlign = TextAlign.Center
                    )
                }
            )
        }
    }
}

@Preview
@Composable
fun DayOfWeekTab() {
    val rangeStartDate = LocalDate.of(2025, 7, 28)
    val dateList = (0..6).map { rangeStartDate.plusDays(it.toLong()) }
    val selectedTabIndex = 2

    ScrollableTabRow(
        selectedTabIndex = selectedTabIndex,
        edgePadding = 16.dp,
        indicator = { tabPositions ->
            TabRowDefaults.SecondaryIndicator(
                Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex])
            )
        }
    ) {
        dateList.forEach { date ->
            val isSelected = date == dateList[selectedTabIndex]
            val dayOfWeek = date.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.ENGLISH)
            val dateString = date.format(DateTimeFormatter.ofPattern("MM/dd"))

            Tab(
                selected = isSelected,
                onClick = {},
                text = {
                    Text(
                        text = "$dayOfWeek\n$dateString",
                        textAlign = TextAlign.Center
                    )
                }
            )
        }
    }
}
