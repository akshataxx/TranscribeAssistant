package com.example.transcribeassistant.repository

import com.example.transcribeassistant.model.Transcript
import com.example.transcribeassistant.network.TranscriptApi

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