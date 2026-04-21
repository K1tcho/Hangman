package com.shubham.hangman.ui

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.shubham.hangman.HangmanViewModel
import com.shubham.hangman.model.AppLanguage
import com.shubham.hangman.model.AppStrings
import com.shubham.hangman.model.GameEngine
import com.shubham.hangman.model.HangmanCategory
import com.shubham.hangman.model.HangmanUiState
import com.shubham.hangman.model.RoundStatus
import com.shubham.hangman.ui.theme.Ink
import com.shubham.hangman.ui.theme.MarginRed
import com.shubham.hangman.ui.theme.NotebookBlue
import com.shubham.hangman.ui.theme.NotebookBlueSoft
import com.shubham.hangman.ui.theme.Paper
import com.shubham.hangman.ui.theme.PaperDeep
import com.shubham.hangman.ui.theme.PatrickHandFont
import com.shubham.hangman.ui.theme.Pencil
import kotlin.math.min

@Composable
fun HangmanApp(viewModel: HangmanViewModel) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var showCredits by remember { mutableStateOf(false) }

    if (showCredits) {
        CreditsScreen(onBack = { showCredits = false })
    } else {
        HangmanScreen(
            uiState = uiState,
            onGuess = viewModel::guessLetter,
            onCategorySelected = viewModel::selectCategory,
            onLanguageSelected = viewModel::selectLanguage,
            onUseHint = viewModel::useHint,
            onRevealClue = viewModel::revealClue,
            onNewRound = viewModel::startNewRound,
            onShowCredits = { showCredits = true },
        )
    }
}

@Composable
private fun HangmanScreen(
    uiState: HangmanUiState,
    onGuess: (Char) -> Unit,
    onCategorySelected: (HangmanCategory) -> Unit,
    onLanguageSelected: (AppLanguage) -> Unit,
    onUseHint: () -> Unit,
    onRevealClue: () -> Unit,
    onNewRound: () -> Unit,
    onShowCredits: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        PaperDeep.copy(alpha = 0.72f),
                        Paper,
                    ),
                ),
            )
            .drawBehind {
                val spacing = 26.dp.toPx()
                val lineColor = NotebookBlue.copy(alpha = 0.12f)
                var y = 0f
                while (y < size.height) {
                    drawLine(
                        color = lineColor,
                        start = Offset(0f, y),
                        end = Offset(size.width, y),
                        strokeWidth = 1.dp.toPx(),
                    )
                    y += spacing
                }
            }
            .statusBarsPadding()
            .navigationBarsPadding(),
    ) {
        BoxWithConstraints(
            modifier = Modifier.fillMaxSize(),
        ) {
            val compactHeight = maxHeight < 760.dp
            val outerPadding = if (compactHeight) 12.dp else 16.dp
            val sectionGap = if (compactHeight) 10.dp else 12.dp

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = outerPadding, vertical = sectionGap),
                verticalArrangement = Arrangement.spacedBy(sectionGap),
            ) {
                TopStrip(
                    uiState = uiState,
                    compact = compactHeight,
                    onCategorySelected = onCategorySelected,
                    onLanguageSelected = onLanguageSelected,
                    onShowCredits = onShowCredits,
                )
                NotebookBoard(
                    uiState = uiState,
                    compact = compactHeight,
                    modifier = Modifier.weight(1.1f),
                )
                KeyboardDock(
                    uiState = uiState,
                    compact = compactHeight,
                    modifier = Modifier.weight(0.9f),
                    onGuess = onGuess,
                    onUseHint = onUseHint,
                    onRevealClue = onRevealClue,
                    onNewRound = onNewRound,
                )
            }
        }
    }
}

