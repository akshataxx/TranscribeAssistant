package com.example.transcribeassistant.data.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.time.Instant
/**
 * Data Transfer Object (DTO) representing a transcript.
 * This class is used to map the JSON response from the server to a Kotlin object.
 *
 * @property id Unique identifier for the transcript.
 * @property videoUrl URL of the video associated with the transcript.
 * @property transcript The actual transcript text.
 * @property structuredContent JSON string containing structured content extracted from transcript.
 * @property description Description of the transcript.
 * @property title Title of the video or content.
 * @property duration Duration of the video in seconds.
 * @property uploadedAt Timestamp when the video was uploaded.
 * @property accountId Unique identifier for the account that uploaded the video.
 * @property account Name of the account that uploaded the video.
 * @property identifierId Unique identifier for the transcript.
 * @property identifier Identifier for the transcript, often used for search or reference.
 * @property categoryId The special, predefined categoryId
 * @property category The special, predefined category name (e.g., "Recipe").
 * @property alias The final, user-visible alias for the transcript (e.g., "Big-Back", "Tech-Tok").
 * @property createdAt Timestamp when the transcript was created.
 */
@JsonClass(generateAdapter = true)
data class TranscriptDto(
    @Json(name = "id")                 val id: String,
    @Json(name = "videoUrl")           val videoUrl: String,
    @Json(name = "transcript")         val transcript: String,
    @Json(name = "structuredContent")  val structuredContent: String?,
    @Json(name = "description")        val description: String,
    @Json(name = "title")              val title: String,
    @Json(name = "duration")           val duration: Double,
    @Json(name = "uploadedAt")         val uploadedAt: Instant,
    @Json(name = "accountId")          val accountId: String,
    @Json(name = "account")            val account: String,
    @Json(name = "identifierId")       val identifierId: String,
    @Json(name = "identifier")         val identifier: String,
    @Json(name = "categoryId")         val categoryId: String,
    @Json(name = "category")           val category: String,
    @Json(name = "alias")              val alias: String?,
    @Json(name = "notes")              val notes: String? = null,
    @Json(name = "createdAt")          val createdAt: Instant,
    @Json(name = "platform")           val platform: String? = null,
    @Json(name = "generatedTitle")     val generatedTitle: String? = null,
    @Json(name = "subcategoryId")      val subcategoryId: String? = null,
    @Json(name = "subcategory")        val subcategory: String? = null
)

