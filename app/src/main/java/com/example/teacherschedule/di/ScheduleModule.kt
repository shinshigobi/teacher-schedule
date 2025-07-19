package com.example.teacherschedule.di

import com.example.teacherschedule.domain.usecase.schedule.GetTimeSlotUseCase
import com.example.teacherschedule.domain.usecase.schedule.GetTimeSlotUseCaseImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
abstract class ScheduleModule {
    @Binds
    abstract fun bindGetTimeSlotUseCase(
        getTimeSlotUseCaseImpl: GetTimeSlotUseCaseImpl
    ): GetTimeSlotUseCase
}
