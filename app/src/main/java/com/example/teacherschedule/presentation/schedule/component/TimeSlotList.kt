package com.example.teacherschedule.presentation.schedule.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.teacherschedule.domain.model.schedule.TimeSlot
import com.example.teacherschedule.domain.model.schedule.TimeSlotStatus
import java.time.LocalDate
import java.time.ZoneId

@Composable
fun TimeSlotList(
    slots: List<TimeSlot>,
    selectedSlot: TimeSlot?,
    onSelect: (TimeSlot) -> Unit,
    modifier: Modifier = Modifier
) {
    FlowRow(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        slots.forEach { slot ->
            val isSelected = slot == selectedSlot

            FilterChip(
                selected = isSelected,
                onClick = { if (slot.status == TimeSlotStatus.AVAILABLE) onSelect(slot) },
                label = { Text(slot.displayTime) },
                enabled = slot.status == TimeSlotStatus.AVAILABLE,
            )
        }
    }
}

@Preview
@Composable
fun TimeSlotList() {
    val slotList = generateFakeTimeSlots()
    val selectedSlot = slotList.firstOrNull()
    FlowRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        slotList.forEach { slot ->
            val isSelected = slot == selectedSlot

            FilterChip(
                selected = isSelected,
                onClick = {},
                label = { Text(slot.displayTime) },
                enabled = slot.status == TimeSlotStatus.AVAILABLE,
            )
        }
    }
}

private fun generateFakeTimeSlots(): List<TimeSlot> {
    val zoneId = ZoneId.systemDefault()
    val startOfDay = LocalDate.now().atTime(7, 0).atZone(zoneId)

    return listOf(
        TimeSlot(
            startUtc = startOfDay,
            displayTime = "07:00",
            status = TimeSlotStatus.AVAILABLE
        ),
        TimeSlot(
            startUtc = startOfDay.plusMinutes(30),
            displayTime = "07:30",
            status = TimeSlotStatus.AVAILABLE
        ),
        TimeSlot(
            startUtc = startOfDay.plusMinutes(60),
            displayTime = "08:00",
            status = TimeSlotStatus.BOOKED
        ),
        TimeSlot(
            startUtc = startOfDay.plusMinutes(90),
            displayTime = "08:30",
            status = TimeSlotStatus.BOOKED
        ),
        TimeSlot(
            startUtc = startOfDay.plusMinutes(120),
            displayTime = "09:00",
            status = TimeSlotStatus.AVAILABLE
        ),
        TimeSlot(
            startUtc = startOfDay.plusMinutes(150),
            displayTime = "09:30",
            status = TimeSlotStatus.AVAILABLE
        )
    )
}