@Composable
private fun TopStrip(
    uiState: HangmanUiState,
    compact: Boolean,
    onCategorySelected: (HangmanCategory) -> Unit,
    onLanguageSelected: (AppLanguage) -> Unit,
    onShowCredits: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // Stats Group
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(if (compact) 4.dp else 6.dp)
        ) {
            StatPillSmall(label = "Score", value = uiState.score.toString(), compact = compact)
            StatPillSmall(label = "🏆", value = uiState.bestStreak.toString(), compact = compact)
        }

        // Category (Flexible)
        Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
            CategoryDropdown(
                selectedCategory = uiState.selectedCategory,
                language = uiState.selectedLanguage,
                compact = compact,
                onCategorySelected = onCategorySelected,
            )
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(if (compact) 4.dp else 8.dp)
        ) {
            StatPillSmall(label = "🔥", value = uiState.streak.toString(), compact = compact)
            LanguageButton(
                selectedLanguage = uiState.selectedLanguage,
                compact = compact,
                onLanguageSelected = onLanguageSelected
            )
            InfoButton(compact = compact, onClick = onShowCredits)
        }
    }
}

@Composable
private fun StatPillSmall(
    label: String,
    value: String,
    compact: Boolean,
) {
    val isEmoji = label.length <= 2
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = PaperDeep.copy(alpha = 0.45f),
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .drawBehind {
                drawRoundRect(
                    color = Ink.copy(alpha = 0.2f),
                    style = Stroke(width = 1.dp.toPx()),
                    cornerRadius = CornerRadius(10.dp.toPx(), 10.dp.toPx()),
                )
            },
    ) {
        Row(
            modifier = Modifier.padding(horizontal = if (compact) 6.dp else 8.dp, vertical = if (compact) 3.dp else 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(if (compact) 3.dp else 4.dp),
        ) {
            Text(
                text = label,
                fontSize = if (isEmoji) (if (compact) 14.sp else 16.sp) else (if (compact) 10.sp else 11.sp),
                color = Ink.copy(alpha = if (isEmoji) 1f else 0.75f),
                fontFamily = PatrickHandFont,
                fontWeight = FontWeight.Medium,
            )
            Text(
                text = value,
                fontSize = if (compact) 14.sp else 16.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = PatrickHandFont,
                color = Color.Black,
            )
        }
    }
}

@Composable
private fun LanguageButton(
    selectedLanguage: AppLanguage,
    compact: Boolean,
    onLanguageSelected: (AppLanguage) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    Surface(
        onClick = { expanded = true },
        shape = RoundedCornerShape(16.dp),
        color = PaperDeep.copy(alpha = 0.8f),
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .drawBehind {
                drawRoundRect(
                    color = Ink.copy(alpha = 0.22f),
                    style = Stroke(width = 1.2.dp.toPx()),
                    cornerRadius = CornerRadius(16.dp.toPx(), 16.dp.toPx()),
                )
            },
    ) {
        Box {
            Row(
                modifier = Modifier.padding(horizontal = 10.dp, vertical = if (compact) 7.dp else 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(5.dp),
            ) {
                // Globe drawn with Canvas
                Canvas(
                    modifier = Modifier.size(if (compact) 15.dp else 16.dp),
                ) {
                    val cx = size.width / 2f
                    val cy = size.height / 2f
                    val r = size.minDimension / 2f - 1.dp.toPx()
                    val s = Stroke(width = 1.4.dp.toPx(), cap = StrokeCap.Round)
                    val col = Ink.copy(alpha = 0.82f)
                    // outer circle
                    drawCircle(col, r, center = Offset(cx, cy), style = s)
                    // horizontal equator
                    drawLine(col, Offset(cx - r, cy), Offset(cx + r, cy), strokeWidth = 1.4.dp.toPx())
                    // vertical meridian
                    drawLine(col, Offset(cx, cy - r), Offset(cx, cy + r), strokeWidth = 1.4.dp.toPx())
                    // left longitude arc (approximate with short arc via oval)
                    drawOval(
                        col,
                        topLeft = Offset(cx - r * 0.38f, cy - r),
                        size = Size(r * 0.76f, r * 2f),
                        style = s,
                    )
                }
                Text(
                    text = selectedLanguage.code,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    fontFamily = PatrickHandFont,
                )
            }

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                containerColor = PaperDeep.copy(alpha = 0.96f),
            ) {
                for (lang in AppLanguage.entries.toList()) {
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = "${lang.flagEmoji} ${lang.nativeName}",
                                fontFamily = PatrickHandFont,
                                fontWeight = if (lang == selectedLanguage) FontWeight.Bold else FontWeight.Medium,
                            )
                        },
                        onClick = {
                            expanded = false
                            if (lang != selectedLanguage) {
                                onLanguageSelected(lang)
                            }
                        },
                    )
                }
            }
        }
    }
}


