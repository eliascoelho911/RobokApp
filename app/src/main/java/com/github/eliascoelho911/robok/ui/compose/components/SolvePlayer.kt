package com.github.eliascoelho911.robok.ui.compose.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProgressIndicatorDefaults
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.github.eliascoelho911.robok.R

/**
 * A modern Material 3 player control for Rubik's Cube solve animations
 *
 * @param currentMove The current move being displayed
 * @param nextMove The next move in the sequence
 * @param currentMoveIndex The index of the current move (0-based)
 * @param totalMoves Total number of moves in the sequence
 * @param isPlaying Whether the animation is currently playing (not used in controls)
 * @param isSolved Whether the cube is solved (all moves complete)
 * @param onPreviousClick Callback when previous button is clicked
 * @param onNextClick Callback when next button is clicked
 * @param modifier Modifier to be applied to the component
 */
@Composable
fun SolvePlayer(
    currentMove: String,
    nextMove: String = "",
    currentMoveIndex: Int,
    totalMoves: Int,
    isPlaying: Boolean,
    isSolved: Boolean = false,
    isEnabled: Boolean = true,
    onPreviousClick: () -> Unit,
    onNextClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val progress = if (totalMoves > 0) {
        currentMoveIndex.toFloat() / totalMoves.toFloat()
    } else {
        0f
    }

    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = ProgressIndicatorDefaults.ProgressAnimationSpec,
        label = "Progress"
    )

    ElevatedCard(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Move display
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Current move display with highlight
                AnimatedVisibility(
                    visible = !isSolved && currentMove.isNotEmpty() && currentMoveIndex > 0,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                        ),
                        modifier = Modifier.padding(end = 16.dp)
                    ) {
                        Text(
                            text = currentMove,
                            style = MaterialTheme.typography.headlineMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                        )
                    }
                }

                AnimatedVisibility(
                    visible = !isSolved && nextMove.isNotEmpty() && currentMoveIndex < totalMoves,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    val alpha = 0.4f
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(
                                alpha = alpha
                            )
                        ),
                        modifier = Modifier.padding(end = 16.dp)
                    ) {
                        Text(
                            text = nextMove,
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = alpha),
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                }

                // "Solved" label
                AnimatedVisibility(
                    visible = isSolved,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.tertiaryContainer
                        )
                    ) {
                        Text(
                            text = stringResource(R.string.solved),
                            style = MaterialTheme.typography.headlineMedium,
                            color = MaterialTheme.colorScheme.onTertiaryContainer,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                // Move counter
                val movesText = buildAnnotatedString {
                    if (currentMoveIndex == 0) {
                        withStyle(
                            style = SpanStyle(
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontWeight = FontWeight.Light
                            )
                        ) {
                            append(stringResource(R.string.move_count))
                            append(": ")
                        }
                    } else {
                        withStyle(
                            style = SpanStyle(
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontWeight = FontWeight.Light
                            )
                        ) {
                            append(stringResource(R.string.move))
                            append(" ")
                        }
                        withStyle(
                            style = SpanStyle(
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold
                            )
                        ) {
                            append("$currentMoveIndex")
                        }
                        withStyle(
                            style = SpanStyle(
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        ) {
                            append("/")
                        }
                    }

                    withStyle(
                        style = SpanStyle(
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    ) {
                        append("$totalMoves")
                    }
                }

                Text(
                    text = movesText,
                    style = MaterialTheme.typography.bodyLarge
                )
            }

            // Progress indicator
            LinearProgressIndicator(
                progress = { animatedProgress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(MaterialTheme.shapes.medium),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )

            // Control buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Previous button
                FilledIconButton(
                    onClick = onPreviousClick,
                    modifier = Modifier.size(56.dp),
                    enabled = currentMoveIndex > 0 && isEnabled,
                    colors = IconButtonDefaults.filledIconButtonColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant,
                        contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                    )
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_previous_24),
                        contentDescription = stringResource(R.string.previous_move),
                        modifier = Modifier.size(28.dp)
                    )
                }

                Spacer(modifier = Modifier.width(32.dp))

                // Next button
                FilledIconButton(
                    onClick = onNextClick,
                    modifier = Modifier.size(56.dp),
                    enabled = currentMoveIndex < totalMoves && !isSolved && isEnabled,
                    colors = IconButtonDefaults.filledIconButtonColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant,
                        contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                    )
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_next_24),
                        contentDescription = stringResource(R.string.next_mode),
                        modifier = Modifier.size(28.dp)
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RubikCubeSolvePlayerPreview_Playing() {
    Surface(color = MaterialTheme.colorScheme.background) {
        SolvePlayer(
            currentMove = "R'",
            currentMoveIndex = 4,
            nextMove = "U",
            totalMoves = 20,
            isPlaying = true,
            isSolved = false,
            onPreviousClick = {},
            onNextClick = {},
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun RubikCubeSolvePlayerPreview_First() {
    Surface(color = MaterialTheme.colorScheme.background) {
        SolvePlayer(
            currentMove = "",
            currentMoveIndex = -1,
            nextMove = "U",
            totalMoves = 20,
            isPlaying = true,
            isSolved = false,
            onPreviousClick = {},
            onNextClick = {},
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun RubikCubeSolvePlayerPreview_Paused() {
    Surface(color = MaterialTheme.colorScheme.background) {
        SolvePlayer(
            currentMove = "F",
            currentMoveIndex = 10,
            totalMoves = 20,
            isPlaying = false,
            isSolved = false,
            onPreviousClick = {},
            onNextClick = {},
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun RubikCubeSolvePlayerPreview_Solved() {
    Surface(color = MaterialTheme.colorScheme.background) {
        SolvePlayer(
            currentMove = "",
            currentMoveIndex = 20,
            totalMoves = 20,
            isPlaying = false,
            isSolved = true,
            onPreviousClick = {},
            onNextClick = {},
            modifier = Modifier.padding(16.dp)
        )
    }
}

/**
 * State holder for RubikCubeSolvePlayer that manages the player state
 */
class RubikCubeSolvePlayerState {
    var currentMove by mutableStateOf("")
        private set

    var nextMove by mutableStateOf("")
        private set

    var currentMoveIndex by mutableStateOf(0)
        private set

    var totalMoves by mutableStateOf(0)
        private set

    var isSolved by mutableStateOf(false)
        private set

    fun updateMove(move: String, nextMove: String = "", index: Int) {
        currentMove = move
        this.nextMove = nextMove
        currentMoveIndex = index
        isSolved = false
    }

    fun updateTotalMoves(total: Int) {
        totalMoves = total
    }

    fun markSolved() {
        currentMove = ""
        nextMove = ""
        isSolved = true
    }

    fun reset() {
        currentMove = ""
        nextMove = ""
        currentMoveIndex = 0
        isSolved = false
    }
}

/**
 * Remember a RubikCubeSolvePlayerState to manage the player state
 */
@Composable
fun rememberRubikCubeSolvePlayerState(): RubikCubeSolvePlayerState {
    return remember { RubikCubeSolvePlayerState() }
}