package com.example.transcribeassistant.domain.repository

import com.example.transcribeassistant.domain.model.Transcript
import com.example.transcribeassistant.data.network.TranscriptApi

/**
 * Repository for fetching transcripts from a video URL.
 * This repository uses the TranscriptApi to make network requests.
 * It provides a method to get the transcript for a given video URL.
 */
class TranscriptRepository (private val api: TranscriptApi) {
    suspend fun getTranscript(videoUrl: String): Transcript {
        return api.getTranscriptFromVideo(mapOf("videoUrl" to videoUrl))
    }
}