@Composable
private fun InfoButton(compact: Boolean, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        shape = CircleShape,
        color = PaperDeep.copy(alpha = 0.8f),
        modifier = Modifier
            .size(if (compact) 34.dp else 38.dp)
            .clip(CircleShape)
            .drawBehind {
                drawCircle(
                    color = Ink.copy(alpha = 0.22f),
                    radius = size.minDimension / 2f,
                    style = Stroke(width = 1.2.dp.toPx()),
                )
            },
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = "i",
                fontFamily = FontFamily.Serif,
                fontStyle = FontStyle.Italic,
                fontWeight = FontWeight.Bold,
                fontSize = if (compact) 16.sp else 18.sp,
                color = Ink,
            )
        }
    }
}

@Composable
private fun CompactStatPill(
    label: String,
    value: String,
    compact: Boolean,
) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = PaperDeep.copy(alpha = 0.62f),
        modifier = Modifier
            .clip(RoundedCornerShape(14.dp))
            .drawBehind {
                drawRoundRect(
                    color = Ink.copy(alpha = 0.18f),
                    style = Stroke(width = 1.dp.toPx()),
                    cornerRadius = CornerRadius(14.dp.toPx(), 14.dp.toPx()),
                )
            },
    ) {
        Row(
            modifier = Modifier.padding(horizontal = if (compact) 9.dp else 10.dp, vertical = if (compact) 6.dp else 7.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
            )
        }
    }
}

@Composable
private fun CategoryDropdown(
    selectedCategory: HangmanCategory,
    language: AppLanguage,
    compact: Boolean,
    onCategorySelected: (HangmanCategory) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    Surface(
        onClick = { expanded = true },
        shape = RoundedCornerShape(16.dp),
        color = PaperDeep.copy(alpha = 0.8f),
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .drawBehind {
                val strokeWidth = 1.2.dp.toPx()
                drawRoundRect(
                    color = Ink.copy(alpha = 0.22f),
                    style = Stroke(width = strokeWidth),
                    cornerRadius = CornerRadius(16.dp.toPx(), 16.dp.toPx()),
                )
            },
    ) {
        Box {
            Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = if (compact) 7.dp else 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                Text(
                    text = selectedCategory.title(language),
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    fontFamily = PatrickHandFont,
                )
                Text(
                    text = "v",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                )
            }

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                containerColor = PaperDeep.copy(alpha = 0.96f),
            ) {
                for (category in HangmanCategory.entries.toList()) {
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = category.title(language),
                                fontFamily = PatrickHandFont,
                                fontWeight = if (category == selectedCategory) FontWeight.Bold else FontWeight.Medium,
                            )
                        },
                        onClick = {
                            expanded = false
                            if (category != selectedCategory) {
                                onCategorySelected(category)
                            }
                        },
                    )
                }
            }
        }
    }
}

@Composable
private fun NotebookBoard(
    uiState: HangmanUiState,
    compact: Boolean,
    modifier: Modifier = Modifier,
) {
    NotebookPaper(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        cornerRadius = 28.dp,
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = if (compact) 18.dp else 22.dp, vertical = if (compact) 12.dp else 16.dp),
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = AppStrings.chancesLeft(uiState.remainingAttempts, uiState.selectedLanguage),
                style = MaterialTheme.typography.titleMedium,
                fontFamily = PatrickHandFont,
                color = Pencil.copy(alpha = 0.75f),
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
            )

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(vertical = if (compact) 4.dp else 8.dp),
                contentAlignment = Alignment.Center
            ) {
                SketchHangman(
                    wrongGuessCount = uiState.wrongGuessCount,
                    compact = compact,
                )
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(if (compact) 6.dp else 8.dp)
            ) {
                WordArea(uiState = uiState, compact = compact)
                ClueRow(uiState = uiState, compact = compact)
            }

            StatusNote(uiState = uiState, compact = compact)
        }
    }
}

