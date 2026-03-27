package com.example.transcribeassistant.ui.screen.transcription

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.transcribeassistant.ui.screen.components.PrimaryText
import com.example.transcribeassistant.ui.screen.components.SecondaryText
import com.example.transcribeassistant.ui.screen.components.ScoopBlue
import com.example.transcribeassistant.ui.screen.components.ScoopCyan
import com.example.transcribeassistant.ui.screen.components.ScoopPurple
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonClass
import com.squareup.moshi.Moshi
import org.json.JSONObject

/**
 * Data classes for structured content types
 */
@JsonClass(generateAdapter = true)
data class RecipeContent(
    val type: String,
    val ingredients: List<String>,
    val steps: List<String>
)

@JsonClass(generateAdapter = true)
data class BeautyContent(
    val type: String,
    val products: List<String>,
    val steps: List<String>
)

@JsonClass(generateAdapter = true)
data class GeneralContent(
    val type: String,
    val keyPoints: List<String>
)

/**
 * Gradient section header — matches iOS GradientText (18sp semibold)
 */
@Composable
private fun SectionHeader(text: String) {
    Text(
        text = text,
        fontSize = 18.sp,
        fontWeight = FontWeight.SemiBold,
        style = androidx.compose.ui.text.TextStyle(
            brush = Brush.linearGradient(colors = listOf(ScoopPurple, ScoopBlue, ScoopCyan))
        )
    )
}

/**
 * Composable to display recipe content with ingredients and steps
 */
@Composable
fun RecipeContentView(content: RecipeContent) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        if (content.ingredients.isNotEmpty()) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                SectionHeader("Ingredients")
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    content.ingredients.forEach { ingredient ->
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Text(text = "•", fontSize = 16.sp, color = SecondaryText)
                            Text(
                                text = ingredient,
                                fontSize = 16.sp,
                                color = SecondaryText,
                                lineHeight = 22.sp,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }
        }

        if (content.steps.isNotEmpty()) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                SectionHeader("Steps")
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    content.steps.forEachIndexed { index, step ->
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Text(
                                text = "${index + 1}.",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = PrimaryText
                            )
                            Text(
                                text = step,
                                fontSize = 16.sp,
                                color = SecondaryText,
                                lineHeight = 22.sp,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Composable to display beauty/skincare content with products and steps
 */
@Composable
fun BeautyContentView(content: BeautyContent) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        if (content.products.isNotEmpty()) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                SectionHeader("Products")
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    content.products.forEach { product ->
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Text(text = "•", fontSize = 16.sp, color = SecondaryText)
                            Text(
                                text = product,
                                fontSize = 16.sp,
                                color = SecondaryText,
                                lineHeight = 22.sp,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }
        }

        if (content.steps.isNotEmpty()) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                SectionHeader("Steps")
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    content.steps.forEachIndexed { index, step ->
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Text(
                                text = "${index + 1}.",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = PrimaryText
                            )
                            Text(
                                text = step,
                                fontSize = 16.sp,
                                color = SecondaryText,
                                lineHeight = 22.sp,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Composable to display general content with key points in bulleted format
 */
@Composable
fun BulletedContentView(content: GeneralContent) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        SectionHeader("Key Points")
        if (content.keyPoints.isNotEmpty()) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                content.keyPoints.forEach { keyPoint ->
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Text(text = "•", fontSize = 16.sp, color = SecondaryText)
                        Text(
                            text = keyPoint,
                            fontSize = 16.sp,
                            color = SecondaryText,
                            lineHeight = 22.sp,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }
    }
}

/**
 * Sealed class to represent parsed content result
 */
sealed class ParsedContent {
    data class Recipe(val content: RecipeContent) : ParsedContent()
    data class Beauty(val content: BeautyContent) : ParsedContent()
    data class General(val content: GeneralContent) : ParsedContent()
    data class Error(val message: String) : ParsedContent()
}

/**
 * Helper function to parse structured content JSON
 */
fun parseStructuredContent(structuredContentJson: String?): ParsedContent? {
    if (structuredContentJson == null || structuredContentJson.isEmpty()) {
        return null
    }

    val moshi = Moshi.Builder().build()

    return try {
        val jsonObject = JSONObject(structuredContentJson)
        val type = jsonObject.optString("type", "general")

        when (type) {
            "recipe" -> {
                val adapter: JsonAdapter<RecipeContent> = moshi.adapter(RecipeContent::class.java)
                val content = adapter.fromJson(structuredContentJson)
                content?.let { ParsedContent.Recipe(it) }
            }
            "beauty" -> {
                val adapter: JsonAdapter<BeautyContent> = moshi.adapter(BeautyContent::class.java)
                val content = adapter.fromJson(structuredContentJson)
                content?.let { ParsedContent.Beauty(it) }
            }
            "general" -> {
                val adapter: JsonAdapter<GeneralContent> = moshi.adapter(GeneralContent::class.java)
                val content = adapter.fromJson(structuredContentJson)
                content?.let { ParsedContent.General(it) }
            }
            else -> {
                val adapter: JsonAdapter<GeneralContent> = moshi.adapter(GeneralContent::class.java)
                val content = adapter.fromJson(structuredContentJson)
                content?.let { ParsedContent.General(it) }
            }
        }
    } catch (e: Exception) {
        ParsedContent.Error("Unable to parse structured content: ${e.message}")
    }
}

/**
 * Main composable that parses structured content JSON and displays the appropriate view
 */
@Composable
fun StructuredContentDisplay(structuredContentJson: String?) {
    val parsedContent = parseStructuredContent(structuredContentJson)

    when (parsedContent) {
        is ParsedContent.Recipe -> RecipeContentView(parsedContent.content)
        is ParsedContent.Beauty -> BeautyContentView(parsedContent.content)
        is ParsedContent.General -> BulletedContentView(parsedContent.content)
        is ParsedContent.Error -> {
            Text(
                text = parsedContent.message,
                fontSize = 16.sp,
                color = SecondaryText
            )
        }
        null -> { /* Do nothing */ }
    }
}
