package com.example.transcribeassistant.ui.screen.feed

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.transcribeassistant.domain.model.Subcategory
import com.example.transcribeassistant.ui.screen.components.ScoopBlue
import com.example.transcribeassistant.ui.screen.components.ScoopCyan
import com.example.transcribeassistant.ui.screen.components.ScoopPurple
import com.example.transcribeassistant.ui.screen.components.SecondaryText

private val SubPillBg = Color(0xFFE0F7FA)
private val SubPillFg = Color(0xFF1F2937)
private val Hairline = Color(0xFFE5E7EB)
private val Fg1 = Color(0xFF1F2937)
private val Fg2 = Color(0xFF6B7280)
private val ActiveBg = Color(0x177165E0)

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun SubcategoryPickerSheet(
    categoryName: String,
    subcategories: List<Subcategory>,
    currentSubcategoryId: String?,
    isBulkMode: Boolean,
    bulkCount: Int,
    isLoading: Boolean,
    isSaving: Boolean,
    sheetState: SheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
    onDismiss: () -> Unit,
    onSave: (subcategoryId: String) -> Unit,
    onCreateSubcategory: (name: String) -> Unit
) {
    var selectedId by remember(currentSubcategoryId, isBulkMode) {
        mutableStateOf(if (isBulkMode) null else currentSubcategoryId)
    }
    var showCreateRow by remember { mutableStateOf(false) }
    var newSubName by remember { mutableStateOf("") }
    val focusRequester = remember { FocusRequester() }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color.White,
        shape = RoundedCornerShape(topStart = 22.dp, topEnd = 22.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 20.dp)
                .padding(bottom = 24.dp)
        ) {
            // Handle
            Box(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(bottom = 14.dp)
                    .size(width = 36.dp, height = 4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(Hairline)
            )

            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (isBulkMode) "Categorize" else "Subcategories",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Fg1
                )
                if (isBulkMode) {
                    Text(
                        text = "$bulkCount items",
                        fontSize = 12.sp,
                        color = Fg2
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Subtitle
            if (isBulkMode) {
                Text(
                    text = "Selected items will be set to exactly this subcategory.",
                    fontSize = 12.sp,
                    color = Fg2
                )
            } else {
                Row {
                    Text(text = "In ", fontSize = 12.sp, color = Fg2)
                    Text(
                        text = categoryName,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Fg1
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            if (isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = ScoopPurple, modifier = Modifier.size(28.dp))
                }
            } else {
                // Subcategory chip grid
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    subcategories.forEach { sub ->
                        val isActive = sub.id == selectedId
                        SubchipItem(
                            label = sub.name,
                            isActive = isActive,
                            onClick = {
                                selectedId = if (isActive) null else sub.id
                            }
                        )
                    }

                    // "+ New" dashed button
                    if (!showCreateRow) {
                        DashedNewChip(onClick = { showCreateRow = true })
                    }
                }

                // Inline create row
                AnimatedVisibility(visible = showCreateRow) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 12.dp)
                            .border(1.5.dp, ScoopPurple, RoundedCornerShape(14.dp))
                            .padding(horizontal = 12.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("+", color = ScoopPurple, fontSize = 16.sp, fontWeight = FontWeight.Medium)
                        Spacer(modifier = Modifier.width(8.dp))
                        BasicTextField(
                            value = newSubName,
                            onValueChange = { newSubName = it },
                            modifier = Modifier
                                .weight(1f)
                                .focusRequester(focusRequester),
                            textStyle = TextStyle(fontSize = 13.sp, color = Fg1),
                            cursorBrush = SolidColor(ScoopPurple),
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                            keyboardActions = KeyboardActions(onDone = {
                                if (newSubName.isNotBlank()) {
                                    onCreateSubcategory(newSubName.trim())
                                    newSubName = ""
                                    showCreateRow = false
                                }
                            }),
                            decorationBox = { inner ->
                                if (newSubName.isEmpty()) {
                                    Text("New subcategory…", fontSize = 13.sp, color = Fg2)
                                }
                                inner()
                            }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(9999.dp))
                                .background(
                                    Brush.linearGradient(
                                        colors = listOf(ScoopPurple, ScoopBlue, ScoopCyan)
                                    )
                                )
                                .clickable {
                                    if (newSubName.isNotBlank()) {
                                        onCreateSubcategory(newSubName.trim())
                                        newSubName = ""
                                        showCreateRow = false
                                    }
                                }
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text("Add", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Save / Apply CTA
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        if (selectedId != null) {
                            Brush.linearGradient(colors = listOf(ScoopPurple, ScoopBlue, ScoopCyan))
                        } else {
                            Brush.linearGradient(colors = listOf(Color.Gray, Color.Gray))
                        }
                    )
                    .clickable(enabled = selectedId != null && !isSaving) {
                        selectedId?.let { onSave(it) }
                    },
                contentAlignment = Alignment.Center
            ) {
                if (isSaving) {
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.size(22.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        text = if (isBulkMode) "Apply to $bulkCount" else "Save",
                        color = Color.White,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun SubchipItem(
    label: String,
    isActive: Boolean,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(9999.dp),
        color = if (isActive) ActiveBg else Color.White,
        border = BorderStroke(
            width = 1.5.dp,
            color = if (isActive) ScoopPurple else Hairline
        ),
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Text(
            text = if (isActive) "✓  $label" else label,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            color = if (isActive) ScoopPurple else Fg1
        )
    }
}

@Composable
private fun DashedNewChip(onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(9999.dp))
            .border(
                width = 1.5.dp,
                color = ScoopPurple,
                shape = RoundedCornerShape(9999.dp)
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 8.dp)
    ) {
        Text(
            text = "+ New",
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            color = ScoopPurple
        )
    }
}
