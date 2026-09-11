package com.horizon.societymanager.core.di

import com.horizon.societymanager.core.session.SessionManager
import com.horizon.societymanager.data.repository.AnnouncementRepository
import com.horizon.societymanager.data.repository.AuthRepository
import com.horizon.societymanager.data.repository.BookingRepository
import com.horizon.societymanager.data.repository.ComplaintRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideSessionManager(): SessionManager = SessionManager()

    @Provides
    @Singleton
    fun provideAuthRepository(sessionManager: SessionManager): AuthRepository =
        AuthRepository(sessionManager)

    @Provides
    @Singleton
    fun provideAnnouncementRepository(): AnnouncementRepository =
        AnnouncementRepository()

    @Provides
    @Singleton
    fun provideComplaintRepository(): ComplaintRepository =
        ComplaintRepository()

    @Provides
    @Singleton
    fun provideBookingRepository(): BookingRepository =
        BookingRepository()
}