@Composable
private fun WordArea(uiState: HangmanUiState, compact: Boolean) {
    val wordFont = when {
        uiState.answer.length >= 11 -> if (compact) 20.sp else 22.sp
        compact -> 24.sp
        else -> 28.sp
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        AnimatedContent(
            targetState = uiState.visibleWord,
            transitionSpec = { fadeIn() togetherWith fadeOut() },
            label = "word",
        ) { visibleWord ->
            Text(
                text = visibleWord,
                textAlign = TextAlign.Center,
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.SemiBold,
                fontSize = wordFont,
                lineHeight = wordFont * 1.25,
                letterSpacing = 1.1.sp,
                modifier = Modifier.fillMaxWidth(),
            )
        }

        Canvas(
            modifier = Modifier
                .fillMaxWidth(0.82f)
                .height(8.dp),
        ) {
            val y = size.height / 2
            drawLine(
                color = Ink.copy(alpha = 0.58f),
                start = Offset(0f, y),
                end = Offset(size.width, y),
                strokeWidth = 2.dp.toPx(),
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(14f, 8f)),
            )
        }
    }
}

@Composable
private fun StatusNote(uiState: HangmanUiState, compact: Boolean) {
    val containerColor by animateColorAsState(
        targetValue = when (uiState.status) {
            RoundStatus.Playing -> NotebookBlueSoft.copy(alpha = 0.72f)
            RoundStatus.Won -> Color(0xFFDDECC8)
            RoundStatus.Lost -> Color(0xFFF0D6D2)
        },
        label = "statusColor",
    )

    Surface(
        shape = RoundedCornerShape(18.dp),
        color = containerColor,
        modifier = Modifier
            .fillMaxWidth()
            .height(if (compact) 78.dp else 84.dp)
            .drawBehind {
                val strokeWidth = 1.2.dp.toPx()
                drawRoundRect(
                    color = Ink.copy(alpha = 0.25f),
                    style = Stroke(width = strokeWidth),
                    cornerRadius = CornerRadius(18.dp.toPx(), 18.dp.toPx()),
                )
            },
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = if (compact) 10.dp else 12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = uiState.bannerMessage,
                fontSize = if (compact) 12.sp else 13.sp,
                lineHeight = if (compact) 16.sp else 18.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
                fontWeight = FontWeight.SemiBold,
                maxLines = 2,
            )
            if (uiState.rewardBreakdown.isNotBlank()) {
                Text(
                    text = uiState.rewardBreakdown,
                    fontSize = if (compact) 10.sp else 11.sp,
                    lineHeight = if (compact) 13.sp else 14.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                )
            }
        }
    }
}

@Composable
private fun ClueRow(uiState: HangmanUiState, compact: Boolean) {
    val clueText = if (uiState.clueRevealed) {
        "Clue: ${uiState.clue}"
    } else {
        ""
    }

    Text(
        text = clueText,
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = if (compact) 20.dp else 22.dp),
        textAlign = TextAlign.Center,
        maxLines = 1,
    )
}

@Composable
private fun KeyboardDock(
    uiState: HangmanUiState,
    compact: Boolean,
    modifier: Modifier = Modifier,
    onGuess: (Char) -> Unit,
    onUseHint: () -> Unit,
    onRevealClue: () -> Unit,
    onNewRound: () -> Unit,
) {
    SketchPanel(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(26.dp),
        cornerRadius = 26.dp,
        backgroundColor = Paper.copy(alpha = 0.94f),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = if (compact) 12.dp else 14.dp, vertical = if (compact) 12.dp else 14.dp),
            verticalArrangement = Arrangement.spacedBy(if (compact) 10.dp else 12.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                SketchActionButton(
                    label = AppStrings.clueButton(uiState.selectedLanguage),
                    compact = compact,
                    enabled = uiState.isRoundActive && !uiState.clueRevealed,
                    modifier = Modifier.weight(1f),
                    onClick = onRevealClue,
                )
                SketchActionButton(
                    label = AppStrings.hintButton(uiState.selectedLanguage),
                    compact = compact,
                    enabled = uiState.isRoundActive && !uiState.hintUsed,
                    modifier = Modifier.weight(1f),
                    onClick = onUseHint,
                )
                SketchActionButton(
                    label = if (uiState.isRoundActive) AppStrings.skipButton(uiState.selectedLanguage) else AppStrings.nextButton(uiState.selectedLanguage),
                    compact = compact,
                    enabled = true,
                    modifier = Modifier.weight(1f),
                    onClick = onNewRound,
                )
            }

            KeyboardGrid(
                uiState = uiState,
                compact = compact,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                onGuess = onGuess,
            )
        }
    }
}

