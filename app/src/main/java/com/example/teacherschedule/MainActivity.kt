package com.example.teacherschedule

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import com.example.teacherschedule.presentation.schedule.ScheduleScreen
import com.example.teacherschedule.ui.theme.TeacherScheduleTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TeacherScheduleTheme {
                Box(modifier = Modifier.fillMaxSize()) {
                    // TODO: Replace with actual teacher id
                    ScheduleScreen("mark-strudwick") {}
                }
            }
        }
    }
}
