package com.example.transcribeassistant.data.dto

import com.google.gson.annotations.SerializedName

/**
 * Data Transfer Object (DTO) representing a transcript.
 * This class is used to map the JSON response from the server to a Kotlin object.
 *
 * @property id Unique identifier for the transcript.
 * @property videoUrl URL of the video associated with the transcript.
 * @property transcript The actual transcript text.
 * @property description Description of the transcript.
 * @property title Title of the video or content.
 * @property duration Duration of the video in seconds.
 * @property uploadedAt Timestamp when the video was uploaded.
 * @property accountId Unique identifier for the account that uploaded the video.
 * @property account Name of the account that uploaded the video.
 * @property identifierId Unique identifier for the transcript.
 * @property identifier Identifier for the transcript, often used for search or reference.
 * @property categories List of categories associated with the transcript, can be null if not applicable.
 * @property createdAt Timestamp when the transcript was created.
 */
data class TranscriptDto(
    @SerializedName("id") val id: String,
    @SerializedName("videoUrl") val videoUrl: String,
    @SerializedName("transcript") val transcript: String,
    @SerializedName("description") val description: String,
    @SerializedName("title") val title: String,
    @SerializedName("duration") val duration: Int,
    @SerializedName("uploadedAt") val uploadedAt: String,
    @SerializedName("accountId") val accountId: String,
    @SerializedName("account") val account: String,
    @SerializedName("identifierId") val identifierId: String,
    @SerializedName("identifier") val identifier: String,
    @SerializedName("categories") val categories: List<String>?,
    @SerializedName("createdAt") val createdAt: String
)