@Composable
private fun SketchActionButton(
    label: String,
    compact: Boolean,
    enabled: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    val background = if (enabled) PaperDeep.copy(alpha = 0.76f) else PaperDeep.copy(alpha = 0.44f)
    val textColor = if (enabled) Ink else Pencil.copy(alpha = 0.64f)

    Surface(
        onClick = onClick,
        enabled = enabled,
        shape = RoundedCornerShape(16.dp),
        color = background,
        modifier = modifier
            .height(if (compact) 42.dp else 46.dp)
            .clip(RoundedCornerShape(16.dp))
            .drawBehind {
                val strokeWidth = 1.4.dp.toPx()
                drawRoundRect(
                    color = Ink.copy(alpha = 0.34f),
                    style = Stroke(width = strokeWidth),
                    cornerRadius = CornerRadius(16.dp.toPx(), 16.dp.toPx()),
                )
                drawRoundRect(
                    color = Ink.copy(alpha = 0.18f),
                    topLeft = Offset(1.5.dp.toPx(), 1.dp.toPx()),
                    size = Size(size.width - 3.dp.toPx(), size.height - 2.dp.toPx()),
                    style = Stroke(width = 1.dp.toPx()),
                    cornerRadius = CornerRadius(14.dp.toPx(), 14.dp.toPx()),
                )
            },
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold,
                color = textColor,
                fontFamily = PatrickHandFont,
            )
        }
    }
}

