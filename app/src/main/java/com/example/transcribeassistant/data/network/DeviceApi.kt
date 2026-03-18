package com.example.transcribeassistant.data.network

import com.example.transcribeassistant.data.dto.DeviceRegistrationRequest
import com.example.transcribeassistant.data.dto.DeviceRegistrationResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.POST
import retrofit2.http.Query

interface DeviceApi {

    @POST("api/device/register")
    suspend fun registerDevice(
        @Body request: DeviceRegistrationRequest
    ): DeviceRegistrationResponse

    @DELETE("api/device/unregister")
    suspend fun unregisterDevice(
        @Query("deviceId") deviceId: String
    ): Response<Unit>
}
