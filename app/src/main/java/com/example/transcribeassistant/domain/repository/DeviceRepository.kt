package com.example.transcribeassistant.domain.repository

interface DeviceRepository {
    suspend fun registerDevice(fcmToken: String)
    suspend fun unregisterDevice()
}
