package com.example.transcribeassistant.data.repository

import android.content.Context
import android.provider.Settings
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.example.transcribeassistant.data.dto.DeviceRegistrationRequest
import com.example.transcribeassistant.data.extensions.dataStore
import com.example.transcribeassistant.data.network.DeviceApi
import com.example.transcribeassistant.domain.repository.DeviceRepository
import kotlinx.coroutines.flow.first

class DeviceRepositoryImpl(
    private val api: DeviceApi,
    private val context: Context
) : DeviceRepository {

    companion object {
        private val DEVICE_ID_KEY = stringPreferencesKey("registered_device_id")
    }

    override suspend fun registerDevice(fcmToken: String) {
        val deviceId = getOrCreateDeviceId()
        val response = api.registerDevice(
            DeviceRegistrationRequest(
                platform = "ANDROID",
                fcmToken = fcmToken,
                deviceId = deviceId
            )
        )
        // Persist the backend-assigned device ID for later unregistration
        context.dataStore.edit { prefs ->
            prefs[DEVICE_ID_KEY] = response.id
        }
    }

    override suspend fun unregisterDevice() {
        val storedId = context.dataStore.data.first()[DEVICE_ID_KEY] ?: return
        api.unregisterDevice(storedId)
        context.dataStore.edit { prefs ->
            prefs.remove(DEVICE_ID_KEY)
        }
    }

    // Uses Android ID as a stable per-device identifier
    private fun getOrCreateDeviceId(): String =
        Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
}
