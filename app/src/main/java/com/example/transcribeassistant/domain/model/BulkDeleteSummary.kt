package com.example.transcribeassistant.domain.model

data class BulkDeleteSummary(
    val requestedCount: Int,
    val deletedCount: Int,
    val failedCount: Int,
    val failureMessages: List<String> = emptyList()
) {
    val title: String
        get() = if (failedCount == 0) "Delete Complete" else "Delete Partially Complete"

    val message: String
        get() {
            if (failedCount == 0) {
                return if (deletedCount == 1) {
                    "1 transcript was deleted."
                } else {
                    "$deletedCount transcripts were deleted."
                }
            }

            val parts = mutableListOf<String>()
            if (deletedCount > 0) {
                parts += if (deletedCount == 1) {
                    "1 transcript was deleted."
                } else {
                    "$deletedCount transcripts were deleted."
                }
            }
            parts += if (failedCount == 1) {
                "1 transcript could not be deleted."
            } else {
                "$failedCount transcripts could not be deleted."
            }
            failureMessages.firstOrNull { it.isNotBlank() }?.let { parts += it }
            return parts.joinToString(separator = " ")
        }
}
