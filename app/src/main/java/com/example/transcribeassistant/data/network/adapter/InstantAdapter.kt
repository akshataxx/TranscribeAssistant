package com.example.transcribeassistant.data.network.adapter

import com.squareup.moshi.FromJson
import com.squareup.moshi.ToJson
import java.time.Instant

class InstantAdapter {

    @ToJson
    fun toJson(instant: Instant): String {
        return instant.toString() // outputs ISO-8601 format
    }

    @FromJson
    fun fromJson(value: String): Instant {
        return Instant.parse(value) // parses ISO-8601 string into Instant
    }
}