@Composable
private fun KeyboardGrid(
    uiState: HangmanUiState,
    compact: Boolean,
    modifier: Modifier = Modifier,
    onGuess: (Char) -> Unit,
) {
    val keyboardRows = GameEngine.keyboardRows(uiState.selectedLanguage)

    BoxWithConstraints(modifier = modifier) {
        val columnGap = if (compact) 4.dp else 6.dp
        val rowGap = if (compact) 6.dp else 8.dp
        val maxColumns = keyboardRows.maxOf { it.length }
        val keyWidth = (maxWidth - (columnGap * (maxColumns - 1))) / maxColumns.toFloat()
        val keyHeight = (maxHeight - (rowGap * (keyboardRows.size - 1))) / keyboardRows.size.toFloat()
        val keySize = minOf(keyWidth, keyHeight).coerceIn(if (compact) 26.dp else 30.dp, if (compact) 48.dp else 56.dp)
        val keyTextSize = min(if (compact) 18f else 22f, keySize.value * 0.56f).sp

        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(rowGap, Alignment.CenterVertically),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            for (rowLetters in keyboardRows) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(columnGap, Alignment.CenterHorizontally),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    for (letter in rowLetters.toCharArray()) {
                        val isUsed = letter in uiState.guessedLetters || letter in uiState.wrongLetters
                        SketchKey(
                            letter = letter,
                            keySize = keySize,
                            textSize = keyTextSize,
                            enabled = uiState.isRoundActive && !isUsed,
                            correct = letter in uiState.guessedLetters,
                            wrong = letter in uiState.wrongLetters,
                            onClick = { onGuess(letter) },
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SketchKey(
    letter: Char,
    keySize: Dp,
    textSize: TextUnit,
    enabled: Boolean,
    correct: Boolean,
    wrong: Boolean,
    onClick: () -> Unit,
) {
    val background = when {
        correct -> Color(0xFFDDECC8)
        wrong -> Color(0xFFF0D6D2)
        else -> PaperDeep.copy(alpha = 0.78f)
    }

    Surface(
        onClick = onClick,
        enabled = enabled,
        shape = RoundedCornerShape(14.dp),
        color = background,
        modifier = Modifier
            .size(width = keySize, height = keySize)
            .clip(RoundedCornerShape(14.dp))
            .drawBehind {
                val strokeWidth = 1.3.dp.toPx()
                drawRoundRect(
                    color = Ink.copy(alpha = 0.34f),
                    style = Stroke(width = strokeWidth),
                    cornerRadius = CornerRadius(14.dp.toPx(), 14.dp.toPx()),
                )
                drawRoundRect(
                    color = Ink.copy(alpha = 0.16f),
                    topLeft = Offset(1.3.dp.toPx(), 1.dp.toPx()),
                    size = Size(size.width - 2.6.dp.toPx(), size.height - 2.dp.toPx()),
                    style = Stroke(width = 0.9.dp.toPx()),
                    cornerRadius = CornerRadius(12.dp.toPx(), 12.dp.toPx()),
                )
            },
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = letter.toString(),
                fontSize = textSize,
                fontWeight = FontWeight.Bold,
                fontFamily = PatrickHandFont,
                color = if (enabled || correct || wrong) Ink else Pencil.copy(alpha = 0.6f),
            )
        }
    }
}

@Composable
private fun SketchHangman(
    wrongGuessCount: Int,
    compact: Boolean,
    modifier: Modifier = Modifier,
) {
    Canvas(
        modifier = modifier
            .fillMaxWidth(if (compact) 0.85f else 0.9f)
            .aspectRatio(if (compact) 1.5f else 1.4f),
    ) {
        val h = size.height
        val w = size.width
        val ink = Ink
        val shadow = Pencil.copy(alpha = 0.2f)
        val stroke = if (compact) 4.dp.toPx() else 5.dp.toPx()
        val sketchStroke = Stroke(width = stroke, cap = StrokeCap.Round)

        val baseY = h * 0.92f
        val poleX = w * 0.22f
        val topY = h * 0.08f
        val beamX = w * 0.66f
        val ropeY = h * 0.22f
        val bodyCenterX = beamX
        val headRadius = h * 0.12f
        val bodyTopY = ropeY + headRadius
        val bodyBottomY = bodyTopY + h * 0.32f

        fun drawSketchLine(start: Offset, end: Offset) {
            drawLine(shadow, start + Offset(1.5f, 1.5f), end + Offset(1.5f, 1.5f), strokeWidth = stroke, cap = StrokeCap.Round)
            drawLine(ink, start, end, strokeWidth = stroke, cap = StrokeCap.Round)
        }

        // Gallows
        drawSketchLine(Offset(w * 0.1f, baseY), Offset(w * 0.85f, baseY))
        drawSketchLine(Offset(poleX, baseY), Offset(poleX, topY))
        drawSketchLine(Offset(poleX, topY), Offset(beamX, topY))
        drawSketchLine(Offset(beamX, topY), Offset(beamX, ropeY))

        if (wrongGuessCount >= 1) { // Head
            drawCircle(shadow, headRadius, center = Offset(bodyCenterX + 1.5f, bodyTopY + 1.5f), style = sketchStroke)
            drawCircle(ink, headRadius, center = Offset(bodyCenterX, bodyTopY), style = sketchStroke)
        }
        if (wrongGuessCount >= 2) { // Body
            drawSketchLine(Offset(bodyCenterX, bodyTopY + headRadius), Offset(bodyCenterX, bodyBottomY))
        }
        if (wrongGuessCount >= 3) { // Left Arm
            drawSketchLine(Offset(bodyCenterX, bodyTopY + headRadius + h * 0.05f), Offset(bodyCenterX - w * 0.1f, bodyTopY + headRadius + h * 0.15f))
        }
        if (wrongGuessCount >= 4) { // Right Arm
            drawSketchLine(Offset(bodyCenterX, bodyTopY + headRadius + h * 0.05f), Offset(bodyCenterX + w * 0.1f, bodyTopY + headRadius + h * 0.15f))
        }
        if (wrongGuessCount >= 5) { // Left Leg
            drawSketchLine(Offset(bodyCenterX, bodyBottomY), Offset(bodyCenterX - w * 0.1f, bodyBottomY + h * 0.2f))
        }
        if (wrongGuessCount >= 6) { // Right Leg
            drawSketchLine(Offset(bodyCenterX, bodyBottomY), Offset(bodyCenterX + w * 0.1f, bodyBottomY + h * 0.2f))
        }
    }
}

@Composable
private fun NotebookPaper(
    modifier: Modifier = Modifier,
    shape: RoundedCornerShape,
    cornerRadius: Dp,
    content: @Composable () -> Unit,
) {
    Box(
        modifier = modifier
            .clip(shape)
            .background(Paper.copy(alpha = 0.96f))
            .drawBehind {
                val spacing = 28.dp.toPx()
                val topInset = 18.dp.toPx()
                val leftInset = 18.dp.toPx()
                val rightInset = 14.dp.toPx()
                val bottomInset = 14.dp.toPx()
                val blue = NotebookBlue.copy(alpha = 0.4f)
                val marginX = 34.dp.toPx()

                var y = topInset + spacing
                while (y < size.height - bottomInset) {
                    drawLine(
                        color = blue,
                        start = Offset(leftInset, y),
                        end = Offset(size.width - rightInset, y),
                        strokeWidth = 1.dp.toPx(),
                    )
                    y += spacing
                }

                drawLine(
                    color = MarginRed.copy(alpha = 0.55f),
                    start = Offset(marginX, topInset),
                    end = Offset(marginX, size.height - bottomInset),
                    strokeWidth = 1.4.dp.toPx(),
                )

                drawRoundRect(
                    color = Ink.copy(alpha = 0.18f),
                    style = Stroke(width = 1.5.dp.toPx()),
                    cornerRadius = CornerRadius(cornerRadius.toPx(), cornerRadius.toPx()),
                )
                drawRoundRect(
                    color = Ink.copy(alpha = 0.08f),
                    topLeft = Offset(2.dp.toPx(), 1.5.dp.toPx()),
                    size = Size(size.width - 4.dp.toPx(), size.height - 3.dp.toPx()),
                    style = Stroke(width = 1.dp.toPx()),
                    cornerRadius = CornerRadius((cornerRadius - 2.dp).toPx(), (cornerRadius - 2.dp).toPx()),
                )
            },
    ) {
        content()
    }
}

@Composable
private fun SketchPanel(
    modifier: Modifier = Modifier,
    shape: RoundedCornerShape,
    cornerRadius: Dp,
    backgroundColor: Color,
    content: @Composable () -> Unit,
) {
    Box(
        modifier = modifier
            .clip(shape)
            .background(backgroundColor)
            .drawBehind {
                val radius = cornerRadius.toPx()
                drawRoundRect(
                    color = Ink.copy(alpha = 0.2f),
                    style = Stroke(width = 1.3.dp.toPx()),
                    cornerRadius = CornerRadius(radius, radius),
                )
                drawRoundRect(
                    color = Ink.copy(alpha = 0.1f),
                    topLeft = Offset(1.6.dp.toPx(), 1.dp.toPx()),
                    size = Size(size.width - 3.2.dp.toPx(), size.height - 2.dp.toPx()),
                    style = Stroke(width = 1.dp.toPx()),
                    cornerRadius = CornerRadius((radius - 2.dp.toPx()).coerceAtLeast(0f), (radius - 2.dp.toPx()).coerceAtLeast(0f)),
                )
            },
    ) {
        content()
    }
}

// ─────────────────────────────────────────────────────────────
// Credits Screen
// ─────────────────────────────────────────────────────────────

@Composable
fun CreditsScreen(onBack: () -> Unit) {
    val context = LocalContext.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        PaperDeep.copy(alpha = 0.72f),
                        Paper,
                    ),
                ),
            )
            .drawBehind {
                val spacing = 26.dp.toPx()
                val lineColor = NotebookBlue.copy(alpha = 0.12f)
                var y = 0f
                while (y < size.height) {
                    drawLine(
                        color = lineColor,
                        start = Offset(0f, y),
                        end = Offset(size.width, y),
                        strokeWidth = 1.dp.toPx(),
                    )
                    y += spacing
                }
            }
            .statusBarsPadding()
            .navigationBarsPadding(),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            // Back button row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Surface(
                    onClick = onBack,
                    shape = RoundedCornerShape(14.dp),
                    color = PaperDeep.copy(alpha = 0.8f),
                    modifier = Modifier
                        .clip(RoundedCornerShape(14.dp))
                        .drawBehind {
                            drawRoundRect(
                                color = Ink.copy(alpha = 0.22f),
                                style = Stroke(width = 1.2.dp.toPx()),
                                cornerRadius = CornerRadius(14.dp.toPx(), 14.dp.toPx()),
                            )
                        },
                ) {
                    Text(
                        text = "← Back",
                        fontFamily = PatrickHandFont,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = Ink,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                    )
                }
            }

            // Main credits card
            NotebookPaper(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                shape = RoundedCornerShape(28.dp),
                cornerRadius = 28.dp,
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 28.dp, vertical = 32.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(32.dp),
                    ) {
                        // App title
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            Text(
                                text = "Hangman",
                                fontFamily = PatrickHandFont,
                                fontWeight = FontWeight.Bold,
                                fontSize = 52.sp,
                                color = Ink,
                                textAlign = TextAlign.Center,
                            )
                            Canvas(
                                modifier = Modifier
                                    .fillMaxWidth(0.6f)
                                    .height(6.dp),
                            ) {
                                val y = size.height / 2
                                drawLine(
                                    color = MarginRed.copy(alpha = 0.7f),
                                    start = Offset(0f, y),
                                    end = Offset(size.width, y),
                                    strokeWidth = 2.dp.toPx(),
                                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 7f)),
                                )
                            }
                        }

                        // Created by
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(4.dp),
                        ) {
                            Text(
                                text = "created by",
                                fontFamily = FontFamily.Serif,
                                fontStyle = FontStyle.Italic,
                                fontSize = 16.sp,
                                color = Pencil.copy(alpha = 0.8f),
                                textAlign = TextAlign.Center,
                            )
                            Text(
                                text = "Shubham Ojha",
                                fontFamily = PatrickHandFont,
                                fontWeight = FontWeight.Bold,
                                fontSize = 36.sp,
                                color = Ink,
                                textAlign = TextAlign.Center,
                            )
                        }

                        // Social links
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            // GitHub button
                            SocialLinkButton(
                                label = "GitHub",
                                glyphText = "{ }",
                                smallGlyph = true,
                                onClick = {
                                    context.startActivity(
                                        Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/K1tcho"))
                                    )
                                },
                            )
                            // LinkedIn button
                            SocialLinkButton(
                                label = "LinkedIn",
                                glyphText = "in",
                                smallGlyph = false,
                                onClick = {
                                    context.startActivity(
                                        Intent(Intent.ACTION_VIEW, Uri.parse("https://www.linkedin.com/in/shubham-ojha-k1tcho/"))
                                    )
                                },
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SocialLinkButton(
    label: String,
    glyphText: String,
    smallGlyph: Boolean,
    onClick: () -> Unit,
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(20.dp),
        color = PaperDeep.copy(alpha = 0.88f),
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .drawBehind {
                drawRoundRect(
                    color = Ink.copy(alpha = 0.25f),
                    style = Stroke(width = 1.4.dp.toPx()),
                    cornerRadius = CornerRadius(20.dp.toPx(), 20.dp.toPx()),
                )
            },
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 22.dp, vertical = 14.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            // Social logo badge
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(11.dp))
                    .background(Ink),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = glyphText,
                    fontFamily = PatrickHandFont,
                    fontWeight = FontWeight.Bold,
                    fontSize = if (smallGlyph) 12.sp else 20.sp,
                    color = Paper,
                    textAlign = TextAlign.Center,
                )
            }
            Text(
                text = label,
                fontFamily = PatrickHandFont,
                fontWeight = FontWeight.SemiBold,
                fontSize = 13.sp,
                color = Ink.copy(alpha = 0.82f),
            )
        }
    }
}

