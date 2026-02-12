package com.example.transcribeassistant.domain.model

data class UserProfile(
    val id: String,
    val name: String?,
    val email: String?
) {
    val initials: String
        get() {
            val parts = (name ?: "").trim().split("\\s+".toRegex())
            return when {
                parts.size >= 2 -> "${parts[0].first().uppercaseChar()}${parts[1].first().uppercaseChar()}"
                parts.isNotEmpty() && parts[0].isNotEmpty() -> "${parts[0].first().uppercaseChar()}"
                else -> "?"
            }
        }
}
