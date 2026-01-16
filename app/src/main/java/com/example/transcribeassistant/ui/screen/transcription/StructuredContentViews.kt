package com.example.transcribeassistant.ui.screen.transcription

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
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
 * Composable to display recipe content with ingredients and steps
 */
@Composable
fun RecipeContentView(content: RecipeContent) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        // Ingredients Section
        Text(
            text = "Ingredients",
            style = MaterialTheme.typography.titleMedium.copy(
                brush = Brush.linearGradient(
                    colors = listOf(ScoopPurple, ScoopBlue, ScoopCyan)
                )
            ),
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        content.ingredients.forEach { ingredient ->
            Text(
                text = "• $ingredient",
                style = MaterialTheme.typography.bodyMedium,
                color = PrimaryText,
                modifier = Modifier.padding(vertical = 2.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Steps Section
        Text(
            text = "Recipe Steps",
            style = MaterialTheme.typography.titleMedium.copy(
                brush = Brush.linearGradient(
                    colors = listOf(ScoopPurple, ScoopBlue, ScoopCyan)
                )
            ),
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        content.steps.forEachIndexed { index, step ->
            Text(
                text = "${index + 1}. $step",
                style = MaterialTheme.typography.bodyMedium,
                color = PrimaryText,
                modifier = Modifier.padding(vertical = 4.dp)
            )
        }
    }
}

/**
 * Composable to display beauty/skincare content with products and steps
 */
@Composable
fun BeautyContentView(content: BeautyContent) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        // Products Section
        Text(
            text = "Products Used",
            style = MaterialTheme.typography.titleMedium.copy(
                brush = Brush.linearGradient(
                    colors = listOf(ScoopPurple, ScoopBlue, ScoopCyan)
                )
            ),
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        content.products.forEach { product ->
            Text(
                text = "• $product",
                style = MaterialTheme.typography.bodyMedium,
                color = PrimaryText,
                modifier = Modifier.padding(vertical = 2.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Steps Section
        Text(
            text = "Steps",
            style = MaterialTheme.typography.titleMedium.copy(
                brush = Brush.linearGradient(
                    colors = listOf(ScoopPurple, ScoopBlue, ScoopCyan)
                )
            ),
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        content.steps.forEachIndexed { index, step ->
            Text(
                text = "${index + 1}. $step",
                style = MaterialTheme.typography.bodyMedium,
                color = PrimaryText,
                modifier = Modifier.padding(vertical = 4.dp)
            )
        }
    }
}

/**
 * Composable to display general content with key points in bulleted format
 */
@Composable
fun BulletedContentView(content: GeneralContent) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "Key Points",
            style = MaterialTheme.typography.titleMedium.copy(
                brush = Brush.linearGradient(
                    colors = listOf(ScoopPurple, ScoopBlue, ScoopCyan)
                )
            ),
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        content.keyPoints.forEach { point ->
            Text(
                text = "• $point",
                style = MaterialTheme.typography.bodyMedium,
                color = PrimaryText,
                modifier = Modifier.padding(vertical = 4.dp)
            )
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
        // First, parse with JSONObject to determine the type
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
                // Fallback: try to display as general content
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
                style = MaterialTheme.typography.bodyMedium,
                color = SecondaryText
            )
        }
        null -> { /* Do nothing */ }
    }
}
